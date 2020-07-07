package com.example.trackopedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Timer;
import java.util.TimerTask;


public class FootStepsMonitoringFragment extends Fragment {

    TableRow nodata;
    TextView steps;
    String uid;
    SharedPreferences sp;
    Timer timer;
    TimerTask task;
    Handler hand=new Handler();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.foot_steps_layout,container,false);
        sp=getActivity().getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");


        steps= (TextView) v.findViewById(R.id.steps);
        nodata= (TableRow) v.findViewById(R.id.nodata);
        timer=new Timer();
        init_timer();
        timer.schedule(task,0,1000);
        return v;
    }

    public void init_timer()
    {
        task=new TimerTask() {
            @Override
            public void run() {
                hand.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            DB db = new DB(getActivity());
                            db.open();
                            String ans = db.getsteps(uid);
                            db.close();

                            if(ans.compareTo("no")!=0)
                            {
                                nodata.setVisibility(View.GONE);

                                String temp[]=ans.split("\\#");
                                for(int i=0;i<temp.length;i++)
                                {
                                    String temp1[]=temp[i].split("\\*");
                                    int fs= Integer.parseInt(temp1[1])/2;
                                    steps.setText("Todays Count: "+fs);
                                }

                                Fragment frag=new chart_fragment();
                                FragmentManager fm=getChildFragmentManager();
                                fm.beginTransaction().replace(R.id.frameid,frag).commit();
                            }
                            else
                            {
                                nodata.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (Exception e){}
                    }
                });
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try
        {
            timer.cancel();}
        catch (Exception e){}
    }


}
