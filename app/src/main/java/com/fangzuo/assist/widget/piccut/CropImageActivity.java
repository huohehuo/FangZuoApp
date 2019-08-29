package com.fangzuo.assist.widget.piccut;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fangzuo.assist.R;
import com.fangzuo.assist.Utils.Lg;
import com.fangzuo.assist.widget.LoadingUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * 编辑图片的activity，通过 start 来启动它
 */
public class CropImageActivity extends AppCompatActivity {
    public static final String RESULT_IMAGE = "result_image";
    private static final String IMAGE_PATH_KEY = "image_path";

    public static final String ORDER_TITLE = "title";
    public static final String ORDER_SELECT_FROM_FILE = "file";
    public static final String ORDER_SELECT_FROM_CAMERA = "camera";
    private static final long MAX_PIX = 2000 * 2000;//最大图片像素

    public static final int FROM_FILE = 1314;
    public static final int FROM_CAREMA = 1315;

    private static Bitmap image;//当前图片
    @BindView(R.id.btn_back)
    RelativeLayout btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.cropView)
    CropImageView cropImageView;
//    private CropImageView cropImageView;
    private String imageTemp = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_crop_image);
        ButterKnife.bind(this);
        imageTemp = getIntent().getStringExtra(IMAGE_PATH_KEY);
        if (imageTemp == null || imageTemp.isEmpty()) {
            Log.e("live36G", "CropImageActivity 必需通过传入imagePath，建议调用CropImageActivity#start(Context context, String imagePath)方法启动activity");
            finish();
            return;
        }


//        cropImageView = (CropImageView) findViewById(R.id.cropView);
        cropImageView.setHandleColor(getResources().getColor(R.color.colorAccent));
        cropImageView.setFrameColor(getResources().getColor(R.color.colorAccent));
        cropImageView.setGuideColor(getResources().getColor(R.color.colorAccent));
        tvTitle.setText("裁剪图片");
        tvRight.setTextColor(getResources().getColor(R.color.black));
        tvRight.setText("完成");

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingUtil.showDialog(CropImageActivity.this, "正在截取...");
                Observable
                        .create(new Observable.OnSubscribe<Boolean>() {
                            @Override
                            public void call(Subscriber<? super Boolean> subscriber) {
                                subscriber.onNext(BitmapHelper.saveBitmap(imageTemp, cropImageView.getCroppedBitmap()));
                                subscriber.onCompleted();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean o) {
                                if (o) {
                                    Intent i = getIntent();
                                    i.putExtra(RESULT_IMAGE, imageTemp);
                                    setResult(RESULT_OK, i);
                                }
                                image.recycle();
                                LoadingUtil.dismiss();
                                finish();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                LoadingUtil.dismiss();
                                throwable.printStackTrace();
                            }
                        });
            }
        });


        image = null;
        if (getIntent().getStringExtra(ORDER_TITLE) == null || getIntent().getStringExtra(ORDER_TITLE).equals(""))
            openCamera();
        else if (getIntent().getStringExtra(ORDER_TITLE).equals(ORDER_SELECT_FROM_FILE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, FROM_FILE);
        } else if (getIntent().getStringExtra(ORDER_TITLE).equals(ORDER_SELECT_FROM_CAMERA))
            openCamera();
        else openCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crop, menu);
        return true;
    }


    @Override
    public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }
        if (image != null && !image.isRecycled()) image.recycle();
        image = null;
        showProgressDialog(R.string.activity_crop_waiting_read);
        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                String path = imageTemp;
                if (requestCode == FROM_FILE) {
                    Uri uri = data.getData();
                    path = BitmapHelper.getRealFilePath(CropImageActivity.this, uri);
                }
                image = BitmapHelper.readBitmapByMaxPix(path, MAX_PIX, BitmapHelper.readPictureDegree(path));
                subscriber.onNext(image);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap o) {
                        cropImageView.setImageBitmap(o);
                        hideProgressDialog();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public static void openPic(Activity activity, String imagePath) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(IMAGE_PATH_KEY, imagePath);
//        if (fromCamera)
//            intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_CAMERA);
//        else
//            intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_FILE);
        activity.startActivity(intent);
    }

    private void openCamera() {
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        File file = new File(imageTemp);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                this.getPackageName()+".new.provider",
                file);
//        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, FROM_CAREMA);
    }

    /**
     * 使用这个方法启动activity
     *
     * @param activity   跳转起始activity
     * @param imagePath  截图完成后图片保存的路径
     * @param fromCamera 是否从摄像机获取图片，默认是从手机中获取图片
     */
    public static void startActivity(Activity activity, String imagePath, boolean fromCamera, int requestCode) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        Lg.e("得到路径",imagePath);
        intent.putExtra(IMAGE_PATH_KEY, imagePath);
        if (fromCamera)
            intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_CAMERA);
        else
            intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_FILE);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Fragment f, String imagePath, boolean fromCamera, int requestCode) {
        Intent intent = new Intent(f.getContext(), CropImageActivity.class);
        intent.putExtra(IMAGE_PATH_KEY, imagePath);
        if (fromCamera)
            intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_CAMERA);
        else
            intent.putExtra(CropImageActivity.ORDER_TITLE, CropImageActivity.ORDER_SELECT_FROM_FILE);
        f.startActivityForResult(intent, requestCode);
    }

    public void showProgressDialog(@StringRes int message) {
        if (progressDialog != null || isFinishing()) return;
        progressDialog = ProgressDialog.show(this
                , getString(R.string.common_hint)
                , getString(message)
                , false, false);
        final ProgressDialog mProgressDialog = progressDialog;
        new Handler().postDelayed(new Runnable() {

            public void run() {
                if (mProgressDialog == progressDialog)
                    hideProgressDialog();//设置ProgressDialog显示的最长时间
            }

        }, 10000);
    }

    public void hideProgressDialog() {
        if (isFinishing() || progressDialog == null) return;
        progressDialog.dismiss();
        progressDialog = null;
    }
}
