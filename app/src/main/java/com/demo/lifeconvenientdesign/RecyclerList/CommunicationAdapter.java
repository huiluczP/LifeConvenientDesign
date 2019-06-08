package com.demo.lifeconvenientdesign.RecyclerList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.demo.lifeconvenientdesign.Dao.PwdDbAdapter;
import com.demo.lifeconvenientdesign.Element.CommInfo;
import com.demo.lifeconvenientdesign.Element.PwdInfo;
import com.demo.lifeconvenientdesign.R;

import java.util.List;

public class CommunicationAdapter extends ArrayAdapter<PwdInfo> {

    private int resourceId;
    CommunicationAdapter me;
    List<PwdInfo> objects;

    public CommunicationAdapter(Context context, int resource,List<PwdInfo> objects) {
        super(context, resource, objects);
        resourceId=resource;
        me=this;
        this.objects=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PwdInfo info=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView information=(TextView)view.findViewById(R.id.comm_info);
        TextView username=(TextView)view.findViewById(R.id.comm_username);
        TextView password=(TextView)view.findViewById(R.id.comm_password);
        Button btn_delete=(Button)view.findViewById(R.id.btn_delete);

        //设置对应信息
        information.setText(info.getInfo());
        username.setText(info.getUsername());
        password.setText(info.getPassword());

        //没准设置一个按钮用来删除
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PwdDbAdapter adapter=new PwdDbAdapter(getContext());
                adapter.open();
                adapter.deleteById(info.getId());

                objects.remove(info);
                me.notifyDataSetChanged();
            }
        });

        return view;
    }
}
