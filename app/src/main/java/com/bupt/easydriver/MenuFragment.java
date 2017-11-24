package com.bupt.easydriver;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {

    private TextView titleText = null;
    private Button exitButton = null;
    private Button heatButton = null;
    private Button carryClusterButton = null;
    private Button recommendButton = null;
    private Button jamButton = null;
    private Button trailButton = null;
    private Button trailanaButton = null;
    private Button settingButton = null;
    private MainActivity activity = null;
    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        exitButton = (Button) view.findViewById(R.id.exit_button);
        heatButton = (Button) view.findViewById(R.id.heatMap_button);
        carryClusterButton = (Button) view.findViewById(R.id.carryCluster_button);
        recommendButton  = (Button) view.findViewById(R.id.recommend_button);
        jamButton = (Button) view.findViewById(R.id.jam_button);
        trailButton = (Button) view.findViewById((R.id.showtrail_button));
        trailanaButton = (Button) view.findViewById(R.id.trailana_button);
        settingButton = (Button) view.findViewById((R.id.setting_button));
        activity = (MainActivity) getActivity();
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new AlertDialog.Builder(activity).setTitle("提示").setMessage("退出应用？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                                activity.drawerLayout.closeDrawers();
                                activity.finish();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });
        carryClusterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View carryClusterSetview = LayoutInflater.from(activity).inflate(R.layout.cluster_set, null);
                final EditText addressClText = (EditText) carryClusterSetview.findViewById(R.id.addressClText);
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle("上车点聚合");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setView(carryClusterSetview);
                builder.setPositiveButton("确定", null)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog=builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String address = addressClText.getText().toString().trim();

                        if (address.equals("")) {
                            if(activity.nowLocaltion != null){
                                activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/allClusterJ?lat="
                                        + activity.nowLocaltion.latitude +"&lng=" + activity.nowLocaltion.longitude;
                                dialog.dismiss();
                                activity.drawerLayout.closeDrawers();
                                activity.cluster(activity.nowLocaltion);
                            }
                        }
                        else{
                            GeoCoder mSearch = GeoCoder.newInstance();
                            OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
                                public void onGetGeoCodeResult(GeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有检索到结果
                                        Toast.makeText(activity,"没有检索到该地址",Toast.LENGTH_SHORT).show();
                                    }
                                    //获取地理编码结果
                                    else{
                                        activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/allClusterJ?lat="
                                                + result.getLocation().latitude +"&lng=" + result.getLocation().longitude;
                                        activity.drawerLayout.closeDrawers();
                                        activity.cluster(result.getLocation());
                                    }
                                }
                                @Override
                                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有找到检索结果
                                    }
                                    //获取反向地理编码结果
                                }
                            };
                            mSearch.setOnGetGeoCodeResultListener(listener);
                            mSearch.geocode(new GeoCodeOption().city("中国").address(address));
                            //mSearch.destroy();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        heatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View heatMapSetview = LayoutInflater.from(activity).inflate(R.layout.heatmap_set, null);
                final EditText addressHeText = (EditText) heatMapSetview.findViewById(R.id.addressHeText);
                final EditText distanceText = (EditText) heatMapSetview.findViewById(R.id.distanceText);
                final TimePicker startTime= (TimePicker) heatMapSetview.findViewById(R.id.htimestart);
                final TimePicker endTime= (TimePicker) heatMapSetview.findViewById(R.id.htimeend);
                startTime.setIs24HourView(true);
                endTime.setIs24HourView(true);
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle("搭载点查询");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setView(heatMapSetview);
                builder.setPositiveButton("确定", null)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog=builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String address = addressHeText.getText().toString().trim();
                        String tmpdistance = distanceText.getText().toString().trim();
                        final String distance;
                        if(tmpdistance.equals("")){
                            distance = "5000";
                        }
                        else{
                            distance = tmpdistance;
                        }
                        if (address.equals("")) {
                            if(activity.nowLocaltion != null){
                                if(startTime.getCurrentMinute() == endTime.getCurrentMinute() && startTime.getCurrentHour() == endTime.getCurrentHour())
                                    activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/heapJ?lat="
                                            + activity.nowLocaltion.latitude +"&lng=" + activity.nowLocaltion.longitude
                                            + "&distance=" + distance;
                                else
                                    activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/heapJ?lat="
                                            + activity.nowLocaltion.latitude +"&lng=" + activity.nowLocaltion.longitude
                                            + "&distance=" + distance
                                            + "&time1=" + startTime.getCurrentHour() + ":"  + startTime.getCurrentMinute()
                                            + "&time2=" + endTime.getCurrentHour() + ":"  + endTime.getCurrentMinute();
//                                activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/heapJ?lat="
//                                        + activity.nowLocaltion.latitude +"&lng=" + activity.nowLocaltion.longitude
//                                        + "&distance=" + distance;
                                dialog.dismiss();
                                activity.showHeatMap(activity.nowLocaltion);
                                activity.drawerLayout.closeDrawers();
                            }
                            else{
                                Toast.makeText(activity,"无法获取当前地址",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            GeoCoder mSearch = GeoCoder.newInstance();
                            OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
                                public void onGetGeoCodeResult(GeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有检索到结果
                                        Toast.makeText(activity,"没有检索到该地址",Toast.LENGTH_SHORT).show();
                                    }
                                    //获取地理编码结果
                                    else{
//                                        activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/heapJ?lat="
//                                                + result.getLocation().latitude +"&lng=" + result.getLocation().longitude
//                                                + "&distance=" + distance;
                                        if(startTime.getCurrentMinute() == endTime.getCurrentMinute() && startTime.getCurrentHour() == endTime.getCurrentHour())
                                            activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/heapJ?lat="
                                                    + result.getLocation().latitude +"&lng=" + result.getLocation().longitude
                                                    + "&distance=" + distance;
                                        else
                                            activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/heapJ?lat="
                                                    + result.getLocation().latitude +"&lng=" + result.getLocation().longitude
                                                    + "&distance=" + distance
                                                    + "&time1=" + startTime.getCurrentHour() + ":"  + startTime.getCurrentMinute()
                                                    + "&time2=" + endTime.getCurrentHour() + ":"  + endTime.getCurrentMinute();
                                        activity.showHeatMap(result.getLocation());
                                        activity.drawerLayout.closeDrawers();
                                    }
                                }
                                @Override
                                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有找到检索结果
                                    }
                                    //获取反向地理编码结果
                                }
                            };
                            mSearch.setOnGetGeoCodeResultListener(listener);
                            mSearch.geocode(new GeoCodeOption().city("中国").address(address));
                            dialog.dismiss();
                            //mSearch.destroy();
                        }
                    }
                });
            }
        });
        recommendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View heatMapSetview = LayoutInflater.from(activity).inflate(R.layout.recommend_set, null);
                final EditText addressReText = (EditText) heatMapSetview.findViewById(R.id.addressReText);
                final EditText countText = (EditText) heatMapSetview.findViewById(R.id.countText);
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle("路线推荐");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setView(heatMapSetview);
                builder.setPositiveButton("确定", null)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog=builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String address = addressReText.getText().toString().trim();
                        String tmpcount = countText.getText().toString().trim();
                        final String count;
                        if(tmpcount.equals("")){
                            count = "10";
                        }
                        else{
                            count = tmpcount;
                        }
                        if (address.equals("")) {
                            if(activity.nowLocaltion != null){
                                activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/recommendRouteJ?lat="
                                        + activity.nowLocaltion.latitude +"&lng=" + activity.nowLocaltion.longitude
                                        + "&k=" + count;
                                dialog.dismiss();
                                activity.recommend(activity.nowLocaltion);
                                activity.drawerLayout.closeDrawers();
                            }
                            else{
                                Toast.makeText(activity,"无法获取当前地址",Toast.LENGTH_SHORT).show();
                            }

                        }
                        else{
                            GeoCoder mSearch = GeoCoder.newInstance();
                            OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
                                public void onGetGeoCodeResult(GeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有检索到结果
                                        Toast.makeText(activity,"没有检索到该地址",Toast.LENGTH_SHORT).show();
                                    }
                                    //获取地理编码结果
                                    else{
                                        activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/recommendRouteJ?lat="
                                                + result.getLocation().latitude +"&lng=" + result.getLocation().longitude
                                                + "&k=" + count;
                                        activity.recommend(result.getLocation());
                                        activity.drawerLayout.closeDrawers();
                                    }
                                }
                                @Override
                                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有找到检索结果
                                    }
                                    //获取反向地理编码结果
                                }
                            };
                            mSearch.setOnGetGeoCodeResultListener(listener);
                            mSearch.geocode(new GeoCodeOption().city("中国").address(address));
                            dialog.dismiss();
                            //mSearch.destroy();
                        }
                    }
                });
            }
        });
        trailanaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View trailAnalyzeSetview = LayoutInflater.from(activity).inflate(R.layout.trail_annlyze, null);
                final EditText addressTrailText = (EditText) trailAnalyzeSetview.findViewById(R.id.addressTrailText);
                final TimePicker startTime= (TimePicker) trailAnalyzeSetview.findViewById(R.id.ttimestart);
                final TimePicker endTime= (TimePicker) trailAnalyzeSetview.findViewById(R.id.ttimeend);
                startTime.setIs24HourView(true);
                endTime.setIs24HourView(true);
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle("高收益司机轨迹分析");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setView(trailAnalyzeSetview);
                builder.setPositiveButton("确定", null)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        });
                final AlertDialog dialog=builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String address = addressTrailText.getText().toString().trim();

                        if (address.equals("")) {
                            if(activity.nowLocaltion != null){
                                if(startTime.getCurrentMinute() == endTime.getCurrentMinute() && startTime.getCurrentHour() == endTime.getCurrentHour())
                                    activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/highIncometrajectoryJ?lat="
                                            + activity.nowLocaltion.latitude +"&lng=" + activity.nowLocaltion.longitude;
                                else
                                    activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/highIncometrajectoryJ?lat="
                                            + activity.nowLocaltion.latitude +"&lng=" + activity.nowLocaltion.longitude
                                            + "&time1=" + startTime.getCurrentHour() + ":"  + startTime.getCurrentMinute()
                                            + "&time2=" + endTime.getCurrentHour() + ":"  + endTime.getCurrentMinute();
                                //activity.address = "http://10.108.219.203:8080/ggg/analyzeJamJ?lat=30.624698&lng=104.0767547&time1=10:30&time2=11:00";
                                dialog.dismiss();
                                activity.drawerLayout.closeDrawers();
                                activity.trailAnalyze(activity.nowLocaltion);
                                //activity.cluster(activity.nowLocaltion);
                            }
                            else{
                                Toast.makeText(activity,"无法获取当前地址",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            GeoCoder mSearch = GeoCoder.newInstance();
                            OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
                                public void onGetGeoCodeResult(GeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有检索到结果
                                        Toast.makeText(activity,"没有检索到该地址",Toast.LENGTH_SHORT).show();
                                    }
                                    //获取地理编码结果
                                    else{
                                        if(startTime.getCurrentMinute() == endTime.getCurrentMinute() && startTime.getCurrentHour() == endTime.getCurrentHour())
                                            activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/highIncometrajectoryJ?lat="
                                                    + result.getLocation().latitude +"&lng=" + result.getLocation().longitude;
                                        else
                                            activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/highIncometrajectoryJ?lat="
                                                    + result.getLocation().latitude +"&lng=" + result.getLocation().longitude
                                                    + "&time1=" + startTime.getCurrentHour() + ":"  + startTime.getCurrentMinute()
                                                    + "&time2=" + endTime.getCurrentHour() + ":"  + endTime.getCurrentMinute();
                                        activity.drawerLayout.closeDrawers();
                                        Log.e("liwan", startTime.getCurrentHour() + ":"  + startTime.getCurrentMinute()
                                                + endTime.getCurrentHour() + ":"  + endTime.getCurrentMinute());
                                        activity.trailAnalyze(result.getLocation());
                                    }
                                }
                                @Override
                                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有找到检索结果
                                    }
                                    //获取反向地理编码结果
                                }
                            };
                            mSearch.setOnGetGeoCodeResultListener(listener);
                            mSearch.geocode(new GeoCodeOption().city("中国").address(address));
                            //mSearch.destroy();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        jamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View jamAnalyzeSetview = LayoutInflater.from(activity).inflate(R.layout.jam_analyze, null);
                final EditText addressJamText = (EditText) jamAnalyzeSetview.findViewById(R.id.addressJamText);
                final TimePicker startTime= (TimePicker) jamAnalyzeSetview.findViewById(R.id.timestart);
                final TimePicker endTime= (TimePicker) jamAnalyzeSetview.findViewById(R.id.timeend);
                startTime.setIs24HourView(true);
                endTime.setIs24HourView(true);
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle("路段拥堵查询");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setView(jamAnalyzeSetview);
                builder.setPositiveButton("确定", null)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }
                        });
                final AlertDialog dialog=builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String address = addressJamText.getText().toString().trim();

                        if (address.equals("")) {
                            if(activity.nowLocaltion != null){
                                if(startTime.getCurrentMinute() == endTime.getCurrentMinute() && startTime.getCurrentHour() == endTime.getCurrentHour())
                                    activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/analyzeJamJ?lat="
                                            + activity.nowLocaltion.latitude +"&lng=" + activity.nowLocaltion.longitude;
                                else
                                    activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/analyzeJamJ?lat="
                                            + activity.nowLocaltion.latitude +"&lng=" + activity.nowLocaltion.longitude
                                            + "&time1=" + startTime.getCurrentHour() + ":"  + startTime.getCurrentMinute()
                                            + "&time2=" + endTime.getCurrentHour() + ":"  + endTime.getCurrentMinute();
                                //activity.address = "http://10.108.219.203:8080/ggg/analyzeJamJ?lat=30.624698&lng=104.0767547&time1=10:30&time2=11:00";
                                dialog.dismiss();
                                activity.drawerLayout.closeDrawers();
                                activity.jamAnalyze(activity.nowLocaltion);
                                //activity.cluster(activity.nowLocaltion);
                            }
                            else{
                                Toast.makeText(activity,"无法获取当前地址",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            GeoCoder mSearch = GeoCoder.newInstance();
                            OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
                                public void onGetGeoCodeResult(GeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有检索到结果
                                        Toast.makeText(activity,"没有检索到该地址",Toast.LENGTH_SHORT).show();
                                    }
                                    //获取地理编码结果
                                    else{
                                        if(startTime.getCurrentMinute() == endTime.getCurrentMinute() && startTime.getCurrentHour() == endTime.getCurrentHour())
                                            activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/analyzeJamJ?lat="
                                                    + result.getLocation().latitude +"&lng=" + result.getLocation().longitude;
                                        else
                                            activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/analyzeJamJ?lat="
                                                    + result.getLocation().latitude +"&lng=" + result.getLocation().longitude
                                                    + "&time1=" + startTime.getCurrentHour() + ":"  + startTime.getCurrentMinute()
                                                    + "&time2=" + endTime.getCurrentHour() + ":"  + endTime.getCurrentMinute();
                                        activity.drawerLayout.closeDrawers();
                                        Log.e("liwan", startTime.getCurrentHour() + ":"  + startTime.getCurrentMinute()
                                                + endTime.getCurrentHour() + ":"  + endTime.getCurrentMinute());
                                        activity.jamAnalyze(result.getLocation());
                                    }
                                }
                                @Override
                                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                                        //没有找到检索结果
                                    }
                                    //获取反向地理编码结果
                                }
                            };
                            mSearch.setOnGetGeoCodeResultListener(listener);
                            mSearch.geocode(new GeoCodeOption().city("中国").address(address));
                            //mSearch.destroy();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        trailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //activity.address = "http://10.210.44.211/gpsData/20140803_train5position.js";
                final View trailQueryview = LayoutInflater.from(activity).inflate(R.layout.trail_query, null);
                final EditText addressTrText = (EditText) trailQueryview.findViewById(R.id.addressTrText);
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle("出租车行驶轨迹");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setView(trailQueryview);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String carId = addressTrText.getText().toString();
                        dialog.dismiss();
                        if(carId.equals("")){
                            carId = "1";
                        }
                        activity.address = "http://" + activity.ip + ":" + activity.port + "/ggg/trajectoryJ?carId=" + carId;
                        activity.showTrail();
                        activity.drawerLayout.closeDrawers();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();

            }
        });
        settingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final View setview = LayoutInflater.from(activity).inflate(R.layout.ipandport, null);
                final EditText ipEditText = (EditText) setview.findViewById(R.id.ipEditText);
                final EditText portEditText = (EditText) setview.findViewById(R.id.portEditText);
                AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                builder.setTitle("请输入");
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setView(setview);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        String ipText = ipEditText.getText().toString().trim();
                        String portText = portEditText.getText().toString().trim();
                        if (!ipText.equals("")) {
                            activity.ip = ipText;
                        }
                        if (!portText.equals("")) {
                            activity.port = portText;
                        }
                        dialog.dismiss();
                        activity.drawerLayout.closeDrawers();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();

            }
        });
    }
}
