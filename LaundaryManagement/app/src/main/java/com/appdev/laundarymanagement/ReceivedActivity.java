package com.appdev.laundarymanagement;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReceivedActivity extends AppCompatActivity {
    BottomNavigationView bv;
    ListView ls;
    TextView tv1;
    SearchView sv;
    LinearLayout ll;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar pb;
    List<List<Object>> dd;
    Boolean issearch=false;
    ArrayList<ReceivedClass> b;
    ArrayList<ReceivedClass> a= new ArrayList<>();
    SharedPreferences sp;
    Integer position2;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received);
        ls=findViewById(R.id.list);
        tv1=findViewById(R.id.textView);
        bv=findViewById(R.id.bottom_nav);
        sv=findViewById(R.id.search);
        ll=findViewById(R.id.homepage);
        pb=findViewById(R.id.progressbar);
        bv=findViewById(R.id.bottom_nav);
        bv.getMenu().findItem(R.id.navigation_received).setChecked(true);
        sp=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        a=new ArrayList<>();
                        readDataFromGoogleSheet();
                        // Your code goes here
                        // In this code, we are just
                        // changing the text in the textbox

                        // This line is important as it explicitly
                        // refreshes only once
                        // If "true" it implicitly refreshes forever
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
        bv.setOnNavigationItemSelectedListener(item -> {
            myClickItem(item);
            return true;
        });
        readDataFromGoogleSheet();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                b = new ArrayList<>();
                if (query.length() == 0) {
                    b.addAll(a);
                    CustomAdapter4 customAdapter2 = new CustomAdapter4(ReceivedActivity.this, R.layout.received_listview, b);
                    ls.setAdapter(customAdapter2);
                    issearch = false;
                } else {
                    issearch = true;
                    b = new ArrayList<>();
                    for (ReceivedClass wp : a) {
                        if (wp.getRoomno().toLowerCase(Locale.getDefault()).contains(query.toLowerCase()) || wp.getName().toLowerCase(Locale.getDefault()).contains(query.toLowerCase())) {
                            b.add(wp);
                        }
                    }
                }
                if (!b.isEmpty()) {
                    if (b.get(0).getRoomno().equals("Room No")) {
                        issearch = false;
                    }
                    CustomAdapter4 customAdapter2 = new CustomAdapter4(ReceivedActivity.this, R.layout.received_listview, b);
                    ls.setAdapter(customAdapter2);
                } else {
                    ArrayList<newClass> c = new ArrayList<>();
                    issearch=false;
                    c.add(new newClass("No Match", "",""));
                    CustomAdapter customAdapter2 = new CustomAdapter(ReceivedActivity.this, R.layout.searchuser_listview, c);
                    ls.setAdapter(customAdapter2);
                    Toast.makeText(ReceivedActivity.this, "No match found", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                b = new ArrayList<>();
                if (newText.length() == 0) {
                    b.addAll(a);
                    CustomAdapter4 customAdapter2 = new CustomAdapter4(ReceivedActivity.this, R.layout.received_listview, b);
                    ls.setAdapter(customAdapter2);
                    issearch = false;
                } else {
                    issearch = true;
                    b = new ArrayList<>();
                    for (ReceivedClass wp : a) {
                        if (wp.getName().toLowerCase(Locale.getDefault()).contains(newText.toLowerCase()) || wp.getRoomno().toLowerCase(Locale.getDefault()).contains(newText.toLowerCase())) {
                            b.add(wp);
                        }
                    }
                    if (!b.isEmpty()) {
                        CustomAdapter4 customAdapter2 = new CustomAdapter4(ReceivedActivity.this, R.layout.received_listview, b);
                        ls.setAdapter(customAdapter2);
                        if (b.get(0).getRoomno().equals("Room No")) {
                            issearch = false;
                        }
                    } else {
                        ArrayList<newClass> c = new ArrayList<>();
                        c.add(new newClass("No Match", "",""));
                        CustomAdapter customAdapter2 = new CustomAdapter(ReceivedActivity.this, R.layout.searchuser_listview, c);
                        issearch=false;
                        ls.setAdapter(customAdapter2);
                    }
                }
                return false;
            }
        });
        registerForContextMenu(ls);
    }
    public void myClickItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_dashboard:
                Intent intent = new Intent(ReceivedActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_pricelist:
                Intent intent2 = new Intent(ReceivedActivity.this, PricelistActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_searchuser:
                Intent intent3 = new Intent(ReceivedActivity.this, SearchUserActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_received:
                Intent intent4 = new Intent(ReceivedActivity.this, ReceivedActivity.class);
                startActivity(intent4);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_settings:
                Intent intent5 = new Intent(ReceivedActivity.this, Feedback.class);
                startActivity(intent5);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
    }
    private void readDataFromGoogleSheet() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet3!A1:AAA";
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
                for(int i=rows.size()-1;i>0;i--){
                    if(rows.get(i).get(0).toString().equals(sp.getString("admin_institute_code",null)) && rows.get(i).get(3).toString().equals("Received")) {
                        a.add(new ReceivedClass(rows.get(i).get(7).toString(), rows.get(i).get(6).toString(), rows.get(i).get(4).toString(), rows.get(i).get(5).toString(), rows.get(i).get(1).toString(), rows.get(i).get(2).toString()));
                    }
                }
                dd=rows;
                CustomAdapter4 customAdapter=new CustomAdapter4(ReceivedActivity.this,R.layout.received_listview,a);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter);
                System.out.println(a.size()==1);
                if(a.size()==0){
                    ArrayList<ReceivedClass> d;
                    d=new ArrayList<ReceivedClass>();
                    d.add(new ReceivedClass("No Received","","","","",""));
                    CustomAdapter4 customAdapter2=new CustomAdapter4(ReceivedActivity.this,R.layout.received_listview,d);
                    ls.setAdapter(customAdapter2);
                }


                // Process the rows here
                //System.out.println(rows.toString());
                // Toast.makeText(ReceivedActivity.this, rows.get(1).get(0).toString(), Toast.LENGTH_SHORT).show();
                //}
//                catch (AssertionError a){
//                    System.out.println(a.getMessage());
//                    Toast.makeText(ReceivedActivity.this, a.getMessage(), Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                Toast.makeText(ReceivedActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                ArrayList<ReceivedClass> e=new ArrayList<ReceivedClass>();
                e.add(new ReceivedClass("No Internet Connection","", "","","",""));
                CustomAdapter4 customAdapter2=new CustomAdapter4(ReceivedActivity.this,R.layout.received_listview,e);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter2);
                // Handle error
            }
        });

    }
    private void readDataFromGoogleSheet2() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet3!A1:AAA";
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
                Integer j=0;
                for(int i=rows.size()-1;i>=0;i--){
                    if(rows.get(i).get(0).toString().equals(sp.getString("admin_institute_code",null)) && rows.get(i).get(3).toString().equals("Received")) {
                        if(j==position2){
                            position2=i;
                        }
                        j=j+1;
                    }
                }
                createSheetsService();
                ValueRange body = new ValueRange()
                        .setValues(Arrays.asList(
                                Arrays.asList("Returned")
                        ));
                editDataToSheet(body);


                // Process the rows here
                //System.out.println(rows.toString());
                // Toast.makeText(ReceivedActivity.this, rows.get(1).get(0).toString(), Toast.LENGTH_SHORT).show();
                //}
