package com.khn.foodition;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MyApplication extends Application {

    private static MyApplication instance;
    private Context mContext;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mContext = getApplicationContext();
    }

    //디데이일 때 알림창 보내기
    public void showNoti(String message, String name) {
        NotificationCompat.Builder builder;
        NotificationManager manager;
        String CHANNEL_ID = "channel1";
        String CHANEL_NAME = "Channel1";

        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //버전 오레오 이상일 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }
        //알림창 제목
        builder.setContentTitle("Foodition");
        //알림창 메시지
        builder.setContentText(message);
        //알림창 아이콘
        builder.setSmallIcon(R.mipmap.foodition_icon);

        // 푸드페이지로 넘어가는 PendingIntent 설정
        Intent intentEvent = new Intent(mContext, Foodition_FoodPage_Activity.class);
        intentEvent.putExtra("Eventname", name); // 메시지에 해당하는 아이템 이름 전달
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intentEvent, PendingIntent.FLAG_UPDATE_CURRENT);

        // PendingIntent 설정
        builder.setContentIntent(pendingIntent);
        //알림창 실행
        Notification notification = builder.build();
        manager.notify(1, notification);
    }
}
