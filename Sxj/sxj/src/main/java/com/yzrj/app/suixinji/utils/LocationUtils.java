package com.yzrj.app.suixinji.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class LocationUtils {
    public static String cityName = "";   //城市名
    public static String adminArea = "";   //省名
    private static Geocoder geocoder;  //此对象能通过经纬度来获取相应的城市等信息
    private static LocationManager locationManager;
    private static Context context;
    private static Handler handler;
    //方位改变是触发，进行调用
    private final static LocationListener locationListener = new LocationListener() {
        String tempCityName;

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("ssssss", "onStatusChanged" + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("ssssss", "onProviderEnabled" + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("ssssss", "onProviderDisabled" + provider);
            updateWithNewLocation(null);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e("ssssss", "onLocationChanged");
            updateWithNewLocation(location);
        }
    };

    //通过地理坐标获取城市名 其中CN分别是city和name的首字母缩写
    public static void getCNBylocation(Context mcontext, Handler mhandler) {
        handler = mhandler;
        context = mcontext;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(context, "没有定位权限", Toast.LENGTH_SHORT).show();
            return;
        }
        geocoder = new Geocoder(context);
        //用于获取Location对象，以及其他

        String serviceName = Context.LOCATION_SERVICE;
        //实例化一个LocationManager对象
        locationManager = (LocationManager) context.getSystemService(serviceName);


        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);    //低精度ACCURACY_LOW   高精度：ACCURACY_FINE
        criteria.setAltitudeRequired(false);       //不要求海拔
        criteria.setBearingRequired(false);       //不要求方位
        criteria.setCostAllowed(true);      //不允许产生资费
        criteria.setPowerRequirement(Criteria.POWER_LOW);   //低功耗

        //provider的类型
        String provider;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            provider = locationManager.getBestProvider(criteria, true);
        }
        Log.e("ssssssd", provider);
        Location location = null;

        //获得当前位置  location为空是一直取  从onLocationChanged里面取
        //通过最后一次的地理位置来获取Location对象
//        location = locationManager.getLastKnownLocation(provider);
//       if(location!=null){
//           updateWithNewLocation(location);
//           return;
//       }
        /*
        第二个参数表示更新的周期，单位为毫秒，
        第三个参数的含义表示最小距离间隔，单位是米，设定每30秒进行一次自动定位
        */
        locationManager.requestLocationUpdates(provider, 1000, 0, locationListener);
//        //移除监听器，在只有一个widget的时候，这个还是适用的
//        locationManager.removeUpdates(locationListener);
    }

    //更新location  return cityName
    private static Address updateWithNewLocation(Location location) {
        if (location == null) {
            return null;
        }
        String mcityName = "";
        List<Address> addList = null;

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        try {
            addList = geocoder.getFromLocation(lat, lng, 1);    //解析经纬度
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address address = addList.get(i);
                if (address != null) {
                    if (!TextUtils.isEmpty(address.getLocality())) {
//                        cityName = address.getLocality().substring(0, (address.getLocality().length() - 1));
                        cityName = address.getLocality();
                    }
                    if (!TextUtils.isEmpty(address.getAdminArea())) {
//                        adminArea = address.getAdminArea().substring(0, (address.getAdminArea().length() - 1));
                        adminArea = address.getAdminArea();
                    }
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "没有定位权限", Toast.LENGTH_SHORT).show();
                    }
                    locationManager.removeUpdates(locationListener);

                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);

                }
                Log.e("ssssss", address.toString());
                return address;
            }
        }
        return null;
    }
}
