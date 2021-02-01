package com.naram.ewoman_project;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostDetailActivity extends AppCompatActivity {

    private final static String TAG = "PostDetailActivity";

    // JSON
    private static final String TAG_RESULTS = "result";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_TITLE = "title";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_LIKE = "post_like";
    private static String IP_ADDRESS = "IP ADDRESS";

    private String JSONString;
    private JSONArray post = null;

    // Database
    private DBOpenHelper dbOpenHelper;

    // Other component
    private ScrollView sv_post_container;

    private LinearLayout ll_progressbar_container;
    private LinearLayout btn_review_like;

    private ProgressBar progressBar;

    private ImageView iv_review_image;
    private TextView tv_review_category;
    private TextView tv_review_title;
    private TextView tv_review_user;
    private TextView tv_review_content;
    private TextView tv_like_count;
    private Button btn_review_delete;
    private Button btn_review_update;

    private String useremail;
    private String post_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

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

            post_no = intent.getStringExtra("post_no");
            Log.d(TAG, "post no : " + post_no);

        }

        Cursor iCursor = dbOpenHelper.selectColumns();

        while (iCursor.moveToNext()) {

            String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
            useremail = tempEmail;

        }

        GetData task = new GetData();
        task.execute(post_no);

        btn_review_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DoPostLikeData doTask = new DoPostLikeData();
                doTask.execute(post_no, useremail);

            }
        });

        btn_review_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 화면으로 이동
//                Intent detail_to_update = new Intent(getApplicationContext(), PostUpdateActivity.class);
//
//                startActivity(detail_to_update);
            }
        });

        btn_review_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 바디
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(PostDetailActivity.this, R.style.AlertDialogStyle);
                // 메세지
                deleteBuilder.setTitle("삭제하시겠습니까?");

                deleteBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DeleteData deleteTask = new DeleteData();
                        deleteTask.execute(post_no);

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

    @Override
    protected void onResume() {
        super.onResume();

        GetData task = new GetData();
        task.execute(post_no);

    }

    public void initAllComponent() {

        sv_post_container = findViewById(R.id.sv_post_container);

        ll_progressbar_container = findViewById(R.id.ll_progressbar_container);

        progressBar = findViewById(R.id.progressBar);

        iv_review_image = findViewById(R.id.iv_review_image);

        tv_review_category = findViewById(R.id.tv_review_category);
        tv_review_title = findViewById(R.id.tv_review_title);
        tv_review_user = findViewById(R.id.tv_review_user);
        tv_review_content = findViewById(R.id.tv_review_content);
        tv_like_count = findViewById(R.id.tv_like_count);

        btn_review_update = findViewById(R.id.btn_review_update);
        btn_review_delete = findViewById(R.id.btn_review_delete);
        btn_review_like = findViewById(R.id.btn_review_like);

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

    }

    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }

    public static Bitmap StringToBitmap(String ImageString) {
        try {
            byte[] bytes = binaryStringToByteArray(ImageString);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Bitmap bitmap = BitmapFactory.decodeStream(bais);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(useremail == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if(useremail != null) {
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
                Intent revdetail_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(revdetail_to_login);
                return true;
            case R.id.menu_signup :
                Intent revdetail_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(revdetail_to_signup);
                return true;
            case R.id.menu_logout :

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(PostDetailActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revdetail = new Intent(getApplicationContext(), PostDetailActivity.class);
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

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String category = item.getString(TAG_CATEGORY);
                tv_review_category.setText(category);

                String name = item.getString(TAG_NAME);
                tv_review_user.setText(name);

                String email = item.getString(TAG_EMAIL);

                String title = item.getString(TAG_TITLE);
                tv_review_title.setText(title);

                String content = item.getString(TAG_CONTENT);
                tv_review_content.setText(content);

                String image = "";
                boolean imcheck = item.isNull(TAG_IMAGE);
                if (imcheck == false || !image.equals("")) {
                    image = item.getString(TAG_IMAGE);
                }

                String like = item.getString(TAG_LIKE);
                tv_like_count.setText(like);

                if (useremail != null) {
                    if (email.equals(useremail)) {
                        btn_review_update.setVisibility(View.VISIBLE);
                        btn_review_delete.setVisibility(View.VISIBLE);
                        btn_review_like.setVisibility(View.GONE);
                    } else {
                        btn_review_update.setVisibility(View.GONE);
                        btn_review_delete.setVisibility(View.GONE);
                        btn_review_like.setVisibility(View.VISIBLE);
                    }
                } else if (useremail == null) {
                    btn_review_update.setVisibility(View.GONE);
                    btn_review_delete.setVisibility(View.GONE);
                    btn_review_like.setVisibility(View.VISIBLE);
                }

                if (imcheck == false || !image.equals("")) {
                    Bitmap bitmap = StringToBitmap(image);
                    iv_review_image.setImageBitmap(bitmap);
                    iv_review_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    iv_review_image.setVisibility(View.VISIBLE);
                }

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(PostDetailActivity.this,
                    "로딩중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null) {
                // 오류 시
            } else {

                JSONString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String post_no = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/selectPost.php";
            String postParameters = "post_no=" + post_no;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "SelectData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private class DeleteData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ll_progressbar_container.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(PostDetailActivity.this, android.R.anim.fade_in));

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ll_progressbar_container.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(PostDetailActivity.this, android.R.anim.fade_out));
            Log.d(TAG, "response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String post_no = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/deletePost.php";
            String postParameters = "post_no=" + post_no;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "DeleteData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    public void showLikeResult(String result) {

        int count = Integer.parseInt(tv_like_count.getText().toString());

        if (result.equals("포스트를 추천했습니다.")) {

            Toast.makeText(getApplicationContext(), "해당 글을 추천했습니다!", Toast.LENGTH_SHORT).show();
            count += 1;

            tv_like_count.setText(Integer.toString(count));

        } else if (result.equals("포스트 추천을 취소했습니다.")) {

            Toast.makeText(getApplicationContext(), "추천을 취소했습니다!", Toast.LENGTH_SHORT).show();
            count -= 1;

            tv_like_count.setText(Integer.toString(count));

        } else {

            Toast.makeText(getApplicationContext(), "오류가 발생했습니다!", Toast.LENGTH_SHORT).show();

        }

    }

    class DoPostLikeData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ll_progressbar_container.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(PostDetailActivity.this, android.R.anim.fade_in));

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ll_progressbar_container.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(PostDetailActivity.this, android.R.anim.fade_out));

            Log.d(TAG, "POST response  - " + result);
            showLikeResult(result);

        }


        @Override
        protected String doInBackground(String... params) {

            String post_no = (String) params[0];
            String email = (String) params[1];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/doPostLike.php";
            String postParameters = "post_no=" + post_no + "&email=" + email;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {

                Log.d(TAG, "Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
    
}
