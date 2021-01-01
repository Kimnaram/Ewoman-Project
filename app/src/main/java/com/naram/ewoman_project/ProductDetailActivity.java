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
import android.widget.Button;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.naram.ewoman_project.CartActivity.binaryStringToByteArray;

public class ProductDetailActivity extends AppCompatActivity {

    private final static String TAG = "ProductDetailActivity";

    private static final String TAG_RESULTS = "result";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_INFORM = "inform";
    private static final String TAG_DELIV_METHOD = "deliv_method";
    private static final String TAG_DELIV_PRICE = "deliv_price";
    private static final String TAG_DELIV_INFORM = "deliv_inform";
    private static final String TAG_MIN_QUANTITY = "minimum_quantity";
    private static final String TAG_MAX_QUANTITY = "maximum_quantity";
    private static final String TAG_WISHLIST = "wishlist";
    private static String IP_ADDRESS = "IP ADDRESS";

    private String JSONString;
    private String JSONWISHString;
    private JSONArray item = null;

    private DBOpenHelper dbOpenHelper;

    private RelativeLayout rl_develop_btn_container;
    private RelativeLayout rl_user_btn_container;

    private RelativeLayout rl_warn_container;

    private TextView tv_toolbar_title;
    private TextView tv_category_home;
    private TextView tv_category_item_name;
    private TextView tv_category_name;
    private ImageView iv_item_image;
    private TextView tv_item_name;
    private TextView tv_item_price;
    private TextView tv_item_detail;
    private TextView tv_item_deliv_method_is;
    private TextView tv_item_deliv_price_is;
    private TextView tv_buy_try;
    private TextView tv_cart_try;
    private TextView tv_wishlist_try;

    private Button btn_count_minus;
    private Button btn_count_plus;
    private TextView tv_count_view;

    private Bitmap img = null;

    private String item_no = null;
    private int count = 0;
    private int min_quantity = 1;
    private int max_quantity = 10;
    private boolean Wishlist = false;
    private String useremail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

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

        if(useremail.equals("develop@naver.com")) {

            rl_user_btn_container.setVisibility(View.GONE);

            rl_develop_btn_container.setVisibility(View.VISIBLE  );

        }

