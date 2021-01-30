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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class eTodayActivity extends AppCompatActivity {

    private final static String TAG = "eTodayActivity";

    // eToday list
    private RecyclerView rv_etoday_container;
    private eTodayListAdapter adapter;
    private ArrayList<ListPost> listeTodays = new ArrayList<>();

    private Thread thread;

    private DBOpenHelper dbOpenHelper;

    private ProgressBar progressBar;

    private String useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_today);

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

        while (iCursor.moveToNext()) {

            String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
            useremail = tempEmail;

        }

        eToday task = new eToday();
        task.execute();

    }

    public void initAllComponent() {

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

        rv_etoday_container = findViewById(R.id.rv_etoday_container);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_etoday_container.setLayoutManager(linearLayoutManager);

        adapter = new eTodayListAdapter(eTodayActivity.this);
        rv_etoday_container.setAdapter(adapter);

        progressBar = findViewById(R.id.progressBar);

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
                Intent etodaylist_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(etodaylist_to_login);
                return true;
            case R.id.menu_signup:
                Intent etodaylist_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(etodaylist_to_signup);
                return true;
            case R.id.menu_logout:

                useremail = null;
                dbOpenHelper.deleteAllColumns();

                final ProgressDialog mDialog = new ProgressDialog(eTodayActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revlist = new Intent(getApplicationContext(), PostListActivity.class);
                mDialog.dismiss();

                finish();
                startActivity(logout_to_revlist);
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class eToday extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(eTodayActivity.this, android.R.anim.fade_in));

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(eTodayActivity.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                String url = "https://www.ewoman.kr/e-today";
                Document p = null;
                Document doc = null;

                p = Jsoup.connect(url).get();
                Elements pages = p.select("ul.pagination").select("li");

                for (int i = 0; i < pages.size(); i++) {

                    Element page = pages.get(i);

                    String page_number = page.select("li")
                            .select("a")
                            .text();

                    Log.d(TAG, "page : " + page_number);

                    if (page_number.matches("^[0-9]*$")) {

                        int page_no = Integer.parseInt(page_number);
                        String page_url = "https://www.ewoman.kr/e-today?page=" + page_no;

                        doc = Jsoup.connect(page_url).get();
                        Elements contents = doc.select("div#post_card_b2020052606daa3a8a12db").select("div.list-style-card");

                        for (Element inner_content : contents) {

                            String href = inner_content.select("div")
                                    .select("a")
                                    .attr("href");

                            String detailLink = "https://www.ewoman.kr" + href;

                            String title = inner_content.select("div")
                                    .select("a")
                                    .select("div.title")
                                    .text();

                            String simple_content = inner_content.select("div")
                                    .select("a")
                                    .select("div.text")
                                    .text();

                            if (title.contains("오늘의 상식 #")) {
                                String issue_no = title.split("#")[1];
                                issue_no = issue_no.split("N")[0];
                                String allTitle = "오늘의 상식 #" + issue_no;

                                ListPost listeToday = new ListPost(allTitle, simple_content, detailLink);
                                adapter.addItem(listeToday);
                            }

                        }

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }

            return null;

        }
    }

}
