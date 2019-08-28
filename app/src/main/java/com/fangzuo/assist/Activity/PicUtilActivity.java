package com.fangzuo.assist.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fangzuo.assist.ABase.BaseActivity;
import com.fangzuo.assist.R;
import com.fangzuo.assist.Utils.Lg;
import com.fangzuo.assist.Utils.Toast;
import com.venusic.handwrite.view.HandWriteView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PicUtilActivity extends BaseActivity {

    @BindView(R.id.view)
    HandWriteView writeView;
    @BindView(R.id.btn_clear)
    Button btnClear;
    @BindView(R.id.btn_getpic)
    Button btnGetpic;
    @BindView(R.id.btn_reset)
    Button btnReset;
    @BindView(R.id.iv_img)
    ImageView ivImg;

    public static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1/fzkj.png";
    public static String path2 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "fangzuokejiaaa.jpg";
    @BindView(R.id.btn_back)
    RelativeLayout btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_pic_util);
        ButterKnife.bind(this);
        tvTitle.setText("设置签名");
    }

    @Override
    protected void initData() {
        showLogo();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void OnReceive(String code) {

    }

    @OnClick({R.id.btn_clear, R.id.btn_getpic, R.id.btn_reset, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_clear:
                writeView.clear();
                break;
            case R.id.btn_getpic:
                showLogo();
                break;
            case R.id.btn_reset:
//                FileInputStream fis = null;
//                try {
//                    Lg.e("图片地址",PicUtilActivity.path);
//                    fis = new FileInputStream(PicUtilActivity.path);
//                    Bitmap bitmap  = BitmapFactory.decodeStream(fis);
//                    if (bitmap==null){
//                        Toast.showText(mContext,"不存在相片");
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                try {
                    writeView.save(path, true, 10, false);
                    Toast.showText(mContext, "保存成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_right:
                finish();
                break;
        }
    }
    //显示本地已存在的签名
    private void showLogo(){
        FileInputStream fisd = null;
        try {
//                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            Lg.e("图片地址", PicUtilActivity.path);
            fisd = new FileInputStream(PicUtilActivity.path);
            Bitmap bitmap = BitmapFactory.decodeStream(fisd);
            if (bitmap == null) {
                Toast.showText(mContext, "不存在相片");
            }
            ivImg.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, PicUtilActivity.class);
        context.startActivity(intent);
    }
}
