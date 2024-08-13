package com.android.bluetooths.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;

public class Util {

    public static  void DisplayToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 100);
        toast.show();
    }

    public static final int REQUEST_FLOAT_CODE = 1001;

    /**
     * 跳转到设置页面申请打开无障碍辅助功能
     */
    private static void accessibilityToSettingPage(Context context) {
        // 开启辅助功能页面
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            e.printStackTrace();
        }
    }

    /**
     * 判断Service是否开启
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (TextUtils.isEmpty(serviceName)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService =
                (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo serviceInfo : runningService) {
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断悬浮窗权限权限
     */
    private static boolean commonROMPermissionCheck(Context context) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Settings.class;
                boolean canDrawOverlays = (boolean) clazz.getDeclaredMethod("canDrawOverlays", Context.class)
                        .invoke(null, context);
                result = canDrawOverlays;
            } catch (Exception e) {
                Log.e("ServiceUtils", Log.getStackTraceString(e));
            }
        }
        return result;
    }

    /**
     * 检查悬浮窗权限是否开启
     */
    public static void checkSuspendedWindowPermission(Activity context) {
        if (commonROMPermissionCheck(context)) {

        } else {
            Toast.makeText(context, "请开启悬浮窗权限", Toast.LENGTH_SHORT).show();
            context.startActivityForResult(
                    new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                            .setData(Uri.parse("package:" + context.getPackageName())),
                    REQUEST_FLOAT_CODE
            );
        }
    }



    public static boolean isNull(Object any) {
        return any == null;
    }
}
