package com.demo.lifeconvenientdesign.Service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.net.URI;

import okhttp3.OkHttpClient;
import okhttp3.Request;

//定时(没准)获取对应的网络信息，包括JSON天气信息和对应的每日一图uri，glide设置图片
public class NetService extends Service {

    private final IBinder mBinder=new NetService.LocalBinder();

    //绑定返回service实例
    public class LocalBinder extends Binder {
        public NetService getService(){
            return NetService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this,"netservice ride on",Toast.LENGTH_SHORT).show();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this,"netservice kick ass",Toast.LENGTH_SHORT).show();
        return false;
    }

    public void threadon(final String url,final okhttp3.Callback callback){
        new Thread(){
            @Override
            public void run() {
                sendRequest(url,callback);
            }
        }.start();
    }

    public void sendRequest(String url,okhttp3.Callback callback){
        Log.i("+++getNeturl+++",url);
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(url)
                .build();
        //获取响应信息，回调操作
        client.newCall(request).enqueue(callback);
    }

}
