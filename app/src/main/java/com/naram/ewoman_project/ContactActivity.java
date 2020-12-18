package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

public class ContactActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseAuth firebaseAuth;

    private TextView tv_contact_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fr_office_map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.fr_office_map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        initAllComponent();

        tv_contact_email.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {tv_contact_email.getText().toString()};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                startActivity(email);
            }
        });
    }

    public void initAllComponent() {

        tv_contact_email = findViewById(R.id.tv_contact_email);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        LatLng ewoman = new LatLng(37.495413, 127.037104);

        Marker marker = new Marker();
        marker.setPosition(new LatLng(37.495413, 127.037104));

        marker.setMap(naverMap);

        CameraPosition cameraPosition = new CameraPosition(ewoman, 17);
        naverMap.setCameraPosition(cameraPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if (firebaseAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.toolbar_al_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login:
                Intent contact_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                finish();
                startActivity(contact_to_login);
                return true;
            case R.id.menu_signup:
                Intent contact_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                finish();
                startActivity(contact_to_signup);
                return true;
            case R.id.menu_logout:

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(ContactActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_contact = new Intent(getApplicationContext(), ContactActivity.class);
                mDialog.dismiss();

                finish();
                startActivity(logout_to_contact);
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}