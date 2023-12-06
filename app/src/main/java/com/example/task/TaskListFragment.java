package com.example.task;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

public class TaskListFragment extends Fragment {

    private ListView taskListView;
    private int categoryId;
    private DatabaseHelper dbHelper;
    private static final int ADD_TASK_REQUEST_CODE = 1; //объявление приватной статической переменной ADD_TASK_REQUEST_CODE и инициализация её значением 1.

    public TaskListFragment() {} //конструктор по умолчанию для класса TaskListFragment

    @Nullable //может возвращать null
    @Override
    // Метод отвечающий за создание и отображение фрагмента
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Иницилизиция элементов интерфейса
        View view = inflater.inflate(R.layout.fragment_task_list, container, false); //создание view путем загрузки макета fragment_task_list в фрагмент.
        taskListView = view.findViewById(R.id.taskListView); // Нахождение списка задач
        dbHelper = new DatabaseHelper(requireContext()); // Иницилизация объэкта базы данных, используя requireContext для доступа к контексту приложения

        // Получить categoryid из аргументов, переданных во фрагмент
        categoryId = getArguments().getInt("categoryId", -1);

        // Загружает задачи для данной категории
        loadTasks();

        // Обрабатывает щелчок по элементу задачи
        taskListView.setOnItemClickListener((parent, view1, position, id) -> {
            // Обработайте элемент задачи, нажав здесь
            Task clickedTask = dbHelper.getTasksForCategory(categoryId).get(position); // Получение задачи, на которую произведен щелчок в списке для указанной категории
            Intent intent = new Intent(requireContext(), EditTaskActivity.class); // Создание нового объекта Intent для перехода к активности
            intent.putExtra("categoryId", categoryId); // Передается идентификатор категории, связанный с выбранной задачей
            intent.putExtra("taskId", clickedTask.getId()); // Поместить идентификатор выбранной задачи в объект Intent для передачи в активность редактирования
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE); // Запуск активности EditTaskActivity с ожиданием результата, чтобы пользователь мог отредактировать выбранную задачу
        });
        Button addTaskButton = view.findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> { //задает действие при нажатии на кнопку addTaskButton
            Intent intent = new Intent(requireContext(), EditTaskActivity.class); //создает новый объект Intent для перехода к активности EditTaskActivity.
            intent.putExtra("categoryId", categoryId); //добавляет в объект Intent дополнительные данные, в данном случае идентификатор категории.
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE); //запускает активность с ожиданием результата с помощью объекта Intent
        });
        return view;
    }

    // Метод открытия EditTaskActivity при добавлении новой задачи
    public void addTask(View view) {
        Intent intent = new Intent(requireContext(), EditTaskActivity.class); //создает новый объект Intent для перехода к активности EditTaskActivity.
        intent.putExtra("categoryId", categoryId); //добавляет идентификатор категории в объект Intent
        startActivityForResult(intent, ADD_TASK_REQUEST_CODE); //запускает активность с ожиданием результата с помощью объекта Intent
    }

    // Метод, вызываемый после завершения действия по добавлению задачи
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // вызывает аналогичный метод из родительского класса
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // Обновление списка задач после добавления новой задачи
            loadTasks();

            // поиск главного фрагмента по идентификатору
            MainFragment mainFragment = (MainFragment) getParentFragmentManager().findFragmentById(R.id.topFragment);

            // Если не равен null, то вызывается метод loadCategories() у главного фрагмента для перезагрузки категорий
            if (mainFragment != null) {
                mainFragment.loadCategories();
            }
        }
    }

    void loadTasks() {
        // Получает список задач для данной категории из базы данных
        List<Task> tasks = dbHelper.getTasksForCategory(categoryId);

        // Создает массив названий задач для отображения в списке
        String[] taskNames = new String[tasks.size()]; //Создается массив, в котором будут храниться названия задач для отображения в списке
        for (int i = 0; i < tasks.size(); i++) { // Цикл прохода по списку задач для получения их названий
            taskNames[i] = tasks.get(i).getTaskName(); // Присваивание названия задачи в массив taskNames из списка задач tasks
        }

        // Использует ArrayAdapter для отображения в ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, taskNames);// Создается новый ArrayAdapter, связывающий данные с ListView, используя контекст фрагмента, стандартный макет для отображения элементов списка и массив названий задач
        taskListView.setAdapter(adapter); // Устанавливается адаптер для отображения данных в ListView
    }
}