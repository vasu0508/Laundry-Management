package com.appdev.laundarymanagement;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddUserActivity extends AppCompatActivity {
    TextInputEditText name,year,cardno,email,program,roomno,deposit;
    String nametext,yeartext,cardnotext,emailtext,programtext,roomnotext,deposittext,institutecode,password;
    ProgressBar pb;
    Button submit,back;
    Boolean entry=true;
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        sharedPreferences=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        pb=findViewById(R.id.pbbar);
        StrictMode.setThreadPolicy(policy);
        name=findViewById(R.id.et_name);
        year=findViewById(R.id.et_year);
        cardno=findViewById(R.id.et_card_no);
        email=findViewById(R.id.et_email);
        program=findViewById(R.id.et_program);
        roomno=findViewById(R.id.et_roomno);
        deposit=findViewById(R.id.et_deposit);
        submit=findViewById(R.id.submit);
        back=findViewById(R.id.back);
        name.setText("");
        name.setError(null);
        email.setText("");
        program.setText("");
        year.setText("");
        cardno.setText("");
        roomno.setText("");
        deposit.setText("");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setEnabled(false);
                entry=true;
                if(name.getText().toString().isEmpty()){
                    submit.setEnabled(true);
                    name.setError("This Field cannot be empty");
                }
                if(email.getText().toString().isEmpty()){
                    submit.setEnabled(true);
                    email.setError("This Field cannot be empty");
                }
                if(program.getText().toString().isEmpty()){
                    submit.setEnabled(true);
                    program.setError("This Field cannot be empty");
                }
                if(year.getText().toString().isEmpty()){
                    submit.setEnabled(true);
                    year.setError("This Field cannot be empty");
                }
                if(cardno.getText().toString().isEmpty()){
                    submit.setEnabled(true);
                    cardno.setError("This Field cannot be empty");
                }
                if(roomno.getText().toString().isEmpty()){
                    submit.setEnabled(true);
                    roomno.setError("This Field cannot be empty");
                }
                if(deposit.getText().toString().isEmpty()){
                    submit.setEnabled(true);
                    deposit.setError("This Field cannot be empty");
                }
                if(!roomno.getText().toString().isEmpty() && !name.getText().toString().isEmpty() && !year.getText().toString().isEmpty() && !email.getText().toString().isEmpty() && !program.getText().toString().isEmpty() && !cardno.getText().toString().isEmpty() && !deposit.getText().toString().isEmpty()){
                    nametext=name.getText().toString();
                    yeartext=year.getText().toString();
                    cardnotext=cardno.getText().toString();
                    emailtext=email.getText().toString();
                    programtext=program.getText().toString();
                    roomnotext=roomno.getText().toString();
                    deposittext=deposit.getText().toString();
                    Random rand=new Random();
                    Integer random=rand.nextInt(9999);
                    password=roomnotext+cardnotext+random.toString();
                    institutecode=sharedPreferences.getString("admin_institute_code",null);
                    pb.setVisibility(View.VISIBLE);
                    readDataFromGoogleSheet();
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            e.printStackTrace();
        }
        sheetsService = new Sheets.Builder(transport, jsonFactory, credential)
                .setApplicationName("Laundry Management")
                .build();
    }
    private static final String SPREADSHEET_ID = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
    private static final String RANGE = "Sheet1!A:T";

    private void appendDataToSheet(ValueRange body) {
        try {
            AppendValuesResponse result = sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, RANGE, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            Log.d(TAG, "Append result: " + result);
            Toast.makeText(this, "Data added Successfully", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
        } catch (IOException e) {
            pb.setVisibility(View.GONE);
            Toast.makeText(this, "Unable to send data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void readDataFromGoogleSheet() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet1!A:T";
        String apiKey = "AIzaSyAtB0JJF5JEcr3gCW6W_wz2AHgtBYhGBmk";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sheets.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SheetsService sheetService = retrofit.create(SheetsService.class);
        Call<ValueRange> call = sheetService.getValues(spreadsheetId, range, apiKey);
        call.enqueue(new Callback<ValueRange>() {
            @Override
            public void onResponse(@NonNull Call<ValueRange> call, @NonNull Response<ValueRange> response) {
                //try {
                System.out.println(response.toString());
                ValueRange values = response.body();
                List<List<Object>> rows = values.getValues();
                for (int i=0;i<rows.size();i++){
                    if(rows.get(i).get(2).toString().equals(cardnotext) && rows.get(i).get(9).toString().equals(sharedPreferences.getString("admin_institute_code",null))){
                        entry=false;
                    }
                }
                if(entry){
                    createSheetsService();
                    ValueRange body = new ValueRange()
                            .setValues(Arrays.asList(
                                    Arrays.asList("","","","","","","","","","",nametext,roomnotext,cardnotext,programtext,yeartext,emailtext,password,"FALSE",deposittext,institutecode)
                            ));
                    appendDataToSheet(body);
                    SendMail sm=new SendMail(AddUserActivity.this,emailtext,"Your Login Credentials for Laundry App","Dear "+nametext+",\n" +
                            "\n" +
                            "We are delighted to welcome you to our Laundry Man App! As promised, we are sending you your login credentials to get started.\n" +
                            "\n" +
                            "Please use the following login details to access the app:\n" +
                            "\n" +
                            "Email: " +emailtext+"\n"+
                            "Password: " +password+"\n"+
                            "Institute Code:" +institutecode+"\n"+
                            "Balance: "+deposittext+
                            "\n" +
                            "If you have any questions or encounter any issues while using our app, please don't hesitate to reach out to our customer support team. We are available 24/7 to assist you.\n" +
                            "\n" +
                            "Thank you for choosing our Laundry Man! We hope you enjoy using it as much as we enjoyed creating it for you.\n" +
                            "\n" +
                            "Best regards,\n" +
                            "Laundry Man");
                    sm.execute();
                    Intent intent=new Intent(AddUserActivity.this,SearchUserActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
                else{
                    pb.setVisibility(View.GONE);
                    cardno.setError("Card number must be unique");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                Toast.makeText(AddUserActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
                entry=false;
                // Handle error
            }
        });

    }
}