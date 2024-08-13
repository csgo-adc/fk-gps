package com.android.bluetooths.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bluetooths.R;
import com.android.bluetooths.adapter.CoordinateAdapter;
import com.android.bluetooths.database.DbManager;
import com.android.bluetooths.database.LocationDao;
import com.android.bluetooths.database.LocationData;
import com.android.bluetooths.databinding.ActivityMainBinding;
import com.android.bluetooths.utils.PermissionUtils;
import com.android.bluetooths.utils.Util;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {

    public static final String LAT_VALUE = "LAT_VALUE";
    public static final String LNG_VALUE = "LON_VALUE";
    public static final String ALT_VALUE = "ALT_VALUE";

    private RecyclerView mRecycleView;
    private CoordinateAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private LocationDao LocDao = DbManager.INSTANCE.getDb().LocationDao();

    private EditText latEdit;
    private EditText lonEdit;

    private double mLat = 0.0;
    private double mLon = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityMainBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        dataBinding.setClickListener(new ClickListener());

        latEdit = dataBinding.latEdit;
        lonEdit = dataBinding.lonEdit;
        //initData(mList);

        mRecycleView = dataBinding.recyclerView;

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(mLinearLayoutManager);
        ArrayList<LocationData> data = (ArrayList<LocationData>) LocDao.queryAll();
        mAdapter = new CoordinateAdapter(data);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LocationData ld = data.get(position);
                mLat = ld.getLatitude();
                mLon = ld.getLongitude();
                latEdit.setText(String.valueOf(mLat));
                lonEdit.setText(String.valueOf(mLon));

            }

            @Override
            public void onItemLongClick(View view, int position) {
                showDeleteDialog(MainActivity.this, data, position);
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void showDeleteDialog(Context context, ArrayList<LocationData> data, int position) {
        new AlertDialog.Builder(context)
                .setTitle("确定要删除本条数据吗")//这里是表头的内容
                .setPositiveButton("确定", (dialog, which) -> {
                    try {
                        LocationData ld = data.get(position);
                        LocDao.deleteLocation(ld);
                        data.remove(position);
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .show();
    }

    public class ClickListener {
        public void onMapClick() {
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
        }

        public void onTeleportClick() {

            if (!checkEditTvEmpty()) {
                Util.DisplayToast(MainActivity.this,"请输入经纬度");
                return;
            }
            mLat = Double.valueOf(latEdit.getText().toString());
            mLon = Double.valueOf(lonEdit.getText().toString());

            if (!PermissionUtils.isNetworkConnected(MainActivity.this)) {
                Util.DisplayToast(getApplicationContext(), "no network");
            }

            if (!PermissionUtils.isGpsOpened(MainActivity.this)) {
                Util.DisplayToast(getApplicationContext(), "no GPS");
            }

            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putDouble(LAT_VALUE, mLat);
            bundle.putDouble(LNG_VALUE, mLon);
            intent.putExtra("Loc", bundle);
            startActivity(intent);


        }

    }

    private boolean checkEditTvEmpty() {
        return !TextUtils.isEmpty(latEdit.getText().toString()) && !TextUtils.isEmpty(lonEdit.getText().toString());
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
