package com.whatsapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whatsapp.R;
import com.whatsapp.adapter.CoordinateAdapter;
import com.whatsapp.listener.OnItemClickListener;
import com.whatsapp.database.DbManager;
import com.whatsapp.database.LocationDao;
import com.whatsapp.database.LocationData;
import com.whatsapp.databinding.ActivityMainBinding;
import com.whatsapp.utils.PermissionUtils;
import com.whatsapp.utils.Util;

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
        mRecycleView = dataBinding.recyclerView;

        initData();

    }

    private void initData() {
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycleView.setLayoutManager(mLinearLayoutManager);
        ArrayList<LocationData> data = new ArrayList<>(LocDao.queryAll());
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
                .setTitle(R.string.location_actions)
                .setPositiveButton(R.string.delete_location, new DialogInterface.OnClickListener() {
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
                .setNegativeButton(R.string.rename_location, new DialogInterface.OnClickListener() {
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

            if (!hasCoordinates()) {
                Util.DisplayToast(MainActivity.this, getString(R.string.coordinates_required));
                return;
            }
            try {
                mLon = Double.parseDouble(lonEdit.getText().toString().trim());
                mLat = Double.parseDouble(latEdit.getText().toString().trim());
            } catch (NumberFormatException exception) {
                Util.DisplayToast(MainActivity.this, getString(R.string.coordinates_invalid));
                return;
            }

            if (!isValidCoordinate(mLat, mLon)) {
                Util.DisplayToast(MainActivity.this, getString(R.string.coordinates_invalid));
                return;
            }

            if (!PermissionUtils.isNetworkConnected(MainActivity.this)) {
                Util.DisplayToast(getApplicationContext(), getString(R.string.network_unavailable));
            }

            if (!PermissionUtils.isGpsOpened(MainActivity.this)) {
                Util.DisplayToast(getApplicationContext(), getString(R.string.gps_unavailable));
            }

            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putDouble(LON_VALUE, mLon);
            bundle.putDouble(LAT_VALUE, mLat);
            intent.putExtra("Loc", bundle);
            startActivity(intent);

        }

    }

    private boolean hasCoordinates() {
        return !TextUtils.isEmpty(latEdit.getText().toString().trim())
                && !TextUtils.isEmpty(lonEdit.getText().toString().trim());
    }

    private boolean isValidCoordinate(double latitude, double longitude) {
        return Double.isFinite(latitude) && Double.isFinite(longitude)
                && latitude >= -90.0 && latitude <= 90.0
                && longitude >= -180.0 && longitude <= 180.0;
    }


}
