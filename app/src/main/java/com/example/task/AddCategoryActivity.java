package com.example.task;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText categoryNameEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        categoryNameEditText = findViewById(R.id.categoryNameEditText);
        dbHelper = new DatabaseHelper(this);
    }

    // Метод для сохранения новой категории
    public void saveCategory(View view) {
        String categoryName = categoryNameEditText.getText().toString(); //получает текст из текстового поля categoryNameEditText.

        if (!categoryName.isEmpty()) { // проверяет, не пустое ли значение categoryName
            TaskCategory newCategory = new TaskCategory(categoryName, 0); //создает новый объект TaskCategory с заданным именем и количеством задач
            dbHelper.addCategory(newCategory); //добавляет новую категорию в базу данных

            // Возвращение результата в MainActivity
            setResult(RESULT_OK);
            finish();
        }
    }
}
