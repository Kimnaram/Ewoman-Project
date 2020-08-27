package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.HashMap;

public class ReviewCreateActivity extends AppCompatActivity {

    private class Review {
        private String title;
        private String content;
        private String uid;
        private int reviewid;
        private String image;


        public Review(String title, String content, String uid, int reviewid, String image) {
            this.title = title;
            this.content = content;
            this.uid = uid;
            this.reviewid = reviewid;
            this.image = image;
        }
    }

    private static final String TAG = "ReviewCreateActivity";

    private static final int REQUEST_CODE = 0001;

    private RelativeLayout rl_image_container;

    private EditText et_review_title;
    private EditText et_review_content;
    private ImageView iv_review_image;
    private ImageButton ib_image_remove;
    private Button btn_image_upload;
    private Button btn_review_upload;

    private FirebaseAuth firebaseAuth;

    private DBOpenHelper dbOpenHelper;

    private String username = "";
    private int count = 0;

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

        btn_image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btn_review_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // InsertDatabase();
                InsertFirebase();
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

        rl_image_container = findViewById(R.id.rl_image_container);

        et_review_title = findViewById(R.id.et_review_title);
        et_review_content = findViewById(R.id.et_review_content);
        iv_review_image = findViewById(R.id.iv_review_image);
        ib_image_remove = findViewById(R.id.ib_image_remove);
        btn_image_upload = findViewById(R.id.btn_image_upload);
        btn_review_upload = findViewById(R.id.btn_review_upload);

    }

//    public void InsertDatabase() {
//
//        dbOpenHelper.open();
//
//        String title = et_review_title.getText().toString().trim();
//        String content = et_review_content.getText().toString().trim();
//        String userid = firebaseAuth.getCurrentUser().getUid();
//        Drawable image = iv_review_image.getDrawable();
//        dbOpenHelper.insertColumn(title, userid, username, content, image);
//
//        dbOpenHelper.close();
//
//        Log.d(TAG, "title : " + title + ", content : " + content);
//
//        finish();
//
//    }

    public void InsertFirebase() {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference boardRef = firebaseDatabase.getReference("board");

        boardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    count = Integer.parseInt(dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        int reviewid = count + 1;
        String title = et_review_title.getText().toString().trim();
        String content = et_review_content.getText().toString().trim();
        String userid = firebaseAuth.getCurrentUser().getUid();
        Drawable image = iv_review_image.getDrawable();

        HashMap<Object, String> hashMap = new HashMap<>();

        if(image != null) {
            hashMap.put("uid", userid);
            hashMap.put("name", username);
            hashMap.put("title", title);
            hashMap.put("content", content);
            hashMap.put("like", "0");
            hashMap.put("image", "Y");
            hashMap.put("category", "Review");
        } else {
            hashMap.put("uid", userid);
            hashMap.put("name", username);
            hashMap.put("title", title);
            hashMap.put("content", content);
            hashMap.put("like", "0");
            hashMap.put("image", "N");
            hashMap.put("category", "Review");
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("board");
        reference.child(reviewid + "").setValue(hashMap);

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
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
