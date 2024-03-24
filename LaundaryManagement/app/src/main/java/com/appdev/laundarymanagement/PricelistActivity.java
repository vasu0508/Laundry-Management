package com.appdev.laundarymanagement;

import static androidx.constraintlayout.widget.Constraints.TAG;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
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
public class PricelistActivity extends AppCompatActivity {
    ListView ls;
    TextView tv1;
    BottomNavigationView bv;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageButton add;
    ProgressBar pb;
    SharedPreferences sharedpref;
    ArrayList<pricelistclass> a = new ArrayList<>();
    List<List<Object>> sheetdata;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pricelist);
        ls = findViewById(R.id.list);
        tv1 = findViewById(R.id.textView);
        bv = findViewById(R.id.bottom_nav);
        add = findViewById(R.id.add);
        pb = findViewById(R.id.progressbar);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        bv.getMenu().findItem(R.id.navigation_pricelist).setChecked(true);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh2);
        sharedpref = getSharedPreferences(getResources().getString(R.string.sharedpref), MODE_PRIVATE);
        createSheetsService();
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        a = new ArrayList<>();
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
        CustomAdapter2 customAdapter = new CustomAdapter2(PricelistActivity.this, R.layout.pricelist_listview, a);
        readDataFromGoogleSheet();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PricelistActivity.this, add_in_price_list_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        bv.setOnNavigationItemSelectedListener(item -> {
            myClickItem(item);
            return true;
        });
        registerForContextMenu(ls);
    }

    public void myClickItem(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_dashboard:
                Intent intent = new Intent(PricelistActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_pricelist:
                Intent intent2 = new Intent(PricelistActivity.this, PricelistActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_searchuser:
                Intent intent3 = new Intent(PricelistActivity.this, SearchUserActivity.class);
                startActivity(intent3);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_received:
                Intent intent4 = new Intent(PricelistActivity.this, ReceivedActivity.class);
                startActivity(intent4);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.navigation_settings:
                Intent intent5 = new Intent(PricelistActivity.this, SettingsActivity.class);
                startActivity(intent5);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
        }
    }

    private void readDataFromGoogleSheet() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet2!A:D";
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
                sheetdata = rows;
                a.add(new pricelistclass(rows.get(0).get(0).toString(), rows.get(0).get(1).toString(), (String) rows.get(0).get(2)));
                for (int i = 1; i < rows.size(); i++) {
                    if(rows.get(i).get(3).toString().equals(sharedpref.getString("admin_institute_code",null))) {
                        a.add(new pricelistclass(rows.get(i).get(0).toString(), rows.get(i).get(1).toString(), (String) rows.get(i).get(2)));
                    }
                }
                CustomAdapter2 customAdapter = new CustomAdapter2(PricelistActivity.this, R.layout.pricelist_listview, a);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter);
                ls.setClickable(true);
                System.out.println(a.size() == 1);
                if (a.size() == 1) {
                    ArrayList<pricelistclass> d;
                    d = new ArrayList<pricelistclass>();
                    d.add(new pricelistclass("No Particulars added", "", ""));
                    CustomAdapter2 customAdapter2 = new CustomAdapter2(PricelistActivity.this, R.layout.pricelist_listview, d);
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
                Toast.makeText(PricelistActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                ArrayList<pricelistclass> e = new ArrayList<pricelistclass>();
                e.add(new pricelistclass("No Internet Connection", "", ""));
                CustomAdapter2 customAdapter2 = new CustomAdapter2(PricelistActivity.this, R.layout.pricelist_listview, e);
                pb.setVisibility(View.GONE);
                ls.setAdapter(customAdapter2);
                ls.setClickable(false);
                // Handle error
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            // Delete the item
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int position = info.position;
            pricelistclass selecteditem = a.get(position);
            for(int i=0;i<sheetdata.size();i++){
                if(sheetdata.get(i).get(0).toString().equals(selecteditem.getParticular()) && sheetdata.get(i).get(2).toString().equals(selecteditem.getRate()) && sheetdata.get(i).get(1).toString().equals(selecteditem.getUnit()) && sharedpref.getString("admin_institute_code",null).equals(sheetdata.get(i).get(3).toString())){
                    deleteRowInBackground(i);
                    break;
                }
            }
//            Integer pos=position+1;
//            String range="Sheet2!A"+pos.toString()+":C"+pos.toString();
            return true;
        } else if (item.getItemId() == R.id.edit) {
            // Delete the item
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Integer position = info.position;
            pricelistclass selecteditem = a.get(position);
            SharedPreferences.Editor editor = sharedpref.edit();
            for(Integer i=0;i<sheetdata.size();i++){
                if(sheetdata.get(i).get(0).toString().equals(selecteditem.getParticular()) && sheetdata.get(i).get(2).toString().equals(selecteditem.getRate()) && sheetdata.get(i).get(1).toString().equals(selecteditem.getUnit()) && sharedpref.getString("admin_institute_code",null).equals(sheetdata.get(i).get(3).toString())){
                    editor.putString("selected position", (i).toString());
                    break;
                }
            }
            editor.putString("selected particular", selecteditem.getParticular());
            editor.putString("selected rate", selecteditem.getRate());
            editor.putString("selected unit", selecteditem.getUnit());
            editor.commit();
            Intent intent = new Intent(PricelistActivity.this, edit_price_list.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int pos = info.position;
        if (pos != 0) {
            getMenuInflater().inflate(R.menu.pricelist_menu, menu);
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

//    private void deleteRow(int rowIndex) {
//        try {
//
//            List<Request> requests = new ArrayList<>();
//            requests.add(new Request()
//                    .setDeleteDimension(new DeleteDimensionRequest()
//                            .setRange(new DimensionRange()
//                                    .setSheetId(467113428)
//                                    .setDimension("ROWS")
//                                    .setStartIndex(rowIndex)
//                                    .setEndIndex(rowIndex + 1))));
//
//            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
//
//            sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, body).execute();
//
//            Toast.makeText(this, "Data Deleted successfully", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            Toast.makeText(this, "Unable to Delete data", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }

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
            if (success) {
                Toast.makeText(PricelistActivity.this, "Data Deleted successfully", Toast.LENGTH_SHORT).show();
                a = new ArrayList<>();
                readDataFromGoogleSheet();
            } else {
                Toast.makeText(PricelistActivity.this, "Unable to delete Data: " + this.exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteRowInBackground(int rowIndex) {

        new DeleteRowTask().execute(rowIndex);
    }
}

