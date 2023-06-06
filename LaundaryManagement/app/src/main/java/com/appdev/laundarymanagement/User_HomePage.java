package com.appdev.laundarymanagement;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class User_HomePage extends AppCompatActivity {
    LinearLayout l1,l2,l3;
    ConstraintLayout cl1;
    Button Logout, feedback;
    SwipeRefreshLayout swipeRefreshLayout;
//    ListView ls;
    TextView t1;
    Integer currentIndex = 1;
    Integer currentColorIndex = 1;
    SharedPreferences sp;
    String laundryreq="FALSE";
    int[] colors;
    String[] texts;
    Integer posit;
    RelativeLayout pb;
    TextView name;
    MaterialTextView amount;
//    RecyclerView rv;
//    ArrayList<UserPendingClass> a;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        name=findViewById(R.id.textView4);
        amount=findViewById(R.id.editTextTextPersonName3);
        l1 = findViewById(R.id.ll1);
        l2 = findViewById(R.id.ll2);
        l3 = findViewById(R.id.ll3);
        cl1 = findViewById(R.id.constraintLayout1);
        t1 = findViewById(R.id.textView8);
        Logout = findViewById(R.id.button);
        feedback = findViewById(R.id.Feedback);
        pb=findViewById(R.id.pbar);
        sp=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        name.setText(sp.getString("user_name",null));
        readDataFromGoogleSheet();
//        rv = findViewById(R.id.my_recycler);
//        ls=findViewById(R.id.my_recycler);
        l3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_HomePage.this, user_PricelistActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        readDataFromGoogleSheet();
                    }
                });
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_HomePage.this, user_history.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_HomePage.this, LauncherActivity.class);
                startActivity(intent);
                sp.edit().putString("user_islogin","false").apply();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_HomePage.this, UserFeedbackActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        colors = new int[]{Color.GREEN, Color.RED};
        texts = new String[]{"Request for Laundry", "Cancel Request"};
        cl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                pb.setVisibility(View.VISIBLE);
                cl1.setClickable(false);
                // Get the current color from the array using the current color index
                readDataFromGoogleSheet2();
            }
        });
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User_HomePage.this, User_Profile.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
private void readDataFromGoogleSheet2() {
    String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
    String range = "Sheet1!K:T";
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
            for(Integer i=0;i<rows.size();i++){
                if(rows.get(i).get(5).equals(sp.getString("user_email_id",null)) && rows.get(i).get(2).equals(sp.getString("user_card_no",null)) && rows.get(i).get(9).equals(sp.getString("user_institute_code",null))){
                    laundryreq=rows.get(i).get(7).toString();
                    posit=i;
                }
            }
            if(laundryreq.equals("TRUE")){
                laundryreq="FALSE";
                createSheetsService();
                ValueRange body = new ValueRange()
                        .setValues(Arrays.asList(
                                Arrays.asList(laundryreq)
                        ));
                editDataToSheet(body);
                t1.setTextColor(colors[0]);
                t1.setText(texts[0]);
            }
            else if(laundryreq.equals("FALSE")){
                laundryreq="TRUE";
                createSheetsService();
                ValueRange body = new ValueRange()
                        .setValues(Arrays.asList(
                                Arrays.asList(laundryreq)
                        ));
                editDataToSheet(body);
                t1.setTextColor(colors[1]);
                t1.setText(texts[1]);
            }
            cl1.setClickable(true);
            pb.setVisibility(View.GONE);
        }
        @Override
        public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
            cl1.setClickable(true);
            pb.setVisibility(View.GONE);
            Toast.makeText(User_HomePage.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
            // Handle error
        }
    });

}
private void readDataFromGoogleSheet() {
    String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
    String range = "Sheet1!K:T";
    String apiKey = "AIzaSyAtB0JJF5JEcr3gCW6W_wz2AHgtBYhGBmk";
    pb.setVisibility(View.VISIBLE);
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
            for(Integer i=0;i<rows.size();i++){
                if(rows.get(i).get(5).equals(sp.getString("user_email_id",null)) && rows.get(i).get(2).equals(sp.getString("user_card_no",null)) && rows.get(i).get(9).equals(sp.getString("user_institute_code",null))){
                    laundryreq=rows.get(i).get(7).toString();
                    amount.setText("Balance: ₹"+rows.get(i).get(8).toString());
                    sp.edit().putString("user_balance",rows.get(i).get(8).toString()).apply();
                    posit=i;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("user_name", rows.get(i).get(0).toString());
                    editor.putString("user_room_no", rows.get(i).get(1).toString());
                    editor.putString("user_card_no", rows.get(i).get(2).toString());
                    editor.putString("user_balance", rows.get(i).get(8).toString());
                    editor.putString("user_program", rows.get(i).get(3).toString());
                    editor.putString("user_year", rows.get(i).get(4).toString());
                    editor.putString("user_email_id", rows.get(i).get(5).toString());
                    editor.putString("user_institute_code", rows.get(i).get(9).toString());
                    editor.apply();
                    name.setText(sp.getString("user_name",null));
                }
            }
            if(laundryreq.equals("FALSE")){
                t1.setTextColor(colors[0]);
                t1.setText(texts[0]);
            }
            if(laundryreq.equals("TRUE")){
                t1.setTextColor(colors[1]);
                t1.setText(texts[1]);
            }
            pb.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }
        @Override
        public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
            cl1.setClickable(true);
            pb.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            // Handle error
        }
    });
}
    private Sheets sheetsService;
    private void createSheetsService() {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        GoogleCredential credential = null;
        try {
            InputStream inputStream = getResources().getAssets().open("laundry-management-377814-045b05e9b598.json");
            credential = GoogleCredential.fromStream(inputStream, transport, jsonFactory)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheetsService = new Sheets.Builder(transport, jsonFactory, credential)
                .setApplicationName("Laundry Management")
                .build();
    }
    private static final String SPREADSHEET_ID = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
    private void editDataToSheet(ValueRange body1) {
        Integer pos=posit;
        pos=pos+1;
        String posit=pos.toString();
        String RANGE = "Sheet1!R"+posit;
        try {
            UpdateValuesResponse result1 = sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, RANGE, body1)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            Log.d(TAG, "Edit result: " + result1);
            Toast.makeText(this, "Laundry Request Successful", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
        } catch (IOException e) {
            Toast.makeText(this, "Unable to send data", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }
}