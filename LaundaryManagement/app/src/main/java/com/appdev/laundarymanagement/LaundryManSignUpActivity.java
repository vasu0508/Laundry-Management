package com.appdev.laundarymanagement;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LaundryManSignUpActivity extends AppCompatActivity {
    TextView t;
    EditText institutecode,email,laundryman;
    AppCompatButton submit;
    String pass;
    Integer code=0;
    ProgressBar pb;
    Button back;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_man_sign_up);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        t=findViewById(R.id.textView2);
        institutecode=findViewById(R.id.editTextTextPersonName2);
        email=findViewById(R.id.editTextTextPersonEmail);
        laundryman=findViewById(R.id.editTextTextPersonName);
        submit=findViewById(R.id.button2);
        pb=findViewById(R.id.pbar);
        back = findViewById(R.id.button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LaundryManSignUpActivity.this, LauncherActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(institutecode.getText().toString().isEmpty()){
                    institutecode.setError("This Field cannot be empty");
                }
                if(laundryman.getText().toString().isEmpty()){
                    laundryman.setError("This Field cannot be empty");
                }
                if(email.getText().toString().isEmpty()){
                    email.setError("This Field cannot be empty");
                }
                if(!email.getText().toString().isEmpty() && !laundryman.getText().toString().isEmpty() && !institutecode.getText().toString().isEmpty()){
                    pb.setVisibility(View.VISIBLE);
                    submit.setEnabled(false);
                    readDataFromGoogleSheet2();
                }
            }
        });
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LaundryManSignUpActivity.this,LoginActivity.class);
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
            pb.setVisibility(View.GONE);
            submit.setEnabled(true);
            e.printStackTrace();
        }
        sheetsService = new Sheets.Builder(transport, jsonFactory, credential)
                .setApplicationName("Laundry Management")
                .build();
    }
    private static final String SPREADSHEET_ID = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
    private static final String RANGE = "Sheet4!A:Z";

    private void appendDataToSheet(ValueRange body) {
        try {
            AppendValuesResponse result = sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, RANGE, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            Log.d(TAG, "Append result: " + result);
            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(LaundryManSignUpActivity.this,LoginActivity.class);
            SendMail sm=new SendMail(LaundryManSignUpActivity.this,email.getText().toString(),"Login Credentials for New LaundryMan","Subject: Login Credentials for New LaundryMan\n" +
                    "\n" +
                    "Dear "+laundryman.getText().toString()+",\n" +
                    "\n" +
                    "We are pleased to inform you that your account has been successfully created and you have been added as a new laundry. Your login credentials are as follows:\n" +
                    "\n" +
                    "Email ID: "+email.getText().toString() +"\n" +
                    "Password: "+ pass.toString()+"\n" +
                    "Institute Code: "+code.toString() +"\n" +
                    "\n" +
                    "Please note that your email ID will be your official communication channel for all work-related correspondence.\n" +
                    "\n" +
                    "If you encounter any issues while logging in or accessing any of our systems, please do not hesitate to contact us. We will be happy to provide any assistance that you may require.\n" +
                    "\n" +
                    "Best regards,\n" +
                    "\n" +
                    "LaundryMan");
            sm.execute();
            pb.setVisibility(View.GONE);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } catch (IOException e) {
            pb.setVisibility(View.GONE);
            submit.setEnabled(true);
            Toast.makeText(this, "Unable to send data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void readDataFromGoogleSheet2() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet4!A:E";
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
                code=0;
                if(rows.size()<2){
                    code=1;
                }
                else {
                    code = Integer.parseInt(rows.get(rows.size() - 1).get(2).toString()) + 1;
                }
                Random rand=new Random();
                Integer random=rand.nextInt(999999);
                pass="admin@"+random.toString();
                createSheetsService();
                ValueRange body1 = new ValueRange()
                        .setValues(Arrays.asList(
                                Arrays.asList(institutecode.getText().toString(),email.getText().toString(),code.toString(),laundryman.getText().toString(),pass)
                        ));
                appendDataToSheet(body1);
            }
            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                submit.setEnabled(true);
                pb.setVisibility(View.GONE);
                // Handle error
            }
        });

    }
}