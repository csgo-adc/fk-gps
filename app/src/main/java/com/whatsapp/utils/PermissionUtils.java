package com.whatsapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.provider.ProviderProperties;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.whatsapp.R;

import java.util.List;

public class PermissionUtils {


    // WIFI是否可用
    public static boolean isWifiConnected(Context context) {
        // 从 API 29 开始，NetworkInfo 被标记为过时，这里更换新方法
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) {
            return false;
        }
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
    }

    public static boolean isWifiEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    //MOBILE网络是否可用
    public static boolean isMobileConnected(Context context) {
        // 从 API 29 开始，NetworkInfo 被标记为过时，这里更换新方法
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) {
            return false;
        }
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    // 断是否有网络连接，但是如果该连接的网络无法上网，也会返回true
    public static boolean isNetworkConnected(Context context) {
        // 从 API 29 开始，NetworkInfo 被标记为过时，这里更换新方法
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) return false;
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
    }

    //网络是否可用
    public static boolean isNetworkAvailable(Context context) {
        return ((isWifiConnected(context) || isMobileConnected(context)) && isNetworkConnected(context));
    }

    //判断GPS是否打开
    public static boolean isGpsOpened(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // 判断是否已在开发者选项中开启模拟位置权限
    @SuppressLint("wrongconstant")
    public static boolean isAllowMockLocation(Context context) {
        boolean canMockPosition = false;
        int index;

        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);//获得LocationManager引用

            List<String> list = locationManager.getAllProviders();
            for (index = 0; index < list.size(); index++) {
                if (list.get(index).equals(LocationManager.GPS_PROVIDER)) {
                    break;
                }
            }

            if (index < list.size()) {

                try {


                    // 注意，由于 android api 问题，下面的参数会提示错误(以下参数是通过相关API获取的真实GPS参数，不是随便写的)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, true, false,
                                false, true, true, true, ProviderProperties.POWER_USAGE_HIGH, ProviderProperties.ACCURACY_FINE);
                    } else {
                        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, true, false,
                                false, true, true, true, Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                    }
                    canMockPosition = true;
                } catch (Exception e) {
                    locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, true, false,
                                false, true, true, true, ProviderProperties.POWER_USAGE_HIGH, ProviderProperties.ACCURACY_FINE);
                    } else {
                        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, true, false,
                                false, true, true, true, Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                    }
                    canMockPosition = true;
                }

            }

            // 模拟位置可用
            if (canMockPosition) {

                // remove test provider
                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);


            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return canMockPosition;
    }

    //提醒开启位置模拟的弹框
    public static void showEnableMockLocationDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.enable_mock_location)
                .setMessage(R.string.enable_mock_location_message)
                .setPositiveButton(R.string.settings, (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                })
                .show();
    }

    //提醒开启悬浮窗的弹框
    public static void showEnableFloatWindowDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.enable_overlay)
                .setMessage(R.string.enable_overlay_message)
                .setPositiveButton(R.string.settings, (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {

                })
                .show();
    }

    //显示开启GPS的提示
    public static void showEnableGpsDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.enable_location_services)
                .setMessage(R.string.enable_location_services_message)
                .setPositiveButton(R.string.settings, (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {

                })
                .show();
    }
}
