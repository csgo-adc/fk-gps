package com.android.bluetooths.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetooths.R;
import com.android.bluetooths.database.DbManager;
import com.android.bluetooths.database.LocationDao;
import com.android.bluetooths.database.LocationData;
import com.android.bluetooths.database.RoomDB;
import com.android.bluetooths.utils.Util;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {


    private RecyclerView mRecycleView;
    private CoordinateAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<LocationData> mList;

    private LocationDao LocDao = DbManager.INSTANCE.getDb().LocationDao();;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mList = new ArrayList<>();
        //initData(mList);

        mRecycleView = findViewById(R.id.recycler_view);

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mRecycleView.setLayoutManager(mLinearLayoutManager);
        ArrayList<LocationData> data =  (ArrayList<LocationData>)LocDao.queryAll();
        mAdapter = new CoordinateAdapter(data, LocDao);
        mRecycleView.setAdapter(mAdapter);



    }

    public void initData(ArrayList<LocationData> list) {
        for (int i = 11; i <= 20; i++) {
            String name = "第" + i + "条数据";
            Double la = 11.22222;
            Double lu = 333.4444;

            LocationData ld = new LocationData(name, la, lu);
            LocDao.addLocation(ld);
        }
    }

    private void insert() {
        String name = "啊啊啊";
        Double la = 11.22222;
        Double lu = 333.4444;

        LocationData ld = new LocationData(name, la, lu);

        LocDao.addLocation(ld);
    }

    private void q() {
        LocationData aaa = LocDao.queryAll().get(0);
        String ss = aaa.toString();
        Util.DisplayToast(this, ss);
    }

}
