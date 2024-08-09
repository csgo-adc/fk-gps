package com.android.bluetooths.ui;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SDKInitializer.setAgreePrivacy(this, true);
        LocationClient.setAgreePrivacy(true);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。

        SDKInitializer.setCoordType(CoordType.BD09LL);


    }


}
