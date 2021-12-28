package com.example.kursach.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kursach.adapter.ListItem;

import java.util.ArrayList;
import java.util.List;

public class MyDbManager {
    private Context context;
    private MyDbHelper myDbHelper;
    private SQLiteDatabase db;

    public MyDbManager(Context context) {
        this.context = context;
        myDbHelper = new MyDbHelper(context);
    }

    public void openDb(){
        db = myDbHelper.getWritableDatabase();
    }
    public void insertToDb(String title, String disc, String uri){
        ContentValues cv = new ContentValues();
        cv.put(MyConstans.TITLE, title);
        cv.put(MyConstans.DISC, disc);
        cv.put(MyConstans.URI, uri);

        db.insert(MyConstans.TABLE_NAME, null, cv);

    }

    public void updateItem(String title, String disc, String uri, int id){
        String selection = MyConstans._ID +"=" + id;
        ContentValues cv = new ContentValues();
        cv.put(MyConstans.TITLE, title);
        cv.put(MyConstans.DISC, disc);
        cv.put(MyConstans.URI, uri);
        db.update(MyConstans.TABLE_NAME,cv,selection,null);
    }

    public void delete(int id){
        String selection = MyConstans._ID +"=" + id;

        db.delete(MyConstans.TABLE_NAME,selection,null);
    }


    public void getFromDb(String searchText, OnDataReceived onDataReceived){
        List<ListItem> tempList = new ArrayList<>();
        String selection = MyConstans.TITLE + " like ?";
        Cursor cursor = db.query(MyConstans.TABLE_NAME, null, selection,
                new String[]{"%"+ searchText + "%"}, null, null, null);
        while(cursor.moveToNext())
        {
            ListItem item = new ListItem();
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MyConstans.TITLE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(MyConstans.DISC));
            String uri = cursor.getString(cursor.getColumnIndexOrThrow(MyConstans.URI));
            int _id = cursor.getInt(cursor.getColumnIndexOrThrow(MyConstans._ID));

            item.setTitle(title);
            item.setDesc(desc);
            item.setUri(uri);
            item.setId(_id);
            tempList.add(item);
        }
        cursor.close();
        onDataReceived.onReceived(tempList);
    }

    public void closeDb(){
        myDbHelper.close();
    }

}
