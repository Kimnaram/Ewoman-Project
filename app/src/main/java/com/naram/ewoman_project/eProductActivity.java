package com.naram.ewoman_project;

import androidx.annotation.NonNull;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DecimalFormat;

import static com.naram.ewoman_project.CartActivity.binaryStringToByteArray;

public class eProductActivity extends AppCompatActivity {

    private static final String TAG = "eProductActivity";

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

    private ListView lv_eProduct_product;
    private ListViewAdapter adapter;
    private ListItem listItem;

    private RelativeLayout rl_warn_container;

    private EditText et_search_text;
    private TextView tv_search_btn;

    private String useremail = null;
    private Bitmap img = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_product);

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

        et_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                rl_warn_container.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = et_search_text.getText().toString();
                adapter.fillter(search);
                if(adapter.getCount() == 0) {
                    rl_warn_container.setVisibility(View.VISIBLE);
                }
            }
        });

        lv_eProduct_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);

                int number = adapter.getItem(position).getItem_no();

                intent.putExtra("item_no", Integer.toString(number));

                adapter.notifyDataSetChanged();

                startActivity(intent);
            }
        });

    }

    public void InitAllComponent() {

        rl_warn_container = findViewById(R.id.rl_warn_container);

        lv_eProduct_product = findViewById(R.id.lv_eProduct_product);
        et_search_text = findViewById(R.id.et_search_text);
        tv_search_btn = findViewById(R.id.tv_search_btn);


        adapter = new ListViewAdapter();
        lv_eProduct_product.setAdapter(adapter);

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

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

        GetData task = new GetData();
        task.execute("eproduct");

        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

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
                Intent main_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(main_to_login);
                return true;
            case R.id.menu_signup:
                Intent main_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(main_to_signup);
                return true;
            case R.id.menu_logout:

                useremail = null;
                dbOpenHelper.deleteAllColumns();

                final ProgressDialog mDialog = new ProgressDialog(eProductActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_main = new Intent(getApplicationContext(), eProductActivity.class);
                mDialog.dismiss();

                finish();
                startActivity(logout_to_main);
                return true;
            case R.id.menu_cart:
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

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(eProductActivity.this,
                    "로딩중입니다.", null, true, true);

        }

        @Override
        protected String doInBackground(String... params) {

            String category = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/selectItems.php";

            String postParameters = "category=" + category;

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
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {

            progressDialog.dismiss();

            Log.d(TAG, "response - " + result);

            if (result == null) {

                Toast.makeText(getApplicationContext(), "오류가 발생했습니다!", Toast.LENGTH_SHORT).show();

            } else {

                if(result.contains("결과가 없습니다.")) {

                    rl_warn_container.setVisibility(View.VISIBLE);

                } else {

                    JSONString = result;
                    showList();

                }
            }
        }
    }

}
