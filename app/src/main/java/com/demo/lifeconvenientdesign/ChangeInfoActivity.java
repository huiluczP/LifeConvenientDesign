package com.demo.lifeconvenientdesign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ChangeInfoActivity extends Activity {

    private EditText changename;
    private EditText changecontent;
    private RadioGroup group;
    private RadioButton male;
    private RadioButton female;
    private RadioButton secret;
    private Button confirm;
    private Button cancel;

    private static int MODE = Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE;
    private static final String PREFERENCE_NAME = "info";
    private SharedPreferences info;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeinfo_layout);
        init();
        inittext();
        initButton();
    }

    private void init(){
        changename=(EditText) findViewById(R.id.changename);
        changecontent=(EditText)findViewById(R.id.changecontent);

        group=(RadioGroup)findViewById(R.id.groupgender);
        male=(RadioButton)findViewById(R.id.gendermale);
        female=(RadioButton)findViewById(R.id.genderfemale);
        secret=(RadioButton)findViewById(R.id.gendersecret);

        confirm=(Button)findViewById(R.id.changeconfirm);
        cancel=(Button)findViewById(R.id.changecancel);
    }

    //获取数据，界面初始化
    private void inittext(){
        info=this.getSharedPreferences(PREFERENCE_NAME,MODE);
        String nametext=info.getString("name","回炉重造P");
        String gendertext=info.getString("gender","保密");
        String contenttext=info.getString("content","写点什么吧");

        changename.setText(nametext);
        changecontent.setText(contenttext);
        switch(gendertext){
            case "男":
                male.performClick();
                break;
            case "女":
                female.performClick();
                break;
            case "保密":
                secret.performClick();
                break;
            default:
                secret.performClick();
                break;
        }
    }

    private void initButton(){
        //确认按钮
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=changename.getText().toString();
                String content=changecontent.getText().toString();
                String gender=((RadioButton)findViewById(group.getCheckedRadioButtonId())).getText().toString();
                SharedPreferences.Editor editor=info.edit();
                editor.putString("name",name);
                editor.putString("gender",gender);
                editor.putString("content",content);
                editor.commit();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
