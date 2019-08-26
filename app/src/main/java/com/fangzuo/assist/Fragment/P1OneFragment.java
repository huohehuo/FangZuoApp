package com.fangzuo.assist.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.fangzuo.assist.ABase.BaseFragment;
import com.fangzuo.assist.Activity.CompanyActivity;
import com.fangzuo.assist.Activity.GetPicActivity;
import com.fangzuo.assist.Activity.PicUtilActivity;
import com.fangzuo.assist.Activity.RegisterActivity;
import com.fangzuo.assist.Adapter.P1OneAdapter;
import com.fangzuo.assist.Beans.SettingList;
import com.fangzuo.assist.R;
import com.fangzuo.assist.Utils.Config;
import com.fangzuo.assist.Utils.GetSettingList;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class P1OneFragment extends BaseFragment {
    @BindView(R.id.ry_data)
    EasyRecyclerView ryData;
    Unbinder unbinder;
    private FragmentActivity mContext;

    public P1OneFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_p1_one, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void initView() {
        mContext = getActivity();

    }

    @Override
    protected void OnReceive(String barCode) {

    }

    P1OneAdapter adapter;
    @Override
    protected void initData() {
//        String getPermit=share.getString(ShareInfo.USER_PERMIT);
//        String[] arylist = getPermit.split("\\-"); // 这样才能得到正确的结果
        ryData.setAdapter(adapter = new P1OneAdapter(mContext));
        ryData.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false));
        adapter.addAll(GetSettingList.getAppList());
    }

