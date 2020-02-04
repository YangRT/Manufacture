package com.example.manufacture.flowlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.manufacture.R;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class FlowActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<String> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);
        mRecyclerView = findViewById(R.id.recycler_flow);
        mDatas.clear();
        for(int i = 0 ; i < 5;i++){
            mDatas.add("Android Studio");
            mDatas.add("eclipse");
            mDatas.add("Dev C++");
            mDatas.add("Pycharm");
            mDatas.add("IntelliJ IDEA");
        }
        FlexBoxLayoutManagerAdapter adapter = new FlexBoxLayoutManagerAdapter(FlowActivity.this,mDatas);
        //recyclerView 结合 FlexboxLayoutManager 完成流式布局
        mRecyclerView.setLayoutManager(new FlexboxLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }
}
