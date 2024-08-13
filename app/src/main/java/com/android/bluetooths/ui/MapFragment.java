package com.android.bluetooths.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.bluetooths.R;
import com.android.bluetooths.database.DbManager;
import com.android.bluetooths.database.LocationDao;
import com.android.bluetooths.database.LocationData;
import com.android.bluetooths.databinding.FragmentMapBinding;
import com.android.bluetooths.service.LocService;
import com.android.bluetooths.utils.MapUtils;
import com.android.bluetooths.utils.PermissionUtils;
import com.android.bluetooths.utils.Util;
import com.android.bluetooths.viewmodel.SearchViewModel;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.EncodePointType;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.aoi.AoiResult;
import com.baidu.mapapi.search.aoi.AoiSearch;
import com.baidu.mapapi.search.aoi.AoiSearchOption;
import com.baidu.mapapi.search.aoi.OnGetAoiSearchResultListener;
import com.baidu.mapapi.search.core.AoiInfo;
import com.baidu.mapapi.search.core.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {


    private Activity mActivity;
    private LocService.ServiceBinder mServiceBinder;
    private ServiceConnection mConnection;

    private SearchViewModel searchViewModel;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private AoiSearch mSearch;
    private LocationClient mLocClient;
    private MyLocationData myLocationData;

    private boolean isNeedLocation = true;
    private boolean isMockServStart = false;
    private String mCurrentCity = null;
    private double mCurrentLat = 0.0;       // 当前位置的百度纬度
    private double mCurrentLon = 0.0;       // 当前位置的百度经度
    private float mCurrentDirection = 0.0f;
    private boolean isFirstLoc = true; // 是否首次定位

    private boolean isMarked = false;
    private static LatLng mMarkLatLngMap = new LatLng(40.070754, 116.324175); // 当前标记的地图点
    private double mAltitude = 250.0; //海拔随便写的

    private LocationDao LocDao = DbManager.INSTANCE.getDb().LocationDao();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("baidu", "onCreate");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("baidu", "onCreateView");

        FragmentMapBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

        mMapView = dataBinding.bmapView;

        dataBinding.setClickListener(new ClickListener());

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (LocService.ServiceBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        return dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e("baidu", "onViewCreated");

        initMap();
        initSearch();

        Bundle bundle = getArguments();
        if (bundle != null) {
            double latitude = bundle.getDouble(MainActivity.LAT_VALUE);
            double longitude = bundle.getDouble(MainActivity.LNG_VALUE);
            LatLng latLng = new LatLng(latitude, longitude);

            isNeedLocation = false;

            doSearch(latLng);

        }
        if (isNeedLocation) {
            initMapLocation();

        }

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (LocService.ServiceBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };


    }

    @Override
    public void onResume() {
        Log.e("baidu", "onResume");

        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        Log.e("baidu", "onPause");

        super.onPause();
        mMapView.onPause();
    }


    @Override
    public void onStart() {
        super.onStart();


        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        mSearch.destroy();
    }

    private void initMap() {

        mBaiduMap = mMapView.getMap();

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                markMap(latLng);
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });
        //指南针
        UiSettings mUiSettings = mBaiduMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);

    }

    private void initMapLocation() {
        try {
            // 定位初始化
            mLocClient = new LocationClient(mActivity);
            BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_blue);
            //导航箭头
            BitmapDescriptor arrow = BitmapDescriptorFactory.fromResource(R.drawable.icon_arrow);
            final MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration.Builder(MyLocationConfiguration.LocationMode.NORMAL, true)
                    .setArrow(arrow).setArrowSize(0.5f)
                    .setCustomMarker(customMarker).setMarkerSize(0.2f)
                    .setAnimation(true).setMarkerRotation(false)
                    .build();

            mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);


            mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mBaiduMap.setMyLocationEnabled(true);
                    mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
                }
            });


            mLocClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    if (location == null || mMapView == null) {
                        return;
                    }
                    mCurrentLat = location.getLatitude();
                    mCurrentLon = location.getLongitude();

                    mCurrentCity = location.getCity();


                    myLocationData = new MyLocationData.Builder()
                            .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                            .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .build();
                    mBaiduMap.setMyLocationData(myLocationData);
                    if (isFirstLoc) {
                        searchViewModel.setCity(mCurrentCity);
                        isFirstLoc = false;
                        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(ll).zoom(18.0f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }
                }

                /**
                 * 错误的状态码
                 * <a><a href="http://lbsyun.baidu.com/index.php?title=android-locsdk/guide/addition-func/error-code">...</a></a>
                 * <p>
                 * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
                 *
                 * @param locType      当前定位类型
                 * @param diagnosticType  诊断类型（1~9）
                 * @param diagnosticMessage 具体的诊断信息释义
                 */
                @Override
                public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
                }
            });
            LocationClientOption locationOption = getLocationClientOption();
            //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
            mLocClient.setLocOption(locationOption);
            //开始定位
            mLocClient.start();
        } catch (Exception e) {
            Log.e("baidu", e.toString());
        }
    }


    private void initSearch() {

        mSearch = AoiSearch.newInstance();
        mSearch.setOnGetAoiSearchResultListener(new OnGetAoiSearchResultListener() {
            @Override
            public void onGetAoiResult(AoiResult aoiResult) {
                mBaiduMap.clear();
                if (aoiResult == null) {
                    return;
                }
                if (aoiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                List<AoiInfo> aoiList = aoiResult.getAoiList();
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                if (null == aoiList) {
                    return;
                }
                for (int i = 0; i < aoiList.size(); i++) {
                    AoiInfo aoiInfo = aoiList.get(i);
                    String polygon = aoiInfo.getPolygon();
                    PolygonOptions polygonOptions = new PolygonOptions();
                    polygonOptions.points(polygon, EncodePointType.AOI);
                    polygonOptions.stroke(new Stroke(5, Color.argb(155, 155, 150, 150)));// 设置多边形边框信息
                    polygonOptions.fillColor(Color.argb(100, 110, 160, 0));// 设置多边形填充颜色
                    mBaiduMap.addOverlay(polygonOptions);

                    LatLngBounds latLngBounds = mBaiduMap.getOverlayLatLngBounds(polygonOptions);
                    if (latLngBounds != null) {
                        boundsBuilder.include(latLngBounds.northeast).include(latLngBounds.southwest);
                    }
                }
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100, 100, 100, 100));
            }
        });


    }

    private void doSearch(LatLng latLng) {
        AoiSearchOption aoiSearchOption = new AoiSearchOption();
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(latLng);
        aoiSearchOption.setLatLngList(latLngs);


        mSearch.requestAoi(aoiSearchOption);

        markMap(latLng);
    }

    private static LocationClientOption getLocationClientOption() {
        LocationClientOption locationOption = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(false);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(true);
        //可选，默认false，设置是否开启Gps定位
        //locationOption.setOpenGps(true);
        locationOption.setOpenGnss(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        return locationOption;
    }


    private void markMap(LatLng latLng) {
        mMarkLatLngMap = latLng;
        isMarked = true;
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_mark);
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .icon(bitmap);
        mBaiduMap.clear();
        mBaiduMap.addOverlay(option);
    }

    private void resetMap() {
        mBaiduMap.clear();
        mMarkLatLngMap = null;
        isMarked = false;
        MyLocationData locData = new MyLocationData.Builder()
                .latitude(mCurrentLat)
                .longitude(mCurrentLon)
                .direction(mCurrentDirection)
                .build();
        mBaiduMap.setMyLocationData(locData);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(new LatLng(mCurrentLat, mCurrentLon)).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    public class ClickListener {
        public void onSendClick() {
            if (!PermissionUtils.isAllowMockLocation(mActivity)) {
                PermissionUtils.showEnableMockLocationDialog(mActivity);
                Util.DisplayToast(mActivity, "模拟位置没打开");
                return;
            }
//            if (!PermissionUtils.isGpsOpened(mActivity)) {
//                Util.DisplayToast(mActivity, "Gps 坏了");
//                return;
//            }

            if (!PermissionUtils.isNetworkAvailable(mActivity)) {
                Util.DisplayToast(mActivity, "network 坏了");
                return;
            }
            if (!Settings.canDrawOverlays(mActivity.getApplicationContext())) {
                PermissionUtils.showEnableFloatWindowDialog(getActivity());
                return;
            }

            startGoLocation();

        }
    }


    private void startGoLocation() {
        if (!isMarked || mMarkLatLngMap == null) {
            Util.DisplayToast(mActivity, "请标记传送地点");
            return;
        }
        LocationData data = new LocationData("地点", mMarkLatLngMap.longitude, mMarkLatLngMap.latitude);
        LocDao.addLocation(data);
        if (isMockServStart) {


            double[] latLng = MapUtils.bd2wgs(mMarkLatLngMap.longitude, mMarkLatLngMap.latitude);

            mServiceBinder.setPosition(latLng[0], latLng[1], mAltitude);
            resetMap();

        } else {

            Intent intent = new Intent(mActivity, LocService.class);
            mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);    // 绑定服务和活动，之后活动就可以去调服务的方法了
            double[] latLng = MapUtils.bd2wgs(mMarkLatLngMap.longitude, mMarkLatLngMap.latitude);
            intent.putExtra(MainActivity.LAT_VALUE, latLng[0]);
            intent.putExtra(MainActivity.LNG_VALUE, latLng[1]);
            intent.putExtra(MainActivity.ALT_VALUE, mAltitude);

            mActivity.startForegroundService(intent);
            isMockServStart = true;
        }
    }

}