//    private AlertDialog.Builder builder;
//    String[] items_sout = new String[]{"原单", "销售订单下推销售出库单","VMI销售订单下推销售出库单","退货通知单下推销售退货单"};
//    String[] items_tb = new String[]{"挑板领料1", "挑板入库1"};
//    String[] items_tb2 = new String[]{"挑板领料2", "挑板入库2"};
//    String[] items_tb3 = new String[]{"挑板领料3", "挑板入库3"};
//    String[] items_pk = new String[]{"盘亏入库", "VMI盘亏入库"};
//    String[] items_gb = new String[]{"改板领料", "改板入库"};
//    String[] items_dc = new String[]{"代存出库", "代存入库"};
////    String[] items_db = new String[]{"组织间调拨", "跨组织调拨", "调拨申请单下推直接调拨单", "VMI调拨申请单下推直接调拨单"};
//    String[] items_db = new String[]{"组织间调拨", "跨组织调拨", "调拨申请单下推直接调拨单"};
////    String[] items_in_out = new String[]{"样板出库", "第三方货物入库","第三方货物出库","出库申请单下推其他出库单"};
//    String[] items_in_out = new String[]{"样板出库", "第三方货物入库","第三方货物出库"};

    @Override
    protected void initListener() {
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SettingList tv = (SettingList) adapter.getAllData().get(position);
                Log.e("listitem", tv.tv);
                switch (tv.activity) {
                    /*-------------二期单据---------------------------------------------*/
                    /*-------------一期单据----------------------------------------------*/
                    case Config.RegisterActivity://箱码调拨单
                        RegisterActivity.start(mContext);
                        break;
                    case Config.CompanyActivity://箱码调拨单
                        CompanyActivity.start(mContext);
                        break;
                    case Config.GetPicActivity://箱码调拨单
                        GetPicActivity.start(mContext);
                        break;

//                    case Config.ProductGet4BoxActivity://生产领料(箱码)
//                        PagerForActivity.start(mContext, Config.ProductGet4BoxActivity);
//                        break;
//                    case Config.SplitBoxP1Activity://拆箱
//                        SplitBoxP1Activity.start(mContext);
//                        break;
//                    case Config.PrintBoxCode4P1Activity://箱码补打
//                        PrintBoxCode4P1Activity.start(mContext);
//                        break;
//                    case Config.P1PdCgrk2ProductGetActivity://采购入库单下推简单生产领料
//                        PushDownPagerActivity.start(getActivity(),28);
//                        break;
//                    case Config.P1PdProductGet2CprkActivity://生产领料单下推产品入库单
//                        PushDownPagerActivity.start(getActivity(),29);
//                        break;
//                    case Config.P1PdProductGet2Cprk2Activity://生产领料单下推产品入库单
//                        PushDownPagerActivity.start(getActivity(),30);
//                        break;
//                    case Config.PdCgOrder2WgrkActivity://实际是采购订单下推外购入库
//                        PushDownPagerActivity.start(getActivity(),1);
//                        break;
////                    case "采购入库":
////                        PagerForActivity.start(mContext, Config.PurchaseInStoreActivity);
////                        startNewActivity(PurchaseInStoreActivity.class, null);
////                        break;
//                    case Config.DBActivity://调拨单
//                        // 创建对话框构建器
//                        builder = new AlertDialog.Builder(getActivity());
//                        // 设置参数
//                        builder.setAdapter(
//                                new ArrayAdapter<String>(getActivity(),
//                                        R.layout.item_choose, R.id.textView, items_db),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        switch (which) {
//                                            case 0:
//                                                PagerForActivity.start(mContext, Config.DBActivity);
////                                                startNewActivity(SaleOutActivity.class, null);
//                                                break;
//                                            case 1:
//                                                PagerForActivity.start(mContext, Config.DB2Activity);
//                                                break;
//                                            case 2:
//                                                PushDownPagerActivity.start(getActivity(),22);
//                                                break;
////                                            case 3:
////                                                PushDownPagerActivity.start(getActivity(),23);
////                                                break;
//                                        }
//                                    }
//                                });
//                        builder.create().show();
////                        startNewActivity(DBActivity.class, null);
//                        break;
//                    case Config.DhInActivity://到货入库1
//                        PagerForActivity.start(mContext, Config.DhInActivity);
//                        break;
//                    case Config.DhIn2Activity://到货入库2
//                        PagerForActivity.start(mContext, Config.DhIn2Activity);
//                        break;
//                    case Config.SaleOutActivity://销售出库
//                        // 创建对话框构建器
//                        builder = new AlertDialog.Builder(getActivity());
//                        // 设置参数
//                        builder.setAdapter(
//                                new ArrayAdapter<String>(getActivity(),
//                                        R.layout.item_choose, R.id.textView, items_sout),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        switch (which) {//"原单", "销售订单下推销售出库单","退货通知单下推销售退货单"
//                                            case 0:
//                                                PagerForActivity.start(mContext, Config.SaleOutActivity);
////                                                startNewActivity(SaleOutActivity.class, null);
//                                                break;
//                                            case 1:
//                                                PushDownPagerActivity.start(getActivity(),2);
////                                                Bundle b = new Bundle();
////                                                b.putInt("123", 2);
//////                                                startNewActivity(PushDownPagerActivity.class, R.anim.activity_fade_in, R.anim.activity_fade_out, false, b);
////                                                startNewActivity(PushDownPagerActivity.class, b);
//                                                break;
//                                            case 2:
//                                                PushDownPagerActivity.start(getActivity(),21);
//                                                break;
//                                            case 3:
//                                                PushDownPagerActivity.start(getActivity(),6);
//                                                break;
//                                        }
//                                    }
//                                });
//                        builder.create().show();
//                        break;
//                    case Config.TbGetActivity://挑板业务1
//                        builder = new AlertDialog.Builder(getActivity());
//                        // 设置参数
//                        builder.setAdapter(
//                                new ArrayAdapter<String>(getActivity(),
//                                        R.layout.item_choose, R.id.textView, items_tb),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        switch (which) {
//                                            case 0:
//                                                PagerForActivity.start(mContext, Config.TbGetActivity);
//                                                break;
//                                            case 1:
//                                                PagerForActivity.start(mContext, Config.TbInActivity);
//                                                break;
//                                        }
//                                    }
//                                });
//                        builder.create().show();
//                        break;
//                    case Config.TbGet2Activity://挑板业务2
//                        builder = new AlertDialog.Builder(getActivity());
//                        // 设置参数
//                        builder.setAdapter(
//                                new ArrayAdapter<String>(getActivity(),
//                                        R.layout.item_choose, R.id.textView, items_tb2),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        switch (which) {
//                                            case 0:
//                                                PagerForActivity.start(mContext, Config.TbGet2Activity);
//                                                break;
//                                            case 1:
//                                                PagerForActivity.start(mContext, Config.TbIn2Activity);
//                                                break;
//                                        }
//                                    }
//                                });
//                        builder.create().show();
//                        break;
//                    case Config.TbGet3Activity://挑板业务3
//                        builder = new AlertDialog.Builder(getActivity());
//                        // 设置参数
//                        builder.setAdapter(
//                                new ArrayAdapter<String>(getActivity(),
//                                        R.layout.item_choose, R.id.textView, items_tb3),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        switch (which) {
//                                            case 0:
//                                                PagerForActivity.start(mContext, Config.TbGet3Activity);
//                                                break;
//                                            case 1:
//                                                PagerForActivity.start(mContext, Config.TbIn3Activity);
//                                                break;
//                                        }
//                                    }
//                                });
//                        builder.create().show();
//                        break;
//                    case Config.GbGetActivity://改板业务
//                        builder = new AlertDialog.Builder(getActivity());
//                        // 设置参数
//                        builder.setAdapter(
//                                new ArrayAdapter<String>(getActivity(),
//                                        R.layout.item_choose, R.id.textView, items_gb),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        switch (which) {
//                                            case 0:
//                                                PagerForActivity.start(mContext, Config.GbGetActivity);
//                                                break;
//                                            case 1:
//                                                PagerForActivity.start(mContext, Config.GbInActivity);
//                                                break;
//                                        }
//                                    }
//                                });
//                        builder.create().show();
//                        break;
//                    case Config.HwOut3Activity://其他出入库
//                        builder = new AlertDialog.Builder(getActivity());
//                        // 设置参数
//                        builder.setAdapter(
//                                new ArrayAdapter<String>(getActivity(),
//                                        R.layout.item_choose, R.id.textView, items_in_out),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        switch (which) {
//                                            case 0:
//                                                PagerForActivity.start(mContext, Config.YbOutActivity);
//                                                break;
//                                            case 1:
//                                                PagerForActivity.start(mContext, Config.HwIn3Activity);
//                                                break;
//                                            case 2:
//                                                PagerForActivity.start(mContext, Config.HwOut3Activity);
//                                                break;
//                                            case 3:
//                                                PushDownPagerActivity.start(getActivity(),24);
//                                                break;
//                                        }
//                                    }
//                                });
//                        builder.create().show();
//                        break;
//                    case Config.PrintHistoryActivity://条码补打
//                        startNewActivity(PrintHistoryActivity.class, null);
//                        break;
//                    case Config.PrintBeforeDataActivity://期初条码补打
//                        startNewActivity(PrintBeforeDataActivity.class, null);
//                        break;
//                    case Config.ProductGetActivity://生产领料
//                        PagerForActivity.start(mContext, Config.ProductGetActivity);
//                        break;
//                    case Config.ProductInStoreActivity://产品入库
//                        PagerForActivity.start(mContext, Config.ProductInStoreActivity);
//                        break;
//                    case Config.PYingActivity://盘盈入库
//                        PagerForActivity.start(mContext, Config.PYingActivity);
//                        break;
//                    case Config.PkuiActivity://盘亏入库
//                        builder = new AlertDialog.Builder(getActivity());
//                        // 设置参数
//                        builder.setAdapter(
//                                new ArrayAdapter<String>(getActivity(),
//                                        R.layout.item_choose, R.id.textView, items_pk),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        switch (which) {
//                                            case 0:
//                                                PagerForActivity.start(mContext, Config.PkuiActivity);
//                                                break;
//                                            case 1:
//                                                PagerForActivity.start(mContext, Config.PkuiVMIActivity);
//                                                break;
//                                        }
//                                    }
//                                });
//                        builder.create().show();
//                        break;
//                    case Config.ScanProductActivity://扫一扫
//                        ScanProductActivity.start(mContext);
//                        break;

//                    case "代存业务":
//                        builder = new AlertDialog.Builder(getActivity());
//                        // 设置参数
//                        builder.setAdapter(
//                                new ArrayAdapter<String>(getActivity(),
//                                        R.layout.item_choose, R.id.textView, items_dc),
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int which) {
//                                        switch (which) {
//                                            case 0:
//                                                PagerForActivity.start(mContext, Config.DcOutActivity);
//                                                break;
//                                            case 1:
//                                                PagerForActivity.start(mContext, Config.DcInActivity);
//                                                break;
//                                        }
//                                    }
//                                });
//                        builder.create().show();
//                        break;
//                    case "其他出库":
////                        startNewActivity(OtherOutStoreActivity.class, null);
//                        PagerForActivity.start(mContext, Config.OtherOutStoreActivity);
//                        break;
//
//                    case "其他入库":
////                        startNewActivity(OtherInStoreActivity.class, null);
//                        PagerForActivity.start(mContext, Config.OtherInStoreActivity);
//                        break;
//                    case "库存查询":
//                        startNewActivity(CheckStoreActivity.class, null);
//                        break;
//                    case "盘点":
//                        startNewActivity(PDActivity.class, null);
//                        break;
//                    case "单据下推":
//                        startNewActivity(PushDownActivity.class, null);
//                        break;
//                    case "采购订单":
//                        startNewActivity(PurchaseOrderActivity.class, null);
//                        break;
//
//                    case "销售订单":
//                        startNewActivity(SaleOrderActivity.class, null);
//                        break;
//                    case 1://外购入库
//                        startNewActivity(PurchaseInStoreActivity.class, null);
//                        break;
//                    case 2://产品入库
////                        startNewActivity(ProductInStorageActivity.class, null);
//                        break;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
