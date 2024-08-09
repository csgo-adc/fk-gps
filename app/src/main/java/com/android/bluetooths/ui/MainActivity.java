package com.android.bluetooths.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetooths.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {


    private RecyclerView mRecycleView;
    private CoordinateAdapter mAdapter;//适配器
    private LinearLayoutManager mLinearLayoutManager;//布局管理器
    private ArrayList<String> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mList = new ArrayList<>();
        initData(mList);

        mRecycleView = findViewById(R.id.recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mRecycleView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CoordinateAdapter(mList);
        mRecycleView.setAdapter(mAdapter);


    }

    public void initData(ArrayList<String> list) {
        for (int i = 1; i <= 40; i++) {
            list.add("第" + i + "条数据");
        }
    }

}
