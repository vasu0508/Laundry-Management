package com.appdev.laundarymanagement;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddLaundryActivity extends AppCompatActivity {
    TextView tvname,tvcardno,tvroomno,tvbalance;
    Button back,submit,calculate;
    ListView ls;
    ArrayList<pricelistclass> a = new ArrayList<>();
    CustomAdapter3 customAdapter2;
    Double totalcost,totalclothes;
    List<List<Object>> values1;
    String name,roomno,balance,cardno;
    SharedPreferences sharedPreferences;
    String posit;
    Integer position=-1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_laundry);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ls=findViewById(R.id.listview);
        back=findViewById(R.id.back);
        submit=findViewById(R.id.submit);
        calculate=findViewById(R.id.calculate);
        tvname=findViewById(R.id.textView4);
        tvroomno=findViewById(R.id.textView12);
        tvcardno=findViewById(R.id.textView13);
        tvbalance=findViewById(R.id.textView18);
        readDataFromGoogleSheet();
        readDataFromGoogleSheet2();
        sharedPreferences=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        name=sharedPreferences.getString("name",null);
        roomno=sharedPreferences.getString("roomno",null);
        balance=sharedPreferences.getString("balance",null);
        cardno=sharedPreferences.getString("cardno",null);
        tvname.setText(name);
        tvroomno.setText(roomno);
        tvcardno.setText(cardno);
        tvbalance.setText(balance);
        ls.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                try {
                    if (customAdapter2.etcurr != null) {
                        customAdapter2.data.set(customAdapter2.etcurr.getId(), customAdapter2.etcurr.getText().toString());
                    }
                }
                catch (NullPointerException e){

                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent2 = new Intent(AddLaundryActivity.this, SearchUserActivity.class);
//                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> ab=new ArrayList();
                totalclothes= Double.valueOf(0);
                totalcost= Double.valueOf(0);
                if (customAdapter2.etcurr != null) {
                    customAdapter2.data.set(customAdapter2.etcurr.getId() ,customAdapter2.etcurr.getText().toString());
                }
                ArrayList<String> arr=customAdapter2.data;
                System.out.println(customAdapter2.data);
                for(int i=0;i<customAdapter2.data.size();i++){
                    Double j=Double.parseDouble(arr.get(i))*Double.parseDouble(a.get(i).getRate());
                    System.out.println(j);
                    totalcost=totalcost+j;
                    totalclothes=totalclothes+Double.parseDouble(customAdapter2.data.get(i));
                    customAdapter2.getItem(i).setTotalprice(j.toString());
                    customAdapter2.getItem(i).setQuantity(arr.get(i));
                    a.get(i).setTotalprice(j.toString());
                    a.get(i).setQuantity(arr.get(i));
                }
                //Toast.makeText(AddLaundryActivity.this, "Total Amount: "+totalcost.toString()+"\n"+"Total Clothes: "+totalclothes.toString(), Toast.LENGTH_SHORT).show();
                ls.setAdapter(customAdapter2);
                customAdapter2.data=arr;
                if(Double.parseDouble(totalcost.toString()) <= Double.parseDouble(balance.toString())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());
                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String currentTime = sdf2.format(new Date());
                    createSheetsService();
                    ValueRange body = new ValueRange();
                    ArrayList<Object> ar = new ArrayList<>();
                    ar.add(sharedPreferences.getString("admin_institute_code",null));
                    ar.add(currentDateandTime);
                    ar.add(currentTime);
                    ar.add("Received");
                    ar.add(totalcost.toString());
                    ar.add(totalclothes.toString());
                    ar.add(roomno);
                    ar.add(name);
                    ar.add(cardno);
                    values1 = new ArrayList<>();
                    for (int i = 0; i < a.size(); i++) {
                        ab.add(a.get(i).getParticular());
                        ab.add(a.get(i).getQuantity());
                        ar.add(a.get(i).getParticular());
                        ar.add(a.get(i).getQuantity());
                    }
                    List<Object> ll = ar;
                    values1.add(ll);
                    body.setValues(values1);
                    appendDataToSheet(body);
                    Double updatedbal=(Double.parseDouble(balance)-totalcost);
                    ValueRange body1 = new ValueRange()
                            .setValues(Arrays.asList(
                                    Arrays.asList("FALSE",updatedbal.toString())
                            ));
                    editDataToSheet(body1);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
                else{
                    Toast.makeText(AddLaundryActivity.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                }
            }
        });
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalclothes= Double.valueOf(0);
                totalcost= Double.valueOf(0);
                if (customAdapter2.etcurr != null) {
                    customAdapter2.data.set(customAdapter2.etcurr.getId() ,customAdapter2.etcurr.getText().toString());
                }
                ArrayList<String> arr=customAdapter2.data;
                System.out.println(customAdapter2.data);
                for(int i=0;i<customAdapter2.data.size();i++){
                    Double j=Double.parseDouble(arr.get(i))*Double.parseDouble(a.get(i).getRate());
                    totalcost=totalcost+j;
                    totalclothes=totalclothes+Double.parseDouble(customAdapter2.data.get(i));
                    System.out.println(j);
                    customAdapter2.getItem(i).setTotalprice(j.toString());
                    customAdapter2.getItem(i).setQuantity(arr.get(i));
                    a.get(i).setTotalprice(j.toString());
                    a.get(i).setQuantity(arr.get(i));
                }
                ls.setAdapter(customAdapter2);
                customAdapter2.data=arr;
                Toast.makeText(AddLaundryActivity.this, "Total Amount: "+totalcost.toString()+"\n"+"Total Clothes: "+totalclothes.toString(), Toast.LENGTH_SHORT).show();
