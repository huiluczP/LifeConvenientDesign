package com.demo.lifeconvenientdesign.Fragment;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.demo.lifeconvenientdesign.ChangeInfoActivity;
import com.demo.lifeconvenientdesign.JsonOperation.JSONChange;
import com.demo.lifeconvenientdesign.JsonOperation.Result;
import com.demo.lifeconvenientdesign.R;
import com.demo.lifeconvenientdesign.Service.AccountService;
import com.demo.lifeconvenientdesign.Service.NetService;

import org.w3c.dom.Text;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.demo.lifeconvenientdesign.JsonOperation.JSONChange.jsonToObj;

public class DailyInfoFragment extends Fragment {

    //百度sdk获取位置信息
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private String city=null;

    private TextView cityname;
    private TextView weathernow;
    private TextView temperature;
    private ProgressBar process;

    private NetService weatherservice;
    Result result;
    private ImageView background;
    View view;

    //记得调用location的start
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getContext());
        mLocationClient.registerLocationListener(myListener);
        //设置对应配置
        initLocationOperation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //每日信息布局
        view= inflater.inflate(R.layout.dailyinfo_layout, container, false);
        cityname=(TextView)view.findViewById(R.id.cityname);
        weathernow=(TextView)view.findViewById(R.id.weathernow);
        temperature=(TextView)view.findViewById(R.id.temperature);
        background=(ImageView)view.findViewById(R.id.dailyimg);
        //background.setImageResource(R.drawable.eugen01);
        process=(ProgressBar)view.findViewById(R.id.process);
        process.setVisibility(ProgressBar.VISIBLE);

        //service绑定
        final Intent serviceIntent = new Intent(getContext(), NetService.class);
        getActivity().bindService(serviceIntent,mConnection,getContext().BIND_AUTO_CREATE);

        mLocationClient.start();
        if(city!=null){
            cityname.setText(city);
        }
        new Thread(){
            @Override
            public void run() {
                while(true){
                    if(weatherservice!=null)
                        break;
                    try {
                        this.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //每图
                weatherservice.sendRequest("http://guolin.tech/api/bing_pic",new Piccallback());
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                while(true){
                    if(city!=null)
                        break;
                    try {
                        this.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String url1="https://api.seniverse.com/v3/weather/now.json?key=smtq3n0ixdggurox&location=";
                String url2="&language=zh-Hans&unit=c";
                String url=url1+city+url2;
                while(true){
                    if(weatherservice!=null)
                        break;
                    try {
                        this.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                weatherservice.threadon(url,new Weathercallback());
            }
        }.start();
        return view;
    }

    //设置位置获取的配置
    private void initLocationOperation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setScanSpan(0);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.SetIgnoreCacheException(false);
        option.setIgnoreKillProcess(true);
        option.setWifiCacheTimeOut(5*60*1000);
        option.setEnableSimulateGps(false);
        //设置获取地点
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    //监听类，调用location start之后进行的操作
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息

            Log.i("+++location+++",addr+" "+city);
        }
    }

    //网络信息获取回调类
    private class Weathercallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            //Toast.makeText(getContext(), "获取当前天气信息失败"+e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("+++JSON+++","zai?为什么失败");
        }

        //处理JSON数据
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String JSONstr=response.body().string();
            Log.i("+++JSON+++",JSONstr);
            result=(Result)JSONChange.jsonToObj(new Result(),JSONstr);
            //显示信息
            handler.post(new WeatherShowThread());
        }
    }

    //图片设置callback
    private class Piccallback implements Callback{
        @Override
        public void onFailure(Call call, IOException e) {
            Log.i("+++JSON+++","zai?为什么失败");
        }

        //设置背景图
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String uri=response.body().string();
            Log.i("+++uri+++",uri);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    RequestOptions options=new RequestOptions().centerCrop();
                    Glide.with(view).load(uri).apply(options).into(background);
                }
            });
        }
    }

    private Handler handler=new Handler();
    //子线程设置天气信息处理显示
    private class WeatherShowThread extends Thread{
        @Override
        public void run() {
            String weather=null;
            String temper=null;
            if(result!=null){
                if(result.getResults()!=null&&result.getResults().size()>0){
                    weather=result.getResults().get(0).getNow().getText();
                    temper=result.getResults().get(0).getNow().getTemperature();
                }
            }
            cityname.setText(city);
            weathernow.setText(weather);
            temperature.setText(temper+"℃");
            //Toast.makeText(getContext(),"天气信息更新",Toast.LENGTH_SHORT).show();

            //加载完设置进度条不可见
            process.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    //connection类
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            weatherservice = ((NetService.LocalBinder)service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            weatherservice = null;
        }
    };
}
