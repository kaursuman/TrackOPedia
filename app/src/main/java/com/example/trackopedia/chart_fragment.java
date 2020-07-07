package com.example.trackopedia;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.example.trackopedia.*;
import java.util.ArrayList;
import java.util.List;


public class chart_fragment extends Fragment {
    String uid;
    SharedPreferences sp;

    LineChart Linechart;
    List<Entry> steps;
    String[] dates;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chart_fragment,container,false);
        sp =getActivity().getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");

        Linechart = (LineChart) v.findViewById(R.id.linechart);
        Linechart.getDescription().setText("Steps Charts");
        Linechart.getDescription().setTextSize(8);

        DB db = new DB(getActivity());
        db.open();
        String ans = db.getsteps(uid);
        db.close();

        if(ans.compareTo("no")!=0) {
            if(ans.contains("*")) {

                String temp[] = ans.split("\\#");
                steps = new ArrayList<Entry>();
                dates = new String[temp.length];
                for (int i = 0; i < temp.length; i++) {
                    String temp1[] = temp[i].split("\\*");
                    dates[i]=temp1[0];

                    int fs= Integer.parseInt(temp1[1])/2;
                    steps.add(new Entry(Float.parseFloat(i + "f"), Float.parseFloat(""+fs)));
                }
                drawline();

            }
        }

        return v;
    }

    public void drawline()
    {
        Linechart.clear();
        XAxis x = Linechart.getXAxis();
        x.setValueFormatter(null);
        try {
            LineDataSet ds = null;
            if (steps.size() > 0) {
                ds = new LineDataSet(steps, "Steps");
                ds.setCircleColor(getResources().getColor(R.color.black));
                ds.setLineWidth(3);
                ds.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ds.setValueTextSize(10);
                ds.setCircleRadius(5);
                ds.setColor(getResources().getColor(R.color.colorPrimary));
            }

            LineData ld = new LineData(ds);

            IAxisValueFormatter formatter=null;
            if(steps.size()>1)
            {
                formatter = new IAxisValueFormatter() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        int i = (int) value;
                        return dates[i];
                    }
                    @Override
                    public int getDecimalDigits() {
                        return 0;
                    }
                };
            }
            else
            {
                final String temp[]=new String[]{dates[0]," "};
                formatter = new IAxisValueFormatter() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if(value==-1.0)
                        {
                            return " ";
                        }
                        else
                        {
                            int i = (int) value;
                            return temp[i];
                        }
                    }

                    @Override
                    public int getDecimalDigits() {
                        return 0;
                    }
                };
            }

            XAxis xAxis = Linechart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            xAxis.setGridColor(Color.TRANSPARENT);
            xAxis.setValueFormatter(formatter);
            xAxis.setAvoidFirstLastClipping(true);

            YAxis left = Linechart.getAxisLeft();
            left.setAxisMinimum(0);
            YAxis right = Linechart.getAxisRight();
            right.setAxisMinimum(0);

            Linechart.getXAxis().mLabelWidth = 1;
            Linechart.setData(ld);
            Linechart.invalidate();
        }
        catch (Exception e)
        {
        }
    }
}
