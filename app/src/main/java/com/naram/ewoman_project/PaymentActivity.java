package com.naram.ewoman_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.enums.Method;
import kr.co.bootpay.enums.PG;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;
import kr.co.bootpay.model.Item;

public class PaymentActivity extends AppCompatActivity {

    private final static String TAG = "PaymentActivity";

    private final static String BILLING_API_KEY = "API KEY";

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ITEMNO = "item_no";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_NAME = "name";
    private static final String TAG_CLASSNAME = "class_name";
    private static String IP_ADDRESS = "IP ADDRESS";

    private String JSONString;
    private JSONArray payments = null;

    private int stock = 10;

    private RelativeLayout rl_payment_container;

    private DBOpenHelper dbOpenHelper;

    private JSONObject jsonObject = new JSONObject();

    private String useremail;

    // Intent로 받아올 정보
    private List<Item> itemList = new ArrayList<Item>();
    private ArrayList<ListOrder> orderList = new ArrayList<ListOrder>();

    private String item_name = "";
    private String phone = "";
    private int price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        Intent intent = getIntent();

        if (intent != null) {

            item_name = intent.getStringExtra("item_name");
            phone = intent.getStringExtra("phone");
            price = Integer.parseInt(intent.getStringExtra("price"));

            int size = Integer.parseInt(intent.getStringExtra("size"));
            for (int i = 0; i < size; i++) {

                int item_no = Integer.parseInt(intent.getStringExtra("itemNo[" + i + "]"));
                String class_name = intent.getStringExtra("className[" + i + "]");
                int count = Integer.parseInt(intent.getStringExtra("count[" + i + "]"));

                ListOrder listOrder = new ListOrder(item_no, class_name, count);
                orderList.add(listOrder);

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

        }

        initAllComponent();

        Cursor iCursor = dbOpenHelper.selectColumns();

        while (iCursor.moveToNext()) {

            String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
            useremail = tempEmail;

        }

    }

