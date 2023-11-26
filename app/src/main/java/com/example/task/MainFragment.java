package com.example.task;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainFragment extends Fragment {

    private ListView categoryListView;
    private Button addCategoryButton;
    private DatabaseHelper dbHelper;

    private static final int ADD_CATEGORY_REQUEST_CODE = 1;
    private static final int ADD_TASK_REQUEST_CODE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        categoryListView = view.findViewById(R.id.categoryListView);
        addCategoryButton = view.findViewById(R.id.addCategoryButton);
        dbHelper = new DatabaseHelper(requireContext());
        dbHelper.getDataFromServer();

        // Загрузка категорий из базы данных и отображение в списке
        dbHelper.setUpdateTaskCallback(() -> getActivity().runOnUiThread(() -> {
            loadCategories();
        }));

        // Обработчик клика по категории
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранную категорию
                TaskCategory selectedCategory = (TaskCategory) parent.getItemAtPosition(position);
                // Заменяем bottom fragment на TaskListFragment
                openTaskListFragment(selectedCategory.getId());
            }
        });


        // Обработчик клика по кнопке добавления категории
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory(v);
            }
        });

        return view;
    }

    // Отображает список категорий и управляет их добавлением и выбором
    void loadCategories() {
        List<TaskCategory> categories = dbHelper.getAllCategories();

        // Создание массива строк для отображения в списке
        String[] categoryNames = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            categoryNames[i] = categories.get(i).getCategoryName() +
                    " (" + categories.get(i).getTaskCount() + ")";
        }

        // Использование ArrayAdapter с переопределением toString()
        ArrayAdapter<TaskCategory> adapter = new ArrayAdapter<TaskCategory>(requireContext(),
                android.R.layout.simple_list_item_1, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                text1.setText(categoryNames[position]);
                return view;
            }
        };
        categoryListView.setAdapter(adapter);
    }

    //  Загружает категории из базы данных и отображает их в списке
    private void openTaskListFragment(int categoryId) {
        TaskListFragment taskListFragment = new TaskListFragment();

        // Передает идентификатор категории в TaskListFragment
        Bundle args = new Bundle();
        args.putInt("categoryId", categoryId);
        taskListFragment.setArguments(args);

        // Заменяет нижний фрагмент на TaskListFragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.bottomFragment, taskListFragment); // Заменяет 'bottomFragment' на ваш идентификатор контейнера фрагмента
        transaction.addToBackStack(null); // Необязательно: Добавьте транзакцию в обратный стек
        transaction.commit();
    }


    private void addCategory(View view) {
        Intent intent = new Intent(requireContext(), AddCategoryActivity.class);
        startActivityForResult(intent, ADD_CATEGORY_REQUEST_CODE);
    }

    // Метод, вызываемый после завершения активити добавления категории
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ADD_CATEGORY_REQUEST_CODE || requestCode == ADD_TASK_REQUEST_CODE) && resultCode == AppCompatActivity.RESULT_OK) {
            // Обновляет списки категорий и задач после добавления новой категории или задачи
            // Ищет MainFragment
            MainFragment mainFragment = (MainFragment) getParentFragmentManager().findFragmentById(R.id.topFragment);

            // Перезагружает категории в главном MainFragment
            if (mainFragment != null) {
                mainFragment.loadCategories();
            }

            // Ищет TaskListFragment
            TaskListFragment taskListFragment = (TaskListFragment) getParentFragmentManager().findFragmentById(R.id.bottomFragment);

            // Перезагружает задачи в TaskListFragment
            if (taskListFragment != null) {
                taskListFragment.loadTasks();
            }
        }
    }
}
