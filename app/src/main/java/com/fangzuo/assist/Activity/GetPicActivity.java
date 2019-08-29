package com.fangzuo.assist.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fangzuo.assist.ABase.BaseActivity;
import com.fangzuo.assist.Activity.Crash.App;
import com.fangzuo.assist.Beans.CommonResponse;
import com.fangzuo.assist.Beans.ImageBean;
import com.fangzuo.assist.R;
import com.fangzuo.assist.RxSerivce.MySubscribe;
import com.fangzuo.assist.Utils.Config;
import com.fangzuo.assist.Utils.ImageUtil;
import com.fangzuo.assist.Utils.Info;
import com.fangzuo.assist.Utils.Lg;
import com.fangzuo.assist.Utils.MathUtil;
import com.fangzuo.assist.Utils.Toast;
import com.fangzuo.assist.Utils.WebApi;
import com.fangzuo.assist.widget.LoadingUtil;
import com.fangzuo.assist.widget.piccut.CropImageActivity;
import com.fangzuo.assist.widget.piccut.SelectPhotoDialog;
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

    @BindView(R.id.tv_move_right)
    TextView tvMRight;
    @BindView(R.id.tv_move_bot)
    TextView tvMBot;
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
    CardView bgSet;
    @BindView(R.id.btn_back)
    RelativeLayout btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1/fzkj_down.jpg";
    private String path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1/fzkj-loc.jpg";
    private String path_4loc = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1/fzkj-loc.jpg";
    private String path_get = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "1/fzkj-get.jpg";
    public static final String Pic_Path   = Environment.getExternalStorageDirectory().getAbsolutePath()+"/1/fzkj-get.jpg";


    private int logoW;
    private int logoH;
    private byte[] imagebyte;
    // 持有这个动画的引用，让他可以在动画执行中途取消
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private SelectPhotoDialog selectPhotoDialog;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_get_pic);
        ButterKnife.bind(this);
        tvTitle.setText("设置水印");
        tvRight.setText("选择照片");
        selectPhotoDialog = new SelectPhotoDialog(GetPicActivity.this, R.style.CustomDialog);
        // 系统默认的短动画执行时间 200
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        DownLoad("http://192.168.0.136:8081/Assist/img/bg.jpg");
    }

    @Override
    protected void initData() {
        sbLeft.setMax(500);
        sbBt.setMax(500);
        logoW = Hawk.get(Config.Logo_W, 100);
        logoH = Hawk.get(Config.Logo_H, 100);
        left = Hawk.get(Config.Logo_Left, 84);
        bottm = Hawk.get(Config.Logo_Bottom, 337);
        sbLeft.setProgress(left);
        sbBt.setProgress(bottm);
        etH.setText(logoH + "");
        etW.setText(logoW + "");
        tvMBot.setText("向下移动" + bottm);
        tvMRight.setText("向右移动" + left);


        Glide.with(GetPicActivity.this)
                .load("http://192.168.0.136:8081/Assist/img/test.jpg")
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//关闭Glide的硬盘缓存机制
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        //加载完成后的处理
                        iv.setImageDrawable(resource);
                        basePic = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                        setNewBitmap();

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
                Hawk.put(Config.Logo_Left, progress);
                tvMRight.setText("向右移动" + left);
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
                Hawk.put(Config.Logo_Bottom, progress);
                tvMBot.setText("向下移动" + bottm);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setNewBitmap();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    getPic();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Hawk.put(Config.Logo_W, MathUtil.toInt(etW.getText().toString()));
        Hawk.put(Config.Logo_H, MathUtil.toInt(etH.getText().toString()));
        super.onDestroy();

    }



    @OnClick({R.id.btn1, R.id.btn_add, R.id.btn2, R.id.btn3, R.id.btn4, R.id.iv, R.id.btn_back, R.id.btn_set})
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
                ImageUtil.saveBitmap(((BitmapDrawable) iv.getDrawable()).getBitmap(), path1);
                break;
            case R.id.btn3:
                uploadPic();
                break;
            case R.id.btn4:
//                basePic = ((BitmapDrawable) iv.getDrawable()).getBitmap();

//                saveBitmap(((BitmapDrawable) iv.getDrawable()).getBitmap(), path3);

