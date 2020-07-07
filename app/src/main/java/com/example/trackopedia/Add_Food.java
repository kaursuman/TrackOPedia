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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Add_Food extends AppCompatActivity {

	TableRow quanttb;
	int count=2;
	ImageView plus,minus;
	TextView totcount;

	RelativeLayout ray;
	SharedPreferences sp;
	String uid;
	SimpleDateFormat sdfd=new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat sdft=new SimpleDateFormat("HH:mm");
	Date d;

	AutoCompleteTextView name;
	EditText carbo,prot,fats;
	Button submit;
	ImageView search,add;
	Spinner cat;
	TextView txt;
	String spinitems[]=new String[]{"Meal","Fruit","Beverage","Snack","Desert","Other"};
	ArrayList<String> fooditems;
	ArrayList<String> foodnames;

	String[] countitem=new String[]{"0.25","0.50","1","2","3","4","5","6","7","8","9","10"};

	Dialog mDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_food_lay);
		sp=getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
		uid=sp.getString("uid","");
		//getSupportActionBar().setTitle("Add Food");
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setHomeButtonEnabled(true);

		ray= (RelativeLayout) findViewById(R.id.adray);
		name= (AutoCompleteTextView) findViewById(R.id.pname);
		carbo= (EditText) findViewById(R.id.pcarb);
		prot= (EditText) findViewById(R.id.ppro);
		fats= (EditText) findViewById(R.id.pfats);
		submit= (Button) findViewById(R.id.psubmit);
		search= (ImageView) findViewById(R.id.psearch);
		cat= (Spinner) findViewById(R.id.cat);
		txt= (TextView) findViewById(R.id.txt);
		add= (ImageView) findViewById(R.id.padd);
		quanttb= (TableRow) findViewById(R.id.quanttb);
		plus= (ImageView) findViewById(R.id.plus);
		minus= (ImageView) findViewById(R.id.minus);
		totcount= (TextView) findViewById(R.id.count);


		mDialog=new Dialog(Add_Food.this, R.style.AppTheme);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mDialog.setContentView(R.layout.circular_dialog);
		mDialog.setCancelable(false);

		name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos=getdetails(name.getText().toString());

				String temp[]=fooditems.get(pos).split("\\*");
				prot.setText(temp[1]);
				carbo.setText(temp[2]);
				fats.setText(temp[3]);

				if(temp[4].compareTo("Meal")==0)
				{
					cat.setSelection(0);
				}
				else if(temp[4].compareTo("Fruit")==0)
				{
					cat.setSelection(1);
				}
				else if(temp[4].compareTo("Beverage")==0)
				{
					cat.setSelection(2);
				}
				else if(temp[4].compareTo("Snack")==0)
				{
					cat.setSelection(3);
				}
				else if(temp[4].compareTo("Desert")==0)
				{
					cat.setSelection(4);
				}
				else if(temp[4].compareTo("Other")==0)
				{
					cat.setSelection(5);
				}
				visible();
				disable();

				InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(ray.getWindowToken(), 0);
			}
		});

		search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				new autofill().execute();
			}
		});

		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				clear();
				visible();
				enable();
				name.requestFocus();
			}
		});

		ArrayAdapter<String> adapt=new ArrayAdapter<String>(Add_Food.this, R.layout.spinner_item, R.id.spinid,spinitems);
		cat.setAdapter(adapt);

		plus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(count>=0 && count<=10)
				{
					count++;
					totcount.setText(countitem[count]);
				}
			}
		});

		minus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(count>=1 && count<=11)
				{
					count--;
					totcount.setText(countitem[count]);
				}

			}
		});

		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			if(name.getText().toString().compareTo("")!=0)
			{
				if(carbo.getText().toString().compareTo("")!=0)
				{
					if(prot.getText().toString().compareTo("")!=0)
					{
						if(fats.getText().toString().compareTo("")!=0)
						{
							int c=(int)(Float.parseFloat(carbo.getText().toString())* Float.parseFloat(totcount.getText().toString()));
							int p=(int)(Float.parseFloat(prot.getText().toString())* Float.parseFloat(totcount.getText().toString()));
							int f=(int)(Float.parseFloat(fats.getText().toString())* Float.parseFloat(totcount.getText().toString()));

							d=new Date();
							new submit().execute(uid,name.getText().toString(),""+c,""+p,""+f,cat.getSelectedItem().toString(),sdfd.format(d.getTime()),sdft.format(d.getTime()),totcount.getText().toString());
						}
						else
						{
							fats.setError("Enter Fats");
							fats.requestFocus();
						}
					}
					else
					{
						prot.setError("Enter Protein");
						prot.requestFocus();
					}
				}
				else
				{
					carbo.setError("Enter Carbohydrates");
					carbo.requestFocus();
				}
			}
			else
			{
				name.setError("Enter Name");
				name.requestFocus();
			}
			}
		});

		new autofill().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}



	public class submit extends AsyncTask<String, JSONObject, String>
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
				JSONObject json=api.mconsumed(params[0],params[1],params[2],params[3],params[4],params[5],params[6],params[7],params[8]);
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
			if(s.compareTo("true")==0)
			{

//				Toast.makeText(Add_Food.this, "Food Added", Toast.LENGTH_SHORT).show();

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
                        mDialog.dismiss();
						clear();
						invisible();
						new autofill().execute();
					}
				},2000);
			}
			else
			{
				if(s.contains("Unable to resolve host"))
				{
					AlertDialog.Builder ad=new AlertDialog.Builder(Add_Food.this);
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
				}
				else {
					mDialog.dismiss();
					Toast.makeText(Add_Food.this, s, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public class search extends AsyncTask<String, JSONObject, String>
	{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			load();
			invisible();
		}

		@Override
		protected String doInBackground(String... params) {
			String a="back";
			RestAPI api=new RestAPI();
			try {
				JSONObject json=api.request_food(params[0]);
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
			if(s.compareTo("no")==0)
			{
				AlertDialog.Builder ad=new AlertDialog.Builder(Add_Food.this);
				ad.setIcon(R.drawable.warning);
				ad.setTitle("Food not Found!");
				ad.setMessage("Would you like to Manually Enter the food details");
				ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						clear();
						visible();
						enable();
					}
				});
				ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				ad.show();
//				dl.cancel();
			}
			else
			{
				if(s.contains("*"))
				{
					String temp[]=s.split("\\*");
					name.setText(temp[0]);
					prot.setText(temp[1]);
					carbo.setText(temp[2]);
					fats.setText(temp[4]);

					if(temp[3].compareTo("Meal")==0)
					{
						cat.setSelection(0);
					}
					else if(temp[3].compareTo("Fruit")==0)
					{
						cat.setSelection(1);
					}
					else if(temp[3].compareTo("Beverage")==0)
					{
						cat.setSelection(2);
					}
					else if(temp[3].compareTo("Snack")==0)
					{
						cat.setSelection(3);
					}
					else if(temp[3].compareTo("Desert")==0)
					{
						cat.setSelection(4);
					}
					else if(temp[3].compareTo("Other")==0)
					{
						cat.setSelection(5);
					}

//					dl.cancel();
					visible();
					disable();
				}
				else
				{
					if(s.contains("Unable to resolve host"))
					{
						AlertDialog.Builder ad=new AlertDialog.Builder(Add_Food.this);
						ad.setTitle("Unable to Connect!");
						ad.setMessage("Check your Internet Connection,Unable to connect the Server");
						ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});
						ad.show();
//						dl.cancel();
					}
					else {
//						dl.cancel();
						Toast.makeText(Add_Food.this, s, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

	public void invisible()
	{
		carbo.setVisibility(View.GONE);
		fats.setVisibility(View.GONE);
		prot.setVisibility(View.GONE);
		submit.setVisibility(View.GONE);
		cat.setVisibility(View.GONE);
		txt.setVisibility(View.GONE);
		quanttb.setVisibility(View.GONE);
	}

	public void visible()
	{
		carbo.setVisibility(View.VISIBLE);
		fats.setVisibility(View.VISIBLE);
		prot.setVisibility(View.VISIBLE);
		submit.setVisibility(View.VISIBLE);
		cat.setVisibility(View.VISIBLE);
		txt.setVisibility(View.VISIBLE);
		quanttb.setVisibility(View.VISIBLE);
	}

	public void disable()
	{
		carbo.setEnabled(false);
		fats.setEnabled(false);
		prot.setEnabled(false);
		cat.setEnabled(false);
	}

	public void enable()
	{
		carbo.setEnabled(true);
		fats.setEnabled(true);
		prot.setEnabled(true);
		cat.setEnabled(true);
	}

	public void clear()
	{
		name.setText("");
		prot.setText("");
		carbo.setText("");
		fats.setText("");
		count=2;
		totcount.setText(countitem[count]);
	}



	public class autofill extends AsyncTask<String, JSONObject, String>
	{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			load();
		}

		@Override
		protected String doInBackground(String... params) {
			String a="back";
			RestAPI api=new RestAPI();
			try {
				JSONObject json=api.getfood();
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

//			Toast.makeText(Add_Food.this, "Auto Fill Response"+s, Toast.LENGTH_LONG).show();

			if(s.compareTo("no")==0)
			{
				AlertDialog.Builder ad=new AlertDialog.Builder(Add_Food.this);
				ad.setIcon(R.drawable.warning);
				ad.setTitle("There are No Food Added");
				ad.setMessage("Would you like to Manually Enter the food details");
				ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						clear();
						visible();
						enable();
					}
				});
				ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				ad.show();
//				dl.cancel();
			}
			else
			{
				if(s.contains("*"))
				{
					String temp[]=s.split("\\#");
					fooditems=new ArrayList<String>();
					foodnames=new ArrayList<String>();
					for(int i=0;i<temp.length;i++)
					{
						String temp1[]=temp[i].split("\\*");
						foodnames.add(temp1[0]);
						fooditems.add(temp[i]);
					}

					ArrayAdapter<String> adapt=new ArrayAdapter<String>(Add_Food.this, R.layout.spinner_item, R.id.spinid,foodnames);
					name.setAdapter(adapt);
//					dl.cancel();

				}
				else
				{
					if(s.contains("Unable to resolve host"))
					{
						AlertDialog.Builder ad=new AlertDialog.Builder(Add_Food.this);
						ad.setTitle("Unable to Connect!");
						ad.setMessage("Check your Internet Connection,Unable to connect the Server");
						ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});
						ad.show();
//						dl.cancel();
					}
					else {
//						dl.cancel();
						Toast.makeText(Add_Food.this, s, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

	public int getdetails(String name)
	{
		int ans=0;
		for(int i=0;i<foodnames.size();i++)
		{
			if(foodnames.get(i).compareTo(name)==0)
			{
				ans=i;
				return i;
			}
		}
		return ans;
	}

}
