package com.example.task;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BlankFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { //Переопределенный метод onCreateView, который вызывается при создании представления для фрагмента.
        return inflater.inflate(R.layout.fragment_blank, container, false); //Возвращает разметку (layout) фрагмента, заполняя ее данными и возвращая корневой элемент представления
    }
}