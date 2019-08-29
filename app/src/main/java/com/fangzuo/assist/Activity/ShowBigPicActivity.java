package com.fangzuo.assist.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.fangzuo.assist.R;
import com.orhanobut.hawk.Hawk;

public class ShowBigPicActivity extends AppCompatActivity {
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private ScaleGestureDetector mScaleGestureDetector = null;
    private Bitmap mBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_pic);
        mSurfaceView = (SurfaceView) this.findViewById(R.id.surfaceview);
        mSurfaceHolder = mSurfaceView.getHolder();
        mScaleGestureDetector = new ScaleGestureDetector(this,
                new ScaleGestureListener());
        mBitmap = Hawk.get("pic",null);
        mSurfaceView.post(new Runnable() {

            @Override
            public void run() {
                if (mBitmap == null)return;
//                mBitmap = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.guanmjie);
                // 锁定整个SurfaceView
                Canvas mCanvas = mSurfaceHolder.lockCanvas();
                // 画图
                mCanvas.drawBitmap(mBitmap, 0f, 0f, null);
                // 绘制完成，提交修改
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                // 重新锁一次
                mSurfaceHolder.lockCanvas(new Rect(0, 0, 0, 0));
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 返回给ScaleGestureDetector来处理
        return mScaleGestureDetector.onTouchEvent(event);
    }

    public class ScaleGestureListener implements
            ScaleGestureDetector.OnScaleGestureListener {

        private float scale;
        private float preScale = 1;// 默认前一次缩放比例为1

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // TODO Auto-generated method stub

            Matrix mMatrix = new Matrix();
            float previousSpan = detector.getPreviousSpan();
            float currentSpan = detector.getCurrentSpan();
            if (currentSpan < previousSpan) {
                // 缩小
                // scale = preScale-detector.getScaleFactor()/3;
                scale = preScale - (previousSpan - currentSpan) / 1000;
            } else {
                // 放大
                // scale = preScale+detector.getScaleFactor()/3;
                scale = preScale + (currentSpan - previousSpan) / 1000;
            }
            mMatrix.setScale(scale, scale);

            // 锁定整个SurfaceView
            Canvas mCanvas = mSurfaceHolder.lockCanvas();
            // 清屏
            mCanvas.drawColor(Color.BLACK);
            // 画缩放后的图
            mCanvas.drawBitmap(mBitmap, mMatrix, null);
            // 绘制完成，提交修改
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            // 重新锁一次
            mSurfaceHolder.lockCanvas(new Rect(100, 0, 0, 0));
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);

            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // 一定要返回true才会进入onScale()这个函数
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            preScale = scale;//记住本次的缩放后的图片比例
        }
    }
    public static void start(Context context) {
        Intent intent = new Intent(context, ShowBigPicActivity.class);
        context.startActivity(intent);
    }
}
