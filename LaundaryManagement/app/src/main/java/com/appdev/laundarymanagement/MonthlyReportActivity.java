package com.appdev.laundarymanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.services.sheets.v4.model.ValueRange;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MonthlyReportActivity extends AppCompatActivity {
    Spinner sp;
    Button button;
    NumberPicker year;
    List<List<Object>> value=new ArrayList<>();
    List<List<Object>> monthlyReport=new ArrayList<>();
    SharedPreferences sharedPreferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_report);

        readDataFromGoogleSheet2();
        sp=findViewById(R.id.spinner);
        button=findViewById(R.id.download);
        year=findViewById(R.id.year);
        sharedPreferences=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        //Assign spinner elements as R.array.month
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.months, android.R.layout.simple_spinner_item);
        sp.setAdapter(adapter);
        year.setMinValue(Calendar.getInstance().get(Calendar.YEAR)-5);
        year.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(MonthlyReportActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    ActivityCompat.requestPermissions(MonthlyReportActivity.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            PERMISSION_REQUEST_CODE);
//                }
                String month=sp.getSelectedItem().toString();
                monthlyReport=new ArrayList<>();
                monthlyReport.add(Arrays.asList("Card No.","Name","Program","Year","Room No.","Email","Balance",month+"'s Spending","Total Clothes in "+month));
                readDataFromGoogleSheet();

            }
        });
    }
    private void readDataFromGoogleSheet() {
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

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date date;
                Integer index=0;
                for(int i=1;i<rows.size();i++){
                    Integer cost=0;
                    Integer clothes=0;

                    if(rows.get(i).get(9).toString().equals(sharedPreferences.getString("admin_institute_code",null))) {
                        index=index+1;
                        monthlyReport.add(Arrays.asList(rows.get(i).get(2), rows.get(i).get(0), rows.get(i).get(3), rows.get(i).get(4), rows.get(i).get(1), rows.get(i).get(5), rows.get(i).get(8), "0", "0"));
                        for(int j=0;j<value.size();j++){
                            try {
                                date=format.parse(value.get(j).get(1).toString());

                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            Integer yearof=date.getYear();
                            System.out.println(yearof);
                            String Monthname=getMonth(date.getMonth());
                            System.out.println(sp.getSelectedItem().toString());
                            if(value.get(j).get(0).toString().equals(rows.get(i).get(9).toString()) && value.get(j).get(8).toString().equals(rows.get(i).get(2).toString()) && sp.getSelectedItem().toString().equals(Monthname) && year.getValue()==yearof+1900){
                                cost=cost+Integer.parseInt(value.get(j).get(4).toString());
                                clothes=clothes+Integer.parseInt(value.get(j).get(5).toString());
                                monthlyReport.get(index).set(7,cost.toString());
                                monthlyReport.get(index).set(8,clothes.toString());
                            }
                        }
                    }

                }
                System.out.println(monthlyReport);
                openInExcel(monthlyReport);
//                Workbook workbook = new XSSFWorkbook();
//                Sheet sheet = workbook.createSheet("MySheet");
//
//                for (int i = 0; i < monthlyReport.size(); i++) {
//                    Row row = sheet.createRow(i);
//                    List<Object> rowData = monthlyReport.get(i);
//                    for (int j = 0; j < rowData.size(); j++) {
//                        Cell cell = row.createCell(j);
//                        cell.setCellValue(rowData.get(j).toString());
//                    }
//                }
//
//// Save the workbook to a file
//                String filePath = Environment.getExternalStorageDirectory() + "/Download/myFile.xlsx";
//                FileOutputStream outputStream = null;
//                try {
//                    outputStream = new FileOutputStream(filePath);
//                } catch (FileNotFoundException e) {
//                    Toast.makeText(MonthlyReportActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
//                    throw new RuntimeException(e);
//                }
//                try {
//                    workbook.write(outputStream);
//                } catch (IOException e) {
//                    Toast.makeText(MonthlyReportActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
//                    throw new RuntimeException(e);
//                }
//                try {
//                    workbook.close();
//                } catch (IOException e) {
//                    Toast.makeText(MonthlyReportActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
//                    throw new RuntimeException(e);
//                }
//                try {
//                    outputStream.close();
//                } catch (IOException e) {
//                    Toast.makeText(MonthlyReportActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
//                    throw new RuntimeException(e);
//                }

            }

            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                Toast.makeText(MonthlyReportActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                // Handle error
            }
        });
    }
    private String getMonth(int month){
        month=month+1;
        if(month==1){
            return "January";
        }
        else if(month==2){
            return "February";
        }
        else if(month==3){
            return "March";
        }
        else if(month==4){
            return "April";
        }
        else if(month==5){
            return "May";
        }
        else if(month==6){
            return "June";
        }
        else if(month==7){
            return "July";
        }
        else if(month==8){
            return "August";
        }
        else if(month==9){
            return "September";
        }
        else if(month==10){
            return "October";
        }
        else if(month==11){
            return "November";
        }
        else if(month==12){
            return "December";
        }
        else{
            return "Invalid Month";
        }
    }
    private void readDataFromGoogleSheet2() {
        String spreadsheetId = "1myN4i5Nu7oTZqm9CrOyT4O7aQjJ7f8AcucQ1-MnmU4w";
        String range = "Sheet3!A:I";
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
                    if(rows.get(i).get(0).toString().equals(sharedPreferences.getString("admin_institute_code",null))) {
                        value.add(rows.get(i));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ValueRange> call, @NonNull Throwable t) {
                Toast.makeText(MonthlyReportActivity.this, "Unable to fetch data", Toast.LENGTH_SHORT).show();
                // Handle error
            }
        });
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            // If request is cancelled, the result arrays are empty.
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission was granted, you can perform the related task
//            } else {
//                Toast.makeText(this, "Grant Permission to Continue", Toast.LENGTH_SHORT).show();
//                // Permission denied, disable the functionality that depends on this permission.
//            }
//        }
//    }
    private void openInExcel(List<List<Object>> data) {
        String csvData = convertToCSV(data);
        ((CsvContentProvider) MonthlyReportActivity.this.getContentResolver().acquireContentProviderClient(CsvContentProvider.CONTENT_URI).getLocalContentProvider()).setCsvData(csvData);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(CsvContentProvider.CONTENT_URI, "text/csv");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application available to view CSV files", Toast.LENGTH_SHORT).show();
        }
    }
    private String convertToCSV(List<List<Object>> data) {
        StringBuilder csvBuilder = new StringBuilder();

        for (List<Object> row : data) {
            boolean firstColumn = true;
            for (Object item : row) {
                if (!firstColumn) {
                    csvBuilder.append(",");
                }
                csvBuilder.append(item.toString());
                firstColumn = false;
            }
            csvBuilder.append("\n");
        }

        return csvBuilder.toString();
    }

}