package com.demo.lifeconvenientdesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.lifeconvenientdesign.Dao.AccountDBAdapter;
import com.demo.lifeconvenientdesign.Element.AccountInfo;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;

public class CreateAccountActivity extends Activity {
    private TextView showaddtime;
    private EditText costnum;
    private com.wx.wheelview.widget.WheelView typeselect;
    private Button confirmadd;
    private AccountDBAdapter adapter;
    private Button back;

    private int[] time;

    private ArrayList<String> types;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount_layout);
        init();
        initButton();
    }

    private void init(){
        showaddtime=(TextView)findViewById(R.id.showaddtime);
        costnum=(EditText)findViewById(R.id.addcost);
        typeselect=(WheelView)findViewById(R.id.wheelview);
        confirmadd=(Button)findViewById(R.id.confirmadd);
        adapter=new AccountDBAdapter(getApplicationContext());
        adapter.open();
        back=(Button)findViewById(R.id.goback);

        //显示当前欲插入年月日
        Intent intent=getIntent();
        time=intent.getIntArrayExtra("timeinfo");
        showaddtime.setText("日程插入时间为"+time[0]+"年"+time[1]+"月"+time[2]+"日");

        //设置对应的wheelview选项
        typeselect.setWheelAdapter(new ArrayWheelAdapter(this)); // 文本数据源
        typeselect.setSkin(WheelView.Skin.Common); // common皮肤
        types=new ArrayList<>();
        types.add("餐饮");
        types.add("学习");
        types.add("其它");
        typeselect.setWheelData(types);  // 数据集合
    }

    //设置按钮行为
    private void initButton(){
        confirmadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cost=costnum.getText().toString();
                int type=typeselect.getCurrentPosition();
                AccountInfo info=new AccountInfo();

                //加上错误处理
                try {
                    info.setCost(Float.parseFloat(cost));
                    info.setType(type);
                    info.setYear(time[0]);
                    info.setMonth(time[1]);
                    info.setDate(time[2]);
                }
                catch(Exception ex){
                    Toast.makeText(CreateAccountActivity.this,"输入错误",Toast.LENGTH_SHORT).show();
                }

                long row=0;
                row=adapter.insert(info);
                if(row>0)
                    Toast.makeText(getApplicationContext(),"成功添加账单信息"+type,Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(),"添加账单失败",Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
