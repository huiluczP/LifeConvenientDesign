package com.demo.lifeconvenientdesign;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.demo.lifeconvenientdesign.Dao.PwdDbAdapter;
import com.demo.lifeconvenientdesign.Element.PwdInfo;

public class CreatePwdActivity extends Activity {
    private EditText username;
    private EditText password;
    private EditText info;

    private Button confirm;
    private Button back;
    PwdDbAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createpwd_layout);
        adapter=new PwdDbAdapter(getApplicationContext());
        adapter.open();

        init();
        initButton();
    }

    private void init(){
        username=(EditText)findViewById(R.id.useradd);
        password=(EditText)findViewById(R.id.pwdadd);
        info=(EditText)findViewById(R.id.infoadd);
        confirm=(Button)findViewById(R.id.confirm);
        back=(Button)findViewById(R.id.goback);
    }

    private void initButton(){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=username.getText().toString();
                String pwd=password.getText().toString();
                String information=info.getText().toString();
                PwdInfo pinfo=new PwdInfo();
                pinfo.setUsername(user);
                pinfo.setPassword(pwd);
                pinfo.setInfo(information);

                //数据库插入数据
                if(adapter.insert(pinfo)>0)
                    Toast.makeText(CreatePwdActivity.this, "账号添加成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(CreatePwdActivity.this, "账号添加失败", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CreatePwdActivity.this,MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
