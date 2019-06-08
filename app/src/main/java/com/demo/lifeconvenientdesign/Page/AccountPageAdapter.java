package com.demo.lifeconvenientdesign.Page;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;

import com.demo.lifeconvenientdesign.Fragment.AccountFragment;
import com.demo.lifeconvenientdesign.Fragment.AccountStatisticFragment;

import java.util.ArrayList;

//pageview滑动显示
public class AccountPageAdapter extends FragmentPagerAdapter {

    private int count=2;
    private ArrayList<String> titleList;
    private ArrayList<Fragment> fragmentlist;

    public AccountPageAdapter(FragmentManager fm) {
        super(fm);
        titleList=new ArrayList<>();
        titleList.add("强力记账页面");
        titleList.add("强力统计页面");

        fragmentlist=new ArrayList<>();
        fragmentlist.add(new AccountFragment());
        fragmentlist.add(new AccountStatisticFragment());
    }

    //轮流返回对应fragment
    @Override
    public Fragment getItem(int i) {
        if (fragmentlist != null && i < fragmentlist.size()) {
            return fragmentlist.get(i);
        }
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SpannableStringBuilder ssb=new SpannableStringBuilder( titleList.get(position));
        ssb.setSpan(new RelativeSizeSpan(2f),0,ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }
}