//                catch (AssertionError a){
//                    System.out.println(a.getMessage());
//                    Toast.makeText(ReceivedActivity.this, a.getMessage(), Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                Toast.makeText(ReceivedActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                ArrayList<ReceivedClass> e=new ArrayList<ReceivedClass>();
                e.add(new ReceivedClass("No Internet Connection","", "","","",""));
                CustomAdapter4 customAdapter2=new CustomAdapter4(ReceivedActivity.this,R.layout.received_listview,e);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter2);
                // Handle error
            }
        });

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.returned) {
            // Delete the item
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = info.position;
            pb.setVisibility(View.VISIBLE);
            readDataFromGoogleSheet2();

//            Integer pos=position+1;
//            String range="Sheet2!A"+pos.toString()+":C"+pos.toString();

            return true;
        }
        if(item.getItemId()==R.id.edit){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = info.position;
            pb.setVisibility(View.VISIBLE);
            readDataFromGoogleSheet3();

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int pos = info.position;
        position2=pos;
        if (pos != 0) {
            getMenuInflater().inflate(R.menu.recieved_menu, menu);
        }
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
        Integer pos=Integer.parseInt(position2.toString());
        pos=pos+1;
        String posit=pos.toString();
        String RANGE = "Sheet3!D"+posit;
        try {
            UpdateValuesResponse result1 = sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, RANGE, body1)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            Log.d(TAG, "Edit result: " + result1);
            Toast.makeText(this, "Laundry Returned Successfully", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
        } catch (IOException e) {
            Toast.makeText(this, "Unable to send data", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }
    private void readDataFromGoogleSheet3() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet3!A1:AAA";
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
                Integer j = 0;
                for (int i = rows.size() - 1; i >= 0; i--) {
                    if (rows.get(i).get(0).toString().equals(sp.getString("admin_institute_code", null)) && rows.get(i).get(3).toString().equals("Received")) {
                        if (j == position2) {
                            position2 = i;
                        }
                        j = j + 1;
                    }
                }
                sp.edit().putString("name", rows.get(position2).get(7).toString()).apply();
                sp.edit().putString("roomno", rows.get(position2).get(6).toString()).apply();
                sp.edit().putString("cardno", rows.get(position2).get(8).toString()).apply();
                sp.edit().putString("institute_code", rows.get(position2).get(9).toString()).apply();
                sp.edit().putString("cost", rows.get(position2).get(4).toString()).apply();
                readDataFromGoogleSheet5();
                createSheetsService();
                deleteRowInBackground(position2);



                // Process the rows here
                //System.out.println(rows.toString());
                // Toast.makeText(ReceivedActivity.this, rows.get(1).get(0).toString(), Toast.LENGTH_SHORT).show();
                //}
//                catch (AssertionError a){
//                    System.out.println(a.getMessage());
//                    Toast.makeText(ReceivedActivity.this, a.getMessage(), Toast.LENGTH_SHORT).show();
//                }
            }
            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                Toast.makeText(ReceivedActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                ArrayList<ReceivedClass> e=new ArrayList<ReceivedClass>();
                e.add(new ReceivedClass("No Internet Connection","", "","","",""));
                CustomAdapter4 customAdapter2=new CustomAdapter4(ReceivedActivity.this,R.layout.received_listview,e);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter2);
                // Handle error
            }
        });

    }
    private void readDataFromGoogleSheet5() {
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
                for(int i=1;i<rows.size();i++){
                    if(rows.get(i).get(9).toString().equals(sp.getString("admin_institute_code",null)) && rows.get(i).get(2).toString().equals(sp.getString("cardno",null))) {
                        sp.edit().putString("balance", ((Integer)(Integer.parseInt(rows.get(i).get(8).toString())+Integer.parseInt(sp.getString("cost",null)))).toString()).apply();
                        createSheetsService2();
                        ValueRange body = new ValueRange()
                                .setValues(Arrays.asList(
                                        Arrays.asList(sp.getString("balance",null))
                                ));
                        editDataToSheet2(body,i);
                    }
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
                Toast.makeText(ReceivedActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                // Handle error
            }
        });

    }
    private class DeleteRowTask extends AsyncTask<Integer, Void, Boolean> {
        private Exception exception;

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {
                int rowIndex = params[0];
                List<Request> requests = new ArrayList<>();
                requests.add(new Request()
                        .setDeleteDimension(new DeleteDimensionRequest()
                                .setRange(new DimensionRange()
                                        .setSheetId(467113428)
                                        .setDimension("ROWS")
                                        .setStartIndex(rowIndex)
                                        .setEndIndex(rowIndex + 1))));

                BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);

                sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, body).execute();

                return true;
            } catch (Exception e) {
                this.exception = e;
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {

        }
    }

    private void deleteRowInBackground(int rowIndex) {

        new ReceivedActivity.DeleteRowTask().execute(rowIndex);
    }
    private void createSheetsService2() {
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
    private void editDataToSheet2(ValueRange body1,Integer pos) {

        pos=pos+1;
        String posit=pos.toString();
        String RANGE = "Sheet1!S"+posit;
        try {
            UpdateValuesResponse result1 = sheetsService.spreadsheets().values()
                    .update(SPREADSHEET_ID, RANGE, body1)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            Log.d(TAG, "Edit result: " + result1);
            pb.setVisibility(View.GONE);
        } catch (IOException e) {
            Toast.makeText(this, "Unable to send data", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }
}