package com.demo.lifeconvenientdesign.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.demo.lifeconvenientdesign.ChangeInfoActivity;
import com.demo.lifeconvenientdesign.CreatePwdActivity;
import com.demo.lifeconvenientdesign.Dao.PwdDbAdapter;
import com.demo.lifeconvenientdesign.Element.CommInfo;
import com.demo.lifeconvenientdesign.Element.PwdInfo;
import com.demo.lifeconvenientdesign.PhotoTakeActivity;
import com.demo.lifeconvenientdesign.R;
import com.demo.lifeconvenientdesign.RecyclerList.CommunicationAdapter;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.permission.PermissionManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//显示个人信息的fragment
public class MyInfoFragment extends Fragment {

    private Button selfinfo;
    private CircleImageView headimg;
    private ListView communication;

    private TextView name;
    private TextView gender;
    private TextView content;
    private TextView add;

    private static int MODE = Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;
    private static final String PREFERENCE_NAME = "info";
    private SharedPreferences info;
    CommunicationAdapter communicationAdapter;

    private ArrayList<PwdInfo> infos=new ArrayList<>();
    PwdDbAdapter dbAdapter;
    private int Camera_Flag=1;
    private int Album_Flag=2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbAdapter=new PwdDbAdapter(getContext());
        dbAdapter.open();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //个人信息布局
        View view= inflater.inflate(R.layout.selfinfo_layout, container, false);
        selfinfo=(Button)view.findViewById(R.id.selfinfochange);
        headimg=(CircleImageView)view.findViewById(R.id.headimg);
        name=(TextView)view.findViewById(R.id.name);
        gender=(TextView)view.findViewById(R.id.gender);
        content=(TextView)view.findViewById(R.id.content);

        add=(TextView)view.findViewById(R.id.comm_label);

        communication=(ListView)view.findViewById(R.id.communication);
        communicationAdapter=new CommunicationAdapter(getContext(),R.layout.communicationitem_layout,infos);
        communication.setAdapter(communicationAdapter);

        //初始化个人信息
        initText();

        //个人信息修改页面跳转
        selfinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ChangeInfoActivity.class);
                startActivityForResult(intent,101);
            }
        });
        //添加账户信息
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CreatePwdActivity.class);
                startActivity(intent);
            }
        });
        //点击头像进行选择
        headimg.setOnClickListener(new headchangeListener());
        new CommInitThread().start();

        return view;
    }

    //点击弹出底部选项栏
    private class headchangeListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            View contentview = LayoutInflater.from(getContext()).inflate(R.layout.photopoplayout,null);
            final PopupWindow popupWindow = new PopupWindow(contentview, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            //设置取消
            popupWindow.setOutsideTouchable(true);
            //背景透明
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置动画
            popupWindow.setAnimationStyle(R.style.popwin_anim_style);
            //设置位置
            View rootview = LayoutInflater.from(getContext()).inflate(R.layout.activity_main,null);
            popupWindow.showAtLocation(rootview, Gravity.BOTTOM,0,0);

            Button btn_camera=popupWindow.getContentView().findViewById(R.id.btn_camera);
            Button btn_album=popupWindow.getContentView().findViewById(R.id.btn_album);
            Button btn_cancel=popupWindow.getContentView().findViewById(R.id.btn_cancel);

            //关闭弹窗
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            //这边加上使用takephoto做成的activity操作
            btn_camera.setOnClickListener(new View.OnClickListener(){
                //打开相机
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), PhotoTakeActivity.class);
                    //传递参数，设置对应的图像获取方式
                    intent.putExtra("flag",Camera_Flag);
                    startActivityForResult(intent,102);
                }
            });
            btn_album.setOnClickListener(new View.OnClickListener() {
                //打开相册
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getContext(), PhotoTakeActivity.class);
                    //传递参数，设置对应的图像获取方式
                    intent.putExtra("flag",Album_Flag);
                    startActivityForResult(intent,103);
                }
            });
        }
    }

    private void initText(){
        info=getActivity().getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);

        String nametext=info.getString("name","回炉重造P");
        String gendertext=info.getString("gender","保密");
        String contenttext=info.getString("content","写点什么吧");

        name.setText(nametext);
        gender.setText(gendertext);
        content.setText(contenttext);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //个人信息修改
        if(requestCode==101){
            String nametext=info.getString("name","回炉重造P");
            String gendertext=info.getString("gender","保密");
            String contenttext=info.getString("content","写点什么吧");

            name.setText(nametext);
            gender.setText(gendertext);
            content.setText(contenttext);
        }
        //头像修改，照相模式
        else if(requestCode==102){
            String uri=data.getStringExtra("data");
            if(!uri.equals(""))
                Glide.with(getContext()).load(uri).into(headimg);
            Toast.makeText(getContext(),"使用相机更换头像"+"?"+uri,Toast.LENGTH_SHORT).show();
        }
        //头像修改，相册模式
        else if(requestCode==103){
            String uri=data.getStringExtra("data");
            if(!uri.equals(""))
                Glide.with(getContext()).load(uri).into(headimg);
            Toast.makeText(getContext(),"使用相册更换头像"+"?"+uri,Toast.LENGTH_SHORT).show();
        }
    }

    //读取对应联系人信息
    private class CommInitThread extends Thread{
        @Override
        public void run() {
            PwdInfo test=new PwdInfo();
            test.setInfo("steam");
            test.setUsername("huiluchongzao233");
            test.setPassword("123456");

            //数据库方法来获取所有的信息
            PwdInfo[] userinfos=dbAdapter.searchUsers();
            if(userinfos==null){
                infos.clear();
                infos.add(test);
            }
            else{
                infos.clear();
                for(PwdInfo p:userinfos){
                    infos.add(p);
                }
            }
            //notify公布
            communicationAdapter.notifyDataSetChanged();
        }
    }

    //设置list点击事件
    private void initList(){
        communication.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //弹出删除页面同时传递id(未编写)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

}
