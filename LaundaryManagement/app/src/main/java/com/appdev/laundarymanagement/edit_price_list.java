package com.appdev.laundarymanagement;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.Arrays;
import java.util.Collections;

public class edit_price_list extends AppCompatActivity {
    SharedPreferences sharedpref;
    String particularval,rateval,unitval,posit;
    EditText particular,unit,rate;
    Button submit,back;
    String p,u,r;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_price_list);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        particular=findViewById(R.id.partiicularet);
        unit=findViewById(R.id.unitet);
        rate=findViewById(R.id.rateet);
        submit=findViewById(R.id.submit);
        back=findViewById(R.id.back);
        pb=findViewById(R.id.pbar);
        pb.setVisibility(View.INVISIBLE);
        sharedpref=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        particularval=sharedpref.getString("selected particular",null);
        rateval=sharedpref.getString("selected rate",null);
        unitval=sharedpref.getString("selected unit",null);
        particular.setText(particularval);
        rate.setText(rateval);
        unit.setText(unitval);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setEnabled(false);
                pb.setVisibility(View.VISIBLE);
                p=particular.getText().toString();
                u=unit.getText().toString();
                r=rate.getText().toString();
                if(p.isEmpty()){
                    pb.setVisibility(View.INVISIBLE);
                    particular.setError("This field cannot be Empty");
                    submit.setEnabled(true);
                }
                else if(u.isEmpty()){
                    pb.setVisibility(View.INVISIBLE);
                    unit.setError("This field cannot be Empty");
                    particular.setError(null);
                    submit.setEnabled(true);
                }
                else if(r.isEmpty()){
                    pb.setVisibility(View.INVISIBLE);
                    rate.setError("This field cannot be Empty");
                    unit.setError(null);
                    particular.setError(null);
                    submit.setEnabled(true);
                }
                else{
                    particular.setError(null);
                    unit.setError(null);
                    rate.setError(null);
                    createSheetsService();
                    ValueRange body = new ValueRange()
                            .setValues(Arrays.asList(
                                    Arrays.asList(p,u,r)
                            ));
                    editDataToSheet(body);
                    Intent intent=new Intent(edit_price_list.this,PricelistActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(edit_price_list.this,PricelistActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
    private void editDataToSheet(ValueRange body1) {
        posit=sharedpref.getString("selected position",null);
        Integer pos=Integer.parseInt(posit);
        pos=pos+1;
        posit=pos.toString();
        String RANGE = "Sheet2!A"+posit+":C"+posit;
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
}