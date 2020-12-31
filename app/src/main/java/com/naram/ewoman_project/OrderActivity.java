package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = "OrderActivity";

    private DBOpenHelper dbOpenHelper;

    private ArrayList<Integer> orderList = new ArrayList<Integer>();

    private String useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        Intent intent = getIntent();

        String prevPage = intent.getStringExtra("prevPage");

        if (prevPage.equals("cartPage")) {
            int size = Integer.parseInt(intent.getStringExtra("size"));
            for (int i = 0; i < size; i++) {

                String item_no = intent.getStringExtra("orderList[" + i + "]");

                orderList.add(Integer.parseInt(item_no));
                Log.d(TAG, "orderList add : " + item_no);

            }
        } else if (prevPage.equals("DetailPage")) {

            String item_no = intent.getStringExtra("item_no");
            String allPrice = intent.getStringExtra("allPrice");

        }

        initAllComponent();

        Cursor iCursor = dbOpenHelper.selectColumns();

        while (iCursor.moveToNext()) {

            String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
            useremail = tempEmail;

        }

    }

    public void initAllComponent() {

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (useremail == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if (useremail != null) {
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
                Intent cart_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(cart_to_login);
                return true;
            case R.id.menu_signup:
                Intent cart_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(cart_to_signup);
                return true;
            case R.id.menu_logout:

                useremail = null;
                dbOpenHelper.deleteAllColumns();

                final ProgressDialog mDialog = new ProgressDialog(OrderActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                finish();
                Intent logout_to_cart = new Intent(getApplicationContext(), MainActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_cart);
                return true;
            case R.id.menu_cart:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
