package com.example.trackopedia;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment {

    TableLayout ray;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    PieChart Piechart;
    List<PieEntry> pieentries;

    int age,weight,height;
    String gender,uid;


    SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
    Date d;

    int tprot=0,tfats=0,tcarbs=0,cprot=0,cfats=0,ccarbs=0;

    TableRow tbhorizontal;
    Button pbtn,fbtn,cbtn,restbtn;

    Dialog mDialog;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.user_home,container,false);

        sp = getActivity().getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");


        mDialog = new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);


        ray= (TableLayout)v.findViewById(R.id.homeray);

        Piechart = (PieChart)v.findViewById(R.id.piechart);
        Piechart.getDescription().setText("");
        Piechart.getDescription().setTextSize(10);

        tbhorizontal= (TableRow)v.findViewById(R.id.tbhorizontal);

        pbtn= (Button)v.findViewById(R.id.btnp);
        fbtn= (Button)v.findViewById(R.id.btnf);
        cbtn= (Button)v.findViewById(R.id.btnc);
        restbtn= (Button)v.findViewById(R.id.btnrest);

        age= Integer.parseInt(sp.getString("age",""));
        height= Integer.parseInt(sp.getString("height",""));
        weight= Integer.parseInt(sp.getString("weight",""));
        gender=sp.getString("gender","");

        Calculate_Intake ci=new Calculate_Intake(age,weight,height,gender);
        String t[] = ci.calculate_all().split("\\*");
        tprot= Integer.parseInt(t[0]);
        tfats= Integer.parseInt(t[1]);
        tcarbs= Integer.parseInt(t[2]);

        d=new Date();
        new getlist().execute(sdf.format(d.getTime()),uid);


        return v;

    }



    public void pie()
    {
        int ttotal=tprot+tfats+tcarbs;
        int ctotal=cprot+cfats+ccarbs;
        int diff=ttotal-ctotal;

        float wp,wf,wc,wrest;

        if(diff<=0)
        {
            diff=0;
            wp=0;wf=0;wc=0;wrest=1;
        }
        else
        {
            wrest=diff;
            wp= Math.abs(ctotal-cprot);
            wf= Math.abs(ctotal-cfats);
            wc= Math.abs(ctotal-ccarbs);
        }

        set_buttons(wp,wf,wc,wrest);
        pieentries=new ArrayList<PieEntry>();
        pieentries.add(new PieEntry(ctotal, "Consumed"));
        pieentries.add(new PieEntry(diff, "Needed"));

        PieDataSet pds=new PieDataSet(pieentries,"");
        pds.setValueTextSize(10);
        pds.setValueTextColor(Color.BLACK);
        pds.setSliceSpace(3);
        int[] clr = new int[]{getResources().getColor(R.color.greenbright), Color.WHITE};
        pds.setColors(clr);

        PieData pd=new PieData(pds);
        Piechart.setHoleRadius(80);
        Piechart.setHoleColor(getResources().getColor(R.color.black));
        Piechart.setDrawHoleEnabled(true);
        Piechart.setData(pd);
        Piechart.setCenterTextSize(20);
        Piechart.setFitsSystemWindows(true);
        Piechart.offsetLeftAndRight(3);
        Piechart.setEntryLabelColor(Color.BLACK);
        String s = "<font color='#E3746D'>Consumed: </font>" + "<font color='#FFFFFF'>"+ctotal+" cal</font>";
        String s1 = "<font color='#E3746D'>Needed: </font>" + "<font color='#FFFFFF'>"+diff+" cal</font>";
        String f=s+"<br>"+s1;
        Piechart.setCenterText(Html.fromHtml(f));
        Piechart.setDrawSlicesUnderHole(true);
        Piechart.setSoundEffectsEnabled(true);
        Piechart.invalidate();

        Legend l=Piechart.getLegend();
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setTextColor(Color.WHITE);
        l.setEnabled(false);

        ray.setVisibility(View.VISIBLE);
    }


    public class getlist extends AsyncTask<String, JSONObject, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cprot=0;
            cfats=0;
            ccarbs=0;
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String a="back";
            RestAPI api=new RestAPI();
            try {
                JSONObject json=api.getconsumed(params[0],params[1]);
                JSONParser jp=new JSONParser();
                a=jp.getString(json);
            } catch (Exception e) {
                a=e.getMessage();
            }

            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (isAdded()) {


                if (s.compareTo("no") == 0) {
                    mDialog.dismiss();
                    pie();
                } else if (s.contains("*")) {
                    String temp[] = s.split("\\#");
                    for (int i = 0; i < temp.length; i++) {
                        String temp1[] = temp[i].split("\\*");
                        cprot += Integer.parseInt(temp1[1]);
                        cfats += Integer.parseInt(temp1[2]);
                        ccarbs += Integer.parseInt(temp1[3]);
                    }
                    mDialog.dismiss();
                    pie();

                } else {
                    if (s.contains("Unable to resolve host")) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                        ad.setTitle("Unable to Connect!");
                        ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                        ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        ad.show();
                        mDialog.dismiss();
                    } else {
                        mDialog.dismiss();
                        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    }
                }

            }else {

                d=new Date();
                new getlist().execute(sdf.format(d.getTime()),uid);
            }
        }
    }

    public void set_buttons(float w1,float w2,float w3,float w4)
    {
        TableRow.LayoutParams p=new TableRow.LayoutParams(0,200,w1);
        pbtn.setLayoutParams(p);

        TableRow.LayoutParams f=new TableRow.LayoutParams(0,200,w2);
        fbtn.setLayoutParams(f);

        TableRow.LayoutParams c=new TableRow.LayoutParams(0,200,w3);
        cbtn.setLayoutParams(c);

        TableRow.LayoutParams r=new TableRow.LayoutParams(0,200,w4);
        restbtn.setLayoutParams(r);

        if(w1==0.0)
        {
            pbtn.setVisibility(View.GONE);
        }
        else
        {
            pbtn.setVisibility(View.VISIBLE);
        }

        if(w2==0.0)
        {
            fbtn.setVisibility(View.GONE);
        }
        else
        {
            fbtn.setVisibility(View.VISIBLE);
        }

        if(w3==0.0)
        {
            cbtn.setVisibility(View.GONE);
        }
        else
        {
            cbtn.setVisibility(View.VISIBLE);
        }

        if(w4==0.0)
        {
            restbtn.setVisibility(View.GONE);
        }
        else
        {
            restbtn.setVisibility(View.VISIBLE);
        }
    }




}
