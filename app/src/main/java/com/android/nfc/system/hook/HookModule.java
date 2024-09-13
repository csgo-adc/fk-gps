package com.android.nfc.system.hook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookModule implements IXposedHookLoadPackage {

    private double latitude = 37.7749;
    private double longtitude = -122.4194;



    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        XposedBridge.log("这是一个调试信息：模块已加载");
        XposedBridge.log("加载了目标应用: " + lpparam.packageName);

        HookLocation location = new HookLocation();
        location.start(lpparam.classLoader);




    }
}