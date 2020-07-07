package com.example.trackopedia;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DietMonitoringFragment extends Fragment {


    TableRow totcaltb;
    TextView totalcal;

    TextView date;
    ListView list;
    ArrayList<String> data;

    SharedPreferences sp;
    String uid;

    DatePickerDialog dpg;
    Calendar cal,now;

    FloatingActionButton floatingActionButton;

    SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");

    Dialog mDialog;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.diet_monitoring,container,false);

        sp = getActivity().getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid=sp.getString("uid","");

        mDialog=new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        floatingActionButton = (FloatingActionButton)v.findViewById(R.id.add_food_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(getActivity(),Add_Food.class);
                startActivity(i);
            }
        });

        date= (TextView)v. findViewById(R.id.fooddate);
        list= (ListView)v. findViewById(R.id.foodlist);
        totalcal= (TextView)v. findViewById(R.id.totalcal);
        totcaltb= (TableRow)v. findViewById(R.id.totalcaltb);

        now= Calendar.getInstance();
        now.set(Calendar.HOUR,0);
        now.set(Calendar.MINUTE,0);
        now.set(Calendar.SECOND,0);
        now.set(Calendar.MILLISECOND,0);
        String s="<font color='#E3746D'>Date: </font>"+"<font color='#FFFFFF'>"+sdf.format(now.getTime())+"</font>";
        date.setText(Html.fromHtml(s));

        cal= Calendar.getInstance();
        dpg=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                cal.set(Calendar.HOUR,0);
                cal.set(Calendar.MINUTE,0);
                cal.set(Calendar.SECOND,0);
                cal.set(Calendar.MILLISECOND,0);

                if(cal.before(now) || cal.equals(now)) {
                    String s = "<font color='#E3746D'>Date: </font>" + "<font color='#FFFFFF'>" + sdf.format(cal.getTime()) + "</font>";
                    date.setText(Html.fromHtml(s));
                    new getlist().execute(sdf.format(cal.getTime()),uid);
                }
                else
                {
                    Toast.makeText(getActivity(),"Date Exceeded Today's Date", Toast.LENGTH_SHORT).show();
                }
            }
        },cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpg.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpg.show();
            }
        });
        return v;

    }


    @Override
    public void onResume() {
        super.onResume();
        new getlist().execute(sdf.format(now.getTime()),uid);
    }

    public class getlist extends AsyncTask<String, JSONObject, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

//            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                if (s.compareTo("no") == 0) {
                    mDialog.dismiss();
                    list.setAdapter(null);
                    Snackbar.make(list, "There is No data for the above date!", Snackbar.LENGTH_SHORT).show();
                    totcaltb.setVisibility(View.GONE);
                } else if (s.contains("*")) {
                    int tot = 0;
                    String temp[] = s.split("\\#");
                    data = new ArrayList<String>();
                    for (int i = 0; i < temp.length; i++) {
                        String temp1[] = temp[i].split("\\*");
                        tot += Integer.parseInt(temp1[1]) + Integer.parseInt(temp1[2]) + Integer.parseInt(temp1[3]);
                        data.add(temp[i]);
                    }

                    Adapter adapt = new Adapter(getActivity(), data);
                    list.setAdapter(adapt);

                    String t = "<font color='#E3746D'>Total Intake: </font>" + "<font color='#262932'>" + tot + " cal</font>";
                    totalcal.setText(Html.fromHtml(t));
                    totcaltb.setVisibility(View.VISIBLE);
                    mDialog.dismiss();

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
                new getlist().execute(sdf.format(now.getTime()),uid);

            }
        }
    }


    public class Adapter extends ArrayAdapter<String>
    {

        Context con;
        ArrayList<String> dataset;
        public Adapter(Context context, ArrayList<String> data) {
            super(context, R.layout.food_dairy_listitem,data);
            con=context;
            dataset=data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v= LayoutInflater.from(con).inflate(R.layout.food_dairy_listitem,null,true);

            final ImageView img= (ImageView) v.findViewById(R.id.fd_img);
            TextView name= (TextView) v.findViewById(R.id.fd_name);
            TextView time= (TextView) v.findViewById(R.id.fd_time);
            TextView cal= (TextView) v.findViewById(R.id.fd_cal);
            TextView qunt= (TextView) v.findViewById(R.id.fd_quant);

            String temp[]=dataset.get(position).split("\\*");
            name.setText(temp[0]);
            time.setText(temp[6].substring(0,temp[6].length()-3));

            int tot= Integer.parseInt(temp[1])+ Integer.parseInt(temp[2])+ Integer.parseInt(temp[3]);
            cal.setText("Total Cal: "+tot+" cal");
            qunt.setText("Quantity: "+temp[7]);

            if(temp[4].compareTo("Meal")==0)
            {
                img.setImageResource(R.drawable.meal);
            }
            else if(temp[4].compareTo("Fruit")==0)
            {
                img.setImageResource(R.drawable.fruit);
            }
            else if(temp[4].compareTo("Beverage")==0)
            {
                img.setImageResource(R.drawable.beverage);
            }
            else if(temp[4].compareTo("Snack")==0)
            {
                img.setImageResource(R.drawable.snack);
            }
            else if(temp[4].compareTo("Desert")==0)
            {
                img.setImageResource(R.drawable.desert);
            }
            else if(temp[4].compareTo("Other")==0)
            {
                img.setImageResource(R.drawable.other);
            }
            return v;
        }
    }


}
