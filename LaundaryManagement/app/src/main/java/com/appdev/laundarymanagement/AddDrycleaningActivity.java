package com.appdev.laundarymanagement;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddDrycleaningActivity extends AppCompatActivity {
    TextView tvname,tvcardno,tvroomno,tvbalance;
    Button back,submit,calculate;
    EditText price,edittext;
    TextView totaltv;
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
        setContentView(R.layout.activity_add_drycleaning);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        back=findViewById(R.id.back);
        totaltv=findViewById(R.id.totaltv);
        price=findViewById(R.id.price);
        edittext=findViewById(R.id.edittext);
        submit=findViewById(R.id.submit);
        calculate=findViewById(R.id.calculate);
        tvname=findViewById(R.id.textView4);
        tvroomno=findViewById(R.id.textView12);
        tvcardno=findViewById(R.id.textView13);
        tvbalance=findViewById(R.id.textView18);
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
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(edittext.getText().toString().equals("0")){
                    edittext.setText("");
                }
            }
        });
        price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(price.getText().toString().equals("0")){
                    price.setText("");
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
                submit.setEnabled(false);
                if(edittext.getText().toString().equals("")||price.getText().toString().equals("")){
                    submit.setEnabled(true);
                    if(edittext.getText().toString().equals("")) {
                        edittext.setError("Enter the number of clothes");
                    }
                    if(price.getText().toString().equals("")) {
                        price.setError("Enter the price");
                    }
                }
                else {
                    totalcost = Double.parseDouble(edittext.getText().toString()) * Double.parseDouble(price.getText().toString());
                    totaltv.setText(totalcost.toString());
                    if(Double.parseDouble(totalcost.toString()) <= Double.parseDouble(balance.toString())) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String currentDateandTime = sdf.format(new Date());
                        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String currentTime = sdf2.format(new Date());
                        createSheetsService();
                        ValueRange body = new ValueRange();
                        ArrayList<Object> ar = new ArrayList<>();
                        ar.add(sharedPreferences.getString("admin_institute_code",null));
                        ar.add(currentDateandTime);
                        ar.add(currentTime);
                        ar.add("Received");
                        ar.add(totalcost.toString());
                        ar.add(edittext.getText().toString());
                        ar.add(roomno);
                        ar.add(name);
                        ar.add(cardno);
                        values1 = new ArrayList<>();
                        ar.add("Dry Cleaning");
                        ar.add(edittext.getText().toString());
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
                        Toast.makeText(AddDrycleaningActivity.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                        submit.setEnabled(true);
                    }
                }

            }
        });
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edittext.getText().toString().equals("")||price.getText().toString().equals("")){
                    if(edittext.getText().toString().equals("")) {
                        edittext.setError("Enter the number of clothes");
                    }
                    if(price.getText().toString().equals("")) {
                        price.setError("Enter the price");
                    }
                }
                else {
                    totalcost = Double.parseDouble(edittext.getText().toString()) * Double.parseDouble(price.getText().toString());
                    totaltv.setText(totalcost.toString());
                }
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
                Toast.makeText(AddDrycleaningActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                // Handle error
            }
        });

    }
}