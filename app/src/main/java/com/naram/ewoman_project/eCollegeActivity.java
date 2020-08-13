package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class eCollegeActivity extends AppCompatActivity {

    private Spinner spinner_menu;
    private ListView lv_eCollege_product;
    private ListViewAdapter adapter;
    private ListProduct P;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private String name;
    private int price;
    private int wishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_college);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GRAY)); //툴바 배경색

        InitAllComponent();

        listviewSetting();

        firebaseAuth = FirebaseAuth.getInstance();

        spinner_menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("등록순")) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void InitAllComponent() {

        spinner_menu = findViewById(R.id.spinner_menu);
        lv_eCollege_product = (ListView) findViewById(R.id.lv_eCollege_product);

    }

    private void listviewSetting() {

        adapter = new ListViewAdapter();

        for(int i = 0; i < 2; i++) {
            firebaseDatabase.getInstance().getReference("product/ecollege/" + i).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Drawable d_image = getResources().getDrawable(R.drawable.ewoman_main_logo);
                    String d_name = " ";
                    int d_price = 0;
                    int d_wishlist = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        if (dataSnapshot.getKey().equals("name")) {
                            d_name = dataSnapshot.getValue().toString();
                        } else if (dataSnapshot.getKey().equals("price")) {
                            d_price = Integer.parseInt(dataSnapshot.getValue().toString());
                        } else if (dataSnapshot.getKey().equals("wishlist")) {
                            d_wishlist = Integer.parseInt(dataSnapshot.getValue().toString());
                        } else {
                            Log.d("eCollegeActivity", "오류가 발생했습니다.");
                        }

                        Log.d("eCollegeActivity", "Value :" + dataSnapshot.getValue());

                    }

                    P = new ListProduct(d_image, d_name, d_price, d_wishlist);
                    adapter.addItem(P.getImage(), P.getName(), P.getPrice(), P.getWishlist());

                    lv_eCollege_product.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//        System.out.println("name : " + name);
//        adapter.addItem(name, price, wishlist);
//
//        lv_eCollege_product.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if(firebaseAuth.getCurrentUser() != null) {
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
