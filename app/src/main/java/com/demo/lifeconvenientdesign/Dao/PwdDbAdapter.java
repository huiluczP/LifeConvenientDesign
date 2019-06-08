package com.demo.lifeconvenientdesign.Dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.demo.lifeconvenientdesign.Element.AccountInfo;
import com.demo.lifeconvenientdesign.Element.PwdInfo;

public class PwdDbAdapter {
    private static final String DB_NAME="user.db";//数据库名
    private static final int DB_VERSION=1;//创建模式
    private static final String TABLE_NAME="users";//表名

    public static final String KEY_ID="id";
    public static final String KEY_USERNAME="username";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_INFO="information";

    private SQLiteDatabase db;
    private Context context;
    private PwdDbAdapter.DBopenHelper openhelper;

    //私有类协助进行数据库创建和打开
    private static class DBopenHelper extends SQLiteOpenHelper
    {
        private static final String DB_CREATE = "create table " +
                TABLE_NAME + " (" + KEY_ID + " integer primary key autoincrement, " +
                KEY_USERNAME+" text, "+KEY_PASSWORD+" text, "+
                KEY_INFO+" text);";

        public DBopenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //创建数据表
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            Log.i("+++create+++","user");
        }

        //数据库升级
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }
    }

    public PwdDbAdapter(Context context){
        this.context=context;
    }

    //打开数据库
    public void open() throws SQLiteException {
        openhelper = new PwdDbAdapter.DBopenHelper(context, DB_NAME, null, DB_VERSION);
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

    //账户插入数据库
    public long insert(PwdInfo info){
        ContentValues newValues=new ContentValues();
        newValues.put(KEY_USERNAME,info.getUsername());
        newValues.put(KEY_PASSWORD,info.getPassword());
        newValues.put(KEY_INFO,info.getInfo());
        return db.insert(TABLE_NAME,null,newValues);
    }

    //删除
    public long deleteById(int id){
        return db.delete(TABLE_NAME,KEY_ID+"="+id,null);
    }

    //工具方法，将指针还原为具体数据类
    private PwdInfo[] ConvertToClass(Cursor cursor) {
        int count=cursor.getCount();
        if(count==0||!cursor.moveToFirst()){
            return null;
        }
        PwdInfo[] infos=new PwdInfo[count];
        for(int i=0;i<count;i++){
            infos[i]=new PwdInfo();
            infos[i].setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            infos[i].setUsername(cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
            infos[i].setPassword(cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
            infos[i].setInfo(cursor.getString(cursor.getColumnIndex(KEY_INFO)));
            cursor.moveToNext();
        }
        return infos;
    }

    //获取全部的账户信息
    public PwdInfo[] searchUsers(){
        String colum[]={KEY_ID,KEY_USERNAME,KEY_PASSWORD,KEY_INFO};
        Cursor cursor=db.query(TABLE_NAME,colum,null,null,null,null,null);
        return ConvertToClass(cursor);
    }
}
