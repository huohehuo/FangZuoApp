package com.fangzuo.assist.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fangzuo.assist.ABase.BaseActivity;
import com.fangzuo.assist.R;
import com.fangzuo.assist.Utils.CommonUtil;
import com.fangzuo.assist.Utils.ImageUtil;
import com.fangzuo.assist.Utils.Toast;
import com.fangzuo.assist.widget.LoadingUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fangzuo.assist.Utils.CommonUtil.getTimeLong;

public class GetPicActivity extends BaseActivity {

    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.iv)
    ImageView iv;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_get_pic);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DownLoad("http://192.168.0.105:8080/Assist/img/banner001.jpg");

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void OnReceive(String code) {

    }

    @OnClick({R.id.btn1, R.id.btn2,R.id.btn3, R.id.iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                Glide.with(GetPicActivity.this)
//                        .asGif()
//                        .load("http://192.168.0.105:8080/Assist/img/logo.gif")
                        .load(R.drawable.test)
                        .into(iv);
                break;
            case R.id.btn2:
                break;
            case R.id.iv:
//                Glide.with(GetPicActivity.this)
//                        .load(R.mipmap.chuku)
//                        .into(iv);
                break;
            case R.id.btn3:
                setNewBitmap();
//                String target = Environment.getExternalStorageDirectory()
//                        + "/NewApp"+getTimeLong(false)+".jpg";
//                File file = new File(target);
//                Glide.with(GetPicActivity.this)
//                        .load(file)
//                        .into(iv);
                break;
        }
    }

    private void setNewBitmap(){
        //获取原始图片
        Bitmap sourBitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        //水印图片
        Bitmap waterBitmap = BitmapFactory.decodeFile(PicUtilActivity.path);

        Bitmap watermarkBitmap = ImageUtil.createWaterMaskCenter(sourBitmap, waterBitmap);
        watermarkBitmap = ImageUtil.createWaterMaskLeftBottom(watermarkBitmap, waterBitmap, 1100, 500);
//        watermarkBitmap = ImageUtil.createWaterMaskRightBottom(watermarkBitmap, waterBitmap, 0, 0);
//        watermarkBitmap = ImageUtil.createWaterMaskLeftTop(watermarkBitmap, waterBitmap, 0, 0);
//        watermarkBitmap = ImageUtil.createWaterMaskRightTop(watermarkBitmap, waterBitmap, 0, 0);

        iv.setImageBitmap(watermarkBitmap);
    }

    private ProgressDialog pDialog;
    private void DownLoad(String downLoadURL) {
        LoadingUtil.dismiss();
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            pDialog = new ProgressDialog(mContext);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setTitle("下载中");
            pDialog.show();
            String target = Environment.getExternalStorageDirectory()
                    + "/NewApp"+getTimeLong(false)+".jpg";
            HttpUtils utils = new HttpUtils();

            utils.download(downLoadURL, target, new RequestCallBack<File>() {

                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    System.out.println("下载进度:" + current + "/" + total);
                    pDialog.setProgress((int) (current*100/total));
                }

                @Override
                public void onSuccess(ResponseInfo<File> arg0) {
                    pDialog.dismiss();
                    LoadingUtil.showAlter(mContext,"","下载完成");
                    System.out.println("下载完成");
//                    try{
//                        CommonUtil.installApk(mContext,arg0.result+"");
////                        Intent intent = new Intent(Intent.ACTION_VIEW);
////                        intent.addCategory(Intent.CATEGORY_DEFAULT);
////                        intent.setDataAndType(Uri.fromFile(arg0.result),
////                                "application/vnd.android.package-archive");
////                        startActivityForResult(intent, 0);
//                    }catch (Exception e){
//                        try{
//                            StringBuilder builder = new StringBuilder();
//                            builder.append("请先退出本软件\n");
//                            builder.append("进入PDA软件主页\n");
//                            builder.append("选择文件管理器\n");
//                            builder.append("找到文件NewApp\n");
//                            builder.append("长按变色点击右下角重命名\n删去后面的数字\n");
//                            builder.append("变成文件名：NewApp.apk\n");
//                            builder.append("点击安装\n");
//                            AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
//                            ab.setTitle("下载成功!\n请按操作重新安装APK");
//                            ab.setMessage(builder.toString());
//                            ab.setPositiveButton("确定", null);
//                            ab.create().show();
//                        }catch (Exception e1){
//
//                        }
//                    }

                }

                @Override
                public void onFailure(com.lidroid.xutils.exception.HttpException arg0, String arg1) {
                    pDialog.dismiss();
                    Toast.showText(mContext, "下载失败");
                }


            });
        } else {
            pDialog.dismiss();
            Toast.showText(mContext, "正在安装");

        }
    }




    public static void start(Context context) {
        Intent intent = new Intent(context, GetPicActivity.class);
        context.startActivity(intent);
    }
}
