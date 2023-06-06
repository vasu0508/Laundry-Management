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
import android.widget.AdapterView;
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

public class user_history extends AppCompatActivity {
    BottomNavigationView bv;
    ListView ls;
    TextView tv1;
    SearchView sv;
    LinearLayout ll;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar pb;
    List<List<Object>> dd;
    Boolean issearch=false;
    ArrayList<UserHistoryClass> b;
    ArrayList<UserHistoryClass> a= new ArrayList<>();
    SharedPreferences sp;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);
        ls=findViewById(R.id.list);
        tv1=findViewById(R.id.textView);
        sv=findViewById(R.id.search);
        ll=findViewById(R.id.homepage);
        pb=findViewById(R.id.progressbar);
        sp=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
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
        readDataFromGoogleSheet();
        ls.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Integer j=i;
                sp.edit().putString("hist_position_date",a.get(i).getDate().toString()).apply();
                sp.edit().putString("hist_position_time",a.get(i).getTime().toString()).apply();
                Intent intent3 = new Intent(user_history.this, History_bif_Activity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return false;
            }
        });
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
                a=new ArrayList<>();
                System.out.println(response.toString());
                ValueRange values = response.body();
                List<List<Object>> rows = values.getValues();
                for(int i=rows.size()-1;i>0;i--){
                    if(rows.get(i).get(0).toString().equals(sp.getString("user_institute_code",null)) && rows.get(i).get(8).toString().equals(sp.getString("user_card_no",null))){
                        {
                            a.add(new UserHistoryClass(rows.get(i).get(3).toString(), rows.get(i).get(4).toString(), rows.get(i).get(5).toString(), rows.get(i).get(1).toString(), rows.get(i).get(2).toString()));
                        }
                    }
                }
                dd=rows;
                CustomAdapter5 customAdapter=new CustomAdapter5(user_history.this,R.layout.user_history_listview,a);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter);
                System.out.println(a.size()==1);
                if(a.size()==0){
                    ArrayList<UserHistoryClass> d;
                    d=new ArrayList<UserHistoryClass>();
                    d.add(new UserHistoryClass("No Laundry","","","",""));
                    CustomAdapter5 customAdapter2=new CustomAdapter5(user_history.this,R.layout.user_history_listview,d);
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
                Toast.makeText(user_history.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                ArrayList<UserHistoryClass> e=new ArrayList<UserHistoryClass>();
                e.add(new UserHistoryClass("No Internet Connection", "","","",""));
                CustomAdapter5 customAdapter2=new CustomAdapter5(user_history.this,R.layout.user_history_listview,e);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter2);
                // Handle error
            }
        });

    }
}