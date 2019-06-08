package com.demo.lifeconvenientdesign.Fragment;

import android.accounts.Account;
import android.app.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.lifeconvenientdesign.CreateAccountActivity;
import com.demo.lifeconvenientdesign.Element.AccountInfo;
import com.demo.lifeconvenientdesign.R;
import com.demo.lifeconvenientdesign.RecyclerList.AccountRecyclerAdapter;
import com.demo.lifeconvenientdesign.Service.AccountService;
import com.haibin.calendarview.CalendarView;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

//显示全部账单信息并操作的fragment
public class AccountFragment extends Fragment {

    private List<AccountInfo> accounts=new ArrayList<AccountInfo>();
    private SwipeRecyclerView recyclerView;
    private AccountRecyclerAdapter adapter;
    private CalendarView calendar;
    private AccountService accountservice;
    private TextView yearshow;
    private TextView add;

    private AccountInfo a1;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //打开service
        final Intent serviceIntent = new Intent(getContext(),AccountService.class);
        if(accountservice==null)
            getContext().bindService(serviceIntent,mConnection,getContext().BIND_AUTO_CREATE);
        Log.i("+++service+++","service on");
        if(accountservice==null)
            Log.i("+++service+++","我服了");

        //每日信息布局
        View view= inflater.inflate(R.layout.accountall_layout, container, false);

        calendar=(CalendarView)view.findViewById(R.id.calendarView);
        adapter=new AccountRecyclerAdapter(accounts);
        recyclerView=(SwipeRecyclerView)view.findViewById(R.id.accountlist);
        yearshow=(TextView)view.findViewById(R.id.calendaryearview);
        add=(TextView)view.findViewById(R.id.additem);
        //设置默认的一个显示栏位
        init();

        //设置删除菜单和数据源
        initRecyclerList();
        LinearLayoutManager manager =new LinearLayoutManager(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        //设置显示
        initCalendar();
        initButton();
        calendar.performClick();
        return view;
    }

    //执行对应的点击时间模拟，获取数据(没准不用)
    @Override
    public void onStart() {
        super.onStart();
    }

    //调试用
    private void init(){
        a1=new AccountInfo();
        a1.setType(1);
        a1.setCost(0f);
        accounts.add(a1);
    }

    //初始化列表对象，设置对应菜单和监听器
    private void initRecyclerList(){
        // 创建删除菜单
        SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext()); // 各种文字和图标属性设置。
                deleteItem.setBackgroundColor(Color.RED);
                deleteItem.setText("删除");
                deleteItem.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
                rightMenu.addMenuItem(deleteItem); // 在Item右侧添加一个菜单。
            }
        };
        //设置菜单
        recyclerView.setSwipeMenuCreator(mSwipeMenuCreator);

        // 菜单点击监听。
        recyclerView.setOnItemMenuClickListener(new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int position) {
                // 任何操作必须先关闭菜单
                menuBridge.closeMenu();

                // 左侧还是右侧菜单：
                int direction = menuBridge.getDirection();
                // 菜单在Item中的Position：
                int menuPosition = menuBridge.getPosition();

                //调用service删除方法
                AccountInfo info=accounts.get(position);
                accountservice.deleteAccountById(info);
                accounts.remove(info);
                adapter.notifyDataSetChanged();
            }
        });
    }

    Handler handle=new Handler();
    //初始化日历对象，实现监听
    private void initCalendar(){
        calendar.setOnDateSelectedListener(new CalendarView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(final com.haibin.calendarview.Calendar calendar, boolean isClick) {
                handle.post(new Runnable() {
                    //调用数据库方法获取当天日程信息（calendar进行对应）
                    //同时对头部的月份进行对应显示
                    @Override
                    public void run() {
                        int year=calendar.getYear();
                        int month=calendar.getMonth();
                        int day=calendar.getDay();

                        //设置对应年月显示
                        yearshow.setText(year+"年"+month+"月");

                        //service中方法调用获取对应数据
                        AccountInfo[] infos=null;
                        if(accountservice!=null)
                            infos=accountservice.searchTodayAccount(year,month,day);
                        accounts.clear();
                        if(infos!=null) {
                            for(int i=0;i<infos.length;i++) {
                                accounts.add(infos[i]);
                                Log.i("+++++info+++++",infos[i].getType()+","+infos[i].getCost());
                            }
                        }
                        else
                            accounts.add(a1);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void initButton(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CreateAccountActivity.class);
                if(calendar.getSelectedCalendar()!=null){
                    com.haibin.calendarview.Calendar c=calendar.getSelectedCalendar();
                    int info[]={c.getYear(),c.getMonth(),c.getDay()};
                    intent.putExtra("timeinfo",info);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getActivity(), "请先选择对应日期", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
