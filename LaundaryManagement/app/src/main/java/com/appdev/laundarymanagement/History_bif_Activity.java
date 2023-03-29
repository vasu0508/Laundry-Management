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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class History_bif_Activity extends AppCompatActivity {
    ListView ls;
    TextView tv1;
    BottomNavigationView bv;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar pb;
    SharedPreferences sp;
    ArrayList<history_bifurcation_class> a = new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_bif);
        ls=findViewById(R.id.list);
        tv1=findViewById(R.id.textView);
        pb=findViewById(R.id.progressbar);
        sp=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh2);

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
        CustomAdapter6 customAdapter=new CustomAdapter6(History_bif_Activity.this,R.layout.history_bifurcation_listview,a);
        readDataFromGoogleSheet();
    }

    private void readDataFromGoogleSheet() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet3!A:AAA";
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
                a.add(new history_bifurcation_class("Particular","Quantity"));
                Integer j=0;
                for(int i=1;i<rows.size();i++){
                    if(rows.get(i).get(0).equals(sp.getString("user_institute_code",null)) && rows.get(i).get(8).toString().equals(sp.getString("user_card_no",null))) {
                        if(j.toString().equals(sp.getString("hist_position",null))){
                            for(int z=9;z<rows.get(i).size();z=z+2){
                                if(z+1==rows.size()){
                                    break;
                                }
                                a.add(new history_bifurcation_class(rows.get(i).get(z).toString(),rows.get(i).get(z+1).toString()));
                            }
                        }
                        j=j+1;
                    }
                }
                CustomAdapter6 customAdapter=new CustomAdapter6(History_bif_Activity.this,R.layout.history_bifurcation_listview,a);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter);
                System.out.println(a.size()==1);
                if(a.size()==1){
                    ArrayList<history_bifurcation_class> d;
                    d=new ArrayList<history_bifurcation_class>();
                    d.add(new history_bifurcation_class("No Particulars added",""));
                    CustomAdapter6 customAdapter2=new CustomAdapter6(History_bif_Activity.this,R.layout.history_bifurcation_listview,d);
                    ls.setAdapter(customAdapter2);
                }


                // Process the rows here
                //System.out.println(rows.toString());
                // Toast.makeText(MainActivity.this, rows.get(1).get(0).toString(), Toast.LENGTH_SHORT).show();
                //}
//                catch (AssertionError a){
//                    System.out.println(a.getMessage());
//                    Toast.makeText(MainActivity.this, a.getMessage(), Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                Toast.makeText(History_bif_Activity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                ArrayList<history_bifurcation_class> e=new ArrayList<history_bifurcation_class>();
                e.add(new history_bifurcation_class("No Internet Connection",""));
                CustomAdapter6 customAdapter2=new CustomAdapter6(History_bif_Activity.this,R.layout.history_bifurcation_listview,e);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter2);
                // Handle error
            }
        });

    }
}