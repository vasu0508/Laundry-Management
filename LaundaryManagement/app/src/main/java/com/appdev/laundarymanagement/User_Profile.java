package com.appdev.laundarymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class User_Profile extends AppCompatActivity {
    TextView fullname,cardno,balance;
    TextInputEditText name,emailid,program,year,roomno,institutecode;
    SharedPreferences sp;
    Button back;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        fullname=findViewById(R.id.Full_name);
        cardno=findViewById(R.id.cardno);
        balance=findViewById(R.id.balance_profile);
        name=findViewById(R.id.name);
        emailid=findViewById(R.id.email);
        program=findViewById(R.id.program);
        year=findViewById(R.id.year);
        roomno=findViewById(R.id.roomno);
        back=findViewById(R.id.back);
        institutecode=findViewById(R.id.institutecode);
        sp=getSharedPreferences(getResources().getString(R.string.sharedpref),MODE_PRIVATE);
        fullname.setText(sp.getString("user_name",null));
        name.setText(sp.getString("user_name",null));
        cardno.setText(sp.getString("user_card_no",null));
        balance.setText(sp.getString("user_balance",null));
        emailid.setText(sp.getString("user_email_id",null));
        program.setText(sp.getString("user_program",null));
        year.setText(sp.getString("user_year",null));
        roomno.setText(sp.getString("user_room_no",null));
        institutecode.setText(sp.getString("user_institute_code",null));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(User_Profile.this,User_HomePage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

    }
}