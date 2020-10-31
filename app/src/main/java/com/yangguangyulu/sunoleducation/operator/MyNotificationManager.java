package com.yangguangyulu.sunoleducation.operator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.yangguangyulu.sunoleducation.R;

import androidx.annotation.RequiresApi;

/**
 * Created by Tian on 17/10/21.
 */

public class MyNotificationManager {
    public static final String TAG = "MyNotificationManager";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initNotificationChannel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("sunoledu", "sunoledumodule"));

        NotificationChannel channel = new NotificationChannel("1", "阳光在线", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        channel.setShowBadge(true);
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Notification getNotification1(Context context) {
        Notification.Builder builder = new Notification.Builder(context, "1");

        return builder.setSmallIcon(R.mipmap.sun_edu_icon)
                .setContentTitle("阳光在线")
                .setContentText("系统正在运行中").build();

    }
}