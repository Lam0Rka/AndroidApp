package com.example.kursach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import com.example.kursach.adapter.ListItem;
import com.example.kursach.adapter.MainAdapter;
import com.example.kursach.db.AppExecuter;
import com.example.kursach.db.MyDbManager;
import com.example.kursach.db.OnDataReceived;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDataReceived {

    private MyDbManager MyDbManager;
    private EditText edTitle, edDisc;
    private RecyclerView rcView;
    private MainAdapter mainAdapter;


    //Инициализируем
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.id_search);
        SearchView sv = (SearchView) item.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                readFromDb(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {

        MyDbManager = new MyDbManager(this);
        edTitle = findViewById(R.id.edTitle);
        edDisc = findViewById(R.id.edDesc);
        rcView = findViewById(R.id.rcView);
        mainAdapter = new MainAdapter(this);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        getItemTouchHelper().attachToRecyclerView(rcView);
        rcView.setAdapter(mainAdapter);
    }
    // Считываем с базы данных

    @Override
    protected void onResume() {

        super.onResume();
        MyDbManager.openDb();
        readFromDb("");

    }

    //Записываем и тут же считываем
    public void OnClickAdd(View view) {

        Intent i = new Intent(MainActivity.this, EditActivity.class);
        startActivity(i);

    }

    //Закрываем
    @Override
    protected void onDestroy() {

        super.onDestroy();
        MyDbManager.closeDb();
    }

    private ItemTouchHelper getItemTouchHelper(){
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mainAdapter.removwItem(viewHolder.getAdapterPosition(), MyDbManager);
            }
        });
    }

    private void readFromDb(final String text){
        AppExecuter.getInstance().getSubIO().execute(new Runnable() {
            @Override
            public void run() {
                MyDbManager.getFromDb(text, MainActivity.this);
            }
        });

    }
    @Override
    public void onReceived(List<ListItem> list) {
        AppExecuter.getInstance().getMainIO().execute(new Runnable() {
            @Override
            public void run() {
                mainAdapter.updateAdapter(list);
            }
        });
    }
}