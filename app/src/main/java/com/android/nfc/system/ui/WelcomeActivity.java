package com.android.nfc.system.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.nfc.system.R;
import com.android.nfc.system.databinding.ActivityWelcomeBinding;
import com.android.nfc.system.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WelcomeActivity extends BaseActivity {
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWelcomeBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);

        mCheckBox = dataBinding.checkbox;

        if (getSharedPreferences("checkInfo", 0).getBoolean("cbState", false)) {
            mCheckBox.setChecked(true);
        }

        if (mCheckBox.isChecked()) {

            dataBinding.access.setVisibility(View.GONE);

            new Handler().postDelayed(() -> {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }, 1500);
        }
        dataBinding.setClickListener(new ClickListener());
    }

    public class ClickListener {
        public void onAccessClick() {
            if (mCheckBox.isChecked()) {
                getSharedPreferences("checkInfo", 0).edit().putBoolean("cbState", true).apply();

                requestPermissions();
            } else {
                Util.DisplayToast(WelcomeActivity.this, "请勾选用户使用协议");
            }
        }
    }

    private void requestPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions = new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE
            };
        }

        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            proceedToMain();
        }

    }

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::onPermissionsResult);

    private void onPermissionsResult(Map<String, Boolean> result) {
        boolean allPermissionsGranted = true;
        for (Boolean granted : result.values()) {
            if (!granted) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (allPermissionsGranted) {
            proceedToMain();
        }
    }

    private void proceedToMain() {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}
