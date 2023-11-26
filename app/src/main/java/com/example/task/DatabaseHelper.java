package com.example.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 1;
    private Runnable updateTaskCallback;

    // SQL-запрос для создания таблицы категорий
    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE categories " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, category_name TEXT, task_count INTEGER)";

    // SQL-запрос для создания таблицы задач
    private static final String CREATE_TABLE_TASK = "CREATE TABLE tasks " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, task_name TEXT, category_id INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Обновление базы данных при необходимости
    }

    // Получает данные с сервера и вставляет их в локальную базу данных
    public void getDataFromServer() {
        new Thread(() -> {
            SQLiteDatabase db = this.getWritableDatabase();
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://37.77.105.18/api/TaskCategories").openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode();
                if (connection.getResponseCode() == 200) {
                    JsonObject data = new JsonParser().parse(new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next()).getAsJsonObject();

                    // Очистить локальную базу данных
                    db.execSQL("DELETE FROM categories");
                    db.execSQL("DELETE FROM tasks");

                    // Вставлять данные с сервера в локальную базу данных
                    JsonArray categoriesArray = data.getAsJsonArray("taskCategories");
                    for (JsonElement catjson : categoriesArray) {
                        JsonObject category = catjson.getAsJsonObject();
                        String categoryName = category.get("category").getAsString();
                        int taskCount = category.get("taskCount").getAsInt();

                        // Вставить категорию в таблицу категорий
                        ContentValues categoryValues = new ContentValues();
                        categoryValues.put("category_name", categoryName);
                        categoryValues.put("task_count", taskCount);
                        long categoryId = db.insert("categories", null, categoryValues);

                        // Вставляем случайные задания для категории
                        for (int i = 0; i < taskCount; i++) {
                            // Сгенерирует случайное название задачи
                            String randomTaskName = "Task " + i;

                            // Вставить задачу в таблицу задач
                            ContentValues taskValues = new ContentValues();
                            taskValues.put("task_name", randomTaskName);
                            taskValues.put("category_id", categoryId);
                            db.insert("tasks", null, taskValues);
                        }
                    }
                } else {
                    return;
                }
            } catch (IOException | SQLiteConstraintException e) {
                Log.e("IOException", e.getMessage());
            } finally {
                if (connection != null) connection.disconnect();
                if (updateTaskCallback != null) {
                    updateTaskCallback.run();
                }
            }
        }).start();
    }

    public void setUpdateTaskCallback(Runnable callback) {
        this.updateTaskCallback = callback;
    }

    // Добавление новой категории
    public long addCategory(TaskCategory category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("category_name", category.getCategoryName());
        values.put("task_count", category.getTaskCount());
        long id = db.insert("categories", null, values);
        db.close();
        return id;
    }

    // Получение списка всех категорий
    @SuppressLint("Range")
    public List<TaskCategory> getAllCategories() {
        List<TaskCategory> categories = new ArrayList<>();
        String query = "SELECT * FROM categories";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                TaskCategory category = new TaskCategory();
                category.setId(cursor.getInt(cursor.getColumnIndex("id")));
                category.setCategoryName(cursor.getString(cursor.getColumnIndex("category_name")));
                category.setTaskCount(cursor.getInt(cursor.getColumnIndex("task_count")));
                categories.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return categories;
    }

    // Добавление новой задачи
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("task_name", task.getTaskName());
        values.put("category_id", task.getCategoryId());
        long id = db.insert("tasks", null, values);
        // Обновление количества задач в категории
        updateTaskCount(task.getCategoryId());
        db.close();
        return id;
    }

    // Получение списка задач для данной категории
    @SuppressLint("Range")
    public List<Task> getTasksForCategory(int categoryId) {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM tasks WHERE category_id = " + categoryId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndex("id")));
                task.setTaskName(cursor.getString(cursor.getColumnIndex("task_name")));
                task.setCategoryId(cursor.getInt(cursor.getColumnIndex("category_id")));
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    // Получение задачи по ее ID
    @SuppressLint("Range")
    public Task getTask(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("tasks", null, "id = ?",
                new String[]{String.valueOf(taskId)}, null, null, null);

        Task task = null;
        if (cursor != null) {
            cursor.moveToFirst();
            task = new Task();
            task.setId(cursor.getInt(cursor.getColumnIndex("id")));
            task.setTaskName(cursor.getString(cursor.getColumnIndex("task_name")));
            task.setCategoryId(cursor.getInt(cursor.getColumnIndex("category_id")));
            cursor.close();
        }

        db.close();
        return task;
    }

    // Обновление задачи
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("task_name", task.getTaskName());
        values.put("category_id", task.getCategoryId());
        int rowsAffected = db.update("tasks", values, "id = ?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return rowsAffected;
    }

    // Удаление задачи
    public void deleteTask(int taskId) {
        Task task = getTask(taskId);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tasks", "id = ?", new String[]{String.valueOf(taskId)});
        // Обновление количества задач в категории
        updateTaskCount(task.getCategoryId());
        db.close();
    }

    // Обновление количества задач в категории
    private void updateTaskCount(int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(*) FROM tasks WHERE category_id = " + categoryId;
        Cursor cursor = db.rawQuery(query, null);
        int taskCount = 0;
        if (cursor.moveToFirst()) {
            taskCount = cursor.getInt(0);
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put("task_count", taskCount);
        db.update("categories", values, "id = ?", new String[]{String.valueOf(categoryId)});
        db.close();
    }
}
