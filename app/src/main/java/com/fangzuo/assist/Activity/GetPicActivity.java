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
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
                zoomImageFromThumb(iv);
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
                    etH.setText(logoH + "");
                    etW.setText(logoW + "");
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
                        CropImageActivity.startActivity(GetPicActivity.this, path_get, true, 2);
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
            }
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

    private void zoomImageFromThumb(final View thumbView) {
        // 如果有动画正在运行，取消这个动画
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        // 加载显示大图的ImageView
        if (imagebyte == null) {
            return;
        }
        iv.setImageBitmap(ImageUtil.getByte2Bitmap(imagebyte));

        // 计算初始小图的边界位置和最终大图的边界位置。
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // 小图的边界就是小ImageView的边界，大图的边界因为是铺满全屏的，所以就是整个布局的边界。
        // 然后根据偏移量得到正确的坐标。
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // 计算初始的缩放比例。最终的缩放比例为1。并调整缩放方向，使看着协调。
        float startScale = 0;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // 横向缩放
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // 竖向缩放
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // 隐藏小图，并显示大图
        thumbView.setAlpha(0f);
        iv.setVisibility(View.VISIBLE);

        // 将大图的缩放中心点移到左上角。默认是从中心缩放
        iv.setPivotX(0f);
        iv.setPivotY(0f);

        //对大图进行缩放动画
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(iv, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(iv, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(iv, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(iv, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // 点击大图时，反向缩放大图，然后隐藏大图，显示小图。
        final float startScaleFinal = startScale;
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(iv, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(iv,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(iv,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(iv,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        iv.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        iv.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, GetPicActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void OnReceive(String code) {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
