package com.demo.lifeconvenientdesign;

import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.lifeconvenientdesign.Fragment.AccountFragment;
import com.demo.lifeconvenientdesign.Fragment.AccountRootFragment;
import com.demo.lifeconvenientdesign.Fragment.AccountStatisticFragment;
import com.demo.lifeconvenientdesign.Fragment.DailyInfoFragment;
import com.demo.lifeconvenientdesign.Fragment.MyInfoFragment;

//主界面显示活动
public class MenuActivity extends AppCompatActivity implements View.OnClickListener{

    private FrameLayout fragmentview;
    //底部菜单
    private TextView dailyinfo_text;
    private TextView account_text;
    private TextView mineinfo_text;

    //fragment切换
    private FragmentManager fManager;
    private Fragment dailyinfo;
    private Fragment account;
    private Fragment mineinfo;
    private int current;//记录当前选择的fragment，方便后台回来时显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu_layout);

        //初始化控件
        init();
        fManager = getSupportFragmentManager();;
        mineinfo_text.performClick();
    }

    //控件初始化
    private void init(){
        dailyinfo_text=(TextView)findViewById(R.id.tabfirst);
        account_text=(TextView)findViewById(R.id.tabsecond);
        mineinfo_text=(TextView)findViewById(R.id.tabthird);
        dailyinfo_text.setOnClickListener(this);
        account_text.setOnClickListener(this);
        mineinfo_text.setOnClickListener(this);
    }

    //判断点击控件，进行fragment切换
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            //每日信息
            case R.id.tabfirst:
                showfragment(dailyinfo_text,dailyinfo,R.id.tabfirst);
                break;
            //记账信息
            case R.id.tabsecond:
                showfragment(account_text,account,R.id.tabsecond);
                break;
            //个人信息
            case R.id.tabthird:
                showfragment(mineinfo_text,mineinfo,R.id.tabthird);
                break;
             default:
                 break;
        }
    }

    //隐藏所有fragment
    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(dailyinfo!=null)
            fragmentTransaction.hide(dailyinfo);
        if(account!=null)
            fragmentTransaction.hide(account);
        if(mineinfo!=null)
            fragmentTransaction.hide(mineinfo);
        fragmentTransaction.commit();
    }

    //取消全部选择
    private void unselectall()
    {
        dailyinfo_text.setSelected(false);
        account_text.setSelected(false);
        mineinfo_text.setSelected(false);
    }

    //显示fragment
    private void showfragment(TextView templetext,Fragment templefragment,int tag){
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);

        FragmentTransaction fTransactionnow = fManager.beginTransaction();
        //设置菜单选择
        unselectall();
        templetext.setSelected(true);

        Log.i("+++changefragment","change to");
        //设置fragment
        if(templefragment==null){
            //初始化对应fragment
            switch(tag){
                case R.id.tabfirst:
                    templefragment=new DailyInfoFragment();
                    break;
                case R.id.tabsecond:
                    //内含viewpager
                    templefragment=new AccountRootFragment();
                    //templefragment=new AccountStatisticFragment();
                    break;
                case R.id.tabthird:
                    templefragment=new MyInfoFragment();
                    break;
                default:
                    break;
            }
            fTransactionnow.replace(R.id.frgmentview,templefragment);
            fTransactionnow.show(templefragment);
            fTransactionnow.commit();
            current=tag;
        }
        else{
            fTransactionnow.show(templefragment);
            fTransactionnow.commit();
            current=tag;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("current",current);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //获取之前页面信息
        int currenttag=savedInstanceState.getInt("current",R.id.tabthird);
        switch(currenttag){
            //每日信息
            case R.id.tabfirst:
                showfragment(dailyinfo_text,dailyinfo,R.id.tabfirst);
                break;
            //记账信息
            case R.id.tabsecond:
                showfragment(account_text,account,R.id.tabsecond);
                break;
            //个人信息
            case R.id.tabthird:
                showfragment(mineinfo_text,mineinfo,R.id.tabthird);
                break;
            default:
                break;
        }
    }

    @Override
    //调用requestPermissions()后，系统弹出权限申请的对话框，选择后回调到下面这个函数，授权结果会封装到grantResults
    //grant授权
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                /*if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this,"权限申请已拒绝",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;*/
            default:
        }
    }
}
