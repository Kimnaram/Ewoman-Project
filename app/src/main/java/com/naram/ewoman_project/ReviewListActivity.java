package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ReviewListActivity extends AppCompatActivity {

    private final static String TAG = "ReviewBoardActivity";

    // Review list
    private RecyclerView rv_review_container;
    private RecyclerAdapter adapter;
    private ListReview listReview;

    // Other component
    private ImageButton ib_write_review;
    private Button btn_delete_review;

    // Database
    private DBOpenHelper mDBOpenHelper;

    private String sort = "_id";
    private String username = "";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_list);

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

        recyclerviewSetting();

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                int _id = adapter.getItem(pos).get_id();

                Intent intent = new Intent(getApplicationContext(), ReviewDetailActivity.class);
                intent.putExtra("_id", Integer.toString(_id));
                Log.d(TAG, "_id : " + _id);

                startActivity(intent);
            }
        });

        ib_write_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() != null) {
                    Intent list_to_create = new Intent(getApplicationContext(), ReviewCreateActivity.class);
                    list_to_create.putExtra("username", username);

                    startActivity(list_to_create);

                } else {

                    Toast.makeText(getApplicationContext(), "후기 작성을 위해서는 로그인이 필요합니다!", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {

        adapter.notifyDataSetChanged();
        recyclerviewSetting();

        super.onResume();

    }

    @Override
    protected void onDestroy() {

        mDBOpenHelper.close();
        super.onDestroy();

    }

    public void initAllComponent() {
        rv_review_container = findViewById(R.id.rv_review_container);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_review_container.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();

        ib_write_review = findViewById(R.id.ib_write_review);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void recyclerviewSetting() {

        mDBOpenHelper = new DBOpenHelper(this);
        mDBOpenHelper.open();
//        mDBOpenHelper.create();

        adapter.clearAllItem();
        showDatabase(sort);

        rv_review_container.setAdapter(adapter);
    }

    public void showDatabase(String sort){
        Cursor iCursor = mDBOpenHelper.sortColumn(sort);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        adapter.clearAllItem();
        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempTitle = iCursor.getString(iCursor.getColumnIndex("title"));
//            tempTitle = setTextLength(tempTitle, 10);
            String tempUID = iCursor.getString(iCursor.getColumnIndex("userid"));
//            tempID = setTextLength(tempID,10);
            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
//            tempName = setTextLength(tempName,10);
            String tempContent = iCursor.getString(iCursor.getColumnIndex("content"));
//            tempContent = setTextLength(tempContent,50);
            String tempLike = iCursor.getString(iCursor.getColumnIndex("like"));
            Drawable tempImage = getResources().getDrawable(R.mipmap.ic_launcher);

            int tempID = Integer.parseInt(tempIndex);

            listReview = new ListReview(tempID, tempTitle, tempUID, tempName, Integer.parseInt(tempLike), tempContent, tempImage);
            adapter.addItem(listReview);
        }
        adapter.notifyDataSetChanged();
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
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
                Intent revlist_to_login = new Intent(getApplicationContext(), mem_LoginActivity.class);

                startActivity(revlist_to_login);
                return true;
            case R.id.menu_signup :
                Intent revlist_to_signup = new Intent(getApplicationContext(), mem_SignupActivity.class);

                startActivity(revlist_to_signup);
                return true;
            case R.id.menu_logout :

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(ReviewListActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revlist = new Intent(getApplicationContext(), ReviewListActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revlist);
                return true;
            case R.id.menu_cart :
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
