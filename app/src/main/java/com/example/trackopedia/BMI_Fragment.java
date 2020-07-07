package com.example.trackopedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class BMI_Fragment extends Fragment {

    TextView bmi,status;
    SharedPreferences sp;
    int age,weight,height;
    String gender,uid;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bmi_layout, container, false);

        bmi= (TextView)v. findViewById(R.id.sfl_bmi);
        status= (TextView)v. findViewById(R.id.sfl_status);

        sp = getActivity().getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");
        age= Integer.parseInt(sp.getString("age",""));
        height= Integer.parseInt(sp.getString("height",""));
        weight= Integer.parseInt(sp.getString("weight",""));
        gender=sp.getString("gender","");


        Calculate_Intake ci=new Calculate_Intake(age,weight,height,gender);
        String t[]=ci.calculate_all().split("\\*");
        String temp[]=ci.bmi().split("\\*");
        String s = "<font color='#E3746D'>BMI: </font>" + "<font color='#FFFFFF'>"+temp[0]+"</font>";
        String s1 = "<font color='#E3746D'>Status: </font>" + "<font color='#FFFFFF'>"+temp[1]+"</font>";
        bmi.setText(Html.fromHtml(s));
        status.setText(Html.fromHtml(s1));

        return v;
    }
}
