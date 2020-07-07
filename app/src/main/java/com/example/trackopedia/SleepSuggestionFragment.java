package com.example.trackopedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;

public class SleepSuggestionFragment extends Fragment {

    EditText agetxt,weighttxt,heighttxt;
    Button determine,edit;
    TextView ans,ans1;
    String uid,gender;
    int age,height,weight;
    SharedPreferences sp;
    DecimalFormat df=new DecimalFormat("#.0");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.sleep_suggestion_layout, container, false);

        sp=getActivity().getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");
        agetxt= (EditText) v.findViewById(R.id.sleep_age);
        weighttxt= (EditText) v.findViewById(R.id.sleep_weight);
        heighttxt= (EditText) v.findViewById(R.id.sleep_height);
        determine= (Button) v.findViewById(R.id.sleep_determine);
        edit= (Button) v.findViewById(R.id.sleep_edit);
        ans= (TextView) v.findViewById(R.id.sleep_ans);
        ans1= (TextView) v.findViewById(R.id.sleep_ans1);

        DB db=new DB(getActivity());
        db.open();
        String ans=db.getdetails(uid);
        db.close();
        String temp[]=ans.split("\\*");
        gender=temp[0];
        age= Integer.parseInt(temp[1]);
        height= Integer.parseInt(temp[2]);
        weight= Integer.parseInt(temp[3]);

        agetxt.setText(""+age);
        weighttxt.setText(""+weight);
        heighttxt.setText(""+height);

        cal_sleeps();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agetxt.setEnabled(true);
                weighttxt.setEnabled(true);
                heighttxt.setEnabled(true);
            }
        });

        determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(agetxt.getText().toString().compareTo("")!=0 || weighttxt.getText().toString().compareTo("")!=0 || heighttxt.getText().toString().compareTo("")!=0)
                {
                    if(agetxt.getText().toString().compareTo("")!=0)
                    {
                        int a= Integer.parseInt(agetxt.getText().toString());

                        if(a<120)
                        {
                            if(heighttxt.getText().toString().compareTo("")!=0)
                            {
                                int h= Integer.parseInt(heighttxt.getText().toString());
                                if(h>=19 && h<250)
                                {
                                    if(weighttxt.getText().toString().compareTo("")!=0)
                                    {
                                        int w= Integer.parseInt(weighttxt.getText().toString());
                                        if(w>0)
                                        {
                                            age= Integer.parseInt(agetxt.getText().toString());
                                            height= Integer.parseInt(heighttxt.getText().toString());
                                            weight= Integer.parseInt(weighttxt.getText().toString());

                                            agetxt.setEnabled(false);
                                            heighttxt.setEnabled(false);
                                            weighttxt.setEnabled(false);
                                            cal_sleeps();
                                        }
                                        else
                                        {
                                            weighttxt.setError("Weight Should be greater then ZERO");
                                            weighttxt.requestFocus();
                                        }
                                    }
                                    else
                                    {
                                        weighttxt.setError("Enter Weight");
                                        weighttxt.requestFocus();
                                    }
                                }
                                else
                                {
                                    heighttxt.setError("Invalid Height");
                                    heighttxt.requestFocus();
                                }
                            }
                            else
                            {
                                heighttxt.setError("Enter Height");
                                heighttxt.requestFocus();
                            }
                        }
                        else
                        {
                            agetxt.setError("Invalid Age");
                            agetxt.requestFocus();
                        }
                    }
                    else
                    {
                        agetxt.setError("Enter Age");
                        agetxt.requestFocus();
                    }
                }
                else
                {
                    Snackbar snack = Snackbar.make(v, "Fill all the Details", Snackbar.LENGTH_SHORT);
                    View vs = snack.getView();
                    TextView txt = (TextView) vs.findViewById(com.google.android.material.R.id.snackbar_text);
                    txt.setTextColor(Color.RED);
                    snack.show();
                }
            }
        });

        return v;
    }

    public void cal_sleeps()
    {
        if(age==0)
        {
            cal_bmi(12,17);
        }
        else if(age>=1 && age<=2)
        {
            cal_bmi(11,14);
        }
        else if(age>=3 && age<=5)
        {
            cal_bmi(10,13);
        }
        else if(age>=6 && age<=13)
        {
            cal_bmi(9,11);
        }
        else if(age>=14 && age<=17)
        {
            cal_bmi(8,10);
        }
        else if(age>=18 && age<=25)
        {
            cal_bmi(7,9);
        }
        else if(age>=26 && age<=64)
        {
            cal_bmi(7,9);
        }
        else if(age>=65)
        {
            cal_bmi(7,8);
        }
    }

    public void cal_bmi(int min,int max)
    {
        double weight_lbs=weight*2.2046226218;
        double height_inches=height/2.54;

        double bmi_w=weight_lbs*0.45;
        double bmi_h=height_inches*0.025;
        double bmi_square=bmi_h*bmi_h;
        double ans_bmi=bmi_w/bmi_square;

        if(ans_bmi>=19.00 && ans_bmi<=25.00)
        {
            int f=min+max/2;
            ans.setText(f+" Hrs");
            ans1.setText("Daily Intake of Sleep");
        }
        else if(ans_bmi<19.00)
        {
            int f=max-1;
            ans.setText(f+" Hrs");
            ans1.setText("Daily Intake of Sleep");
        }
        else if(ans_bmi>=30.00)
        {
            int f=min-1;
            ans.setText(f+" Hrs");
            ans1.setText("Daily Intake of Sleep");
        }
        else if(ans_bmi>25.00)
        {
            int f=min;
            ans.setText(f+" Hrs");
            ans1.setText("Daily Intake of Sleep");
        }
    }
}
