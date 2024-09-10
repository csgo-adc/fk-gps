package com.android.nfc.system.hook;

import static android.provider.Settings.Secure.LOCATION_MODE_SENSORS_ONLY;

import android.app.AndroidAppHelper;
import android.content.ContentResolver;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LocationSpoofingModule implements IXposedHookLoadPackage {

    private double latitude = 37.7749;
    private double longtitude = -122.4194;



    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        XposedBridge.log("这是一个调试信息：模块已加载");
        XposedBridge.log("加载了目标应用: " + lpparam.packageName);

        HookLocation location = new HookLocation();
        location.HookLastLocation(lpparam);




    }
}