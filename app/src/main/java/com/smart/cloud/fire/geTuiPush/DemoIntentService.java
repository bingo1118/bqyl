package com.smart.cloud.fire.geTuiPush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.global.MyApp;
import com.smart.cloud.fire.mvp.Alarm.AlarmActivity;
import com.smart.cloud.fire.mvp.Alarm.UserAlarmActivity;
import com.smart.cloud.fire.mvp.fragment.MapFragment.HttpError;
import com.smart.cloud.fire.pushmessage.DisposeAlarm;
import com.smart.cloud.fire.pushmessage.GetUserAlarm;
import com.smart.cloud.fire.pushmessage.PushAlarmMsg;
import com.smart.cloud.fire.retrofit.ApiStores;
import com.smart.cloud.fire.retrofit.AppClient;
import com.smart.cloud.fire.rxjava.ApiCallback;
import com.smart.cloud.fire.rxjava.SubscriberCallBack;
import com.smart.cloud.fire.utils.SharedPreferencesManager;
import com.smart.cloud.fire.utils.TimeFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Random;

import fire.cloud.smart.com.smartcloudfire.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2016/12/2.
 */
public class DemoIntentService extends GTIntentService {
    private CompositeSubscription mCompositeSubscription;
    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override
    public void onReceiveClientId(Context context, String cid) {
        String userID = SharedPreferencesManager.getInstance().getData(context,
                SharedPreferencesManager.SP_FILE_GWELL,
                SharedPreferencesManager.KEY_RECENTNAME);
        SharedPreferencesManager.getInstance().putData(context,SharedPreferencesManager.SP_FILE_GWELL,
                "CID",cid);//@@5.27存储个推cid
        PushManager.getInstance().bindAlias(this.getApplicationContext(),userID);
        goToServer(cid,userID);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        String msg = new String(gtTransmitMessage.getPayload());
            try {
                JSONObject dataJson = new JSONObject(msg);
                String alarmTime=dataJson.getString("alarmTime");
                //过滤30分钟前的报警
//                if(null!=alarmTime&&(System.currentTimeMillis()- TimeFormat.date2TimeStamp(alarmTime))>30*60*1000){
//                    return;
//                }
                int deviceType = dataJson.getInt("deviceType");
                switch (deviceType){
                    case 1://烟感
                    case 2://燃气
                    case 7://声光
                    case 10://水压@@4.28
                    case 8://手报
                    case 61://嘉德烟感
                    case 58://移动烟感
                        String message = null;
                        int alarmType = dataJson.getInt("alarmType");
                        switch (deviceType){
                            case 61:
                            case 58:
                            case 1:
                                if(alarmType==202) {
                                    message="发生烟雾报警";
                                }else if(alarmType==14){
                                    message="该设备已被拆除";
                                }else if(alarmType==15||alarmType==20){
                                    message="发生防拆恢复报警";
                                }else if(alarmType==67){
                                    message="发生自检报警";
                                }else if(alarmType==69){
                                    message="发生报警恢复";
                                }else if(alarmType==102){
                                    message="发生烟雾报警恢复";
                                }else if(alarmType==103){
                                    message="发生温度报警";
                                }else if(alarmType==104){
                                    message="发生温度报警恢复";
                                }else if(alarmType==105){
                                    message="发生烟雾低电量报警";
                                }else if(alarmType==106){
                                    message="发生烟雾低电量报警恢复";
                                }else if(alarmType==107){
                                    message="发生低电量报警";
                                }else if(alarmType==108){
                                    message="发生低电量报警恢复";
                                }else if(alarmType==109){
                                    message="发生烟雾故障报警";
                                }else if(alarmType==110){
                                    message="发生烟雾故障报警恢复";
                                }else if(alarmType==111){
                                    message="发生温湿度故障报警";
                                }else if(alarmType==112){
                                    message="发生温湿度故障报警恢复";
                                }else if(alarmType==113){
                                    message="发生手动报警";
                                }else if(alarmType==193){
                                    message="电量低，请更换电池";
                                }else if(alarmType==194){
                                    message="低电压已恢复";
                                }else{
                                    message="发生未知类型报警";
                                }
                                break;
                            case 2:
                                message="燃气发生泄漏";
                                break;
                            case 7:
                                message="声光发出报警";
                                break;
                            case 8:
                                message="手动报警";
                                break;
                            case 10://@@4.28
                                if(alarmType==218) {
                                    message="发生高水压报警";
                                }else if(alarmType==209){
                                    message="发生低水压报警";
                                }else{
                                    message="电量低，请更换电池";
                                }
                                break;
                        }
                        PushAlarmMsg mPushAlarmMsg = jsJson(dataJson);
                        Random random1 = new Random();
                        showDownNotification(context,message,mPushAlarmMsg,random1.nextInt(),AlarmActivity.class);
                        Intent intent1 = new Intent(context, AlarmActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.putExtra("mPushAlarmMsg",mPushAlarmMsg);
                        intent1.putExtra("alarmMsg",message);
                        context.startActivity(intent1);
                        break;
                    case 5://电气
                        PushAlarmMsg pushAlarmMsg1 = jsJson(dataJson);
                        int alarmFamily = pushAlarmMsg1.getAlarmFamily();
                        int alarmType36 = pushAlarmMsg1.getAlarmType();
                        String alarmMsg = null;
                        switch (alarmFamily){
                            case 43://电气报警
                                alarmMsg = "电气探测器发出：过压报警";
                                if(alarmType36!=0){
                                    alarmMsg += "，报警值:"+alarmType36+"V";
                                }
                                break;
                            case 36:
                                switch (alarmType36){
                                    case 1:
                                        alarmMsg = "电气探测器发出：漏电流故障报警";
                                        break;
                                    case 2:
                                        alarmMsg = "电气探测器发出：温度故障报警";
                                        break;
                                    case 3:
                                        alarmMsg = "电气探测器发出：供电中断报警";
                                        break;
                                    case 4:
                                        alarmMsg = "电气探测器发出：错相报警";
                                        break;
                                    case 5:
                                        alarmMsg = "电气探测器发出：缺相报警";
                                        break;
                                    case 6:
                                        alarmMsg = "电气探测器发出：电弧故障报警";
                                        break;
                                    case 7:
                                        alarmMsg = "电气探测器发出：负载故障报警";
                                        break;
                                    case 8:
                                        alarmMsg = "电气探测器发出：短路故障报警";
                                        break;
                                    case 9:
                                        alarmMsg = "电气探测器发出：断路故障报警";
                                        break;
                                    case 10:
                                        alarmMsg = "电气探测器发出：故障报警";
                                        break;
                                }
                                break;
                            case 45://电气报警
                                alarmMsg = "电气探测器发出：过流报警";
                                if(alarmType36!=0){
                                    alarmMsg += "，报警值:"+alarmType36+"A";
                                }
                                break;
                            case 44://欠压报警
                                alarmMsg = "电气探测器发出：欠压报警";
                                if(alarmType36!=0){
                                    alarmMsg += "，报警值:"+alarmType36+"V";
                                }
                                break;
                            case 46://漏电报警
                                alarmMsg = "电气探测器发出：漏电报警";
                                if(alarmType36!=0){
                                    alarmMsg += "，报警值:"+alarmType36+"mA";
                                }
                                break;
                            case 47://电气报警
                                alarmMsg = "电气探测器发出：温度报警";
                                if(alarmType36!=0){
                                    alarmMsg += "，报警值:"+alarmType36+"℃";
                                }
                                break;
                            case 48://分闸报警@@6.28
                                int alarmType6 = pushAlarmMsg1.getAlarmType();
//                                if(alarmType6!=0){
                                    alarmMsg = "电气探测器发出：分闸报警";
//                                }
                                break;
                            case 49://短路报警
                                alarmMsg = "电气探测器发出：短路报警";
                                break;
                            case 50://过热报警
                                alarmMsg = "电气探测器发出：过热报警";
                                break;
                            case 51:
                                alarmMsg = "电气探测器发出：分闸报警";
                                break;
                            default:
                                break;
                        }
                        Random random = new Random();
                        showDownNotification(context,alarmMsg,pushAlarmMsg1,random.nextInt(),AlarmActivity.class);
                        Intent intent2 = new Intent(context, AlarmActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent2.putExtra("mPushAlarmMsg",pushAlarmMsg1);
                        intent2.putExtra("alarmMsg",alarmMsg);
                        context.startActivity(intent2);
                        break;
                    case 6://一键报警和报警回复
                        int alarmType1 = dataJson.getInt("alarmType");
                        if(alarmType1==3){
                            GetUserAlarm getUserAlarm = new GetUserAlarm();
                            getUserAlarm.setAddress(dataJson.getString("address"));
                            getUserAlarm.setAlarmSerialNumber(dataJson.getString("alarmSerialNumber"));
                            getUserAlarm.setAlarmTime(dataJson.getString("alarmTime"));
                            getUserAlarm.setAreaName(dataJson.getString("areaName"));
                            getUserAlarm.setCallerId(dataJson.getString("callerId"));
                            getUserAlarm.setInfo(dataJson.getString("info"));
                            getUserAlarm.setLatitude(dataJson.getString("latitude"));
                            getUserAlarm.setLongitude(dataJson.getString("longitude"));
                            getUserAlarm.setSmoke(dataJson.getString("smoke"));
                            getUserAlarm.setCallerName(dataJson.getString("callerName"));
                            Random random3 = new Random();
                            showDownNotification(context,"您收到一条紧急报警消息",getUserAlarm,random3.nextInt(),UserAlarmActivity.class);
                            Intent intent3 = new Intent(context, UserAlarmActivity.class);
                            intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent3.putExtra("mPushAlarmMsg",getUserAlarm);
                            context.startActivity(intent3);
                        }else{
                            DisposeAlarm disposeAlarm = new DisposeAlarm();
                            disposeAlarm.setAlarmType(alarmType1);
                            disposeAlarm.setPolice(dataJson.getString("police"));
                            disposeAlarm.setTime(dataJson.getString("time"));
                            disposeAlarm.setPoliceName(dataJson.getString("policeName"));
                            Random random4 = new Random();
                            showDownNotification(context,disposeAlarm.getPoliceName()+"警员已处理您的消息",null,random4.nextInt(),null);
                        }
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private PushAlarmMsg jsJson(JSONObject dataJson) throws JSONException {
        PushAlarmMsg mPushAlarmMsg = new PushAlarmMsg();
        mPushAlarmMsg.setAddress(dataJson.getString("address"));
        mPushAlarmMsg.setAlarmType(dataJson.getInt("alarmType"));
        mPushAlarmMsg.setAreaId(dataJson.getString("areaId"));
        mPushAlarmMsg.setLatitude(dataJson.getString("latitude"));
        mPushAlarmMsg.setLongitude(dataJson.getString("longitude"));
        mPushAlarmMsg.setName(dataJson.getString("name"));
        mPushAlarmMsg.setPlaceAddress(dataJson.getString("placeAddress"));
        mPushAlarmMsg.setIfDealAlarm(dataJson.getInt("ifDealAlarm"));
        mPushAlarmMsg.setPrincipal1(dataJson.getString("principal1"));
        mPushAlarmMsg.setPlaceType(dataJson.getString("placeType"));
        mPushAlarmMsg.setPrincipal1Phone(dataJson.getString("principal1Phone"));
        mPushAlarmMsg.setPrincipal2(dataJson.getString("principal2"));
        mPushAlarmMsg.setPrincipal2Phone(dataJson.getString("principal2Phone"));
        mPushAlarmMsg.setAlarmTime(dataJson.getString("alarmTime"));
        mPushAlarmMsg.setDeviceType(dataJson.getInt("deviceType"));
        mPushAlarmMsg.setAlarmFamily(dataJson.getInt("alarmFamily"));
        try{
            JSONObject jsonObject =  dataJson.getJSONObject("camera");
            if(jsonObject!=null) {
                PushAlarmMsg.CameraBean cameraBean = new PushAlarmMsg.CameraBean();
                cameraBean.setCameraId(jsonObject.getString("cameraId"));
                cameraBean.setCameraPwd(jsonObject.getString("cameraPwd"));
                mPushAlarmMsg.setCamera(cameraBean);
            }
        }catch (Exception e){
        }
        mPushAlarmMsg.setMac(dataJson.getString("mac"));
        return mPushAlarmMsg;
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {
        if(b){
            MyApp.app.setPushState("Online");
        }else{
            MyApp.app.setPushState("Offline");
        }
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {
        System.out.print(gtCmdMessage);
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    private void goToServer(String cid,String userId){
        ApiStores apiStores = AppClient.retrofit(ConstantValues.SERVER_PUSH).create(ApiStores.class);
        Observable observable = apiStores.bindAlias( userId,cid,"scfire");
        addSubscription(observable,new SubscriberCallBack<>(new ApiCallback<HttpError>() {
            @Override
            public void onSuccess(HttpError model) {
                MyApp.app.setPushState(model.getState());
            }

            @Override
            public void onFailure(int code, String msg) {
            }

            @Override
            public void onCompleted() {
//                stopSelf();
            }
        }));
    }

    private void addSubscription(Observable observable, Subscriber subscriber) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    @SuppressWarnings("deprecation")
    private void showDownNotification(Context context, String message, Serializable mPushAlarmMsg, int id, Class clazz){
        NotificationCompat.Builder m_builder = new NotificationCompat.Builder(context);
        m_builder.setContentTitle(message); // 主标题

        //从系统服务中获得通知管理器
        NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //具体的通知内容

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher); // 将PNG图片转
        m_builder.setLargeIcon(icon);

        m_builder.setSmallIcon(R.mipmap.ic_launcher); //设置小图标
        m_builder.setWhen(System.currentTimeMillis());
        m_builder.setAutoCancel(true);
        if(clazz!=null){
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//设置提示音
            m_builder.setSound(uri);
            m_builder.setContentText("点击查看详情"); //设置主要内容
            //通知消息与Intent关联
            Intent it=new Intent(context,clazz);
            it.putExtra("mPushAlarmMsg",mPushAlarmMsg);
            it.putExtra("alarmMsg",message);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent contentIntent=PendingIntent.getActivity(context, id, it, PendingIntent.FLAG_CANCEL_CURRENT);
            m_builder.setContentIntent(contentIntent);
        }
        //执行通知
        nm.notify(id, m_builder.build());
    }
}
