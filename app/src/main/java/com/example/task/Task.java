package com.example.task;
public class Task {
    private int id;
    private String taskName;
    private int categoryId; // Ссылка на категорию

    public Task() {
        // Пустой конструктор
    }

    public Task(String taskName, int categoryId) {
        this.taskName = taskName;
        this.categoryId = categoryId;
    }

    // конструкторы и методы доступа для всех полей

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
