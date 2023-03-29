package com.appdev.laundarymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class User_Login extends AppCompatActivity {
    Button login, back;
    EditText email,password,institute_code;
    ProgressBar pb;
    SharedPreferences sp;
    Integer flag=0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        login = findViewById(R.id.button2);
        email=findViewById(R.id.editTextTextPersonName4);
        password=findViewById(R.id.editTextTextPersonName5);
        institute_code=findViewById(R.id.editTextTextPersonName7);
        sp=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        back = findViewById(R.id.button3);
        pb=findViewById(R.id.pbar);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(User_Login.this, User_HomePage.class);
                startActivity(intent);*/
                if(institute_code.getText().toString().isEmpty()){
                    institute_code.setError("This Field cannot be empty");
                }
                if(password.getText().toString().isEmpty()){
                    password.setError("This Field cannot be empty");
                }
                if(email.getText().toString().isEmpty()){
                    email.setError("This Field cannot be empty");
                }
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !institute_code.getText().toString().isEmpty()){
                    pb.setVisibility(View.VISIBLE);
                    login.setEnabled(false);
                    readDataFromGoogleSheet2();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Login.this, LauncherActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }
    private void readDataFromGoogleSheet2() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet1!A:J";
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
                    if(rows.get(i).get(5).equals(email.getText().toString()) && rows.get(i).get(6).equals(password.getText().toString()) && rows.get(i).get(9).equals(institute_code.getText().toString())){
                        sp.edit().putString("user_name",rows.get(i).get(0).toString()).apply();
                        sp.edit().putString("user_room_no",rows.get(i).get(1).toString()).apply();
                        sp.edit().putString("user_card_no",rows.get(i).get(2).toString()).apply();
                        sp.edit().putString("user_program",rows.get(i).get(3).toString()).apply();
                        sp.edit().putString("user_year",rows.get(i).get(4).toString()).apply();
                        sp.edit().putString("user_email_id",rows.get(i).get(5).toString()).apply();
                        sp.edit().putString("user_password",rows.get(i).get(6).toString()).apply();
                        sp.edit().putString("user_laundry_required",rows.get(i).get(7).toString()).apply();
                        sp.edit().putString("user_balance",rows.get(i).get(8).toString()).apply();
                        sp.edit().putString("user_institute_code",rows.get(i).get(9).toString()).apply();
                        sp.edit().putString("user_islogin","true").apply();
                        Intent intent=new Intent(User_Login.this,User_HomePage.class);
                        Toast.makeText(User_Login.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                        flag=1;
                    }

                }
                if(flag==0) {
                    login.setEnabled(true);
                    pb.setVisibility(View.GONE);
                    password.setText("");
                    institute_code.setText("");
                    email.setText("");
                    Toast.makeText(User_Login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
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