//                for(int i=0;i<ls.getCount();i++) {
//
//                    Double num=Double.parseDouble(editText.getText().toString());
//                    TextView tv=view1.findViewById(R.id.textView6);
//                    Double num2=Double.parseDouble(tv.getText().toString());
//                    TextView tv2=view1.findViewById(R.id.totaltv);
//                    Double num3=num*num2;
//                    tv2.setText(num3.toString());
//                }

            }
        });
    }
    private void readDataFromGoogleSheet() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet2!A:C";
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
                for (int i = 1; i < rows.size(); i++) {
                    a.add(new pricelistclass(rows.get(i).get(0).toString(), rows.get(i).get(1).toString(), (String) rows.get(i).get(2)));
                }
                customAdapter2 = new CustomAdapter3(AddLaundryActivity.this, R.layout.addlaundrylistview, a,a.size());
                ls.setAdapter(customAdapter2);
                ls.setClickable(true);
                System.out.println(a.size() == 1);
                if (a.size() == 1) {
                    ArrayList<pricelistclass> d;
                    d = new ArrayList<pricelistclass>();
                    d.add(new pricelistclass("No Particulars added", "", ""));
                    customAdapter2 = new CustomAdapter3(AddLaundryActivity.this, R.layout.addlaundrylistview, d,d.size());
                    ls.setAdapter(customAdapter2);
                    ls.setClickable(false);
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
                Toast.makeText(AddLaundryActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                ArrayList<pricelistclass> e = new ArrayList<pricelistclass>();
                e.add(new pricelistclass("No Internet Connection", "", ""));
                customAdapter2 = new CustomAdapter3(AddLaundryActivity.this, R.layout.addlaundrylistview, e,e.size());
                ls.setAdapter(customAdapter2);
                ls.setClickable(false);
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
    private static final String RANGE = "Sheet3!A:Z";

    private void appendDataToSheet(ValueRange body) {
        try {
            AppendValuesResponse result = sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, RANGE, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            Log.d(TAG, "Append result: " + result);
            Toast.makeText(this, "Laundry added Successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to send data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void editDataToSheet(ValueRange body1) {
        Integer pos=position;
        pos=pos+1;
        posit=pos.toString();
        String RANGE = "Sheet1!R"+posit+":"+"S"+posit;
        try {
            UpdateValuesResponse result1 = sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, RANGE, body1)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            Log.d(TAG, "Edit result: " + result1);
            Toast.makeText(this, "Data Edited Successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to send data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
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
                    if(cardno.equals(rows.get(i).get(2).toString()) && sharedPreferences.getString("admin_institute_code",null).equals(rows.get(i).get(9))){
                        position=i;
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                Toast.makeText(AddLaundryActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                // Handle error
            }
        });

    }
}