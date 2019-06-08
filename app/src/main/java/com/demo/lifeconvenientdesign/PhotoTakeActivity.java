package com.demo.lifeconvenientdesign;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

//TakePhoto使用，通过intent传来的值确定对应的照片获取方法
public class PhotoTakeActivity extends AppCompatActivity implements TakePhoto.TakeResultListener, InvokeListener {
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private int flag;//判断对应的获取方式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        //获取对应的获取方式
        flag = getIntent().getIntExtra("flag", 0);
        File file = new File(getExternalCacheDir(), "img" + Calendar.getInstance().getTime().toString() + ".png");
        Uri uri = Uri.fromFile(file);
        Log.i("+++takephoto+++",uri.toString());
        int size = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        //设置裁剪方式
        CropOptions cropOptions = new CropOptions.Builder().setOutputX(size).setOutputX(size).setWithOwnCrop(false).create();
        if (flag == 1) {
            //相机获取照片并剪裁
            takePhoto.onPickFromCaptureWithCrop(uri, cropOptions);
        } else if (flag == 2) {
            //相册获取照片并剪裁
            takePhoto.onPickFromGalleryWithCrop(uri, cropOptions);
        }
    }

    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        //设置压缩规则，最大500kb
        //takePhoto.onEnableCompress(new CompressConfig.Builder().setMaxSize(500 * 1024).create(), true);
        return takePhoto;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      getTakePhoto().onActivityResult(requestCode, resultCode, data);
       super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public void takeSuccess(TResult result) {
        if (flag != 3) {
            String compressPath = result.getImage().getCompressPath();
            setResult(Activity.RESULT_OK, new Intent().putExtra("data", null != compressPath ? compressPath : result.getImage().getOriginalPath()));
            Log.v("WZ", "compressPath:" + compressPath);
            Log.v("WZ", "OriginalPath:" + result.getImage().getOriginalPath());
        } else {
            ArrayList<TImage> images = result.getImages();
            setResult(Activity.RESULT_OK, new Intent().putExtra("data", images));
        }
        finish();
    }


    @Override
    public void takeFail(TResult result, String msg) {
        Log.v("WZ", "takeFail:" + msg);
        if (flag != 3) {
            setResult(Activity.RESULT_OK, new Intent().putExtra("data", ""));
        } else {
            setResult(Activity.RESULT_OK, new Intent().putExtra("data", new ArrayList()));
        }
        finish();
    }

    @Override
    public void takeCancel() {
        Log.v("WZ", getResources().getString(R.string.msg_operation_canceled));
        if (flag != 3) {
            setResult(Activity.RESULT_OK, new Intent().putExtra("data", ""));
        } else {
            setResult(Activity.RESULT_OK, new Intent().putExtra("data", new ArrayList()));
        }
        finish();
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }


}
