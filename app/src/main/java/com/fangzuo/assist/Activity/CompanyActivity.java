package com.fangzuo.assist.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fangzuo.assist.ABase.BaseActivity;
import com.fangzuo.assist.Activity.Crash.App;
import com.fangzuo.assist.Adapter.CompanyAdapter;
import com.fangzuo.assist.Adapter.RegisterAdapter;
import com.fangzuo.assist.Beans.CommonResponse;
import com.fangzuo.assist.Beans.Company;
import com.fangzuo.assist.Beans.DownloadReturnBean;
import com.fangzuo.assist.R;
import com.fangzuo.assist.RxSerivce.MySubscribe;
import com.fangzuo.assist.Utils.JsonCreater;
import com.fangzuo.assist.Utils.Lg;
import com.fangzuo.assist.Utils.Toast;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompanyActivity extends BaseActivity {
    @BindView(R.id.btn_back)
    RelativeLayout btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.ry_data)
    EasyRecyclerView ryData;
    private CompanyAdapter adapter;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);
        tvRight.setText("更新");
        tvTitle.setText("公司项目信息");
        getRegisterData();
    }

    @Override
    protected void initData() {
        ryData.setAdapter(adapter = new CompanyAdapter(mContext));
        ryData.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));


    }
    private void getRegisterData(){
        App.getRService().doIOAction("GetCompany", "查询注册用户", new MySubscribe<CommonResponse>() {
            @Override
            public void onNext(CommonResponse commonResponse) {
                super.onNext(commonResponse);
                DownloadReturnBean dBean = JsonCreater.gson.fromJson(commonResponse.returnJson, DownloadReturnBean.class);
                if (dBean.companies.size()>0){
                    adapter.addAll(dBean.companies);
                }else{
                    adapter.clear();
                    Toast.showText(mContext,"无数据");
                }

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    protected void initListener() {
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Company company = adapter.getAllData().get(position);
                Lg.e("得到公司：",company);
            }
        });
    }

    @Override
    protected void OnReceive(String code) {

    }

    @OnClick({R.id.btn_back, R.id.tv_title, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_title:
                break;
            case R.id.tv_right:
                getRegisterData();
                break;
        }
    }
    public static void start(Context context){
        Intent intent = new Intent(context,CompanyActivity.class);
        context.startActivity(intent);
    }
}
