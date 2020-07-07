package com.example.trackopedia;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private TextInputLayout tilUid,tilPwd;
    EditText user, pass;
    LinearLayout ray;
    Dialog mDialog;
    TextView tv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
        String uid = sp.getString("uid", "");


        mDialog = new Dialog(LoginActivity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.CYAN));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        tilUid=findViewById(R.id.lid);
        tilPwd=findViewById(R.id.lp);

        stopService(new Intent(LoginActivity.this, BackgroundService.class));
        startService(new Intent(LoginActivity.this, BackgroundService.class));

        if (uid.compareTo("") != 0) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            setContentView(R.layout.login_lay);
            ray = (LinearLayout) findViewById(R.id.lray);
            user = (EditText) findViewById(R.id.home_id);
            pass = (EditText) findViewById(R.id.home_pass);
            tv1 = findViewById(R.id.textView1);
            tv1.setPaintFlags(tv1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    public void Login_Click(View v) {
        if (user.getText().toString().compareTo("") != 0 && pass.getText().toString().compareTo("") != 0) {
            if (user.getText().toString().compareTo("") != 0) {
                if (isEmailValid(user.getText().toString())) {
                    if (pass.getText().toString().compareTo("") != 0) {
                        new loginasync().execute(user.getText().toString(), pass.getText().toString());
                    } else {
                        pass.requestFocus();
                        Snackbar snack = Snackbar.make(v, "Enter Password!", Snackbar.LENGTH_SHORT);
                        View vs = snack.getView();
                        TextView txt = (TextView) vs.findViewById(com.google.android.material.R.id.snackbar_text);
                        txt.setTextColor(Color.RED);
                        snack.show();
                    }
                } else {
                    user.requestFocus();
                    Snackbar snack = Snackbar.make(v, "Invalid Email!", Snackbar.LENGTH_SHORT);
                    View vs = snack.getView();
                    TextView txt = (TextView) vs.findViewById(com.google.android.material.R.id.snackbar_text);
                    txt.setTextColor(Color.RED);
                    snack.show();
                }
            } else {
                user.requestFocus();
                Snackbar snack = Snackbar.make(v, "Enter Email!", Snackbar.LENGTH_SHORT);
                View vs = snack.getView();
                TextView txt = (TextView) vs.findViewById(com.google.android.material.R.id.snackbar_text);
                txt.setTextColor(Color.RED);
                snack.show();
            }
        } else {
            Snackbar snack = Snackbar.make(v, "Enter Email & Password to Proceed!", Snackbar.LENGTH_SHORT);
            View vs = snack.getView();
            TextView txt = (TextView) vs.findViewById(com.google.android.material.R.id.snackbar_text);
            txt.setTextColor(Color.RED);
            snack.show();
        }
    }

    public void Reg_Click(View v) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        user.setText("");
        pass.setText("");
    }


    public class loginasync extends AsyncTask<String, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String check = "false";
            RestAPI api = new RestAPI();

            try {
                JSONObject json = api.login(params[0], params[1]);
                JSONParser jp = new JSONParser();
                check = jp.getString(json);
            } catch (Exception e) {
                check = e.getMessage();
            }
            return check;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.contains("*")) {
                String temp[] = result.split("\\*");

                editor = sp.edit();
                editor.putString("uid", user.getText().toString());
                editor.putString("age", temp[0]);
                editor.putString("height", temp[2]);
                editor.putString("weight", temp[1]);
                editor.putString("gender", temp[3]);
                editor.commit();

                DB db = new DB(LoginActivity.this);
                db.open();
                Boolean res = db.checkemail(user.getText().toString());
                db.close();
                if (res) {
//                    user.setError("Email Already Exists!");
//                    user.requestFocus();
                } else {
                    DB db1 = new DB(LoginActivity.this);
                    db1.open();

                    Boolean ans = db1.register("", temp[0], temp[2], temp[1], temp[3], "", user.getText().toString(), pass.getText().toString());
                    db1.close();

//                    if (ans) {
//                        Toast.makeText(LoginActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }

                }

                stopService(new Intent(LoginActivity.this, BackgroundService.class));
                startService(new Intent(LoginActivity.this, BackgroundService.class));


                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                mDialog.dismiss();
            } else if (result.compareTo("false") == 0) {
                user.setText("");
                pass.setText("");
                user.requestFocus();
                Snackbar snack = Snackbar.make(ray, "Authentication Failed!", Snackbar.LENGTH_SHORT);
                View vs = snack.getView();
                TextView txt = (TextView) vs.findViewById(com.google.android.material.R.id.snackbar_text);
                txt.setTextColor(Color.RED);
                snack.show();
                mDialog.dismiss();
            } else {
                if (result.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this);
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
                    Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
