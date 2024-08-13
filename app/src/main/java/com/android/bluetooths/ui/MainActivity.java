package com.android.bluetooths.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetooths.R;
import com.android.bluetooths.SuspendwindowService;
import com.android.bluetooths.database.DbManager;
import com.android.bluetooths.database.LocationDao;
import com.android.bluetooths.database.LocationData;
import com.android.bluetooths.databinding.ActivityMainBinding;
import com.android.bluetooths.service.LocService;
import com.android.bluetooths.utils.PermissionUtils;
import com.android.bluetooths.utils.Util;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {


    private RecyclerView mRecycleView;
    private CoordinateAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private ArrayList<LocationData> mList;

    private LocationDao LocDao = DbManager.INSTANCE.getDb().LocationDao();
    private LocService.ServiceBinder mServiceBinder;
    private ServiceConnection mConnection;


    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityMainBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        dataBinding.setClickListener(new ClickListener());


        mList = new ArrayList<>();
        //initData(mList);

        mRecycleView = dataBinding.recyclerView;

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(mLinearLayoutManager);
        ArrayList<LocationData> data = (ArrayList<LocationData>) LocDao.queryAll();
        mAdapter = new CoordinateAdapter(data, LocDao);
        mRecycleView.setAdapter(mAdapter);


        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (LocService.ServiceBinder)service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };


    }

    public class ClickListener {
        public void onMapClick() {
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
        }

        public void onTeleportClick() {

            if (!PermissionUtils.isNetworkConnected(MainActivity.this)) {
                Util.DisplayToast(getApplicationContext(), "no network");
            }

            if (!PermissionUtils.isGpsOpened(MainActivity.this)) {
                Util.DisplayToast(getApplicationContext(), "no GPS");
            }

            Intent serviceIntent = new Intent(MainActivity.this, LocService.class);

            bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }

        }

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
