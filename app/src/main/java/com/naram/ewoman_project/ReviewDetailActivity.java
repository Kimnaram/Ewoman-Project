package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;

public class ReviewDetailActivity extends AppCompatActivity {

    private final static String TAG = "ReviewDetailActivity";

    // Other component
    private ImageView iv_review_image;
    private TextView tv_review_title;
    private TextView tv_review_user;
    private TextView tv_review_content;
    private Button btn_review_delete;
    private Button btn_review_update;

    // Database
    private DBOpenHelper mDBOpenHelper;

    private String sort = "_id";
    private int index;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

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
            String TempID = intent.getStringExtra("_id");
            index = Integer.parseInt(TempID);
//            selectColumn(index);
            selectFirebase(index);
        }


        btn_review_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 화면으로 이동
                Intent detail_to_update = new Intent(getApplicationContext(), ReviewUpdateActivity.class);
                detail_to_update.putExtra("_id", Long.toString(index));

                startActivity(detail_to_update);
            }
        });

        btn_review_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 바디
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(ReviewDetailActivity.this, R.style.AlertDialogStyle);
                // 메세지
                deleteBuilder.setTitle("삭제하시겠습니까?");

                deleteBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mDBOpenHelper.deleteColumn(index);

                        finish();

                    }
                });
                // "아니오" 버튼을 누르면 실행되는 리스너
                deleteBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return; // 아무런 작업도 하지 않고 돌아간다
                    }
                });

                deleteBuilder.show();

            }
        });

    }

//    @Override
//    protected void onStart() {
//
//        Intent intent = getIntent();
//        if(intent != null) {
//            String TempID = intent.getStringExtra("_id");
//            index = Long.parseLong(TempID);
//            selectColumn(index);
//        }
//
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//
//        selectColumn(index);
//
//        super.onResume();
//
//    }

    public void initAllComponent() {

        iv_review_image = findViewById(R.id.iv_review_image);

        tv_review_title = findViewById(R.id.tv_review_title);
        tv_review_user = findViewById(R.id.tv_review_user);
        tv_review_content = findViewById(R.id.tv_review_content);

        btn_review_update = findViewById(R.id.btn_review_update);
        btn_review_delete = findViewById(R.id.btn_review_delete);

        firebaseAuth = FirebaseAuth.getInstance();

        mDBOpenHelper = new DBOpenHelper(this);

    }

//    public void selectColumn(long _id) {
//
//        mDBOpenHelper.open();
//
//        Cursor iCursor = mDBOpenHelper.selectColumn(_id);
//
//        while(iCursor.moveToNext()) {
//            String tempTitle = iCursor.getString(iCursor.getColumnIndex("title"));
//            String tempUID = iCursor.getString(iCursor.getColumnIndex("userid"));
//            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
//            String tempContent = iCursor.getString(iCursor.getColumnIndex("content"));
//            String tempLike = iCursor.getString(iCursor.getColumnIndex("like"));
//            String tempImage = iCursor.getString(iCursor.getColumnIndex("image"));
//
//            Log.d(TAG, tempImage);
//
//            if(tempImage.equals("Y")) {
//                Drawable image = mDBOpenHelper.selectImageColumns(_id);
//                iv_review_image.setVisibility(View.VISIBLE);
//                iv_review_image.setImageDrawable(image);
//            } else {
//                iv_review_image.setVisibility(View.GONE);
//            }
//
//            tv_review_title.setText(tempTitle);
//            tv_review_user.setText(tempName);
//            tv_review_content.setText(tempContent);
//
//            if(firebaseAuth.getCurrentUser() != null) {
//                if (firebaseAuth.getCurrentUser().getUid().equals(tempUID)) {
//                    btn_review_update.setVisibility(View.VISIBLE);
//                    btn_review_delete.setVisibility(View.VISIBLE);
//                } else {
//                    btn_review_update.setVisibility(View.GONE);
//                    btn_review_delete.setVisibility(View.GONE);
//                }
//            } else if (firebaseAuth.getCurrentUser() == null) {
//                btn_review_update.setVisibility(View.GONE);
//                btn_review_delete.setVisibility(View.GONE);
//            }
//        }
//
//    }

    public void selectFirebase(int index) {

        final int id = index;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("board/" + index).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.getKey().equals("title"))
                        tv_review_title.setText(dataSnapshot.getValue().toString());
                    else if(dataSnapshot.getKey().equals("name"))
                        tv_review_user.setText(dataSnapshot.getValue().toString());
                    else if(dataSnapshot.getKey().equals("content"))
                        tv_review_content.setText(dataSnapshot.getValue().toString());
                    else if(dataSnapshot.getKey().equals("image")) {
                        if (dataSnapshot.getValue().toString().equals("Y")) {
                            selectStorage(id);
                        } else {
                            iv_review_image.setVisibility(View.GONE);
                        }
                    } else if(dataSnapshot.getKey().equals("uid")) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            if (firebaseAuth.getCurrentUser().getUid().equals(dataSnapshot.getValue().toString())) {
                                btn_review_update.setVisibility(View.VISIBLE);
                                btn_review_delete.setVisibility(View.VISIBLE);
                            } else {
                                btn_review_update.setVisibility(View.GONE);
                                btn_review_delete.setVisibility(View.GONE);
                            }
                        } else if (firebaseAuth.getCurrentUser() == null) {
                            btn_review_update.setVisibility(View.GONE);
                            btn_review_delete.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void selectStorage(int index) {

        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReference("board/" + index);
        StorageReference pathReference = storageReference.child("image.png");

        pathReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Drawable image = Drawable.createFromStream(byteArrayInputStream, "Product Image");

                iv_review_image.setVisibility(View.VISIBLE);
                iv_review_image.setImageDrawable(image);
            }
        });

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
                Intent revdetail_to_login = new Intent(getApplicationContext(), mem_LoginActivity.class);

                startActivity(revdetail_to_login);
                return true;
            case R.id.menu_signup :
                Intent revdetail_to_signup = new Intent(getApplicationContext(), mem_SignupActivity.class);

                startActivity(revdetail_to_signup);
                return true;
            case R.id.menu_logout :

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(ReviewDetailActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revdetail = new Intent(getApplicationContext(), ReviewDetailActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revdetail);
                return true;
            case R.id.menu_cart :
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
