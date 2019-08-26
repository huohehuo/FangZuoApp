package com.fangzuo.assist.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fangzuo.assist.ABase.BaseActivity;
import com.fangzuo.assist.R;
import com.fangzuo.assist.Utils.Config;
import com.fangzuo.assist.Utils.ImageUtil;
import com.fangzuo.assist.Utils.Lg;
import com.fangzuo.assist.Utils.MathUtil;
import com.fangzuo.assist.Utils.Toast;
import com.fangzuo.assist.widget.LoadingUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetPicActivity extends BaseActivity {

    @BindView(R.id.btn1)
    Button btn1;

    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.sb_left)
    SeekBar sbLeft;
    @BindView(R.id.sb_bt)
    SeekBar sbBt;
    @BindView(R.id.et_w)
    EditText etW;
    @BindView(R.id.et_h)
    EditText etH;
    @BindView(R.id.bg_set)
    LinearLayout bgSet;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1/fzkj_down.jpg";
    private String path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1/fzkj-loc.jpg";
    private String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1/fzkj-new.jpg";
    private String path3 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1/fzkj-new_with_logo.jpg";

    private int logoW;
    private int logoH;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_get_pic);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DownLoad("http://192.168.0.136:8081/Assist/img/bg.jpg");
    }

    @Override
    protected void initData() {
        sbLeft.setMax(500);
        sbBt.setMax(200);
        logoW = Hawk.get(Config.Logo_W,100);
        logoH = Hawk.get(Config.Logo_H,100);
        left = Hawk.get(Config.Logo_Left,100);
        bottm = Hawk.get(Config.Logo_Bottom,100);
        sbLeft.setProgress(left);
        sbBt.setProgress(bottm);

        Glide.with(GetPicActivity.this)
                .load("http://192.168.0.105:8080/Assist/img/test.jpg")
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        //加载完成后的处理
                        iv.setImageDrawable(resource);
                        basePic = ((BitmapDrawable) iv.getDrawable()).getBitmap();

                    }
                });
    }

    @Override
    protected void initListener() {
        sbLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Lg.e("当前Left",progress);
                left = progress;
                Hawk.put(Config.Logo_Left,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setNewBitmap();
            }
        });
        sbBt.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Lg.e("当前Bottom",progress);
                bottm = progress;
                Hawk.put(Config.Logo_Bottom,progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setNewBitmap();
            }
        });


    }

    @Override
    protected void onDestroy() {
        Hawk.put(Config.Logo_W,MathUtil.toInt(etW.getText().toString()));
        Hawk.put(Config.Logo_H,MathUtil.toInt(etH.getText().toString()));
        super.onDestroy();

    }

    @Override
    protected void OnReceive(String code) {

    }

    @OnClick({R.id.btn1, R.id.btn_add, R.id.btn2, R.id.btn3, R.id.btn4, R.id.iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                PicUtilActivity.start(mContext);
                break;
            case R.id.btn_add:
                setNewBitmap();
//                Glide.with(GetPicActivity.this)
////                        .asGif()
////                        .load("http://192.168.0.105:8080/Assist/img/logo.gif")
//                        .load(R.drawable.test)
//                        .into(iv);
                break;
            case R.id.btn2:
                saveBitmap(((BitmapDrawable) iv.getDrawable()).getBitmap(), path1);
                break;
            case R.id.btn3:
                saveBitmap(((BitmapDrawable) iv.getDrawable()).getBitmap(), path2);
                break;
            case R.id.btn4:
                basePic = ((BitmapDrawable) iv.getDrawable()).getBitmap();

//                saveBitmap(((BitmapDrawable) iv.getDrawable()).getBitmap(), path3);

//                String target = Environment.getExternalStorageDirectory()
//                        + "/NewApp"+getTimeLong(false)+".jpg";
//                File file = new File(target);
//                Glide.with(GetPicActivity.this)
//                        .load(file)
//                        .into(iv);
                break;
            case R.id.iv:
                if (bgSet.getVisibility()==View.GONE){
                    etH.setText(logoH+"");
                    etW.setText(logoW+"");
                    bgSet.setVisibility(View.VISIBLE);
                }else{
                    logoH = MathUtil.toInt(etH.getText().toString());
                    logoW = MathUtil.toInt(etW.getText().toString());
                    Hawk.put(Config.Logo_W,logoW);
                    Hawk.put(Config.Logo_H,logoH);
                    bgSet.setVisibility(View.GONE);
                }
                break;
        }
    }

    int left = 0;
    int bottm = 0;
    Bitmap basePic;

    private void setNewBitmap() {
        Lg.e("当前left-bottm", left + "-" + bottm);
        if (null==basePic)return;
        //获取原始图片
//        Bitmap sourBitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        //水印图片
        Bitmap waterBitmap = BitmapFactory.decodeFile(PicUtilActivity.path);

//        Bitmap watermarkBitmap = ImageUtil.createWaterMaskCenter(sourBitmap, waterBitmap);
        Bitmap watermarkBitmap = ImageUtil.createWaterMaskLeftTop(basePic, waterBitmap, left, bottm, MathUtil.toInt(etW.getText().toString()),  MathUtil.toInt(etH.getText().toString()));
//        watermarkBitmap = ImageUtil.createWaterMaskRightBottom(watermarkBitmap, waterBitmap, 0, 0);
//        watermarkBitmap = ImageUtil.createWaterMaskLeftTop(watermarkBitmap, waterBitmap, 0, 0);
//        watermarkBitmap = ImageUtil.createWaterMaskRightTop(watermarkBitmap, waterBitmap, 0, 0);

        iv.setImageBitmap(watermarkBitmap);
    }

    /**
     * 保存方法
     */
    public void saveBitmap(Bitmap bm, String picName) {
        File f = new File(picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
//            String target = Environment.getExternalStorageDirectory()
//                    + "/NewApp" + getTimeLong(false) + ".jpg";
            HttpUtils utils = new HttpUtils();

            utils.download(downLoadURL, path, new RequestCallBack<File>() {

                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    System.out.println("下载进度:" + current + "/" + total);
                    pDialog.setProgress((int) (current * 100 / total));
                }

                @Override
                public void onSuccess(ResponseInfo<File> arg0) {
                    pDialog.dismiss();
                    LoadingUtil.showAlter(mContext, "", "下载完成");
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
                public void onFailure(HttpException arg0, String arg1) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
