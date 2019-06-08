package com.demo.lifeconvenientdesign.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.demo.lifeconvenientdesign.Dao.AccountDBAdapter;
import com.demo.lifeconvenientdesign.Element.AccountInfo;

import java.util.ArrayList;
import java.util.List;

public class AccountService extends Service {

    private static String[] types={"餐饮","学习","其它"};
    private static AccountDBAdapter dbadapter;

    private final IBinder mBinder=new LocalBinder();

    //绑定返回service实例
    public class LocalBinder extends Binder{
        public AccountService getService(){
            return AccountService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this,"记账service ride on",Toast.LENGTH_SHORT).show();
        dbadapter=new AccountDBAdapter(getApplicationContext());
        dbadapter.open();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this,"记账service kick ass",Toast.LENGTH_SHORT).show();
        return false;
    }

    //根据付账类型获取对应的统计信息，之后用来显示
    private List selectAccountByType(int type, AccountInfo[] accounts){
        ArrayList<AccountInfo> typeaccounts=new ArrayList<AccountInfo>();
        if(accounts!=null) {
            for (int i = 0; i < accounts.length; i++) {
                if (accounts[i].getType() == type) {
                    typeaccounts.add(accounts[i]);
                }
            }
        }
        return typeaccounts;
    }

    //删除账单
    public void deleteAccountById(AccountInfo info){
        dbadapter.deleteById(info.getId());
    }

    //查询当日账单
    public AccountInfo[] searchTodayAccount(int year,int month,int day){
        AccountInfo[] accountToday=dbadapter.searchByTime(year,month,day);
        return accountToday;
    }

    //查询当月账单并获取对应type数据总和
    public float[] searchMonthAccountSumByType(int year,int month){
        float[] sums=new float[types.length];
        for(int i=0;i<types.length;i++){
            sums[i]=0;
        }
        AccountInfo[] monthaccount= dbadapter.searchByTime(year,month);
        for(int i=0;i<types.length;i++){
            float sum=0;
            List infos=selectAccountByType(i,monthaccount);
            for(int j=0;j<infos.size();j++){
                sum+=((AccountInfo)infos.get(j)).getCost();
            }
            sums[i]=sum;
        }
        return sums;
    }
}
