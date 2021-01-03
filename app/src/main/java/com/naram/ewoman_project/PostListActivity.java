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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostListActivity extends AppCompatActivity {

    private final static String TAG = "PostListActivity";

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "post_no";
    private static final String TAG_NAME = "name";
    private static final String TAG_TITLE = "title";
    private static final String TAG_LIKE = "count(*)";
    private static String IP_ADDRESS = "IP ADDRESS";

    private String JSONString;
    private JSONArray reviews = null;

    // Review list
    private RecyclerView rv_review_container;
    private RecyclerAdapter adapter;
    private ListPost listPost;

    // Other component
    private EditText et_search_text;

    private ImageButton ib_write_review;

    // Database
    private String useremail = null;
    private int Post_no = -1;
    private int Like = -1;
    private boolean state = false;

    private DBOpenHelper dbOpenHelper;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        initAllComponent();

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

        Cursor iCursor = dbOpenHelper.selectColumns();

        while(iCursor.moveToNext()) {

            String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
            useremail = tempEmail;

        }

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                int _id = adapter.getItem(pos).get_id();

                Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("post_no", Integer.toString(_id));
                Log.d(TAG, "_id : " + _id);

                startActivity(intent);
            }
        });

        et_search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        et_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = et_search_text.getText().toString();
                adapter.filter(search);
            }
        });


        ib_write_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(useremail != null) {
                    startActivity(new Intent(getApplicationContext(), PostCreateActivity.class));
                } else {

                }
            }
        });

    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {

        adapter.clearAllItem();
        GetData("http://" + IP_ADDRESS + "/ewoman-php/selectAllPost.php");
        adapter.notifyDataSetChanged();

        super.onResume();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    public void initAllComponent() {

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

        rv_review_container = findViewById(R.id.rv_review_container);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_review_container.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        rv_review_container.setAdapter(adapter);

        et_search_text = findViewById(R.id.et_search_text);

        ib_write_review = findViewById(R.id.ib_write_review);

        firebaseAuth = FirebaseAuth.getInstance();

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
                Intent revlist_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(revlist_to_login);
                return true;
            case R.id.menu_signup:
                Intent revlist_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(revlist_to_signup);
                return true;
            case R.id.menu_logout:

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(PostListActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revlist = new Intent(getApplicationContext(), PostListActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revlist);
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void GetData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(PostListActivity.this,
                        "로딩중입니다.", null, true, true);

            }

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {

                progressDialog.dismiss();

                JSONString = result;

                Log.d(TAG, "response - " + result);
                showList();

            }
        }
    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(JSONString);
            reviews = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < reviews.length(); i++) {
                JSONObject c = reviews.getJSONObject(i);
                String id = c.getString(TAG_ID);
                int post_no = Integer.parseInt(id);
                String name = c.getString(TAG_NAME);
                String title = c.getString(TAG_TITLE);
                String like = c.getString(TAG_LIKE);

                listPost = new ListPost(post_no, title, name, Integer.parseInt(like));
                adapter.addItem(listPost);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
