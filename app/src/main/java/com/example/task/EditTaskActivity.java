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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        taskNameEditText = findViewById(R.id.taskNameEditText);
        dbHelper = new DatabaseHelper(this);

        // Получение categoryId и taskId из интента
        categoryId = getIntent().getIntExtra("categoryId", -1);
        taskId = getIntent().getIntExtra("taskId", -1);

        // Если taskId != -1, значит, редактируем существующую задачу
        if (taskId != -1) {
            Task task = dbHelper.getTask(taskId);
            taskNameEditText.setText(task.getTaskName());
        }
    }

    // Метод для сохранения задачи
    public void saveTask(View view) {
        String taskName = taskNameEditText.getText().toString();

        if (taskId == -1) {
            dbHelper.addTask(new Task(taskName, categoryId));
        } else {
            Task task = new Task(taskName, categoryId);
            task.setId(taskId);
            dbHelper.updateTask(task);
        }

        // Устанавливает результат таким образом, чтобы он указывал на то, что произошло изменение
        setResult(RESULT_OK);
        finish();
    }

    // Метод для удаления задачи
    public void deleteTask(View view) {
        if (taskId != -1) {
            dbHelper.deleteTask(taskId);
        }

        // Завершение активити и возвращение результата в TaskListActivity
        setResult(RESULT_OK);
        finish();
    }
}
