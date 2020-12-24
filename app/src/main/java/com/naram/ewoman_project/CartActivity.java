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
import android.widget.RelativeLayout;
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

public class CartActivity extends AppCompatActivity {

    private final static String TAG = "CartActivity";

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ITEMNO = "item_no";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_COUNT = "count";
    private static final String TAG_DATE = "date";
    private static String IP_ADDRESS = "IP ADDRESS";

    private String JSONString;
    private JSONArray cart = null;

    private DBOpenHelper dbOpenHelper;

    private RelativeLayout rl_warn_container;

    private ListView lv_cart_product;
    private ListCartAdapter adapter;
    private ListCart listCart;

    private int count = 0;
    private Bitmap img;

    private String useremail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        initAllComponent();

        Cursor iCursor = dbOpenHelper.selectColumns();

        while (iCursor.moveToNext()) {

            String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
            useremail = tempEmail;

        }

        lv_cart_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);

                adapter.notifyDataSetChanged();

                startActivity(intent);
            }
        });
    }

    public void initAllComponent() {

        rl_warn_container = findViewById(R.id.rl_warn_container);

        lv_cart_product = findViewById(R.id.lv_cart_product);
        adapter = new ListCartAdapter();

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

    }

    @Override
    public void onResume() {
        super.onResume();

        GetData task = new GetData();
        task.execute(useremail);

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
                Intent cart_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(cart_to_login);
                return true;
            case R.id.menu_signup:
                Intent cart_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(cart_to_signup);
                return true;
            case R.id.menu_logout:

                useremail = null;
                dbOpenHelper.deleteAllColumns();

                final ProgressDialog mDialog = new ProgressDialog(CartActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_cart = new Intent(getApplicationContext(), CartActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_cart);
                return true;
            case R.id.menu_cart:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CartActivity.this,
                    "로딩중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null) {
                Toast.makeText(getApplicationContext(), "오류가 발생했습니다!", Toast.LENGTH_SHORT).show();
            } else {

                if(result.contains("카트에 담은 것이 없습니다.")) {

                    rl_warn_container.setVisibility(View.VISIBLE);


                } else {

                    JSONString = result;
                    showResult();

                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String email = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/selectCart.php";
            Log.d(TAG, "serverURL : " + serverURL);
            String postParameters = "email=" + email;

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

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                int item_no = Integer.parseInt(item.getString(TAG_ITEMNO));
                String name = item.getString(TAG_NAME);
                String price = item.getString(TAG_PRICE);
                int count = Integer.parseInt(item.getString(TAG_COUNT));
                String date = item.getString(TAG_DATE);
                String image = "";
                boolean imcheck = item.isNull(TAG_IMAGE);
                if (imcheck == false) {
                    image = item.getString(TAG_IMAGE);
                }

                if (imcheck == false) {
                    img = StringToBitmap(image);
                }

                listCart = new ListCart(item_no, name, date, img, count, price);

                adapter.addItem(listCart);

            }

            lv_cart_product.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

}
