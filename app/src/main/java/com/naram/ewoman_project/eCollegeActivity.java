package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class eCollegeActivity extends AppCompatActivity {

    private int LoginCode = 0;

    private Spinner spinner_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_college);

        Intent intent = getIntent();
        if(intent != null) {
            LoginCode = intent.getExtras().getInt("LoginCode");
        }

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GRAY)); //툴바 배경색

        InitAllComponent();

        spinner_menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).toString().equals("등록순")) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void InitAllComponent() {

        spinner_menu = findViewById(R.id.spinner_menu);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(LoginCode != 1000) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if(LoginCode == 1000) {
            getMenuInflater().inflate(R.menu.toolbar_al_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login :
                Intent main_to_login = new Intent(getApplicationContext(), mem_LoginActivity.class);

                startActivity(main_to_login);
                return true;
            case R.id.menu_signup :
                Intent main_to_signup = new Intent(getApplicationContext(), mem_SignupActivity.class);

                startActivity(main_to_signup);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
