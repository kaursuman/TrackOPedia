package com.example.trackopedia;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

public class ProfileFragment extends Fragment {


    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String uid;
    EditText name, email, cont, age, height, weight, gender;
    Button update;
    Dialog mDialog;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile, container, false);

        sp = getActivity().getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");

        mDialog = new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        name = (EditText) v.findViewById(R.id.P_Name);
        email = (EditText) v.findViewById(R.id.P_Email);
        cont = (EditText) v.findViewById(R.id.P_Contact);
        age = (EditText) v.findViewById(R.id.P_Age);
        height = (EditText) v.findViewById(R.id.P_Height);
        weight = (EditText) v.findViewById(R.id.P_Weight);
        gender = (EditText) v.findViewById(R.id.P_Gender);
        update = (Button) v.findViewById(R.id.P_Submit);

        new getprofile().execute(uid);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cont.getText().toString().compareTo("") != 0) {
                    if (cont.getText().toString().length() == 10) {
                        if (age.getText().toString().compareTo("") != 0) {
                            int a = Integer.parseInt(age.getText().toString());

                            if (a > 0 && a < 120) {
                                if (height.getText().toString().compareTo("") != 0) {
                                    int h = Integer.parseInt(height.getText().toString());
                                    if (h >= 19 && h < 250) {
                                        if (weight.getText().toString().compareTo("") != 0) {
                                            int w = Integer.parseInt(weight.getText().toString());
                                            if (w > 0) {
                                                new updateprofile().execute(uid, cont.getText().toString(), age.getText().toString(), weight.getText().toString(), height.getText().toString());
                                            } else {
                                                weight.setError("Weight Should be greater then ZERO");
                                                weight.requestFocus();
                                            }
                                        } else {
                                            weight.setError("Enter Weight");
                                            weight.requestFocus();
                                        }
                                    } else {
                                        height.setError("Invalid Height");
                                        height.requestFocus();
                                    }
                                } else {
                                    height.setError("Enter Height");
                                    height.requestFocus();
                                }
                            } else {
                                age.setError("Invalid Age");
                                age.requestFocus();
                            }
                        } else {
                            age.setError("Enter Age");
                            age.requestFocus();
                        }
                    } else {
                        cont.setError("Contact should have 10 Digits");
                        cont.requestFocus();
                    }
                } else {
                    cont.setError("Enter Contact");
                    cont.requestFocus();
                }

            }
        });

        return v;
    }


    public class getprofile extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.getProfile(params[0]);
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


                if (s.contains("*")) {
                    String temp[] = s.split("\\*");
                    name.setText(temp[0]);
                    email.setText(temp[1]);
                    cont.setText(temp[2]);
                    age.setText(temp[3]);
                    weight.setText(temp[4]);
                    height.setText(temp[5]);
                    gender.setText(temp[6]);
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
                new getprofile().execute(uid);

            }

        }
    }


    public class updateprofile extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.updateprofile(params[0], params[1], params[2], params[3], params[4]);
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
            if (s.compareTo("true") == 0) {
                editor = sp.edit();
                editor.putString("age", age.getText().toString());
                editor.putString("height", height.getText().toString());
                editor.putString("weight", weight.getText().toString());
                editor.commit();

                DB db = new DB(getActivity());
                db.open();
                db.updateData(email.getText().toString(), age.getText().toString(), height.getText().toString()
                        , weight.getText().toString(), cont.getText().toString());
                db.close();




                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }, 1500);

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
    }
}
