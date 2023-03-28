package com.appdev.laundarymanagement;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class add_in_price_list_Activity extends AppCompatActivity {
    EditText particular,unit,rate;
    Button submit,back;
    ProgressBar pb;
    String p,u,r;
    SharedPreferences sharedPreferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_in_price_list);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        particular=findViewById(R.id.partiicularet);
        unit=findViewById(R.id.unitet);
        rate=findViewById(R.id.rateet);
        submit=findViewById(R.id.submit);
        back=findViewById(R.id.back);
        pb=findViewById(R.id.pbar);
        pb.setVisibility(View.INVISIBLE);
        sharedPreferences=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                p=particular.getText().toString();
                u=unit.getText().toString();
                r=rate.getText().toString();
                if(p.isEmpty()){
                    pb.setVisibility(View.INVISIBLE);
                    particular.setError("This field cannot be Empty");
                }
                else if(u.isEmpty()){
                    pb.setVisibility(View.INVISIBLE);
                    unit.setError("This field cannot be Empty");
                    particular.setError(null);
                }
                else if(r.isEmpty()){
                    pb.setVisibility(View.INVISIBLE);
                    rate.setError("This field cannot be Empty");
                    unit.setError(null);
                    particular.setError(null);
                }
                else{
                    particular.setError(null);
                    unit.setError(null);
                    rate.setError(null);
                    createSheetsService();
                    ValueRange body = new ValueRange()
                                .setValues(Arrays.asList(
                                        Arrays.asList(p,u,r,sharedPreferences.getString("admin_institute_code",null))
                                ));
                    appendDataToSheet(body);
                    Intent intent=new Intent(add_in_price_list_Activity.this,PricelistActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    }
                }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(add_in_price_list_Activity.this,PricelistActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
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
    private static final String RANGE = "Sheet2!A:D";

    private void appendDataToSheet(ValueRange body) {
        try {
            AppendValuesResponse result = sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, RANGE, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            Log.d(TAG, "Append result: " + result);
            Toast.makeText(this, "Data added Successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Unable to send data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


//    Particular particular1 = new Particular(p,u,r,String.valueOf(index));
//    public void writedata() {
//        HttpTransport httpTransport = null;
//        try {
//            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//        GoogleCredential credential = null;
//        try {
//            AssetManager assetManager = getAssets();
//            InputStream inputStream = assetManager.open("laundry-management-377814-045b05e9b598.json");
//            credential = GoogleCredential.fromStream(inputStream)
//                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Sheets service = new Sheets.Builder(httpTransport, jsonFactory, credential)
//                .setApplicationName("My Application")
//                .build();
//
//// Append data to the spreadsheet
//        List<List<Object>> values = new ArrayList<>();
//        values.add(Arrays.asList("", "Woolen", "per","10"));
//
//        ValueRange body = new ValueRange()
//                .setValues(values);
//
//        try {
//            AppendValuesResponse response = service.spreadsheets().values()
//                    .append("1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w", "Sheet2!A:D", body)
//                    .setValueInputOption("USER_ENTERED")
//                    .execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    private void readDataFromGoogleSheet() {
//        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
//        String range = "Sheet2!A1:D100";
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
//                Toast.makeText(add_in_price_list_Activity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//                //ArrayList<pricelistclass> e=new ArrayList<pricelistclass>();
//                //e.add(new pricelistclass("No Internet Connection","", ""));
//                //CustomAdapter2 customAdapter2=new CustomAdapter2(add_in_price_list_Activity.this,R.layout.pricelist_listview,e);
//                //pb.setVisibility(View.GONE);
//                //ls.setAdapter(customAdapter2);
//                // Handle error
//            }
//        });
//
//    }


}