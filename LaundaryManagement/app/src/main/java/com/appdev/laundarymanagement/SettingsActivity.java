package com.appdev.laundarymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {
    BottomNavigationView bv;
    Button monthlyReport,feedback;
    ImageButton logout;
    SharedPreferences sp;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        bv=findViewById(R.id.bottom_nav);
        logout=findViewById(R.id.logout);
        monthlyReport=findViewById(R.id.monthlyreport);
        feedback=findViewById(R.id.feedback);
        sp=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        bv.getMenu().findItem(R.id.navigation_settings).setChecked(true);
        bv.setOnNavigationItemSelectedListener(item -> {
            myClickItem(item);
            return true;
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putString("admin_islogin","false").apply();
                sp.edit().clear();
                Intent intent =new Intent(SettingsActivity.this,LauncherActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, Feedback.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        monthlyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MonthlyReportActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
    public void myClickItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_dashboard:
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_pricelist:
                Intent intent2 = new Intent(SettingsActivity.this, PricelistActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_searchuser:
                Intent intent3 = new Intent(SettingsActivity.this, SearchUserActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_received:
                Intent intent4 = new Intent(SettingsActivity.this, ReceivedActivity.class);
                startActivity(intent4);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_settings:
                Intent intent5 = new Intent(SettingsActivity.this, SettingsActivity.class);
                startActivity(intent5);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
    }
}