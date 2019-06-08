package com.demo.lifeconvenientdesign.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.lifeconvenientdesign.Page.AccountPageAdapter;
import com.demo.lifeconvenientdesign.R;

public class AccountRootFragment extends Fragment {
    private ViewPager viewpager;
    private AccountPageAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.account_root_layout, container, false);
        viewpager=(ViewPager)view.findViewById(R.id.viewpager);
        initpager();
        return view;
    }
    //设置viewpager切换
    private void initpager(){
        adapter=new AccountPageAdapter(getChildFragmentManager());
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);
    }
}
