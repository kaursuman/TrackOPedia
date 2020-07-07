package com.example.trackopedia;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    RadioGroup rg;
    String gender = "Male";
    EditText name, emailid, contact, age, weight, height, password;
    Button submit;
    Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_lay);


        mDialog = new Dialog(RegisterActivity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        name = (EditText) findViewById(R.id.R_Name);
        emailid = (EditText) findViewById(R.id.R_Email);
        contact = (EditText) findViewById(R.id.R_Contact);
        age = (EditText) findViewById(R.id.R_Age);
        weight = (EditText) findViewById(R.id.R_Weight);
        height = (EditText) findViewById(R.id.R_Height);
        password = (EditText) findViewById(R.id.R_Password);
        submit = (Button) findViewById(R.id.R_Submit);

        rg = (RadioGroup) findViewById(R.id.R_Gender);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male) {
                    gender = "Male";
                } else if (checkedId == R.id.female) {
                    gender = "Female";
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().compareTo("") != 0 || emailid.getText().toString().compareTo("") != 0 || contact.getText().toString().compareTo("") != 0 || age.getText().toString().compareTo("") != 0 || weight.getText().toString().compareTo("") != 0 || height.getText().toString().compareTo("") != 0 || password.getText().toString().compareTo("") != 0) {
                    if (name.getText().toString().compareTo("") != 0) {
                        if (emailid.getText().toString().compareTo("") != 0) {
                            if (isEmailValid(emailid.getText().toString())) {
                                if (contact.getText().toString().compareTo("") != 0) {
                                    if (contact.getText().toString().length() == 10) {
                                        if (age.getText().toString().compareTo("") != 0) {
                                            int a = Integer.parseInt(age.getText().toString());

                                            if (a > 0 && a < 120) {
                                                if (height.getText().toString().compareTo("") != 0) {
                                                    int h = Integer.parseInt(height.getText().toString());
                                                    if (h >= 19 && h < 250) {
                                                        if (weight.getText().toString().compareTo("") != 0) {
                                                            int w = Integer.parseInt(weight.getText().toString());
                                                            if (w > 0) {
                                                                if (gender.compareTo("") != 0) {
                                                                    if (password.getText().toString().compareTo("") != 0) {
                                                                        new reg().execute(name.getText().toString(), emailid.getText().toString(), contact.getText().toString(), age.getText().toString(), weight.getText().toString(), height.getText().toString(), gender, password.getText().toString());
                                                                    } else {
                                                                        password.setError("Enter Password");
                                                                        password.requestFocus();
                                                                    }
                                                                } else {
                                                                    Snackbar snack = Snackbar.make(v, "Select your Gender!", Snackbar.LENGTH_SHORT);
                                                                    View vs = snack.getView();
                                                                    TextView txt = (TextView) vs.findViewById(com.google.android.material.R.id.snackbar_text);
                                                                    txt.setTextColor(Color.RED);
                                                                    snack.show();
                                                                }
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
                                        contact.setError("Contact should have 10 Digits");
                                        contact.requestFocus();
                                    }
                                } else {
                                    contact.setError("Enter Contact");
                                    contact.requestFocus();
                                }
                            } else {
                                emailid.setError("Invalid Email");
                                emailid.requestFocus();
                            }
                        } else {
                            emailid.setError("Enter Email");
                            emailid.requestFocus();
                        }
                    } else {
                        name.setError("Enter Name");
                        name.requestFocus();
                    }
                } else {
                    Snackbar snack = Snackbar.make(v, "Fill all the Fields!", Snackbar.LENGTH_SHORT);
                    View vs = snack.getView();
                    TextView txt = (TextView) vs.findViewById(com.google.android.material.R.id.snackbar_text);
                    txt.setTextColor(Color.RED);
                    snack.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public class reg extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.register(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]);
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

                DB db1 = new DB(RegisterActivity.this);
                db1.open();
                Boolean ans = db1.register(name.getText().toString(), age.getText().toString(), height.getText().toString(), weight.getText().toString(), gender, contact.getText().toString(), emailid.getText().toString(), password.getText().toString());
                db1.close();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        finish();
                    }
                }, 3000);
            } else if (s.compareTo("already") == 0) {
                mDialog.dismiss();
                emailid.setError("Email Already Exists!");
                emailid.requestFocus();
            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(RegisterActivity.this);
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
                    Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}