package com.android.nfc.system.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.nfc.system.R;
import com.android.nfc.system.adapter.CoordinateAdapter;
import com.android.nfc.system.listener.OnItemClickListener;
import com.android.nfc.system.database.DbManager;
import com.android.nfc.system.database.LocationDao;
import com.android.nfc.system.database.LocationData;
import com.android.nfc.system.databinding.ActivityMainBinding;
import com.android.nfc.system.utils.PermissionUtils;
import com.android.nfc.system.utils.Util;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {

    public static final String LAT_VALUE = "LAT_VALUE";
    public static final String LON_VALUE = "LON_VALUE";
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
        mRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LocationData ld = data.get(position);
                mLat = ld.getLatitude();
                mLon = ld.getLongitude();
                latEdit.setText(String.valueOf(mLat));
                lonEdit.setText(String.valueOf(mLon));

            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemLongClick(View view, int position) {
                showDeleteDialog(MainActivity.this, data, position);
            }
        });

    }

    @SuppressLint({"NotifyDataSetChanged", "ResourceType"})
    private void showDeleteDialog(Context context, ArrayList<LocationData> data, int position) {
        final EditText input = new EditText(context);
        input.setHint(getResources().getString(R.string.rename));
        input.setText(data.get(position).getPositionName());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setView(input)
                .setTitle("请选择")
                .setPositiveButton("删除数据", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            LocationData ld = data.get(position);
                            LocDao.deleteLocation(ld);
                            data.remove(position);
                            mAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("修改地点名称", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocationData ld = data.get(position);
                        ld.setPositionName(input.getText().toString());
                        LocDao.updateLocation(ld);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    public class ClickListener {
        public void onMapClick() {
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
        }

        public void onTeleportClick() {

            if (!checkEditTvEmpty()) {
                Util.DisplayToast(MainActivity.this, "请输入经纬度");
                return;
            }
            mLon = Double.valueOf(lonEdit.getText().toString());

            mLat = Double.valueOf(latEdit.getText().toString());

            if (!PermissionUtils.isNetworkConnected(MainActivity.this)) {
                Util.DisplayToast(getApplicationContext(), "no network");
            }

            if (!PermissionUtils.isGpsOpened(MainActivity.this)) {
                Util.DisplayToast(getApplicationContext(), "no GPS");
            }

            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putDouble(LON_VALUE, mLon);
            bundle.putDouble(LAT_VALUE, mLat);
            intent.putExtra("Loc", bundle);
            startActivity(intent);


        }

    }

    private boolean checkEditTvEmpty() {
        return !TextUtils.isEmpty(latEdit.getText().toString()) && !TextUtils.isEmpty(lonEdit.getText().toString());
    }


}
