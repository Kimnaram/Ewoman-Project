package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class eTodayActivity extends AppCompatActivity {

    private final static String TAG = "eTodayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_today);

        new Thread() {
            @Override
            public void run() {

//                String url = "https://www.instagram.com/ewoman.kr/";
                String url = "https://pf.kakao.com/_xdQaQxb";
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                    Elements contents = doc.select("div#root div#kakaoWrap div#kakaoContent.cont_plus div#mArticle");

                    Log.d(TAG, "size : " + contents.size());

                    for (Element inner_content : contents) {
                        String href = inner_content.select("div")
                                .select("a")
                                .attr("href");
                        Log.d(TAG, "href = " + href);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
