package com.appdev.laundarymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    TextView t;
    EditText email,password,instcode;
    AppCompatButton login;
    ProgressBar pb;
    Button back;
    SharedPreferences sp;
    Integer flag=0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        t = findViewById(R.id.textView2);
        email=findViewById(R.id.editTextTextPersonEmail);
        instcode=findViewById(R.id.editTextTextPersonName2);
        password=findViewById(R.id.editTextTextPersonName);
        login=findViewById(R.id.button2);
        back = findViewById(R.id.button);
        pb=findViewById(R.id.pbar);
        sp=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, LauncherActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,LaundryManSignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(instcode.getText().toString().isEmpty()){
                    instcode.setError("This Field cannot be empty");
                }
                if(password.getText().toString().isEmpty()){
                    password.setError("This Field cannot be empty");
                }
                if(email.getText().toString().isEmpty()){
                    email.setError("This Field cannot be empty");
                }
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !instcode.getText().toString().isEmpty()){
                    pb.setVisibility(View.VISIBLE);
                    login.setEnabled(false);
                    readDataFromGoogleSheet2();
                }

            }
        });
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
                for(Integer i=0;i<rows.size();i++){
                    if(rows.get(i).get(1).equals(email.getText().toString()) && rows.get(i).get(4).equals(password.getText().toString()) && rows.get(i).get(2).equals(instcode.getText().toString())){
                        sp.edit().putString("admin_institute_name",rows.get(i).get(0).toString()).apply();
                        sp.edit().putString("admin_email_id",rows.get(i).get(1).toString()).apply();
                        sp.edit().putString("admin_institute_code",rows.get(i).get(2).toString()).apply();
                        sp.edit().putString("admin_laundry_name",rows.get(i).get(3).toString()).apply();
                        sp.edit().putString("admin_password",rows.get(i).get(4).toString()).apply();
                        sp.edit().putString("admin_islogin","true").apply();
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finishAffinity();
                        flag=1;
                    }

                }
                if(flag==0) {
                    login.setEnabled(true);
                    pb.setVisibility(View.GONE);
                    password.setText("");
                    instcode.setText("");
                    email.setText("");
                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                login.setEnabled(true);
                pb.setVisibility(View.GONE);
                // Handle error
            }
        });

    }
}