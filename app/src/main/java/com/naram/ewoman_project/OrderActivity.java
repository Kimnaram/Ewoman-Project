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
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private static final String TAG = "OrderActivity";

    private static final int SEARCH_ADDRESS_ACTIVITY = 1002;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ITEMNO = "item_no";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_COUNT = "count";
    private static final String TAG_DELIVPRICE = "deliv_price";
    private static final String TAG_CLASSNAME = "class_name";
    private static final String TAG_CLASSPRICE = "class_price";
    private static String IP_ADDRESS = "IP ADDRESS";

    private String JSONString;
    private JSONArray orders = null;

    private DBOpenHelper dbOpenHelper;

    private OrderListAdapter adapter;
    private ListView lv_order_product;
    private ListOrder listOrder;

    private ArrayList<ListOrder> orderList = new ArrayList<ListOrder>();

    private TextView tv_item_price;
    private TextView tv_item_deliv_price;
    private TextView tv_item_all_price;

    private EditText et_order_name;
    private EditText et_order_phone;
    private EditText et_order_zipcode;
    private EditText et_order_address1;
    private EditText et_order_address2;

    private Button btn_order_zipcode;
    private Button btn_billing_try;

    private Spinner sp_order_deliv_message;

    private WebView daum_webView;

    private Handler handler;

    private String[] messageArray = null;
    private String selected_spinner = null;

    private String useremail;

    private int itemPrice = 0;
    private int delivPrice = 0;
    private int allPrice = 0;

    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        Intent intent = getIntent();

        String prevPage = intent.getStringExtra("prevPage");

        if (prevPage.equals("cartPage")) {
            int size = Integer.parseInt(intent.getStringExtra("size"));
            for (int i = 0; i < size; i++) {

                int item_no = Integer.parseInt(intent.getStringExtra("itemNo[" + i + "]"));
                String class_name = intent.getStringExtra("className[" + i + "]");
                int count = Integer.parseInt(intent.getStringExtra("count[" + i + "]"));

                orderList.add(new ListOrder(item_no, class_name, count));
                Log.d(TAG, "orderList add : " + item_no + ", " + class_name + ", " + count);

            }
        } else if (prevPage.equals("DetailPage")) {

            String item_no = intent.getStringExtra("item_no");
            String allPrice = intent.getStringExtra("price");

            Log.d(TAG, "allPrice : " + allPrice);

        }

        String itemNo_list = "(";
        String className_list = "(";

        for(int i = 0; i < orderList.size(); i++) {
            if(i < orderList.size() - 1) {
                itemNo_list += orderList.get(i).getItem_no() + ",";
                className_list += "\'" + orderList.get(i).getClass_name() + "\'" + ",";
            } else {
                itemNo_list += orderList.get(i).getItem_no() + ")";
                className_list += "\'" + orderList.get(i).getClass_name() + "\'" + ")";

                Log.d(TAG, "itemNo_list : " + itemNo_list + "\nclassName_list : " + className_list);

                GetData task = new GetData();
                task.execute(itemNo_list, className_list);
            }
        }

        initAllComponent();

        Cursor iCursor = dbOpenHelper.selectColumns();

        while (iCursor.moveToNext()) {

            String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
            useremail = tempEmail;

        }

        sp_order_deliv_message = findViewById(R.id.sp_order_deliv_message);
        messageArray = getResources().getStringArray(R.array.delivery_message);
        selected_spinner = messageArray[0];
        final ArrayAdapter<CharSequence> spinnerLargerAdapter =
                ArrayAdapter.createFromResource(this, R.array.delivery_message, R.layout.spinner_item);
        sp_order_deliv_message.setAdapter(spinnerLargerAdapter);
        sp_order_deliv_message.setSelection(0);

        et_order_phone.addTextChangedListener(new TextWatcher() {

            private int _beforeLenght = 0;
            private int _afterLenght = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                _beforeLenght = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    Log.d("addTextChangedListener", "onTextChanged: Intput text is wrong (Type : Length)");
                    return;
                }

                char inputChar = s.charAt(s.length() - 1);
                if (inputChar != '-' && (inputChar < '0' || inputChar > '9')) {
                    et_order_phone.getText().delete(s.length() - 1, s.length());
                    Log.d("addTextChangedListener", "onTextChanged: Intput text is wrong (Type : Number)");
                    return;
                }

                _afterLenght = s.length();

                // 삭제 중
                if (_beforeLenght > _afterLenght) {
                    // 삭제 중에 마지막에 -는 자동으로 지우기
                    if (s.toString().endsWith("-")) {
                        et_order_phone.setText(s.toString().substring(0, s.length() - 1));
                    }
                }
                // 입력 중
                else if (_beforeLenght < _afterLenght) {
                    if (_afterLenght == 4 && s.toString().indexOf("-") < 0) {
                        et_order_phone.setText(s.toString().subSequence(0, 3) + "-" + s.toString().substring(3, s.length()));
                    } else if (_afterLenght == 9) {
                        et_order_phone.setText(s.toString().subSequence(0, 8) + "-" + s.toString().substring(8, s.length()));
                    } else if (_afterLenght == 14) {
                        et_order_phone.setText(s.toString().subSequence(0, 13) + "-" + s.toString().substring(13, s.length()));
                    }
                }
                et_order_phone.setSelection(et_order_phone.length());

//                if(s.length() == 18) {
//                    et_order_phone.setBackground(
//                            ContextCompat.getDrawable(OrderActivity.this, R.drawable.btn_active));
//                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 생략
            }
        });

        btn_order_zipcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DaumAddressActivity.class);

                startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);
            }
        });

        btn_billing_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_order_name.getText().toString().isEmpty() || et_order_phone.getText().toString().isEmpty()  || et_order_zipcode.getText().toString().isEmpty()
                        || et_order_address1.getText().toString().isEmpty() || et_order_address2.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "모든 입력항목을 채워야 합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 결제 화면으로 이동
                    String price = tv_item_all_price.getText().toString().replace(",", "");
                    price = price.split("원")[0];

                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    intent.putExtra("item_name", "테스트");
                    intent.putExtra("price", Integer.parseInt(price));

                    startActivity(intent);
                }
            }
        });

    }

    public void initAllComponent() {

        lv_order_product = findViewById(R.id.lv_order_product);

        adapter = new OrderListAdapter();

        lv_order_product.setAdapter(adapter);

        tv_item_price = findViewById(R.id.tv_item_price);
        tv_item_deliv_price = findViewById(R.id.tv_item_deliv_price);
        tv_item_all_price = findViewById(R.id.tv_item_all_price);

        et_order_name = findViewById(R.id.et_order_name);
        et_order_phone = findViewById(R.id.et_order_phone);
        et_order_zipcode = findViewById(R.id.et_order_zipcode);
        et_order_address1 = findViewById(R.id.et_order_address1);
        et_order_address2 = findViewById(R.id.et_order_address2);

        btn_order_zipcode = findViewById(R.id.btn_order_zipcode);
        btn_billing_try = findViewById(R.id.btn_billing_try);

        sp_order_deliv_message = findViewById(R.id.sp_order_deliv_message);

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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        et_order_zipcode.setText(data.substring(0, 5));
                        et_order_address1.setText(data.substring(7));
                    }
                }
                break;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
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
                Intent order_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                finish();
                startActivity(order_to_login);
                return true;
            case R.id.menu_signup:
                Intent order_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                finish();
                startActivity(order_to_signup);
                return true;
            case R.id.menu_logout:

                useremail = null;
                dbOpenHelper.deleteAllColumns();

                final ProgressDialog mDialog = new ProgressDialog(OrderActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                finish();
                Intent logout_to_order = new Intent(getApplicationContext(), MainActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_order);
                return true;
            case R.id.menu_cart:
                Intent order_to_cart = new Intent(getApplicationContext(), CartActivity.class);

                finish();
                startActivity(order_to_cart);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResult() {
        try {

            JSONObject jsonObj = new JSONObject(JSONString);
            orders = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < orders.length(); i++) {
                JSONObject order = orders.getJSONObject(i);
                int item_no = Integer.parseInt(order.getString(TAG_ITEMNO));
                String image = order.getString(TAG_IMAGE);
                img = StringToBitmap(image);
                String name = order.getString(TAG_NAME);
                int price = Integer.parseInt(order.getString(TAG_PRICE));

                String class_name = null;
                int class_price = 0;
                int count = 1;
                int deliv_price = 0;

                if (!order.isNull(TAG_CLASSNAME)) {
                    class_name = order.getString(TAG_CLASSNAME);
                }

                if (!order.isNull(TAG_CLASSPRICE)) {
                    class_price = Integer.parseInt(order.getString(TAG_CLASSPRICE));
                }

                if (!order.isNull(TAG_DELIVPRICE)) {
                    deliv_price = Integer.parseInt(order.getString(TAG_DELIVPRICE));
                }

                if(class_price != 0) {
                    itemPrice += class_price;
                } else {
                    itemPrice += price;
                }

                for(int j = 0; j < orderList.size(); j++) {
                    if (item_no == orderList.get(j).getItem_no()) {

                        Log.d(TAG, orderList.get(j).getName() + " : " + orderList.get(j).getCount());

                        count = orderList.get(j).getCount();
                    }
                }

                if(class_price != 0 && class_price >= 50000) {
                    allPrice += class_price * count;
                    delivPrice = 0;
                } else if (class_price != 0 && class_price < 50000) {
                    allPrice += class_price * count + deliv_price;
                    delivPrice = deliv_price;
                } else if (price != 0 && price >= 50000) {
                    allPrice += price * count;
                    delivPrice = 0;
                } else if (price != 0 && price < 50000) {
                    allPrice += price * count + deliv_price;
                    delivPrice = deliv_price;
                }

                ListOrder listOrder = new ListOrder(item_no, name, img, price, class_name, class_price, count, deliv_price);
                orderList.add(listOrder);
                adapter.addItem(listOrder);

            }

            DecimalFormat decimalFormat = new DecimalFormat("###,###");

            tv_item_price.setText(decimalFormat.format(itemPrice) + "원");
            tv_item_deliv_price.setText(decimalFormat.format(delivPrice) + "원");
            tv_item_all_price.setText(decimalFormat.format(allPrice) + "원");
            setListViewHeightBasedOnChildren(lv_order_product);

        } catch (JSONException e) {

        }

    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(OrderActivity.this, R.style.AlertDialogStyle);
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

                if (result.contains("주문한 상품이 존재하지 않습니다.")) {

                    Toast.makeText(getApplicationContext(), "오류가 발생했습니다!", Toast.LENGTH_SHORT).show();

                } else {

                    JSONString = result;
                    showResult();

                }
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String item_no = params[0];
            String class_name = params[1];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/selectOrder.php";
            String postParameters = "item_no=" + item_no + "&class_name=" + class_name;

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

    class InsertOrderData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(OrderActivity.this, R.style.AlertDialogStyle);
            progressDialog.setTitle("주문중입니다.");
            progressDialog.show();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "POST response  - " + result);

            if (result.contains("주문 목록에 추가했습니다.")) {

                Toast.makeText(getApplicationContext(), "추가 완료되었습니다.", Toast.LENGTH_SHORT).show();

            } else {

                if(result.contains("Integrity constraint violation: 1062 Duplicate entry")) {

                    Toast.makeText(getApplicationContext(), "이미 주문 목록에 존재하는 상품입니다!", Toast.LENGTH_SHORT).show();

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
            String class_name = (String) params[4];

            String serverURL = (String) params[0];
            String postParameters = "item_no=" + item_no + "&email=" + email + "&count=" + count + "&class_name=" + class_name;

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

}