package com.demo.lifeconvenientdesign.RecyclerList;

import com.bumptech.glide.Glide;
import com.demo.lifeconvenientdesign.R;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.lifeconvenientdesign.Element.AccountInfo;
import com.yanzhenjie.recyclerview.*;

import java.util.List;

public class AccountRecyclerAdapter extends RecyclerView.Adapter<AccountRecyclerAdapter.ViewHolder> {
    private List<AccountInfo> accounts;
    private String[] types={"餐饮","学习","其它"};
    private int[] pics={R.drawable.eatting,R.drawable.studying,R.drawable.other};
    View view;

    //初始化子布局
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView itemicon;
        TextView itemtype;
        TextView itemcost;

        public ViewHolder(View itemView) {
            super(itemView);
            itemicon=(ImageView)itemView.findViewById(R.id.itemtypeicon);
            itemtype=(TextView)itemView.findViewById(R.id.itemtype);
            itemcost=(TextView)itemView.findViewById(R.id.itemcost);
        }
    }

    public AccountRecyclerAdapter(List<AccountInfo> accounts){
        this.accounts=accounts;
    }

    //初始化布局
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.accountitem_layout,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    //子项显示设置
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        AccountInfo info=accounts.get(i);

        int imgresource=pics[info.getType()];
        Glide.with(view).load(imgresource).into(viewHolder.itemicon);
        viewHolder.itemtype.setText(types[info.getType()]);
        viewHolder.itemcost.setText("-"+info.getCost());
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }
}
