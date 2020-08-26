package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.InputStream;
import java.sql.Blob;

public class ReviewUpdateActivity extends AppCompatActivity {

    private static final String TAG = "ReviewCreateActivity";

    private static final int REQUEST_CODE = 0001;

    private RelativeLayout rl_image_container;

    private EditText et_review_title;
    private EditText et_review_content;
    private ImageView iv_review_image;
    private ImageButton ib_image_remove;
    private Button btn_image_upload;
    private Button btn_review_update;

    private FirebaseAuth firebaseAuth;

    private DBOpenHelper dbOpenHelper;

    private long index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_update);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        initAllComponent();

        Intent intent = getIntent();
        if (intent != null) {
            String _id = intent.getStringExtra("_id");
            index = Long.parseLong(_id);
            selectColumn(index);
        }

        et_review_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        btn_image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btn_review_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateDatabase();
            }
        });

        ib_image_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_review_image.setImageDrawable(null);
                rl_image_container.setVisibility(View.GONE);
            }
        });

    }

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

        rl_image_container = findViewById(R.id.rl_image_container);

        et_review_title = findViewById(R.id.et_review_title);
        et_review_content = findViewById(R.id.et_review_content);
        iv_review_image = findViewById(R.id.iv_review_image);
        ib_image_remove = findViewById(R.id.ib_image_remove);
        btn_image_upload = findViewById(R.id.btn_image_upload);
        btn_review_update = findViewById(R.id.btn_review_update);

    }

    public void selectColumn(long _id) {
        Cursor iCursor = dbOpenHelper.selectColumn(_id);
        Log.d(TAG, "DB Size: " + iCursor.getCount());

        while (iCursor.moveToNext()) {
            String tempTitle = iCursor.getString(iCursor.getColumnIndex("title"));
            String tempUID = iCursor.getString(iCursor.getColumnIndex("userid"));
            String tempContent = iCursor.getString(iCursor.getColumnIndex("content"));
            String tempImage = iCursor.getString(iCursor.getColumnIndex("image"));

            if(tempImage.equals("Y")) {
                Drawable image = dbOpenHelper.selectImageColumns(_id);

                rl_image_container.setVisibility(View.VISIBLE);
                iv_review_image.setImageDrawable(image);
            } else {
                rl_image_container.setVisibility(View.GONE);
            }
            et_review_title.setText(tempTitle);
            et_review_content.setText(tempContent);

        }

    }

    public void UpdateDatabase() {

        String title = et_review_title.getText().toString().trim();
        String content = et_review_content.getText().toString().trim();
        Drawable image = iv_review_image.getDrawable();

        dbOpenHelper.updateColumn(index, title, content, image);
        dbOpenHelper.close();

        Log.d(TAG, "title : " + title + ", content : " + content);

        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    rl_image_container.setVisibility(View.VISIBLE);
                    iv_review_image.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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
                Intent revupdate_to_login = new Intent(getApplicationContext(), mem_LoginActivity.class);

                startActivity(revupdate_to_login);
                return true;
            case R.id.menu_signup:
                Intent revupdate_to_signup = new Intent(getApplicationContext(), mem_SignupActivity.class);

                startActivity(revupdate_to_signup);
                return true;
            case R.id.menu_logout:

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(ReviewUpdateActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revupdate = new Intent(getApplicationContext(), ReviewUpdateActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revupdate);
                return true;
            case R.id.menu_cart:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
