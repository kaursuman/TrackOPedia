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

public class WaterAlarmFragment extends Fragment {

    EditText agetxt,weighttxt,heighttxt;
    Button determine,edit;
    TextView ans,ans1;
    String uid,gender;
    int age,height,weight;
    SharedPreferences sp;
    DecimalFormat df=new DecimalFormat("#.0");

    int weights[]=new int[]{45,49,54,58,63,68,72,77,81,86,90,95,99,104,108,113};
    double w_intake[]=new double[]{1.9,2.1,2.3,2.5,2.7,2.9,3.1,3.3,3.5,3.7,3.9,4.1,4.3,4.5,4.7,4.9};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.water_alarm_layout,container,false);

        sp=getActivity().getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");
        agetxt= (EditText) v.findViewById(R.id.water_age);
        weighttxt= (EditText) v.findViewById(R.id.water_weight);
        heighttxt= (EditText) v.findViewById(R.id.water_height);
        determine= (Button) v.findViewById(R.id.water_determine);
        edit= (Button) v.findViewById(R.id.water_edit);
        ans= (TextView) v.findViewById(R.id.water_ans);
        ans1= (TextView) v.findViewById(R.id.water_ans1);

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

        cal_age();

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
                                            cal_age();
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

    public void cal_age()
    {
        double p_intake=0;
        if(age==0)
        {
            p_intake=0.8;
        }
        else if(age>=1 && age<=3)
        {
            p_intake=1.3;
        }
        else if(age>=4 && age<=8)
        {
            p_intake=1.7;
        }
        else if(age>=9 && age<=13 && gender.compareTo("Male")==0)
        {
            p_intake=2.4;
        }
        else if(age>=14 && age<=18 && gender.compareTo("Male")==0)
        {
            p_intake=3.3;
        }
        else if(age>=9 && age<=13 && gender.compareTo("Female")==0)
        {
            p_intake=2.1;
        }
        else if(age>=14 && age<=18 && gender.compareTo("Female")==0)
        {
            p_intake=2.3;
        }
        else if(age>18 && gender.compareTo("Male")==0)
        {
            p_intake=3.7;
        }
        else if(age>18 && gender.compareTo("Female")==0)
        {
            p_intake=2.7;
        }

        cal_weight(p_intake);
    }

    public void cal_weight(double p_intake)
    {
        for(int i=0;i<weights.length;i++)
        {
            if(i==0)
            {
                if(weight<=weights[i])
                {
                    final_cal(p_intake,w_intake[i]);
                }
            }
            else if(i==weights.length-1)
            {
                if(weight>weights[i])
                {
                    final_cal(p_intake,w_intake[i]);
                }
                else if(weight>weights[i-1] && weight<=weights[i])
                {
                    final_cal(p_intake,w_intake[i]);
                }
            }
            else
            {
                if(weight>weights[i-1] && weight<=weights[i])
                {
                    final_cal(p_intake,w_intake[i]);
                }
            }
        }
    }

    public void final_cal(double p_intake,double p1_intake)
    {
        double f_intake=(p_intake+p1_intake)/2;
        cal_bmi(f_intake);
    }

    public void cal_bmi(double f_intake)
    {
        double weight_lbs=weight*2.2046226218;
        double height_inches=height/2.54;

        double bmi_w=weight_lbs*0.45;
        double bmi_h=height_inches*0.025;
        double bmi_square=bmi_h*bmi_h;
        double ans_bmi=bmi_w/bmi_square;

        if(ans_bmi>=19.00 && ans_bmi<=25.00)
        {

            ans.setText(df.format(f_intake)+" L");
            ans1.setText("Daily Intake of Water");
        }
        else if(ans_bmi<19.00)
        {
            f_intake-=0.2;
            ans.setText(df.format(f_intake)+" L");
            ans1.setText("Daily Intake of Water");
        }
        else if(ans_bmi>=30.00)
        {
            f_intake+=1.0;
            ans.setText(df.format(f_intake)+" L");
            ans1.setText("Daily Intake of Water");
        }
        else if(ans_bmi>25.00)
        {
            f_intake+=0.2;
            ans.setText(df.format(f_intake)+" L");
            ans1.setText("Daily Intake of Water");
        }
    }
}
