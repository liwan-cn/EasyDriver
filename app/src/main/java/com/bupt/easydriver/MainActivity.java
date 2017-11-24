package com.bupt.easydriver;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.HeatMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.WeightedLatLng;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.platform.comapi.map.B;
import com.bupt.easydriver.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Button home = null;
    private Button traffic = null;
    private Button sat_nor = null;

    public LocationClient mLocationClient;

    public boolean isMovetoLocation = true;
    private boolean isHeat = false;
    private boolean isMove = false;
    //private boolean isEndPlan = false;

    private DrivingRouteResult mRouteResult = null;
    private DrivingRouteLine mRouteLine = null;
    private List<DrivingRouteLine.DrivingStep> mDrivingStep = null;
    //private List<LatLng> mPointList = null;
    private Iterator<LatLng> it = null;
    private Marker lastMarker = null;
    private Marker startMarker = null;
    private Marker endMarker = null;
    private HeatMap mHeatMap = null;
    private DrivingRouteOverlay mDrivingRouteOverlay = null;
    private Polyline mPolyline = null;
    private List<BitmapDescriptor> mBitMapList = null;

    public LatLng nowLocaltion = null;
    public String ip = "10.128.250.67";
    public String port = "8080";
    public String address = null;
    public int[] color = null;
    public int[] jamStatusColor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.GCJ02);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();//隐藏标题栏
        }
        setContentView(R.layout.activity_main);
        initViews();
    }
    private void initViews(){
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        //json_test = (TextView) findViewById(R.id.json_test);
        home = (Button) findViewById(R.id.home_button);
        traffic = (Button) findViewById(R.id.traffic_button);
        sat_nor = (Button) findViewById(R.id.nor_sat_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mBitMapList = new ArrayList<>();
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark1));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark2));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark3));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark4));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark5));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark6));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark7));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark8));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark9));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_mark10));
        mBitMapList.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding));

        color = new int[10];
        color[0] = 0xFFFFD700;
        color[1] = 0xFFFF4500;
        color[2] = 0xFFDC143C;
        color[3] = 0xFFADFF2F;
        color[4] = 0xFF87CEFA;
        color[5] = 0xFF7FFF00;
        color[6] = 0xFF1E90FF;
        color[7] = 0xFF00FF7F;
        color[8] = 0xFF0000CD;
        color[9] = 0xFF99cc33;

        jamStatusColor = new int[7];
        jamStatusColor[0] = 0x00000000;
        jamStatusColor[1] = 0xFF00FF00;
        jamStatusColor[2] = 0xFF32CD32;
        jamStatusColor[3] = 0xFFADFF2F;
        jamStatusColor[4] = 0xFFFFA500;
        jamStatusColor[5] = 0xFFFF4500;
        jamStatusColor[6] = 0xFFFF0000;


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        sat_nor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL){
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    sat_nor.setBackgroundResource(R.drawable.sat_o);
                    Toast.makeText(getApplicationContext(), "显示卫星地图",Toast.LENGTH_SHORT ).show();
                }
                else{
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    sat_nor.setBackgroundResource(R.drawable.nor_o);
                    Toast.makeText(getApplicationContext(), "关闭卫星地图",Toast.LENGTH_SHORT ).show();
                }
            }
        });
        traffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBaiduMap.isTrafficEnabled() == true){
                    mBaiduMap.setTrafficEnabled(false);
                    traffic.setBackgroundResource(R.drawable.ic_traffic_no);
                    Toast.makeText(getApplicationContext(), "关闭实时路况",Toast.LENGTH_SHORT ).show();
                }
                else{
                    mBaiduMap.setTrafficEnabled(true);
                    traffic.setBackgroundResource(R.drawable.ic_traffic_yes);
                    Toast.makeText(getApplicationContext(), "开启实时路况",Toast.LENGTH_SHORT ).show();
                }
            }
        });
        requestLocation();
    }
    private void requestLocation(){
        initLocation();
        mLocationClient.start();//主机位置监听运行
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        //option.setCoorType("bd09ll");//("gcj02");//("bd09ll");
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    public void navigateTo(LatLng latLng, boolean isShowLocation){
        if (isMovetoLocation){
            LatLng ll = new LatLng(latLng.latitude,latLng.longitude);
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            mBaiduMap.animateMapStatus(update);
            isMovetoLocation = false;
        }
        if(isShowLocation){
            MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
            locationBuilder.latitude(latLng.latitude);
            locationBuilder.longitude(latLng.longitude);
            MyLocationData locationData = locationBuilder.build();
            mBaiduMap.setMyLocationData(locationData);
        }
    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    ||location.getLocType() == BDLocation.TypeNetWorkLocation){
                nowLocaltion = new LatLng(location.getLatitude(),location.getLongitude());
                navigateTo(new LatLng(location.getLatitude(),location.getLongitude()),true);
                //Toast.makeText(HelloWorldActivity.this,location.getLongitude()+"+"+location.getLatitude(),Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
    public void jamAnalyze(LatLng localtion){
        Log.e("liwan",address);
        isMove = false;
        if(mHeatMap != null) mHeatMap.removeHeatMap();
        mBaiduMap.clear();
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(localtion);
        mBaiduMap.animateMapStatus(update);
        Log.e("liwan",address);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseText);
                    JSONArray jsonArray = jsonObject.getJSONArray("datas");
                    Log.e("liwan",responseText);
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int status = obj.getInt("status");
                        JSONArray each = obj.getJSONArray("data");
                        for(int j = 0; j < each.length(); j++){
                            JSONArray item = each.getJSONArray(j);
                            LatLng temp = new LatLng(item.getDouble(1),item.getDouble(0));
                            OverlayOptions dot = new DotOptions().center(temp).color(jamStatusColor[status]).radius(10);
                            mBaiduMap.addOverlay(dot);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void cluster(LatLng localtion){
        Log.e("liwan",address);
        isMove = false;
        mBaiduMap.clear();
        if(mHeatMap != null) mHeatMap.removeHeatMap();
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(localtion);
        mBaiduMap.animateMapStatus(update);
        //Log.e("liwan",""+localtion.latitude+" "+localtion.longitude);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseText);
                    JSONArray jsonArray = jsonObject.getJSONArray("points");
                    Log.e("liwan",""+jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONArray each = jsonArray.getJSONArray(i);
                        Log.e("liwan",""+each.length());
                        for(int j = 0; j < each.length(); j++){
                            JSONObject item = each.getJSONObject(j);
                            LatLng temp = new LatLng(item.getDouble("lat"),item.getDouble("lng"));
                            OverlayOptions dot = new DotOptions().center(temp).color(color[i%10]).radius(10);
                            mBaiduMap.addOverlay(dot);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void recommend(LatLng localtion) {
        Log.e("liwan",address);
        isMove = false;
        mBaiduMap.clear();
        if(mHeatMap != null) mHeatMap.removeHeatMap();
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(localtion);
        mBaiduMap.animateMapStatus(update);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
//                Pattern p = Pattern.compile("\\{[\\s\\S]*\\}");
//                Matcher matcher = p.matcher(responseText);
                RoutePlanSearch routePlanSearch = RoutePlanSearch.newInstance();
                DrivingRoutePlanOption drivingOption = new DrivingRoutePlanOption();
                List<PlanNode> nodes = new ArrayList< >();
                try {
                    JSONObject jsonObject = new JSONObject(responseText);
                    JSONArray jsonArray = jsonObject.getJSONArray("points");
                    if(jsonArray.length() > 1) {
                        JSONObject obj = jsonArray.getJSONObject(0);
                        PlanNode from = PlanNode.withLocation(new LatLng(obj.getDouble("lat"), obj.getDouble("lng")));//创建起点
                        drivingOption.from(from);//设置起点
                        obj = jsonArray.getJSONObject(jsonArray.length() - 1);
                        PlanNode to = PlanNode.withLocation(new LatLng(obj.getDouble("lat"), obj.getDouble("lng")));//创建终点
                        drivingOption.to(to);//设置终点

                        for (int i = 1; i < jsonArray.length(); i++) {
                            obj = jsonArray.getJSONObject(i);
                            LatLng point = new LatLng(obj.getDouble("lat"), obj.getDouble("lng"));
                            if (i != jsonArray.length() - 1)
                                nodes.add(PlanNode.withLocation(point));
                            OverlayOptions option = new MarkerOptions()
                                    .position(point)
                                    .icon(mBitMapList.get(i <= 10 ? i - 1 : 10));
                            //在地图上添加Marker，并显示
                            mBaiduMap.addOverlay(option);
                        }
                        drivingOption.passBy(nodes);
                        drivingOption.policy(DrivingRoutePlanOption.DrivingPolicy.ECAR_DIS_FIRST);//设置策略，驾乘检索策略常量：最短距离
                        routePlanSearch.drivingSearch(drivingOption);
                    }
                    else{
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "未搜索到合适路线",Toast.LENGTH_SHORT ).show();
                        Looper.loop();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
                    @Override
                    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

                    }
                    @Override
                    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

                    }
                    @Override
                    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

                    }
                    @Override
                    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                        if (drivingRouteResult == null
                                || SearchResult.ERRORNO.RESULT_NOT_FOUND == drivingRouteResult.error) {
                            Toast.makeText(getApplicationContext(), "未搜索到结果",Toast.LENGTH_SHORT ).show();
                            return;
                        }
                        mRouteResult = drivingRouteResult;
                        mDrivingRouteOverlay = new DrivingRouteOverlay(mBaiduMap);
                        mBaiduMap.setOnMarkerClickListener(mDrivingRouteOverlay);
                        mDrivingRouteOverlay.setData(drivingRouteResult.getRouteLines().get(0));//设置线路为搜索结果的第一条
                        mDrivingRouteOverlay.addToMap();
                        mDrivingRouteOverlay.zoomToSpan();
                        //isEndPlan = true;
                        isMove = true;
                        mRouteLine = mRouteResult.getRouteLines().get(0);
                        mDrivingStep = mRouteLine.getAllStep();
                        List<LatLng> mPointList = new ArrayList<>();//把途径点置为空
                        for(DrivingRouteLine.DrivingStep ds:mDrivingStep){
                            mPointList.addAll(ds.getWayPoints());
                        }
                        it = mPointList.iterator();
                        handler.sendEmptyMessageDelayed(0, 500);
                    }
                    @Override
                    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

                    }

                    @Override
                    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

                    }
                });
            }
        });

    }
    public void showHeatMap(LatLng localtion){
        Log.e("liwan",address);
        isMove = false;
        if(mHeatMap != null) mHeatMap.removeHeatMap();
        mBaiduMap.clear();
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(localtion);
        mBaiduMap.animateMapStatus(update);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                List<WeightedLatLng> HeatData = new ArrayList<>();
                Log.e("liwan",""+responseText.length());
                try {
                    JSONObject jsonObject = new JSONObject(responseText);
                    JSONArray jsonArray = jsonObject.getJSONArray("points");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        WeightedLatLng data = new WeightedLatLng(new LatLng(obj.getDouble("lat"),obj.getDouble("lng")),obj.getDouble("count"));
                        HeatData.add(data);
                        //Log.e("liwan",""+ i +"-"+obj.getDouble("lng") +"-"+ obj.getDouble("lat") +"-"+ obj.getDouble("count")
                        //+ HeatData.size());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(HeatData.size() > 0){
                    HeatMap.Builder heatMapBuild = new HeatMap.Builder().weightedData(HeatData);
                    mHeatMap = heatMapBuild.build();
                    mBaiduMap.addHeatMap(mHeatMap);
                }
                else{
                    Looper.prepare();
                    Toast.makeText(MainActivity.this,"该地址附近信息未收录",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }
    public void trailAnalyze(LatLng localtion){
        Log.e("liwan",address);
        isMove = false;
        mBaiduMap.clear();
        if(mHeatMap != null) mHeatMap.removeHeatMap();
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(localtion);
        mBaiduMap.animateMapStatus(update);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseText);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONArray array = jsonArray.getJSONArray(i);
                        OverlayOptions dot = new DotOptions().center(new LatLng(array.getDouble(1),array.getDouble(0))).color(jamStatusColor[4]).radius(10);
                        mBaiduMap.addOverlay(dot);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public void showTrail(){
        Log.e("liwan",address);
        isMove = false;
        mBaiduMap.clear();
        if(mHeatMap != null) mHeatMap.removeHeatMap();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                List<LatLng> points = new ArrayList<>();

                try {
                    JSONObject jsonObject = new JSONObject(responseText);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONArray array = jsonArray.getJSONArray(i);
                        points.add(new LatLng(array.getDouble(1),array.getDouble(0)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(points.size() > 0){
                    //起点
                    BitmapDescriptor bitmap_s = BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
                    OverlayOptions option_s = new MarkerOptions()
                            .position(points.get(0))
                            .icon(bitmap_s)
                            .anchor(0.5f,0.1f);
                    startMarker = (Marker) mBaiduMap.addOverlay(option_s);
                    BitmapDescriptor bitmap_e = BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
                    OverlayOptions option_e = new MarkerOptions()
                            .position(points.get(points.size()-1))
                            .icon(bitmap_e)
                            .anchor(0.5f,0.1f);
                    endMarker = (Marker) mBaiduMap.addOverlay(option_e);
                    //BitmapDescriptor custom = BitmapDescriptorFactory.fromResource(R.drawable.icon_road_blue_arrow);
                    OverlayOptions polyline = new PolylineOptions().color(Color.BLUE).width(10).points(points);
                    mPolyline = (Polyline) mBaiduMap.addOverlay(polyline);

                    MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(points.get(0));
                    mBaiduMap.animateMapStatus(update);
                    isMove = true;
                    it = points.iterator();
                    handler.sendEmptyMessageDelayed(0, 500);
                }
                else {

                    Looper.prepare();
                    Toast.makeText(MainActivity.this,"该车信息未收录",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        });
    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    // 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
                    handler.removeMessages(0);
                    // app的功能逻辑处理
                    if(it.hasNext()){
                        LatLng point = it.next();
                        if(lastMarker != null){
                            lastMarker.remove();
                        }
                        //构建Marker图标
                        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.car);
                        //构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions option = new MarkerOptions()
                                .position(point)
                                .icon(bitmap)
                                .anchor(0.5f,0.6f);
                        //在地图上添加Marker，并显示
                        lastMarker = (Marker) mBaiduMap.addOverlay(option);
                        if(isMove) {
                            handler.sendEmptyMessageDelayed(0, 100);
                        }
                        else{
                            handler.sendEmptyMessageDelayed(1, 500);
                            if(lastMarker != null){
                                lastMarker.remove();
                            }
                        }
                    }
                    else{
                        handler.sendEmptyMessageDelayed(1, 500);
                        isMove = false;
                    }
                    // 再次发出msg，循环更新
                    break;

                case 1:
                    // 直接移除，定时器停止
                    handler.removeMessages(0);
                    break;
                default:
                    break;
            }
        }
    };
}
