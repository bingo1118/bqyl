package com.smart.cloud.fire.mvp.fragment.ConfireFireFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.jakewharton.rxbinding.view.RxView;
import com.obsessive.zbar.CaptureActivity;
import com.smart.cloud.fire.GetLocationActivity;
import com.smart.cloud.fire.base.ui.MvpFragment;
import com.smart.cloud.fire.global.Area;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.global.ShopType;
import com.smart.cloud.fire.mvp.fragment.MapFragment.Smoke;
import com.smart.cloud.fire.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fire.cloud.smart.com.smartcloudfire.R;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/9/21.
 */
public class ConfireFireFragment extends MvpFragment<ConfireFireFragmentPresenter> implements ConfireFireFragmentView {
    @Bind(R.id.dtu_add)
    RelativeLayout dtu_btn;
    @Bind(R.id.nb_add)
    RelativeLayout vb_btn;
    @Bind(R.id.zhongduan_add)
    RelativeLayout zd_btn;
    private Context mContext;
    private int scanType = 0;//0表示扫描中继器，1表示扫描烟感
    private int privilege;
    private String userID;
    private ShopType mShopType;
    private Area mArea;
    private String areaId = "";
    private String shopTypeId = "";
    private String camera = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_fire, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        userID = SharedPreferencesManager.getInstance().getData(mContext,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        privilege = MyApp.app.getPrivilege();
        init();
    }

    private void init() {
//        addCameraRelative.setVisibility(View.VISIBLE);
//        addFireZjq.setEditTextHint("区域");
//        addFireType.setEditTextHint("类型");
        RxView.clicks(dtu_btn).throttleFirst(2, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
//                addFire();
                Intent intent=new Intent(getActivity(),AddDeviceActivity.class);
                intent.putExtra("type",0);
                getActivity().startActivity(intent);
            }
        });
        RxView.clicks(vb_btn).throttleFirst(2, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
//                addFire();
                Intent intent=new Intent(getActivity(),AddDeviceActivity.class);
                intent.putExtra("type",1);
                getActivity().startActivity(intent);
            }
        });
        RxView.clicks(zd_btn).throttleFirst(2, TimeUnit.SECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
//                addFire();
                Intent intent=new Intent(getActivity(),AddDeviceActivity.class);
                intent.putExtra("type",2);
                getActivity().startActivity(intent);
            }
        });
    }



    @Override
    protected ConfireFireFragmentPresenter createPresenter() {
        ConfireFireFragmentPresenter mConfireFireFragmentPresenter = new ConfireFireFragmentPresenter(ConfireFireFragment.this);
        return mConfireFireFragmentPresenter;
    }

    @Override
    public String getFragmentName() {
        return "ConfireFireFragment";
    }



    @Override
    public void onDestroy() {
        mvpPresenter.stopLocation();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        mvpPresenter.initLocation();
        super.onStart();
    }



    @Override
    public void getLocationData(BDLocation location) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void getDataFail(String msg) {

    }

    @Override
    public void getDataSuccess(Smoke smoke) {

    }

    @Override
    public void getShopType(ArrayList<Object> shopTypes) {

    }

    @Override
    public void getShopTypeFail(String msg) {

    }

    @Override
    public void getAreaType(ArrayList<Object> shopTypes) {

    }

    @Override
    public void getAreaTypeFail(String msg) {

    }

    @Override
    public void addSmokeResult(String msg, int errorCode) {

    }

    @Override
    public void getChoiceArea(Area area) {

    }

    @Override
    public void getChoiceShop(ShopType shopType) {

    }
}
