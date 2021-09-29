package com.example.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button iniciar;
    TextView tvTiempo;

    CountDownTimer countDownTimer;
    long time = 5000;
    static MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciar  = findViewById(R.id.iniciar);
        tvTiempo = findViewById(R.id.tvTiempo);

        createNotificationChannel();
        iniciar.setOnClickListener(v -> iniciarConteo());
    }

    private void iniciarConteo() {

        countDownTimer = new CountDownTimer(time,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                String strTiempo = String.format(getResources().getConfiguration().locale,"%1d",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));

                tvTiempo.setText(strTiempo);

            }

            @Override
            public void onFinish() {

                Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

                AudioAttributes audioAttributes = new AudioAttributes.Builder().
                        setUsage(AudioAttributes.USAGE_ALARM).build();

                mediaPlayer.setAudioAttributes(audioAttributes);
                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

                try {
                    mediaPlayer.setDataSource(getBaseContext(), sonido);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                }

                Intent stopIntent = new Intent(getBaseContext(), StopReceiver.class);
                @SuppressLint("UnspecifiedImmutableFlag")
                PendingIntent stopPendingIntent =
                        PendingIntent.getBroadcast(getBaseContext(), 0, stopIntent,0);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), "Channel")
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                        .setContentTitle(getString(R.string.temporizador))
                        .setContentText(getString(R.string.tiempo_finalizado))
                        //.setSound(sonido) Este sonido se actualiza correctamente en API<26, en API>=26 se utiliza el sonido inicial fijado mediante setSound() en el Channel creado
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .addAction(android.R.drawable.ic_media_pause, getString(R.string.detener), stopPendingIntent)
                        .setOngoing(true)
                        .setUsesChronometer(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(1, builder.build());

                tvTiempo.setText("");

            }
        }.start();
    }

    private void createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// >= Android 8.0 26 O

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            CharSequence name   = "Fin Conteo";
            String description  = "Tiempo finalizado";
            int importance      = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("Channel", name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            notificationManager.createNotificationChannel(channel);
            //El sonido inicial no se puede cambiar

        }
    }

}