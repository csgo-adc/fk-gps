package com.android.bluetooths.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.bluetooths.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        requestPermissions();
    }

    private void requestPermissions() {
        String[] permissions = new String[] {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
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
        } else {
            // 处理权限未授予的情况
            // 可以在这里显示提示或终止应用
        }
    }

    private void proceedToMain() {
        // 权限授予后停留2秒并跳转到主页面
        new Handler().postDelayed(() -> {
            startActivity(new Intent(WelcomeActivity.this, LocationActivity.class));
            finish();
        }, 1000); // 2秒
    }
}
