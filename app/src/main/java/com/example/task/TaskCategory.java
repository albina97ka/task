package com.example.task;

public class TaskCategory {
    private int id;
    private String categoryName;
    private int taskCount;

    public TaskCategory() {
        // Пустой конструктор
    }

    public TaskCategory(String categoryName, int taskCount) {
        // Присваивание значений параметров конструктора переменным объекта класса.
        this.categoryName = categoryName;
        this.taskCount = taskCount;
    }

    // конструкторы и методы доступа для всех полей

    //Объявление метода для возврата значения
    public int getId() {
        return id;
    }

    //Объявление метода для установки значения
    public void setId(int id) {
        this.id = id;//Присваивание переменной id нового значения
    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }
}
