package com.appdev.laundarymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReceivedActivity extends AppCompatActivity {
    BottomNavigationView bv;
    ListView ls;
    TextView tv1;
    SearchView sv;
    LinearLayout ll;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar pb;
    List<List<Object>> dd;
    Boolean issearch=false;
    ArrayList<ReceivedClass> b;
    ArrayList<ReceivedClass> a= new ArrayList<>();
    SharedPreferences sp;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received);
        ls=findViewById(R.id.list);
        tv1=findViewById(R.id.textView);
        bv=findViewById(R.id.bottom_nav);
        sv=findViewById(R.id.search);
        ll=findViewById(R.id.homepage);
        pb=findViewById(R.id.progressbar);
        bv=findViewById(R.id.bottom_nav);
        bv.getMenu().findItem(R.id.navigation_received).setChecked(true);
        sp=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        a=new ArrayList<>();
                        readDataFromGoogleSheet();
                        // Your code goes here
                        // In this code, we are just
                        // changing the text in the textbox

                        // This line is important as it explicitly
                        // refreshes only once
                        // If "true" it implicitly refreshes forever
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        bv.setOnNavigationItemSelectedListener(item -> {
            myClickItem(item);
            return true;
        });
        readDataFromGoogleSheet();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                b = new ArrayList<>();
                if (query.length() == 0) {
                    b.addAll(a);
                    CustomAdapter4 customAdapter2 = new CustomAdapter4(ReceivedActivity.this, R.layout.received_listview, b);
                    ls.setAdapter(customAdapter2);
                    issearch = false;
                } else {
                    issearch = true;
                    b = new ArrayList<>();
                    for (ReceivedClass wp : a) {
                        if (wp.getRoomno().toLowerCase(Locale.getDefault()).contains(query.toLowerCase()) || wp.getName().toLowerCase(Locale.getDefault()).contains(query.toLowerCase())) {
                            b.add(wp);
                        }
                    }
                }
                if (!b.isEmpty()) {
                    if (b.get(0).getRoomno().equals("Room No")) {
                        issearch = false;
                    }
                    CustomAdapter4 customAdapter2 = new CustomAdapter4(ReceivedActivity.this, R.layout.received_listview, b);
                    ls.setAdapter(customAdapter2);
                } else {
                    ArrayList<newClass> c = new ArrayList<>();
                    issearch=false;
                    c.add(new newClass("No Match", "",""));
                    CustomAdapter customAdapter2 = new CustomAdapter(ReceivedActivity.this, R.layout.searchuser_listview, c);
                    ls.setAdapter(customAdapter2);
                    Toast.makeText(ReceivedActivity.this, "No match found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                b = new ArrayList<>();
                if (newText.length() == 0) {
                    b.addAll(a);
                    CustomAdapter4 customAdapter2 = new CustomAdapter4(ReceivedActivity.this, R.layout.received_listview, b);
                    ls.setAdapter(customAdapter2);
                    issearch = false;
                } else {
                    issearch = true;
                    b = new ArrayList<>();
                    for (ReceivedClass wp : a) {
                        if (wp.getName().toLowerCase(Locale.getDefault()).contains(newText.toLowerCase()) || wp.getRoomno().toLowerCase(Locale.getDefault()).contains(newText.toLowerCase())) {
                            b.add(wp);
                        }
                    }
                    if (!b.isEmpty()) {
                        CustomAdapter4 customAdapter2 = new CustomAdapter4(ReceivedActivity.this, R.layout.received_listview, b);
                        ls.setAdapter(customAdapter2);
                        if (b.get(0).getRoomno().equals("Room No")) {
                            issearch = false;
                        }
                    } else {
                        ArrayList<newClass> c = new ArrayList<>();
                        c.add(new newClass("No Match", "",""));
                        CustomAdapter customAdapter2 = new CustomAdapter(ReceivedActivity.this, R.layout.searchuser_listview, c);
                        issearch=false;
                        ls.setAdapter(customAdapter2);
                    }
                }
                return false;
            }
        });
    }
    public void myClickItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_dashboard:
                Intent intent = new Intent(ReceivedActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_pricelist:
                Intent intent2 = new Intent(ReceivedActivity.this, PricelistActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_searchuser:
                Intent intent3 = new Intent(ReceivedActivity.this, SearchUserActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_received:
                Intent intent4 = new Intent(ReceivedActivity.this, ReceivedActivity.class);
                startActivity(intent4);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_settings:
                Intent intent5 = new Intent(ReceivedActivity.this, Feedback.class);
                startActivity(intent5);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
    }
    private void readDataFromGoogleSheet() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet3!A1:AAA";
        String apiKey = "AIzaSyAtB0JJF5JEcr3gCW6W_wz2AHgtBYhGBmk";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sheets.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SheetsService sheetsService = retrofit.create(SheetsService.class);

        Call<ValueRange> call = sheetsService.getValues(spreadsheetId, range, apiKey);
        call.enqueue(new Callback<ValueRange>() {
            @Override
            public void onResponse(@NonNull Call<ValueRange> call, @NonNull Response<ValueRange> response) {
                //try {
                System.out.println(response.toString());
                ValueRange values = response.body();
                List<List<Object>> rows = values.getValues();
                for(int i=rows.size()-1;i>0;i--){
                    if(rows.get(i).get(0).toString().equals(sp.getString("admin_institute_code",null))) {
                        a.add(new ReceivedClass(rows.get(i).get(7).toString(), rows.get(i).get(6).toString(), rows.get(i).get(4).toString(), rows.get(i).get(5).toString(), rows.get(i).get(1).toString(), rows.get(i).get(2).toString()));
                    }
                }
                dd=rows;
                CustomAdapter4 customAdapter=new CustomAdapter4(ReceivedActivity.this,R.layout.received_listview,a);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter);
                System.out.println(a.size()==1);
                if(a.size()==0){
                    ArrayList<ReceivedClass> d;
                    d=new ArrayList<ReceivedClass>();
                    d.add(new ReceivedClass("No Received","","","","",""));
                    CustomAdapter4 customAdapter2=new CustomAdapter4(ReceivedActivity.this,R.layout.received_listview,d);
                    ls.setAdapter(customAdapter2);
                }


                // Process the rows here
                //System.out.println(rows.toString());
                // Toast.makeText(ReceivedActivity.this, rows.get(1).get(0).toString(), Toast.LENGTH_SHORT).show();
                //}
//                catch (AssertionError a){
//                    System.out.println(a.getMessage());
//                    Toast.makeText(ReceivedActivity.this, a.getMessage(), Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                Toast.makeText(ReceivedActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                ArrayList<ReceivedClass> e=new ArrayList<ReceivedClass>();
                e.add(new ReceivedClass("No Internet Connection","", "","","",""));
                CustomAdapter4 customAdapter2=new CustomAdapter4(ReceivedActivity.this,R.layout.received_listview,e);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter2);
                // Handle error
            }
        });

    }
}