package com.android.nfc.system.hook

import android.annotation.SuppressLint
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.ClassFinder
import com.github.kyuubiran.ezxhelper.finders.FieldFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookLocation {
    @SuppressLint("PrivateApi")
    fun HookLastLocation(lpparam: XC_LoadPackage.LoadPackageParam) {

        XposedBridge.log("HookLastLocation")

        val clazz = lpparam.classLoader.loadClass("com.android.server.location.provider.LocationProviderManager")



        XposedHelpers.findAndHookMethod(clazz, "onReportLocation", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                XposedBridge.log("Hooked onReportLocation")
                hookOnReportLocation(clazz, param)
            }
        })


    }

    private fun hookOnReportLocation(clazz: Class<*>, param: XC_MethodHook.MethodHookParam) {
        XposedBridge.log("Hooked onReportLocation")

    }


}



