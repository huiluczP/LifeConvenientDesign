package com.demo.lifeconvenientdesign.Fragment;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demo.lifeconvenientdesign.R;
import com.demo.lifeconvenientdesign.Service.AccountService;
import com.demo.lifeconvenientdesign.Statistic.ChartTool;

import java.util.Calendar;

import lecho.lib.hellocharts.view.ColumnChartView;

//统计信息页面
public class AccountStatisticFragment extends Fragment {

    private AccountService accountservice;
    private ColumnChartView chartview;
    int year;
    int month;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            accountservice = ((AccountService.LocalBinder)service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            accountservice = null;
        }
    };

    //启动绑定service
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    //这边也是利用service获取数据，并使用统计控件显示信息
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //打开service
        final Intent serviceIntent = new Intent(getContext(),AccountService.class);
        if(accountservice==null)
            getContext().bindService(serviceIntent,mConnection,getContext().BIND_AUTO_CREATE);
        Log.i("+++service+++","service on");
        if(accountservice==null)
            Log.i("+++service+++","我服了");

        View view= inflater.inflate(R.layout.account_statistic_layout, container, false);
        Calendar c=Calendar.getInstance();
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH)+1;

        final SwipeRefreshLayout reflash=(SwipeRefreshLayout)view.findViewById(R.id.swiperereshlayout);
        reflash.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //刷新时调用当前数据库,延迟两秒
            @Override
            public void onRefresh() {
                        ChartTool tool=new ChartTool();
                        float []sums={30,20,30};
                        tool.setSums(sums);
                        tool.setSums(accountservice.searchMonthAccountSumByType(year,month));
                        tool.setChartViewData(chartview);
                        reflash.setRefreshing(false);
            }
        });

        //初始化时间
        TextView time=(TextView)view.findViewById(R.id.statistictime);
        time.setText(year+"年"+month+"月");

        chartview=(ColumnChartView)view.findViewById(R.id.chart);
        new ChartThread().start();
        return view;
    }

    //设置对应的图表信息
    private class ChartThread extends Thread{
        @Override
        public void run() {
            while(accountservice==null){
                //循环等待service被调用
            }
            //这边帮chartview设置,service获取数据
            ChartTool tool=new ChartTool();
            float []sums={30,20,30};
            tool.setSums(sums);
            tool.setSums(accountservice.searchMonthAccountSumByType(year,month));
            tool.setChartViewData(chartview);
        }
    }
}
