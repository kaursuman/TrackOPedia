package com.example.trackopedia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.trackopedia.R;

public class MainActivity extends AppCompatActivity {
    ImageView close, home;
    RelativeLayout ray;
    ListView list;
    String[] menu = new String[]{"Home", "Profile", "BMI", "Diet Monitoring", "Diet Suggestion", "Footsteps Monitoring", "Sleep Suggestion", "Water Alarm", "Logout"};
    int pos = 0;
    TextView head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
        ray = (RelativeLayout) findViewById(R.id.ray);
        close = (ImageView) findViewById(R.id.close);
        home = (ImageView) findViewById(R.id.home);
        list = (ListView) findViewById(R.id.list);
        head = (TextView) findViewById(R.id.heading);

        final Adapter adapt = new Adapter(MainActivity.this, menu);
        list.setAdapter(adapt);

        load_page();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ray.setVisibility(View.GONE);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ray.setVisibility(View.VISIBLE);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pos = i;
                adapt.notifyDataSetChanged();
                load_page();
            }
        });
    }

    public class Adapter extends ArrayAdapter<String> {
        Context con;
        String[] menu;

        public Adapter(@NonNull Context context, String[] menu) {
            super(context, R.layout.menu_item, menu);
            con = context;
            this.menu = menu;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.menu_item, null, true);
            ImageView img = (ImageView) v.findViewById(R.id.menu_img);
            TextView name = (TextView) v.findViewById(R.id.menu_text);

            name.setText(menu[position]);

            if (position == pos) {
                img.setVisibility(View.VISIBLE);
                name.setTextColor(getResources().getColor(R.color.greenbright));
            } else {
                img.setVisibility(View.INVISIBLE);
                name.setTextColor(getResources().getColor(R.color.white));
            }
            return v;
        }
    }


    public void load_page() {
        if (pos == 8) {
            //Logout
            SharedPreferences sp = getSharedPreferences("trackopedia", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor;

            editor = sp.edit();
            editor.putString("uid", "");
            editor.commit();

//            stopService(new Intent(MainActivity.this,BackgroundService.class));


            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

        } else {
            head.setText(menu[pos]);
            switch (pos) {
                case 0:
                    //Home [MainActivity]
                    Fragment frag = new HomeFragment();
                    FragmentManager mgr = getSupportFragmentManager();
                    mgr.beginTransaction().replace(R.id.frame, frag).commit();
                    break;

                case 1:
                    //Profile
                    Fragment frag1 = new ProfileFragment();
                    FragmentManager mgr1 = getSupportFragmentManager();
                    mgr1.beginTransaction().replace(R.id.frame, frag1).commit();
                    break;

                case 2:
                    //BMI
                    Fragment frag2 = new BMI_Fragment();
                    FragmentManager mgr2 = getSupportFragmentManager();
                    mgr2.beginTransaction().replace(R.id.frame, frag2).commit();
                    break;

                case 3:
                    //Diet Monitoring
                    Fragment frag3 = new DietMonitoringFragment();
                    FragmentManager mgr3 = getSupportFragmentManager();
                    mgr3.beginTransaction().replace(R.id.frame, frag3).commit();
                    break;

                case 4:
                    //Diet Suggestion
                    Fragment frag4 = new DietSuggestionFragment();
                    FragmentManager mgr4 = getSupportFragmentManager();
                    mgr4.beginTransaction().replace(R.id.frame, frag4).commit();
                    break;

                case 5:
                    //FootSteps Monitoring
                    Fragment frag5 = new FootStepsMonitoringFragment();
                    FragmentManager mgr5 = getSupportFragmentManager();
                    mgr5.beginTransaction().replace(R.id.frame, frag5).commit();
                    break;

                case 6:
                    //Sleep Suggestion
                    Fragment frag6 = new SleepSuggestionFragment();
                    FragmentManager mgr6 = getSupportFragmentManager();
                    mgr6.beginTransaction().replace(R.id.frame, frag6).commit();
                    break;

                case 7:
                    //Water Alarm
                    Fragment frag7 = new WaterAlarmFragment();
                    FragmentManager mgr7 = getSupportFragmentManager();
                    mgr7.beginTransaction().replace(R.id.frame, frag7).commit();
                    break;


            }
            ray.setVisibility(View.GONE);
        }
    }

}

