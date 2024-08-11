package com.android.bluetooths.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.bluetooths.R;
import com.android.bluetooths.utils.Util;
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
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment {


    private static String ARG_PARAM = "param_key";

    private String mParam;
    private Activity mActivity;

    private MapView mMapView;
    private TextView mTextView;
    private BaiduMap mBaiduMap;

    private float mCurrentAccracy;

    private AoiSearch mSearch;

    private LocationClient mLocClient;
    private SuggestionSearch mSuggestionSearch = null;
    private MyLocationData myLocationData;

    public static String mCurrentCity = null;

    private double mCurrentLat = 0.0;       // 当前位置的百度纬度
    private double mCurrentLon = 0.0;       // 当前位置的百度经度
    private float mCurrentDirection = 0.0f;
    private boolean isFirstLoc = true; // 是否首次定位
    private static LatLng mMarkLatLngMap = new LatLng(16.547743718042415, 117.07018449827267); // 当前标记的地图点


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = view.findViewById(R.id.bmapView);
        mTextView = view.findViewById(R.id.wacc);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initMap();
        initMapLocation();

        initSearch();
        Bundle bundle = getArguments();
        if (bundle != null) {
            double latitude = Double.parseDouble(bundle.getString("latitude"));
            double longitude = Double.parseDouble(bundle.getString("longitude"));

            LatLng latLng = new LatLng(latitude, longitude);
            doSearch(latLng);

        }

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
            mBaiduMap.setMyLocationEnabled(true);
            BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_blue);
            //导航箭头
            BitmapDescriptor arrow = BitmapDescriptorFactory.fromResource(R.drawable.icon_arrow);
            final MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration.Builder(MyLocationConfiguration.LocationMode.NORMAL, true)
                    .setArrow(arrow).setArrowSize(0.5f)
                    .setCustomMarker(customMarker).setMarkerSize(0.2f)
                    .setAnimation(true).setMarkerRotation(false)
                    .build();

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
                    mCurrentAccracy = location.getRadius();
                    myLocationData = new MyLocationData.Builder()
                            .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                            .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .build();
                    mBaiduMap.setMyLocationData(myLocationData);
                    if (isFirstLoc) {
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
            Log.e("main", e.toString());
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
                    polygonOptions.stroke(new Stroke(5, Color.argb(255, 0, 150, 150)));// 设置多边形边框信息
                    polygonOptions.fillColor(Color.argb(100, 110, 160, 0));// 设置多边形填充颜色
                    mBaiduMap.addOverlay(polygonOptions);

                    LatLngBounds latLngBounds = mBaiduMap.getOverlayLatLngBounds(polygonOptions);
                    if (latLngBounds != null) {
                        boundsBuilder.include(latLngBounds.northeast).include(latLngBounds.southwest);
                    }

                    Log.e("TAG ", "onGetAoiResult: " + aoiInfo.getNearestDistance());
                    Log.e("TAG ", "onGetAoiResult: " + polygon);
                }
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100, 100, 100, 100));
            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng ptCenter = new LatLng(25.070754, 116.324175);

                doSearch(ptCenter);

            }
        });

    }

    private void doSearch(LatLng latLng) {
        AoiSearchOption aoiSearchOption = new AoiSearchOption();
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(latLng);
        aoiSearchOption.setLatLngList(latLngs);

        mSearch.requestAoi(aoiSearchOption);
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
        //LatLng point = new LatLng(39.963175, 116.400244);
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
}
