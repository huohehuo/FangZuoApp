package com.fangzuo.assist.widget.piccut;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fangzuo.assist.R;


/**
 * Created by sky on 2017/1/20.
 */

public class SelectPhotoDialog extends Dialog {

    public static final int BN_TAKEPHOTO = 0;
    public static final int BN_SELECTPHOTO = 1;
    public static final int BN_CANCEL = 3;
    private float alpha;
    private int height;
    private int width;
    private int x;
    private int y;
    private TextView takePhoto = null;
    private TextView selectPhote = null;
    private TextView cancel = null;
    private SelectPhoteDialogCallBack dialogCallBack;


    public SelectPhotoDialog(Context context, int themeResId) {
        super(context, themeResId);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        this.width = wm.getDefaultDisplay().getWidth();
        this.height = wm.getDefaultDisplay().getHeight()/4;
        this.alpha = 0.5f;
        this.x = 0;
        this.y = 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_image);
        initUI();
        setAlpha();
        setSize();
        setLocation();
    }

    public void setDialogCallBack(SelectPhoteDialogCallBack dialogCallBack) {
        this.dialogCallBack = dialogCallBack;
    }

    public void initUI() {
        takePhoto = (TextView) findViewById(R.id.tv_take_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCallBack.OnclickLiseten(BN_TAKEPHOTO);
            }
        });
        selectPhote = (TextView) findViewById(R.id.tv_album);
        selectPhote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCallBack.OnclickLiseten(BN_SELECTPHOTO);
            }
        });
        cancel = (TextView) findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCallBack.OnclickLiseten(BN_CANCEL);
            }
        });
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setSize(int wight, int height) {
        this.width = wight;
        this.height = height;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private void setAlpha() {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = alpha;
        this.getWindow().setAttributes(lp);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void setSize() {
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.ll_select_photo);
        android.view.ViewGroup.LayoutParams lp = ll.getLayoutParams();
        lp.height = height;
        lp.width = width;

    }

    private void setLocation() {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        lp.x = x;
        lp.y = y;
        dialogWindow.setAttributes(lp);

    }


    public  interface SelectPhoteDialogCallBack {
        public void OnclickLiseten(int id);
    }

}
