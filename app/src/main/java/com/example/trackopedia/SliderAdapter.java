package com.example.trackopedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    //Arrays
    public int[] slide_images = {
            R.drawable.run7,
            R.drawable.exercse3,
            R.drawable.yoga1
    };

    public String[] slide_headings = {
            "TIP 1: RUNNING",
            "TIP 2: EXERCISE",
            "TIP 3: YOGA"
    };

    public String[] slide_description = {
            "A mix of very easy runs some days, faster tempo runs other " +
                    "days and intervals on days in between is the " +
                    "way to build muscle and burn calories!!",
            "About 150 to 250 minutes of moderate-intensity physical activity per week is " +
                    "likely to produce modest weight loss. That's roughly 22 to 35 minutes " +
                    "of exercise per day to lose weight.",
            "Yoga increases body awareness, relieves stress, reduces muscle tension, strain, " +
                    "and inflammation, sharpens attention and concentration, and " +
                    "calms and centers the nervous system"
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container,false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.imageViewSlides);
        TextView textHeadingView = (TextView) view.findViewById(R.id.textViewHeading);
        TextView textDescriptionView = (TextView) view.findViewById(R.id.textViewDescription);

        slideImageView.setImageResource(slide_images[position]);
        textHeadingView.setText(slide_headings[position]);
        textDescriptionView.setText(slide_description[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