        Intent intent = getIntent();
        if (intent != null) {
            item_no = intent.getStringExtra("item_no");

            if (tv_category_name.getText().toString().equals("e-College")) {
                tv_buy_try.setText("예약하기");
            } else {
                tv_buy_try.setText("구매하기");
            }

            tv_category_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tv_category_name.getText().toString().equals("e-College")) {
                        Intent intent = new Intent(getApplicationContext(), eCollegeActivity.class);

                        finish();
                        startActivity(intent);
                    } else if (tv_category_name.getText().toString().equals("e-Product")) {
                        Intent intent = new Intent(getApplicationContext(), eProductActivity.class);

                        finish();
                        startActivity(intent);
                    }
                }
            });

            tv_category_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    finish();
                    startActivity(intent);
                }
            });

        }

        tv_wishlist_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // InsertWishData

                if (useremail != null) {

                    String wish_string = tv_wishlist_try.getText().toString();
                    int wish_int = Integer.parseInt(wish_string);

                    if (Wishlist == false) {
                        // 아직 추천을 하지 않았다면

                        InsertWishData task = new InsertWishData();
                        task.execute("http://" + IP_ADDRESS + "/ewoman-php/insertWish.php", item_no, useremail);
                        Wishlist = true;

                        wish_int += 1;

                        tv_wishlist_try.setText(Integer.toString(wish_int));

                    } else if (Wishlist == true) {
                        // 추천을 했다면

                        DeleteWishData task = new DeleteWishData();
                        task.execute(item_no, useremail);
                        Wishlist = false;

                        wish_int -= 1;

                        tv_wishlist_try.setText(Integer.toString(wish_int));

                    }
                } else {

                    Toast.makeText(getApplicationContext(), "로그인이 필요한 기능입니다.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        tv_cart_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // InsertCartData

                if (useremail != null) {

                    Date date = new Date();
                    date.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String now = dateFormat.format(date);
                    String count = tv_count_view.getText().toString();

                    InsertCartData cartTask = new InsertCartData();
                    cartTask.execute("http://" + IP_ADDRESS + "/ewoman-php/insertCart.php", item_no, useremail, count, now);

                } else {

                    Toast.makeText(getApplicationContext(), "로그인이 필요한 기능입니다.", Toast.LENGTH_SHORT).show();

                }
            }
        });

        tv_buy_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int price = Integer.parseInt(tv_item_price.getText().toString());
                int count = Integer.parseInt(tv_count_view.getText().toString());
                int deliv_price = Integer.parseInt(tv_item_deliv_price_is.getText().toString());

                if (tv_buy_try.getText().toString().equals("예약하기")) {
                    // 예약하기 화면으로 넘어가기
                } else {
                    // 주문하기 화면으로 넘어가기
                    int allPrice = count * price + deliv_price;

                    Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                    intent.putExtra("prevPage", "DetailPage");
                    intent.putExtra("item_no", item_no);
                    intent.putExtra("price", Integer.toString(allPrice));

                }

            }
        });

        count = Integer.parseInt(tv_count_view.getText().toString());

        if (min_quantity <= count && count < max_quantity) {
            btn_count_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count != min_quantity) {
                        count -= 1;
                        tv_count_view.setText(count + "");
                    }
                }
            });
            btn_count_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count += 1;
                    tv_count_view.setText(count + "");
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        GetData task = new GetData();
        task.execute(item_no);

        if(useremail != null) {

            GetWishData wishTask = new GetWishData();
            wishTask.execute(item_no, useremail);

        }
    }

    public void initAllComponent() {

        rl_develop_btn_container = findViewById(R.id.rl_develop_btn_container);
        rl_user_btn_container = findViewById(R.id.rl_user_btn_container);

        rl_warn_container = findViewById(R.id.rl_warn_container);

        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);

        tv_category_home = findViewById(R.id.tv_category_home);
        tv_category_name = findViewById(R.id.tv_category_name);
        tv_category_item_name = findViewById(R.id.tv_category_item_name);

        iv_item_image = findViewById(R.id.iv_item_image);
        tv_item_name = findViewById(R.id.tv_item_name);
        tv_item_price = findViewById(R.id.tv_item_price);
        tv_item_detail = findViewById(R.id.tv_item_detail);
        tv_item_deliv_method_is = findViewById(R.id.tv_item_deliv_method_is);
        tv_item_deliv_price_is = findViewById(R.id.tv_item_deliv_price_is);

        tv_buy_try = findViewById(R.id.tv_buy_try);
        tv_cart_try = findViewById(R.id.tv_cart_try);
        tv_wishlist_try = findViewById(R.id.tv_wishlist_try);

        btn_count_minus = findViewById(R.id.btn_count_minus);
        btn_count_plus = findViewById(R.id.btn_count_plus);
        tv_count_view = findViewById(R.id.tv_count_view);

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

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
    public void onBackPressed() {

        finish();

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
                Intent product_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                finish();
                startActivity(product_to_login);
                return true;
            case R.id.menu_signup:
                Intent product_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                finish();
                startActivity(product_to_signup);
                return true;
            case R.id.menu_logout:

                final ProgressDialog mDialog = new ProgressDialog(ProductDetailActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                useremail = null;

                dbOpenHelper.deleteAllColumns();

                Intent logout_to_product = new Intent(getApplicationContext(), ProductDetailActivity.class);
                logout_to_product.putExtra("item_no", item_no);
                mDialog.dismiss();

                finish();
                startActivity(logout_to_product);
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            item = jsonObject.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < item.length(); i++) {

                JSONObject component = item.getJSONObject(i);

                String category = component.getString(TAG_CATEGORY);
                if(category.equals("ecollege")) {
                    tv_category_name.setText("e-College");
                } else if(category.equals("eproduct")) {
                    tv_category_name.setText("e-Product");
                }
                String name = component.getString(TAG_NAME);
                tv_toolbar_title.setText(name);
                tv_item_name.setText(name);
                tv_category_item_name.setText(name);
                int price = Integer.parseInt(component.getString(TAG_PRICE));
                DecimalFormat format = new DecimalFormat("###,###");
                String s_price = format.format(price);
                tv_item_price.setText(s_price);
                String image = component.getString(TAG_IMAGE);
                img = StringToBitmap(image);
                iv_item_image.setImageBitmap(img);
                String inform = null;
                if (!component.isNull(TAG_INFORM)) {
                    inform = component.getString(TAG_INFORM);
                    tv_item_detail.setText(inform);
                }
                String deliv_method = null;
                int deliv_price;
                String deliv_inform = null;
                int minimum_quantity;
                int maximum_quantity;
                int wishlist;

                if (!component.isNull(TAG_DELIV_METHOD)) {
                    deliv_method = component.getString(TAG_DELIV_METHOD);
                    tv_item_deliv_method_is.setText(deliv_method);

                }
                if (!component.isNull(TAG_DELIV_PRICE)) {
                    deliv_price = Integer.parseInt(component.getString(TAG_DELIV_PRICE));
                    if (deliv_price == 0) {
                        tv_item_deliv_price_is.setText("무료");
                    }
                }
                if (!component.isNull(TAG_DELIV_INFORM)) {
                    deliv_inform = component.getString(TAG_DELIV_INFORM);
                }
                if (!component.isNull(TAG_MIN_QUANTITY)) {
                    minimum_quantity = Integer.parseInt(component.getString(TAG_MIN_QUANTITY));
                    min_quantity = minimum_quantity;
                }
                if (!component.isNull(TAG_MAX_QUANTITY)) {
                    maximum_quantity = Integer.parseInt(component.getString(TAG_MAX_QUANTITY));
                    max_quantity = maximum_quantity;
                }
                if (!component.isNull(TAG_WISHLIST)) {
                    wishlist = Integer.parseInt(component.getString(TAG_WISHLIST));

                    tv_wishlist_try.setText(Integer.toString(wishlist));
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

            progressDialog = new ProgressDialog(ProductDetailActivity.this, R.style.AlertDialogStyle);
            progressDialog.setTitle("로딩중입니다.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null) {

                Toast.makeText(getApplicationContext(), "오류가 발생했습니다!", Toast.LENGTH_SHORT).show();

            } else {

                if (result.contains("상품이 존재하지 않습니다.")) {
                    rl_warn_container.setVisibility(View.VISIBLE);
                } else {
                    JSONString = result;
                    showResult();
                }

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String item_no = params[0];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/selectItem.php";

            String postParameters = "item_no=" + item_no;

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

    class InsertCartData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ProductDetailActivity.this, R.style.AlertDialogStyle);
            progressDialog.setTitle("상품 추가중입니다.");
            progressDialog.show();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "POST response  - " + result);

            if (result.contains("상품을 카트에 추가했습니다.")) {

                Toast.makeText(getApplicationContext(), "추가 완료되었습니다.", Toast.LENGTH_SHORT).show();

            } else {

                if(result.contains("Integrity constraint violation: 1062 Duplicate entry")) {

                    Toast.makeText(getApplicationContext(), "이미 장바구니에 존재하는 상품입니다!", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();

                }
            }

            progressDialog.dismiss();


        }


        @Override
        protected String doInBackground(String... params) {

            String item_no = (String) params[1];
            String email = (String) params[2];
            String count = (String) params[3];
            String date = (String) params[4];

            String serverURL = (String) params[0];
            String postParameters = "item_no=" + item_no + "&email=" + email + "&count=" + count + "&date=" + date;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
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

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    private void showWishResult() {
        try {
            JSONObject jsonObject = new JSONObject(JSONWISHString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String wishlist = item.getString(TAG_WISHLIST);
                int my_wish = Integer.parseInt(wishlist);

                if (my_wish > 0) {
                    Wishlist = true;
                } else if (my_wish <= 0) {
                    Wishlist = false;
                }

            }

        } catch (JSONException e) {

            Log.d(TAG, "showLikeResult : ", e);
        }

    }

    private class GetWishData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ProductDetailActivity.this, R.style.AlertDialogStyle);
            progressDialog.setTitle("로딩중입니다.");
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result == null) {
                // 오류 시
            } else {

                JSONWISHString = result;
                showWishResult();

            }
        }

        @Override
        protected String doInBackground(String... params) {

            String item_no = params[0];
            String email = params[1];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/selectWish.php";
            String postParameters = "item_no=" + item_no + "&email=" + email;

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

                Log.d(TAG, "GetData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    class InsertWishData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ProductDetailActivity.this, R.style.AlertDialogStyle);
            progressDialog.setTitle("Wishlist에 추가중입니다.");
            progressDialog.show();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "POST response  - " + result);

            if (result.contains("상품을 Wishlist에 추가했습니다.")) {

                Toast.makeText(getApplicationContext(), "추가 완료되었습니다.", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();

            }

            progressDialog.dismiss();


        }

        @Override
        protected String doInBackground(String... params) {

            String item_no = (String) params[1];
            String email = (String) params[2];

            String serverURL = (String) params[0];
            String postParameters = "item_no=" + item_no + "&email=" + email;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000);
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

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    private class DeleteWishData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(ProductDetailActivity.this,
                    "Wishlist에서 삭제중입니다.", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {

            String item_no = params[0];
            String email = params[1];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/deleteWish.php";
            String postParameters = "item_no=" + item_no + "&email=" + email;

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

                Log.d(TAG, "DeleteLikeData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

}
