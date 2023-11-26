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
    private static final int ADD_TASK_REQUEST_CODE = 1;

    public TaskListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        taskListView = view.findViewById(R.id.taskListView);
        dbHelper = new DatabaseHelper(requireContext());

        // Получить categoryid из аргументов
        categoryId = getArguments().getInt("categoryId", -1);

        // Загружает задачи для данной категории
        loadTasks();

        // Обрабатывает щелчок по элементу задачи
        taskListView.setOnItemClickListener((parent, view1, position, id) -> {
            // Обработайте элемент задачи, нажав здесь
            Task clickedTask = dbHelper.getTasksForCategory(categoryId).get(position);
            Intent intent = new Intent(requireContext(), EditTaskActivity.class);
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("taskId", clickedTask.getId());
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
        });
        Button addTaskButton = view.findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditTaskActivity.class);
            intent.putExtra("categoryId", categoryId);
            startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
        });
        return view;
    }

    // Метод открытия EditTaskActivity при добавлении новой задачи
    public void addTask(View view) {
        Intent intent = new Intent(requireContext(), EditTaskActivity.class);
        intent.putExtra("categoryId", categoryId);
        startActivityForResult(intent, ADD_TASK_REQUEST_CODE);
    }

    // Метод, вызываемый после завершения действия по добавлению задачи
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // Обновление списка задач после добавления новой задачи
            loadTasks();

            // Ищет основной фрагмент
            MainFragment mainFragment = (MainFragment) getParentFragmentManager().findFragmentById(R.id.topFragment);

            // Перезагрузка категории в MainFragment
            if (mainFragment != null) {
                mainFragment.loadCategories();
            }
        }
    }

    void loadTasks() {
        // Получает список задач для данной категории из базы данных
        List<Task> tasks = dbHelper.getTasksForCategory(categoryId);

        // Создает массив названий задач для отображения в списке
        String[] taskNames = new String[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            taskNames[i] = tasks.get(i).getTaskName();
        }

        // Использует ArrayAdapter для отображения в ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, taskNames);
        taskListView.setAdapter(adapter);
    }
}