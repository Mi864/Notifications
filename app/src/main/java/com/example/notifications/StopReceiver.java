package com.example.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class StopReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BROADCAST RECIEVER", "ON");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (MainActivity.mediaPlayer.isPlaying()) {
            MainActivity.mediaPlayer.stop();
            MainActivity.mediaPlayer.release();
            MainActivity.mediaPlayer = new MediaPlayer();
            notificationManager.cancel(1);
        }
    }
}