//                String target = Environment.getExternalStorageDirectory()
//                        + "/NewApp"+getTimeLong(false)+".jpg";
//                File file = new File(target);
//                Glide.with(GetPicActivity.this)
//                        .load(file)
//                        .into(iv);
                break;
            case R.id.btn_set:
                if (bgSet.getVisibility() == View.GONE) {
                    etH.setText(logoH + "");
                    etW.setText(logoW + "");
                    bgSet.setVisibility(View.VISIBLE);
                } else {
                    logoH = MathUtil.toInt(etH.getText().toString());
                    logoW = MathUtil.toInt(etW.getText().toString());
                    Hawk.put(Config.Logo_W, logoW);
                    Hawk.put(Config.Logo_H, logoH);
                    bgSet.setVisibility(View.GONE);
                    setNewBitmap();
                }
                break;
            case R.id.iv:
                if (bgSet.getVisibility() == View.GONE) {
                    Hawk.put("pic",((BitmapDrawable) iv.getDrawable()).getBitmap());
                    ShowBigPicActivity.start(mContext);
//                    etH.setText(logoH + "");
//                    etW.setText(logoW + "");
//                    bgSet.setVisibility(View.VISIBLE);
                } else {
                    logoH = MathUtil.toInt(etH.getText().toString());
                    logoW = MathUtil.toInt(etW.getText().toString());
                    Hawk.put(Config.Logo_W, logoW);
                    Hawk.put(Config.Logo_H, logoH);
                    bgSet.setVisibility(View.GONE);
                    setNewBitmap();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    //上传图片到服务器
    private void uploadPic() {
        ImageBean bean = new ImageBean();
        bean.bitmapByte = ImageUtil.getBitmap2Byte(((BitmapDrawable) iv.getDrawable()).getBitmap());
        App.getRService().doIOAction("ImageUpload", gson.toJson(bean), new MySubscribe<CommonResponse>() {
            @Override
            public void onNext(CommonResponse commonResponse) {
                super.onNext(commonResponse);
                Lg.e("上传成功");
                Toast.showText(mContext, "上传成功");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                Lg.e("上传失败" + e.getMessage());
                Toast.showText(mContext, "上传失败" + e.getMessage());
            }
        });
    }

    int left = 0;
    int bottm = 0;
    Bitmap basePic;

    //设置水印位置及大小
    private void setNewBitmap() {
        Lg.e("当前left-bottm", left + "-" + bottm);
        if (null == basePic) return;
        //获取原始图片
//        Bitmap sourBitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        //水印图片
        Bitmap waterBitmap = BitmapFactory.decodeFile(PicUtilActivity.path);
        if (null == waterBitmap)return;
//        Bitmap watermarkBitmap = ImageUtil.createWaterMaskCenter(sourBitmap, waterBitmap);
        Bitmap watermarkBitmap = ImageUtil.createWaterMaskLeftTop(basePic, waterBitmap, left, bottm, MathUtil.toInt(etW.getText().toString()), MathUtil.toInt(etH.getText().toString()));
//        watermarkBitmap = ImageUtil.createWaterMaskRightBottom(watermarkBitmap, waterBitmap, 0, 0);
//        watermarkBitmap = ImageUtil.createWaterMaskLeftTop(watermarkBitmap, waterBitmap, 0, 0);
//        watermarkBitmap = ImageUtil.createWaterMaskRightTop(watermarkBitmap, waterBitmap, 0, 0);

        iv.setImageBitmap(watermarkBitmap);
    }

    private void getPic(){
        selectPhotoDialog.setDialogCallBack(new SelectPhotoDialog.SelectPhoteDialogCallBack() {
            @Override
            public void OnclickLiseten(int id) {
                switch (id) {
                    case SelectPhotoDialog.BN_TAKEPHOTO:
//                        File file = new File(Pic_Path);
//                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
//                                mContext.getPackageName()+".new.provider",
//                                file);
                        CropImageActivity.startActivity(GetPicActivity.this, Pic_Path, true, 3);
                        selectPhotoDialog.dismiss();
                        break;
                    case SelectPhotoDialog.BN_SELECTPHOTO:
                        CropImageActivity.startActivity(GetPicActivity.this, path_get, false, 2);
                        selectPhotoDialog.dismiss();
                        break;
                    case SelectPhotoDialog.BN_CANCEL:
                        selectPhotoDialog.dismiss();
                        break;
                }
            }
        });
        selectPhotoDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
//                case REQUEST_NICKNAME:
////                    binding.tvNickName.setText(data.getStringExtra(RESULT));
//                    break;
//                case REQUEST_MOTTO:
////                    binding.tvMotto.setText(data.getStringExtra(RESULT));
//                    break;
                case 2:
                    Log.e("pp", "获取到图片");
                    // Glide.with(EditMyInfoActivity.this).load(new File(URL.PATH_SELECT_AVATAR)).into(binding.ciAvatar);
                    Bitmap bitmap = BitmapFactory.decodeFile(path_get);
                    if (bitmap != null) {
                        imagebyte = ImageUtil.getBitmap2Byte(bitmap);
                    }
                    iv.setImageBitmap(bitmap);
                    basePic = bitmap;
                    break;
                case 3:
                    Log.e("pp", "获取到图片");
                    // Glide.with(EditMyInfoActivity.this).load(new File(URL.PATH_SELECT_AVATAR)).into(binding.ciAvatar);
                    Bitmap bitmap2 = BitmapFactory.decodeFile(Pic_Path);
                    if (bitmap2 != null) {
                        imagebyte = ImageUtil.getBitmap2Byte(bitmap2);
                    }
                    iv.setImageBitmap(bitmap2);
                    basePic = bitmap2;
                    break;
            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, GetPicActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void OnReceive(String code) {

    }
}
