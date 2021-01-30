package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class eTodayDetailActivity extends AppCompatActivity {

    private final static String TAG = "eTodayDetail";

    private DBOpenHelper dbOpenHelper;

    private LinearLayout ll_content_container;

    private ProgressBar progressBar;

    private TextView tv_item_title;
    private TextView tv_item_content;

    private String useremail;

    private String allContent = "";
    private String tag = "";

    private ArrayList<String> imageList = new ArrayList<String>();

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_today_detail);

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

        Intent intent = getIntent();

        if(intent != null) {

            String title = intent.getStringExtra("title");
            tv_item_title.setText(title);

            String url = intent.getStringExtra("detail_url");

            eToday task = new eToday();
            task.execute(url);

        }


    }

    public void initAllComponent() {

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

        ll_content_container = findViewById(R.id.ll_content_container);

        progressBar = findViewById(R.id.progressBar);

        tv_item_title = findViewById(R.id.tv_item_title);
        tv_item_content = findViewById(R.id.tv_item_content);
    }

    private class eToday extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(eTodayDetailActivity.this, android.R.anim.fade_in));

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            tv_item_content.setText(allContent);

            DownloadFilesTask imgTask = new DownloadFilesTask();
            imgTask.execute(Integer.toString(imageList.size()));

            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(eTodayDetailActivity.this, android.R.anim.fade_out));
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(String... params) {

            String url = params[0];

            try {

                Document doc = null;

                doc = Jsoup.connect(url).get();
                Elements contents = doc.select("div.margin-top-xxl").select("p");

                Log.d(TAG, "size : " + contents.size());

                for(int i = 0; i < contents.size(); i++) {

                    Element content = contents.get(i);

                    String p = content.select("p")
                            .text();

                    String img = content.select("img")
                            .attr("src");

                    if(!img.equals("")) {
                        imageList.add(img);
                    }
                    Log.d(TAG, "image : " + img);

                    p += "\n";

                    if(p.equals("")) {
                        p = "\n";
                    }

                    if(p.contains("#슬기로운e우먼")) {
                        tag = p;
                    } else {
                        allContent += p;
                        if(allContent.contains("\n\n\n")) {
                            allContent = allContent.split("\n\n\n")[0];
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

    private class DownloadFilesTask extends AsyncTask<String, Void, ArrayList<Bitmap>> {
        @Override
        protected ArrayList<Bitmap> doInBackground(String... strings) {

            ArrayList<Bitmap> bmpList = new ArrayList<>();
            Bitmap bmp = null;

            try {
                String array_size = strings[0];

                int size = imageList.size();

                for(int i = 0; i < size; i++) {

                        URL url = new URL(imageList.get(i));
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        bmpList.add(bmp);

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(ArrayList<Bitmap> resultList) {
            // doInBackground 에서 받아온 total 값 사용 장소

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            for(int i = 0; i < resultList.size(); i++) {

                ImageView iv_item_image = new ImageView(eTodayDetailActivity.this);
                iv_item_image.setImageBitmap(resultList.get(i));
                iv_item_image.setBackgroundColor(getResources().getColor(R.color.colorCocoa));
                iv_item_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                param.setMargins(0, 0, 0, 20);
                iv_item_image.setLayoutParams(param);
                ll_content_container.addView(iv_item_image);
            }

            //Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquarebold.ttf");
            // 커스텀폰트 오류 발생, 시스템폰트 사용으로 변경
            Typeface typeface = null;
            try {
                typeface = Typeface.createFromAsset(eTodayDetailActivity.this.getAssets(), "nanumbarungothicbold.ttf");
            } catch (Exception e) {
                typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
            }

            TextView tv_item_tag = new TextView(eTodayDetailActivity.this);
            tv_item_tag.setText(tag);
            tv_item_tag.setTextSize(16);
            tv_item_tag.setTypeface(typeface);
            param.setMargins(0, 40, 0, 0);
            tv_item_tag.setLayoutParams(param);
            ll_content_container.addView(tv_item_tag);

        }
    }

}
