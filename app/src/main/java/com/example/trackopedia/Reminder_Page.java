package com.example.trackopedia;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class Reminder_Page extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_page);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel("cc",100);

        Intent i=getIntent();
        int pos=i.getIntExtra("nid",0);

        ImageView img= (ImageView) findViewById(R.id.rem_img);
        if(pos==0)
        {
            img.setImageResource(R.drawable.b);
        }
        else if(pos==1)
        {
            img.setImageResource(R.drawable.l);
        }
        else if(pos==2)
        {
            img.setImageResource(R.drawable.e);
        }
        else if(pos==3)
        {
            img.setImageResource(R.drawable.d);
        }
    }
}