    void initAllComponent() {

        rl_payment_container = findViewById(R.id.rl_payment_container);

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

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

                // 다이얼로그 바디
                AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(PaymentActivity.this, R.style.AlertDialogStyle);
                // 메세지
                logoutBuilder.setTitle("결제를 그만두시겠습니까?");

                logoutBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        useremail = null;
                        dbOpenHelper.deleteAllColumns();

                        final ProgressDialog mDialog = new ProgressDialog(PaymentActivity.this);
                        mDialog.setMessage("로그아웃 중입니다.");
                        mDialog.show();

                        finish();
                        Intent logout_to_payment = new Intent(getApplicationContext(), MainActivity.class);
                        mDialog.dismiss();

                        startActivity(logout_to_payment);

                    }
                });
                // "아니오" 버튼을 누르면 실행되는 리스너
                logoutBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return; // 아무런 작업도 하지 않고 돌아간다
                    }
                });

                logoutBuilder.show();

                return true;
            case R.id.menu_cart:

                // 다이얼로그 바디
                AlertDialog.Builder cartBuilder = new AlertDialog.Builder(PaymentActivity.this, R.style.AlertDialogStyle);
                // 메세지
                cartBuilder.setTitle("결제를 그만두시겠습니까?");

                cartBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        startActivity(new Intent(getApplicationContext(), CartActivity.class));

                    }
                });
                // "아니오" 버튼을 누르면 실행되는 리스너
                cartBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return; // 아무런 작업도 하지 않고 돌아간다
                    }
                });

                cartBuilder.show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showResult() {
        try {

            JSONObject jsonObj = new JSONObject(JSONString);
            payments = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < payments.length(); i++) {
                JSONObject payment = payments.getJSONObject(i);
                int item_no = Integer.parseInt(payment.getString(TAG_ITEMNO));
                String name = payment.getString(TAG_NAME);
                String category = payment.getString(TAG_CATEGORY);

                String class_name = null;
                int count = 1;

                if (!payment.isNull(TAG_CLASSNAME)) {
                    class_name = payment.getString(TAG_CLASSNAME);
                }


                for(int j = 0; j < orderList.size(); j++) {
                    if (item_no == orderList.get(j).getItem_no()) {

                        count = orderList.get(j).getCount();
                    }
                }

                if(class_name.isEmpty()) {
                    class_name = "클래스 없음";
                }

                Double dprice = Double.parseDouble(Integer.toString(price));
                Item item = new Item(name, count, class_name, dprice, "HOME", category, Integer.toString(item_no));
                itemList.add(item);

            }

            // 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
            // 앱에서 확인하지 말고 꼭 웹 사이트에서 확인하자. 앱의 application id 갖다 쓰면 안됨!!!
            BootpayAnalytics.init(this, BILLING_API_KEY);

            BootUser bootUser = new BootUser().setPhone(phone); // !! 자신의 핸드폰 번호로 바꾸기
            BootExtra bootExtra = new BootExtra().setQuotas(new int[]{0, 2, 3});

            Bootpay.init(getFragmentManager())
                    .setApplicationId(BILLING_API_KEY) // 해당 프로젝트(안드로이드)의 application id 값(위의 값 복붙)
                    .setPG(PG.INICIS) // 결제할 PG 사
                    .setMethod(Method.CARD) // 결제수단
                    .setContext(PaymentActivity.this)
                    .setBootUser(bootUser)
                    .setBootExtra(bootExtra)
                    .setUX(UX.PG_DIALOG)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                    .setName(item_name) // 결제할 상품명
                    .setOrderId("1234") // 결제 고유번호 (expire_month)
                    .setPrice(price) // 결제할 금액
                    .addItems(itemList)
//                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
//                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                    .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                        @Override
                        public void onConfirm(@Nullable String message) {

                            if (0 < stock) Bootpay.confirm(message); // 재고가 있을 경우.
                            else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                            Log.d("confirm", message);
                        }
                    })
                    .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                        @Override
                        public void onDone(@Nullable String message) {
                            Log.d("done", message);

                            try {
                                JSONArray jArray = new JSONArray();
                                for (int j = 0; j < itemList.size(); j++) {
                                    JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                                    sObject.put("itemNo", itemList.get(j).getCat3());
                                    sObject.put("userEmail", useremail);
                                    sObject.put("itemCount", itemList.get(j).getQty());
                                    sObject.put("className", itemList.get(j).getUnique());
                                    jArray.put(sObject);

                                    if (j >= itemList.size() - 1) {

                                        jsonObject.put("payment", jArray);

                                        System.out.println(jsonObject.toString());

                                        InsertPaymentData task = new InsertPaymentData();
                                        task.execute("http://" + IP_ADDRESS + "/ewoman-php/insertPayment.php", jsonObject.toString());

                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    })
                    .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                        @Override
                        public void onReady(@Nullable String message) {
                            Log.d("ready", message);
                        }
                    })
                    .onCancel(new CancelListener() { // 결제 취소시 호출
                        @Override
                        public void onCancel(@Nullable String message) {

                            Log.d("cancel", message);
                        }
                    })
                    .onClose(
                            new CloseListener() { //결제창이 닫힐때 실행되는 부분
                                @Override
                                public void onClose(String message) {
                                    Log.d("close", "close");

                                    rl_payment_container.setVisibility(View.VISIBLE);
                                }
                            })
                    .request();

        } catch (JSONException e) {

        }

    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(PaymentActivity.this, R.style.AlertDialogStyle);
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

                if (result.contains("결제 에러")) {

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

    class InsertPaymentData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(PaymentActivity.this, R.style.AlertDialogStyle);
            progressDialog.setTitle("주문중입니다.");
            progressDialog.show();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "POST response  - " + result);

            if (result.contains("결제 정보를 추가했습니다.")) {

                for (int i = 0; i < itemList.size(); i++) {

                    String items = "(";

                    if(i < itemList.size() - 1) {

                        items += itemList.get(i).getCat3() + ",";

                    } else {

                        items += itemList.get(i).getCat3() + ")";
                        DeleteCartData task = new DeleteCartData();
                        task.execute(items, useremail);

                    }

                }

                Toast.makeText(getApplicationContext(), "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show();

            } else {

                if(result.contains("Integrity constraint violation: 1062 Duplicate entry")) {

                    Toast.makeText(getApplicationContext(), "이미 결제된 상품입니다!", Toast.LENGTH_SHORT).show();

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

    private class DeleteCartData extends AsyncTask<String, Void, String> {

//        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

//            progressDialog.dismiss();

            Log.d(TAG, "response - " + result);

            if(result.equals("삭제 성공")) {
                rl_payment_container.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getApplicationContext(), "카트에서 상품을 삭제하지 못했습니다.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String item_no = params[0];
            String email = params[1];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/deleteCart.php";
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

                Log.d(TAG, "DeleteData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }
}
