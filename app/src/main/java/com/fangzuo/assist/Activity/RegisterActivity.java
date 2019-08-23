package com.fangzuo.assist.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fangzuo.assist.ABase.BaseActivity;
import com.fangzuo.assist.Activity.Crash.App;
import com.fangzuo.assist.Adapter.RegisterAdapter;
import com.fangzuo.assist.Beans.CommonResponse;
import com.fangzuo.assist.Beans.DownloadReturnBean;
import com.fangzuo.assist.Beans.EventBusEvent.ClassEvent;
import com.fangzuo.assist.Beans.RegisterBean;
import com.fangzuo.assist.Dao.Product;
import com.fangzuo.assist.R;
import com.fangzuo.assist.RxSerivce.MySubscribe;
import com.fangzuo.assist.Utils.Config;
import com.fangzuo.assist.Utils.EventBusInfoCode;
import com.fangzuo.assist.Utils.EventBusUtil;
import com.fangzuo.assist.Utils.JsonCreater;
import com.fangzuo.assist.Utils.Lg;
import com.fangzuo.assist.Utils.MD5;
import com.fangzuo.assist.Utils.Toast;
import com.fangzuo.assist.Utils.WebApi;
import com.fangzuo.assist.widget.LoadingUtil;
import com.fangzuo.assist.zxing.CustomCaptureActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.orhanobut.hawk.Hawk;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.btn_back)
    RelativeLayout btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.ry_data)
    EasyRecyclerView ryData;
    @BindView(R.id.ed_code)
    EditText edCode;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    private RegisterAdapter adapter;

    @Override
    protected void receiveEvent(ClassEvent event) {
        switch (event.Msg) {
            case EventBusInfoCode.ScanResult://
                BarcodeResult res = (BarcodeResult) event.postEvent;
                edCode.setText(res.getResult().getText());
                break;
            case EventBusInfoCode.Register_Result://
                String resl = (String) event.postEvent;
                if ("OK".equals(resl)){
                    LoadingUtil.showAlter(mContext,"回执",event.Msg2);
                }else{
                    LoadingUtil.showAlter(mContext,"回执",resl);
                }
                break;
        }
    }
    @Override
    protected void initView() {
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        tvRight.setText("更新");
        tvTitle.setText("注册用户");
        getRegisterData();
    }

    @Override
    protected void initData() {

        ryData.setAdapter(adapter = new RegisterAdapter(mContext));
        ryData.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


    }

    private void getRegisterData() {
        App.getRService().doIOAction("GetRegister", "查询注册用户", new MySubscribe<CommonResponse>() {
            @Override
            public void onNext(CommonResponse commonResponse) {
                super.onNext(commonResponse);
                DownloadReturnBean dBean = JsonCreater.gson.fromJson(commonResponse.returnJson, DownloadReturnBean.class);
                if (dBean.registerBeans.size() > 0) {
                    tvMsg.setText("统计数据："+dBean.registerBeans.size());
                    adapter.addAll(dBean.registerBeans);
                } else {
                    adapter.clear();
                    Toast.showText(mContext, "无数据");
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    //用户pda的注册码
    private void register(String mac){
        if ("".equals(mac)){
            Toast.showText(mContext,"请输入注册码");
            return;
        }
        String register_code = MD5.getMD5(mac) + "fzkj601";
        String newRegister = MD5.getMD5(register_code);
        final String lastRegister = MD5.getMD5(newRegister);//最终的写入码
//        App.getRService().doIOAction(WebApi.RegisterCheck, lastRegister, new MySubscribe<CommonResponse>() {
//            @Override
//            public void onNext(CommonResponse commonResponse) {
//                super.onNext(commonResponse);
//                if (!commonResponse.state)return;
//                if (commonResponse.returnJson.equals("OK")){
//                    Lg.e("存在注册码");
//                    //存在注册码
//                    EventBusUtil.sendEvent(new ClassEvent(EventBusInfoCode.Register_Result,"该注册码已被注册","OK"));
//                }else{
                    //不存在注册码，进行注册
                    Lg.e("不存在注册码");
                    App.getRService().doIOAction(WebApi.RegisterCode, lastRegister, new MySubscribe<CommonResponse>() {
                        @Override
                        public void onNext(CommonResponse commonResponse) {
                            super.onNext(commonResponse);
                            if (!commonResponse.state) return;//注册成功
                            EventBusUtil.sendEvent(new ClassEvent(EventBusInfoCode.Register_Result,"注册成功","OK"));
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            EventBusUtil.sendEvent(new ClassEvent(EventBusInfoCode.Register_Result,"注册失败"));
//                                        LoadingUtil.showAlter(WelcomeActivity.this, "提示", "注册失败");
                        }
                    });
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
////                            super.onError(e);
//                EventBusUtil.sendEvent(new ClassEvent(EventBusInfoCode.Register_Result,"查询用户错误"+e.getMessage()));
//            }
//        });
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                RegisterBean company = adapter.getAllData().get(position);
                Lg.e("得到注册信息：", company);
            }
        });
    }

    @Override
    protected void OnReceive(String code) {

    }

    @OnClick({R.id.btn_back, R.id.tv_right,R.id.iv_scan,R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_register:
                register(edCode.getText().toString());
                break;
            case R.id.iv_scan:
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                // 设置自定义扫描Activity
                intentIntegrator.setCaptureActivity(CustomCaptureActivity.class);
                intentIntegrator.initiateScan();
                break;
            case R.id.tv_right:
                getRegisterData();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

}
