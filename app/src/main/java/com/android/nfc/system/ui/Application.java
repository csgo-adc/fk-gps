package com.android.nfc.system.ui;

import com.android.nfc.system.R;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.upgrade.bean.UpgradeConfig;
import com.tencent.upgrade.core.DefaultUpgradeStrategyRequestCallback;
import com.tencent.upgrade.core.UpgradeManager;

public class Application extends android.app.Application {

    private static Application instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        SDKInitializer.setAgreePrivacy(this, true);
        LocationClient.setAgreePrivacy(true);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppReportDelay(1000);
        strategy.setEnableCatchAnrTrace(true);
        // “R.string.buggly_id” 是腾讯的buggly ID
        // https://bugly.qq.com
        CrashReport.initCrashReport(getApplicationContext(), getResources().getString(R.string.buggly_id), true, strategy);

    }

    public static Application getInstance() {
        return instance;
    }

}
