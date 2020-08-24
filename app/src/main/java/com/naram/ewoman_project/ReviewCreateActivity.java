package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ReviewCreateActivity extends AppCompatActivity {

    private static final String TAG = "ReviewCreateActivity";

    private EditText et_review_title;
    private EditText et_review_content;
    private Button btn_image_upload;
    private Button btn_review_upload;

    private FirebaseAuth firebaseAuth;

    private DBOpenHelper dbOpenHelper;

    private String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_create);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        initAllComponent();

        Intent intent = getIntent();
        if(intent != null) {
            username = intent.getStringExtra("username");
        }

        et_review_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        btn_review_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertDatabase();
            }
        });

    }

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

        et_review_title = findViewById(R.id.et_review_title);
        et_review_content = findViewById(R.id.et_review_content);
        btn_image_upload = findViewById(R.id.btn_image_upload);
        btn_review_upload = findViewById(R.id.btn_review_upload);

    }

    public void InsertDatabase() {

        String title = et_review_title.getText().toString().trim();
        String content = et_review_content.getText().toString().trim();
        String userid = firebaseAuth.getCurrentUser().getUid();

        dbOpenHelper.insertColumn(title, userid, username, content);
        dbOpenHelper.close();

        Log.d(TAG, "title : " + title + ", content : " + content);

        finish();

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
                Intent revcreate_to_login = new Intent(getApplicationContext(), mem_LoginActivity.class);

                startActivity(revcreate_to_login);
                return true;
            case R.id.menu_signup :
                Intent revcreate_to_signup = new Intent(getApplicationContext(), mem_SignupActivity.class);

                startActivity(revcreate_to_signup);
                return true;
            case R.id.menu_logout :

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(ReviewCreateActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revcreate = new Intent(getApplicationContext(), ReviewCreateActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revcreate);
                return true;
            case R.id.menu_cart :
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
