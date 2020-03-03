package com.example.manufacture.drawer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.manufacture.R;

import java.util.ArrayList;
import java.util.List;

public class DrawerActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        initList();
        RAdapter adapter = new RAdapter(list);
        RecyclerView recyclerView = findViewById(R.id.drawer_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    private void initList(){
        for (int i = 0;i < 8;i++){
            list.add("超时");
            list.add("开飞机");
            list.add("数据");
        }
    }
}
