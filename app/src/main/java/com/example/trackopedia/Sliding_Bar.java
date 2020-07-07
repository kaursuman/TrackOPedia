package com.example.trackopedia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Slide;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Sliding_Bar extends AppCompatActivity {

    private ViewPager myViewPager;
    private LinearLayout myLinearLayout;
    private ImageView[] myDots;
    private SliderAdapter mySliderAdapter;
    private Timer timer;
    private int current_position = 0;
    private List<Slide> slideList = new ArrayList<>();
    private int custom_position =0;
    private Button myNextButton;
    private int myCurrentPage;
    private PagerAdapter adapter;

    private static int SPLASH_TIME_OUT = 17000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding__bar);

        myViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        myLinearLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        //myDots = (TextView[]) findViewById(R.id.textViewDots);

        myNextButton = (Button) findViewById(R.id.btnNext);
        mySliderAdapter = new SliderAdapter(this);

        myViewPager.setAdapter(mySliderAdapter);
        addDotIndicator(custom_position++);
        createSlideShow();

        myViewPager.addOnPageChangeListener(viewListener);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run() {
                Intent homeIntent = new Intent(Sliding_Bar.this, LoginActivity.class);
                startActivity(homeIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }


    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position>2)
                position = 0;
            addDotIndicator(position++);
            myCurrentPage = position;
            if(position ==0 || position ==1)
            {
                myNextButton.setVisibility(View.INVISIBLE);
            }
            else
            {
                myNextButton.setVisibility(View.VISIBLE);
                myNextButton.setEnabled(true);
                myNextButton.setText("Finish");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private void createSlideShow()
    {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable()
        {
            @Override
            public void run() {
                if(current_position == slideList.size())
                    current_position =0;
                myViewPager.setCurrentItem(current_position++,true);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                handler.post(runnable);

            }
        },1500,4500);
    }

    private void addDotIndicator(int position){
        if(myLinearLayout.getChildCount()>0)
            myLinearLayout.removeAllViews();
        ImageView myDots[] = new ImageView[3];
        for (int i = 0; i < myDots.length; i++)
        {
            myDots[i] = new ImageView(this);
            if(i==position)
                myDots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.active_dot));
            else
                myDots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.inactive_dot));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4,0,4,0);
            myLinearLayout.addView(myDots[i],layoutParams);
        }
    }
}

