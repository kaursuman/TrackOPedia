package com.example.trackopedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try
        {
            SharedPreferences sp=context.getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
            String str=sp.getString("uid","");
            if(str.compareTo("")!=0) {
                Intent i = new Intent(context, BackgroundService.class);
                context.startService(i);
            }
        }
        catch (Exception e){}
    }
}
