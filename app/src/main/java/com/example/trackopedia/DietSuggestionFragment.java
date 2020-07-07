package com.example.trackopedia;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DietSuggestionFragment extends Fragment {

    RelativeLayout ray;
    SharedPreferences sp;
    int age, weight, height;
    String gender, uid;
    ArrayList<String> data;
    ListView list;
    int tprot = 0, tfats = 0, tcarbs = 0, cprot = 0, cfats = 0, ccarbs = 0;
    TextView bmi, status, intake;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    Date d;
    Dialog mDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.diet_suggestion, container, false);

        sp = getActivity().getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        age = Integer.parseInt(sp.getString("age", ""));
        height = Integer.parseInt(sp.getString("height", ""));
        weight = Integer.parseInt(sp.getString("weight", ""));
        gender = sp.getString("gender", "");

        mDialog = new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);


        ray = (RelativeLayout) v.findViewById(R.id.sray);
        list = (ListView) v.findViewById(R.id.slist);
        bmi = (TextView) v.findViewById(R.id.sfl_bmi);
        status = (TextView) v.findViewById(R.id.sfl_status);
        intake = (TextView) v.findViewById(R.id.sfl_intake);

        Calculate_Intake ci = new Calculate_Intake(age, weight, height, gender);
        String t[] = ci.calculate_all().split("\\*");
        tprot = Integer.parseInt(t[0]);
        tfats = Integer.parseInt(t[1]);
        tcarbs = Integer.parseInt(t[2]);
        String temp[] = ci.bmi().split("\\*");
        String s = "<font color='#E3746D'>BMI: </font>" + "<font color='#FFFFFF'>" + temp[0] + "</font>";
        String s1 = "<font color='#E3746D'>Status: </font>" + "<font color='#FFFFFF'>" + temp[1] + "</font>";
        bmi.setText(Html.fromHtml(s));
        status.setText(Html.fromHtml(s1));

        d = new Date();
        new suggest().execute(uid, "" + tprot, "" + tfats, "" + tcarbs, sdf.format(d.getTime()));

        return v;

    }


    public class suggest extends AsyncTask<String, JSONObject, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.suggest(params[0], Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]), params[4]);
                JSONParser jp = new JSONParser();
                a = jp.getString(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (isAdded()) {
                if (s.contains("no***")) {
                    mDialog.dismiss();
                    Snackbar.make(ray, "There are No Foods Added!", Snackbar.LENGTH_SHORT);
                    String s1 = "<font color='#E3746D'>Consumed: </font>" + "<font color='#FFFFFF'>" + s.replace("no***", "") + " cal</font>";
                    intake.setText(Html.fromHtml(s1));
                } else {
                    if (s.contains("*")) {
                        data = new ArrayList<String>();
                        String temp[] = s.split("\\$");
                        String s1 = "<font color='#E3746D'>Consumed: </font>" + "<font color='#FFFFFF'>" + temp[1] + " cal</font>";
                        intake.setText(Html.fromHtml(s1));
                        String temp1[] = temp[0].split("\\#");
                        for (int i = 0; i < temp1.length; i++) {
                            data.add(temp1[i]);
                        }

                        Adapter adapt = new Adapter(getActivity(), data);
                        list.setAdapter(adapt);
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
                }

            } else {
                d = new Date();
                new suggest().execute(uid, "" + tprot, "" + tfats, "" + tcarbs, sdf.format(d.getTime()));
            }
        }
    }


    public class Adapter extends ArrayAdapter<String> {
        Context con;
        ArrayList<String> dataset;

        public Adapter(Context context, ArrayList<String> data) {
            super(context, R.layout.list_item, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.list_item, null, true);
            LinearLayout lay = (LinearLayout) v.findViewById(R.id.linear_lay);
            final String temp[] = dataset.get(position).split("\\*");
            ImageView img = (ImageView) v.findViewById(R.id.li_img);
            TextView name, carbo, prot, fats;
            name = (TextView) v.findViewById(R.id.li_name);
            carbo = (TextView) v.findViewById(R.id.li_c);
            prot = (TextView) v.findViewById(R.id.li_p);
            fats = (TextView) v.findViewById(R.id.li_f);

            name.setText(temp[0]);
            carbo.setText("Carbohydrates: " + temp[1] + " cal");
            prot.setText("Protiens: " + temp[2] + " cal");
            fats.setText("Fats: " + temp[3] + " cal");

            if (temp[4].compareTo("Meal") == 0) {
                img.setImageResource(R.drawable.meal);
            } else if (temp[4].compareTo("Fruit") == 0) {
                img.setImageResource(R.drawable.fruit);
            } else if (temp[4].compareTo("Beverage") == 0) {
                img.setImageResource(R.drawable.beverage);
            } else if (temp[4].compareTo("Snack") == 0) {
                img.setImageResource(R.drawable.snack);
            } else if (temp[4].compareTo("Desert") == 0) {
                img.setImageResource(R.drawable.desert);
            } else if (temp[4].compareTo("Other") == 0) {
                img.setImageResource(R.drawable.other);
            }

            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.co.in/search?q=Recipe of " + temp[0]));
                    startActivity(browserIntent);
                }
            });

            return v;
        }
    }


}
