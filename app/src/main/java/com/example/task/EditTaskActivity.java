package com.example.task;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditTaskActivity extends AppCompatActivity {

    private EditText taskNameEditText;
    private int categoryId;
    private int taskId;
    private DatabaseHelper dbHelper;

    @Override //Указывает, что следующий метод переопределяет метод из родительского класса.
    protected void onCreate(Bundle savedInstanceState) { //Переопределенный метод onCreate, который вызывается при создании активности.
        super.onCreate(savedInstanceState); //Вызов реализации метода onCreate из родительского класса.
        setContentView(R.layout.activity_edit_task);

        taskNameEditText = findViewById(R.id.taskNameEditText); //Находит и связывает текстовое поле taskNameEditText в макете с переменной taskNameEditText.
        dbHelper = new DatabaseHelper(this); // Создает новый экземпляр DatabaseHelper для взаимодействия с базой данных.

        // Получение данных из интента
        categoryId = getIntent().getIntExtra("categoryId", -1);
        taskId = getIntent().getIntExtra("taskId", -1);

        // Если значение taskId не равно -1, то получаем существующую задачу из базы данных и устанавливаем его название в текстовое поле taskNameEditText
        if (taskId != -1) { // Проверяет, был ли передан дополнительный параметр taskId.
            Task task = dbHelper.getTask(taskId); // Получает задачу из базы данных по заданному taskId.
            taskNameEditText.setText(task.getTaskName()); //Устанавливает текстовое поле taskNameEditText в соответствии с именем полученной задачи.
        }
    }

    // Метод для сохранения задачи
    public void saveTask(View view) {
        String taskName = taskNameEditText.getText().toString(); // Он получает введенное название задачи из текстового поля
        //  В зависимости от значения taskId, добавляет новую задачу в базу данных или обновляет существующую
        if (taskId == -1) { //Проверяет, был ли передан дополнительный параметр taskId.
            dbHelper.addTask(new Task(taskName, categoryId)); //Добавляет новую задачу в базу данных, если taskId равен -1.
        } else {
            Task task = new Task(taskName, categoryId); // Создает новую задачу.
            task.setId(taskId); // Устанавливает идентификатор задачи.
            dbHelper.updateTask(task); //Обновляет существующую задачу в базе данных.
        }

        // Устанавливает результат таким образом, чтобы он указывал на то, что произошло изменение
        setResult(RESULT_OK); // Устанавливает код результата для возврата в вызывающую активность.
        finish(); //Завершает текущую активность.
    }

    // Метод для удаления задачи
    public void deleteTask(View view) {
        if (taskId != -1) {
            dbHelper.deleteTask(taskId); //Удаляет задачу из базы данных.
        }
        // Завершение активити и возвращение результата в TaskListActivity
        setResult(RESULT_OK);
        finish();
    }
}
