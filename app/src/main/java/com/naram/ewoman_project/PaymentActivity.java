package com.naram.ewoman_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

public class PaymentActivity extends AppCompatActivity {

    private final static String TAG = "PaymentActivity";

    private final static String BILLING_API_KEY = "API KEY";

    private static String IP_ADDRESS = "IP ADDRESS";

    private int stock = 10;

    // Intent로 받아올 정보
    private ArrayList<ListOrder> itemList = new ArrayList<ListOrder>();

    private String item_name = "";
    private String phone = "";
    private int price = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

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
                Log.d(TAG, "상품 : " + listOrder.getItem_no());
                itemList.add(listOrder);

            }

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
                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
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
                            }
                        })
                .request();

    }

    class InsertOrderData extends AsyncTask<String, Void, String> {

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
