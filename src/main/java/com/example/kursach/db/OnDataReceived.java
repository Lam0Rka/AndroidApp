package com.example.kursach.db;

import com.example.kursach.adapter.ListItem;

import java.util.List;

public interface OnDataReceived {
    void onReceived(List<ListItem> list);
}
