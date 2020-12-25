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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import static com.naram.ewoman_project.CartActivity.binaryStringToByteArray;

public class eCollegeActivity extends AppCompatActivity {

    private static final String TAG = "eCollegeActivity";

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ITEMNO = "item_no";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_WISHLIST = "wishlist";
    private static String IP_ADDRESS = "IP ADDRESS";

    private String JSONString;
    private JSONArray items = null;

    private DBOpenHelper dbOpenHelper;

    private ListView lv_eCollege_product;
    private ListViewAdapter adapter;
    private ListItem listItem;

    private FirebaseAuth firebaseAuth;

    private String useremail = "";
    private Bitmap img = null;

    private EditText et_search_text;
    private TextView tv_search_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_college);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        InitAllComponent();

        Cursor iCursor = dbOpenHelper.selectColumns();

        while (iCursor.moveToNext()) {

            String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
            useremail = tempEmail;

        }

        lv_eCollege_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);

                int number = adapter.getItem(position).getItem_no();

                intent.putExtra("item_no", Integer.toString(number));

                onPause();
                startActivity(intent);
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

        super.onResume();

        adapter.clearAllItems();
        getData("http://" + IP_ADDRESS + "/ewoman-php/selectItems.php");
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    public void InitAllComponent() {

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

        lv_eCollege_product = findViewById(R.id.lv_eCollege_product);
        et_search_text = findViewById(R.id.et_search_text);
        tv_search_btn = findViewById(R.id.tv_search_btn);

        adapter = new ListViewAdapter();
        lv_eCollege_product.setAdapter(adapter);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    // 바이너리 바이트를 스트링으로
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
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
                Intent main_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(main_to_login);
                return true;
            case R.id.menu_signup :
                Intent main_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(main_to_signup);
                return true;
            case R.id.menu_logout :

                useremail = null;
                dbOpenHelper.deleteAllColumns();

                final ProgressDialog mDialog = new ProgressDialog(eCollegeActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_main = new Intent(getApplicationContext(), eCollegeActivity.class);
                mDialog.dismiss();

                finish();
                startActivity(logout_to_main);
                return true;
            case R.id.menu_cart :
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(JSONString);
            items = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < items.length(); i++) {
                JSONObject c = items.getJSONObject(i);
                int item_no = Integer.parseInt(c.getString(TAG_ITEMNO));
                String image = c.getString(TAG_IMAGE);
                img = StringToBitmap(image);
                String name = c.getString(TAG_NAME);
                int price = Integer.parseInt(c.getString(TAG_PRICE));
                DecimalFormat format = new DecimalFormat("###,###");
                String real_price = format.format(price);
                int wishlist = Integer.parseInt(c.getString(TAG_WISHLIST));

                listItem = new ListItem(item_no, img, name, real_price, wishlist);
                adapter.addItem(listItem);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(eCollegeActivity.this,
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
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}
