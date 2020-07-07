package com.example.trackopedia;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {

    float x, y, z, stepcount = 0;
    float lastx, lasty, lastz;
    SharedPreferences sp;
    String uid;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    Timer timer;
    TimerTask task;
    Handler hand = new Handler();
    Boolean takevalue = false;


    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    Date d;
    String time[] = new String[]{"09:00", "13:00", "17:00", "22:00"};
    String status[] = new String[]{"Breakfast", "Lunch", "Evening Snacks", "Dinner"};
    String wishing[] = new String[]{"Morning", "Afternoon", "Evening", "Evening"};
    Boolean found = false;

    Timer timer1;
    TimerTask task1;
    Handler hand1 = new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static String CHANNEL_ID = "TrackOPedia";

    public static void createNotificationChannel(@NonNull Context context, @NonNull String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Smart health";
            String description = "Smart health app";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.d("NotificationLog", "NotificationManagerNull");
            }
        }
    }
    private void startServices() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        createNotificationChannel(getApplicationContext(), CHANNEL_ID);

        Notification notification = new NotificationCompat
                .Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Your app is running in the background")
                .setContentIntent(pendingIntent)
                //.setPriority(Notification.PRIORITY_MIN)
                .setAutoCancel(true)
                .build();

        startForeground(123, notification);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        startServices();
        sp = getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
//        Toast.makeText(this, "Inside onCreate In Background", Toast.LENGTH_SHORT).show();
        acce();

        timer = new Timer();
        init_timer();
        timer.schedule(task, 0, 500);

        timer1=new Timer();
        start_timer();
        timer1.schedule(task1,0,60000);

    }

    public void init_timer() {
        task = new TimerTask() {
            @Override
            public void run() {
                hand.post(new Runnable() {
                    @Override
                    public void run() {
                        stepcount = 0;
                        int finalcount = 0;
                        takevalue = true;
                        float t_lx = lastx;
                        float t_ly = lasty;
                        float t_nx = x;
                        float t_ny = y;

                        float xans = t_lx - t_nx;
                        float yans = t_ly - t_ny;

                        if (Math.abs(xans) >= 1.7 || Math.abs(yans) >= 1.7) {
                            stepcount++;
                            finalcount = (int) stepcount;

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                            Date d = new Date();

                            DB db = new DB(BackgroundService.this);
                            db.open();
                            db.update(sdf.format(d.getTime()), uid, "" + finalcount);
                            db.close();
//                            Toast.makeText(BackgroundService.this, finalcount+"", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
    }

    public void acce() {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        senSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor mySensor = event.sensor;

                if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];

                    if (takevalue) {
                        lastx = x;
                        lasty = y;
                        lastz = z;
                        takevalue = false;
                    }

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void start_timer()
    {
        task1=new TimerTask() {
            @Override
            public void run() {
                hand1.post(new Runnable() {
                    @Override
                    public void run() {
                        uid = sp.getString("uid","");
                        if(uid.compareTo("")!=0) {
                            if (!found) {
                                for (int i = 0; i < time.length; i++) {
                                    d = new Date();
                                    if (sdf.format(d.getTime()).compareTo(time[i]) == 0) {
                                        found = true;
                                        send_notification(i);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        };
    }

    public void send_notification(int pos) {
        Intent notificationIntent = new Intent(BackgroundService.this, Reminder_Page.class);
        notificationIntent.putExtra("nid", pos);

        PendingIntent contentIntent = PendingIntent.getActivity(BackgroundService.this, 100, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(BackgroundService.this);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.app_icon)
                .setTicker("Smart Health Monitoring")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(status[pos] + " Reminder!")
                .setContentText("Good " + wishing[pos] + ", Its " + status[pos]);

        Notification n = builder.getNotification();
        nm.notify("cc", 100, n);
        found = false;
    }

}
