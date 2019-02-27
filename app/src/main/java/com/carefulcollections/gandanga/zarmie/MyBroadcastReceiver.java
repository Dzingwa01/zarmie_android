package com.carefulcollections.gandanga.zarmie;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class MyBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, InviteService.class);
        Log.d("Check","Order received");
    }
}
