package com.demo.lifeconvenientdesign;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private ImageView imgstart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgstart=(ImageView)findViewById(R.id.startimg);
        Glide.with(this).load(R.drawable.start2).into(imgstart);
        //延时启动
        new waitingthread().start();
    }

    private class waitingthread extends Thread{
        @Override
        public void run() {
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //转换到真的activity
            Intent intent=new Intent(MainActivity.this,MenuActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade,R.anim.fade);
        }
    }
}
