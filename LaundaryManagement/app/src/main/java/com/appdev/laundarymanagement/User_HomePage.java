package com.appdev.laundarymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class User_HomePage extends AppCompatActivity {
    LinearLayout l1;
    LinearLayout l2;
    LinearLayout l3;
    ConstraintLayout cl1;
//    ListView ls;
    TextView t1;
    Integer currentIndex = 1;
    Integer currentColorIndex = 1;
//    RecyclerView rv;
//    ArrayList<UserPendingClass> a;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        l1 = findViewById(R.id.ll1);
        l2 = findViewById(R.id.ll2);
        l3 = findViewById(R.id.ll3);
        cl1 = findViewById(R.id.constraintLayout1);
        t1 = findViewById(R.id.textView8);
//        rv = findViewById(R.id.my_recycler);
//        ls=findViewById(R.id.my_recycler);
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoadNewActivity = new Intent(User_HomePage.this, PricelistActivity.class);
                startActivity(intentLoadNewActivity);
            }
        });

        int[] colors = {Color.GREEN, Color.RED};
        String[] texts = {"Request for Laundry", "Cancel Request"};

        cl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current color from the array using the current color index
                int currentColor = colors[currentColorIndex%2];
                String currentText = texts[currentIndex%2];
                currentIndex = currentIndex +1;

                // Set the text color of the TextView to the current color
                t1.setTextColor(currentColor);
                t1.setText(currentText);
                Toast.makeText(getApplicationContext(), "Text changed to " + currentText, Toast.LENGTH_SHORT).show();

                // Increment the current color index and wrap around if necessary
                currentColorIndex = (currentColorIndex + 1);
            }
        });
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_HomePage.this, User_Profile.class);
                startActivity(intent);
            }
        });
    }
//    public void readDataFromGoogleSheet() {
//        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
//        String range = "Sheet3!A1:AAA";
//        String apiKey = "AIzaSyAtB0JJF5JEcr3gCW6W_wz2AHgtBYhGBmk";
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://sheets.googleapis.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        SheetsService sheetsService = retrofit.create(SheetsService.class);
//
//        Call<ValueRange> call = sheetsService.getValues(spreadsheetId, range, apiKey);
//        call.enqueue(new Callback<ValueRange>() {
//            @Override
//            public void onResponse(@NonNull Call<ValueRange> call, @NonNull Response<ValueRange> response) {
//                //try {
//                System.out.println(response.toString());
//                ValueRange values = response.body();
//                List<List<Object>> rows = values.getValues();
//                for (int i = rows.size()-1; i < 0; i--) {
//                    a.add(new UserPendingClass(rows.get(i).get(4).toString(), rows.get(i).get(0).toString(), (String) rows.get(i).get(3), rows.get(i).get(1).toString()));
//                }
//                CustomAdapter3 customAdapter = new CustomAdapter3(User_HomePage.this, R.layout.pricelist_listview, a);
//                ls.setAdapter(customAdapter);
//                ls.setClickable(true);
//                System.out.println(a.size() == 1);
//                if (a.size() == 1) {
//                    ArrayList<UserPendingClass> d;
//                    d = new ArrayList<UserPendingClass>();
//                    d.add(new UserPendingClass("No Particulars added", "", "",""));
//                    CustomAdapter3 customAdapter2 = new CustomAdapter3(User_HomePage.this, R.layout.activity_pending_cardview, d);
//                    ls.setAdapter(customAdapter2);
//                    ls.setClickable(false);
//                }
//
//
//                // Process the rows here
//                //System.out.println(rows.toString());
//                // Toast.makeText(MainActivity.this, rows.get(1).get(0).toString(), Toast.LENGTH_SHORT).show();
//                //}
////                catch (AssertionError a){
////                    System.out.println(a.getMessage());
////                    Toast.makeText(MainActivity.this, a.getMessage(), Toast.LENGTH_SHORT).show();
////                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
//                Toast.makeText(User_HomePage.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
//                ArrayList<UserPendingClass> e = new ArrayList<UserPendingClass>();
//                e.add(new UserPendingClass("No Internet Connection", "", "",""));
//                CustomAdapter3 customAdapter2 = new CustomAdapter3(User_HomePage.this, R.layout.activity_pending_cardview, e);
//                ls.setAdapter(customAdapter2);
//                ls.setClickable(false);
//                // Handle error
//            }
//        });
//
//    }
}