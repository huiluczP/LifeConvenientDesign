package com.demo.lifeconvenientdesign.Dao;

import android.accounts.Account;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.demo.lifeconvenientdesign.Element.AccountInfo;

//数据库操作辅助类
public class AccountDBAdapter {
    private static final String DB_NAME="account.db";//数据库名
    private static final int DB_VERSION=1;//创建模式
    private static final String TABLE_NAME="account";//表名

    public static final String KEY_ID="id";
    public static final String KEY_TYPE="title";
    public static final String KEY_YEAR="year";
    public static final String KEY_MONTH="month";
    public static final String KEY_DATE="date";
    public static final String KEY_COST="cost";

    private SQLiteDatabase db;
    private Context context;
    private DBopenHelper openhelper;

    //私有类协助进行数据库创建和打开
    private static class DBopenHelper extends SQLiteOpenHelper
    {
        private static final String DB_CREATE = "create table " +
                TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, " +
                KEY_TYPE+" integer, "+KEY_YEAR+" integer, "+
                KEY_MONTH+" integer, "+KEY_DATE+" integer, "+
                KEY_COST+" REAL);";

        public DBopenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //创建数据表
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        //数据库升级
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }
    }

    public AccountDBAdapter(Context context){
        this.context=context;
    }

    //打开数据库
    public void open() throws SQLiteException {
        openhelper = new DBopenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = openhelper.getWritableDatabase();
        }
        catch(SQLiteException ex){
            ex.toString();
        }
    }
    //关闭数据库
    public void close(){
        if(db!=null){
            db.close();
            db=null;
        }
    }

    //插入账单信息
    public long insert(AccountInfo info){
        ContentValues newValues=new ContentValues();
        //newValues.put(KEY_ID,info.getId());
        newValues.put(KEY_COST,info.getCost());
        newValues.put(KEY_YEAR,info.getYear());
        newValues.put(KEY_MONTH,info.getMonth());
        newValues.put(KEY_DATE,info.getDate());
        newValues.put(KEY_TYPE,info.getType());
        return db.insert(TABLE_NAME,null,newValues);
    }

    //根据年月日查找账单信息
    public AccountInfo[] searchByTime(int year,int month,int date){
        String colum[]={KEY_ID,KEY_TYPE,KEY_YEAR,KEY_MONTH,KEY_DATE,KEY_COST};
        Cursor cursor=db.query(TABLE_NAME,colum,KEY_YEAR+"="+year+" and "+KEY_MONTH+"="+month+" and "+KEY_DATE+"="+date,null,null,null,null);
        return ConvertToClass(cursor);
    }

    //工具方法，将指针还原为具体数据类
    private AccountInfo[] ConvertToClass(Cursor cursor) {
        int count=cursor.getCount();
        if(count==0||!cursor.moveToFirst()){
            return null;
        }
        AccountInfo[] accounts=new AccountInfo[count];
        for(int i=0;i<count;i++){
            accounts[i]=new AccountInfo();
            accounts[i].setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            accounts[i].setYear(cursor.getInt(cursor.getColumnIndex(KEY_YEAR)));
            accounts[i].setMonth(cursor.getInt(cursor.getColumnIndex(KEY_MONTH)));
            accounts[i].setDate(cursor.getInt(cursor.getColumnIndex(KEY_DATE)));
            accounts[i].setType(cursor.getInt(cursor.getColumnIndex(KEY_TYPE)));
            accounts[i].setCost(cursor.getFloat(cursor.getColumnIndex(KEY_COST)));
            cursor.moveToNext();
        }
        return accounts;
    }

    //根据id更新数据(可能不用)
    public long updateById(int id,AccountInfo info){
        ContentValues newValues=new ContentValues();
        newValues.put(KEY_YEAR,info.getYear());
        newValues.put(KEY_MONTH,info.getMonth());
        newValues.put(KEY_DATE,info.getDate());
        newValues.put(KEY_TYPE,info.getType());
        newValues.put(KEY_COST,info.getCost());
        return db.update(TABLE_NAME,newValues,KEY_ID+"="+id,null);
    }

    //根据id删除
    public long deleteById(int id){
        return db.delete(TABLE_NAME,KEY_ID+"="+id,null);
    }

    //根据年月查找，用来进行后续统计
    public AccountInfo[] searchByTime(int year,int month){
        String colum[]={KEY_ID,KEY_TYPE,KEY_YEAR,KEY_MONTH,KEY_DATE,KEY_COST};
        Cursor cursor=db.query(TABLE_NAME,colum,KEY_YEAR+"="+year+" and "+KEY_MONTH+"="+month,null,null,null,null);
        return ConvertToClass(cursor);
    }
}
