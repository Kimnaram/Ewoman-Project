package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.naram.ewoman_project.CartActivity.binaryStringToByteArray;

public class ItemUpdateActivity extends AppCompatActivity {

    private static final String TAG = "ItemUpdateActivity";
    private static final int REQUEST_CODE = 1001;

    private class Class {

        private String item_name;
        private String class_name;
        private String class_price;

        public Class(String item_name, String class_name, String class_price) {
            this.item_name = item_name;
            this.class_name = class_name;
            this.class_price = class_price;
        }

        public String getClass_name() {
            return class_name;
        }

        public String getClass_price() {
            return class_price;
        }

        public String getItem_Name() {
            return item_name;
        }

    }

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
    private static final String TAG_CLASSNAME = "class_name";
    private static final String TAG_CLASSPRICE = "class_price";
    private static String IP_ADDRESS = "34.228.20.230";

    private String JSONString;
    private String JSONWISHString;
    private JSONArray item = null;

    private DBOpenHelper dbOpenHelper;

    private LinearLayout ll_class_container;
    private RelativeLayout rl_image_container;

    private RadioGroup rg_category;

    private RadioButton rb_category_ecollege;
    private RadioButton rb_category_eproduct;

    private TextView tv_upload_pic;

    private EditText et_name_data;
    private EditText et_price_data;
    private EditText et_info_data;
    private EditText et_delivery_method_data;
    private EditText et_delivery_price_data;
    private EditText et_delivery_inform_data;
    private EditText et_minimum_quantity_data;
    private EditText et_maximum_quantity_data;
    private EditText et_class_data;

    private ImageView iv_image_data;

    private ImageButton ib_item_plus;
    private ImageButton ib_image_remove;

    private Button btn_item_save;

    private String itemCategory = null;
    private String itemName = null;
    private String itemPrice = null;
    private String itemImage = null;
    private String itemInform = null;
    private String itemDeliveryMethod;
    private String itemDeliveryPrice;
    private String itemDeliveryInform;
    private String itemMinimumQuantity;
    private String itemMaximumQuantity;

    private Bitmap img;

    private List<EditText> allClass = new ArrayList<EditText>();
    private List<Class> classList = new ArrayList<Class>();

    private JSONObject jsonObject = new JSONObject();

    private String item_no = null;
    private String username = null;
    private String useremail = null;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_update);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorWhite)); //툴바 배경색

        InitAllComponent();

        Intent intent = getIntent();

        if (intent != null) {
            item_no = intent.getStringExtra("item_no");
        }

        GetData task = new GetData();
        task.execute(item_no);

        btn_item_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rg_category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                    }
                });

                itemName = et_name_data.getText().toString();
                itemPrice = et_price_data.getText().toString();
                itemInform = et_info_data.getText().toString();
                itemDeliveryMethod = et_delivery_method_data.getText().toString();
                itemDeliveryPrice = et_delivery_price_data.getText().toString();
                itemDeliveryInform = et_delivery_inform_data.getText().toString();
                itemMinimumQuantity = et_minimum_quantity_data.getText().toString();
                itemMaximumQuantity = et_maximum_quantity_data.getText().toString();

                if (!itemName.isEmpty() && !itemPrice.isEmpty() && iv_image_data.getDrawable() != null && !itemCategory.isEmpty()) {
                    if (iv_image_data.getDrawable() != null) {

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] bytes = baos.toByteArray();
                        itemImage = byteArrayToBinaryString(bytes);

                    }

                    if (itemDeliveryPrice.isEmpty()) {
                        itemDeliveryPrice = "null";
                    }

                    if (itemMaximumQuantity.isEmpty()) {
                        itemMaximumQuantity = "null";
                    }

                    if (itemMinimumQuantity.isEmpty()) {
                        itemMinimumQuantity = "null";
                    }

                }

                UpdateData task = new UpdateData();
                task.execute(item_no, itemCategory, itemName, itemPrice, itemImage, itemInform, itemDeliveryMethod,
                        itemDeliveryPrice, itemDeliveryInform, itemMinimumQuantity, itemMaximumQuantity);

            }
        });

        ib_item_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addEditTextsetHint("클래스의 이름과 가격, 우선순위를 /로 구분해주세요.");

            }
        });
    }

    public void InitAllComponent() {

        username = "developer";
        useremail = "develop@naver.com";

        ll_class_container = findViewById(R.id.ll_class_container);

        rl_image_container = findViewById(R.id.rl_image_container);

        rg_category = findViewById(R.id.rg_category);

        rb_category_ecollege = findViewById(R.id.rb_category_ecollege);
        rb_category_eproduct = findViewById(R.id.rb_category_eproduct);

        tv_upload_pic = findViewById(R.id.tv_upload_pic);

        et_name_data = findViewById(R.id.et_name_data);
        et_price_data = findViewById(R.id.et_price_data);
        et_info_data = findViewById(R.id.et_info_data);
        et_delivery_method_data = findViewById(R.id.et_delivery_method_data);
        et_delivery_price_data = findViewById(R.id.et_delivery_price_data);
        et_delivery_inform_data = findViewById(R.id.et_delivery_inform_data);
        et_minimum_quantity_data = findViewById(R.id.et_minimum_quantity_data);
        et_maximum_quantity_data = findViewById(R.id.et_maximum_quantity_data);
        et_class_data = findViewById(R.id.et_class_data);

        iv_image_data = findViewById(R.id.iv_image_data);

        ib_item_plus = findViewById(R.id.ib_item_plus);
        ib_image_remove = findViewById(R.id.ib_image_remove);

        btn_item_save = findViewById(R.id.btn_item_save);

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img = BitmapFactory.decodeStream(in);
                    img = resize(img);
                    in.close();
                    // 이미지 표시
                    rl_image_container.setVisibility(View.VISIBLE);
                    iv_image_data.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap resize(Bitmap bm) {
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 800)
            bm = Bitmap.createScaledBitmap(bm, 500, 400, true);
        else if (config.smallestScreenWidthDp >= 600)
            bm = Bitmap.createScaledBitmap(bm, 400, 300, true);
        else if (config.smallestScreenWidthDp >= 400)
            bm = Bitmap.createScaledBitmap(bm, 300, 200, true);
        else if (config.smallestScreenWidthDp >= 360)
            bm = Bitmap.createScaledBitmap(bm, 200, 150, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);
        return bm;
    }

    // 바이너리 바이트 배열을 스트링으로
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
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

    public void addEditTextsetHint(String arg) {

        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final EditText et_data = new EditText(this);
        et_data.setBackground(getResources().getDrawable(R.drawable.btn_style_border_line_white_background));
        et_data.setHint(arg);
        et_data.setTextColor(getResources().getColor(R.color.colorGray));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/nanumbarungothicbold.ttf");
        et_data.setTypeface(typeface, Typeface.NORMAL);
        et_data.setPadding(5, 0, 0, 0);
        et_data.setInputType(EditText.AUTOFILL_TYPE_TEXT);
        param1.setMargins(0, 0, 0, 20);
        et_data.setLayoutParams(param1);

        allClass.add(et_data);
        ll_class_container.addView(et_data);

    }

    public void addEditText(String arg) {

        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final EditText et_data = new EditText(this);
        et_data.setBackground(getResources().getDrawable(R.drawable.btn_style_border_line_white_background));
        et_data.setText(arg);
        et_data.setTextColor(getResources().getColor(R.color.colorGray));
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/nanumbarungothicbold.ttf");
        et_data.setTypeface(typeface, Typeface.NORMAL);
        et_data.setInputType(EditText.AUTOFILL_TYPE_TEXT);
        et_data.setPadding(5, 0, 0, 0);
        param1.setMargins(0, 0, 0, 20);
        et_data.setLayoutParams(param1);

        allClass.add(et_data);
        ll_class_container.addView(et_data);

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

                username = null;
                useremail = null;
                dbOpenHelper.deleteAllColumns();

                final ProgressDialog mDialog = new ProgressDialog(ItemUpdateActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_main = new Intent(getApplicationContext(), MainActivity.class);
                mDialog.dismiss();

                finish();
                startActivity(logout_to_main);
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(JSONString);
            item = jsonObject.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < item.length(); i++) {

                JSONObject component = item.getJSONObject(i);

                String category = component.getString(TAG_CATEGORY);
                itemCategory = category;
                if (category.equals("ecollege")) {
                    rb_category_ecollege.setChecked(true);
                } else if (category.equals("eproduct")) {
                    rb_category_eproduct.setChecked(true);
                }
                String name = component.getString(TAG_NAME);
                et_name_data.setText(name);
                String price = component.getString(TAG_PRICE);
                et_price_data.setText(price);
                String image = component.getString(TAG_IMAGE);
                rl_image_container.setVisibility(View.VISIBLE);
                img = StringToBitmap(image);
                iv_image_data.setImageBitmap(img);
                String inform = component.getString(TAG_INFORM);
                et_info_data.setText(inform);

                String deliv_method = null;
                String deliv_price = null;
                String deliv_inform = null;
                String minimum_quantity = null;
                String maximum_quantity = null;
                String class_name = null;
                String class_price = null;

                if (!component.isNull(TAG_DELIV_METHOD)) {
                    deliv_method = component.getString(TAG_DELIV_METHOD);
                    et_delivery_method_data.setText(deliv_method);
                }
                if (!component.isNull(TAG_DELIV_PRICE)) {
                    deliv_price = component.getString(TAG_DELIV_PRICE);
                    et_delivery_price_data.setText(deliv_price);
                }
                if (!component.isNull(TAG_DELIV_INFORM)) {
                    deliv_inform = component.getString(TAG_DELIV_INFORM);
                    et_delivery_inform_data.setText(deliv_inform);
                }
                if (!component.isNull(TAG_MIN_QUANTITY)) {
                    minimum_quantity = component.getString(TAG_MIN_QUANTITY);
                    et_minimum_quantity_data.setText(minimum_quantity);
                }
                if (!component.isNull(TAG_MAX_QUANTITY)) {
                    maximum_quantity = component.getString(TAG_MAX_QUANTITY);
                    et_maximum_quantity_data.setText(maximum_quantity);
                }
                if (!component.isNull(TAG_CLASSNAME) && !component.isNull(TAG_CLASSPRICE)) {
                    class_name = component.getString(TAG_CLASSNAME);
                    class_price = component.getString(TAG_CLASSPRICE);
                    String strClass = null;
                    strClass = class_name + "/" + class_price;

                    addEditText(strClass);

                }

            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }


    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ItemUpdateActivity.this, R.style.AlertDialogStyle);
            progressDialog.setTitle("로딩중입니다.");
            progressDialog.show();

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

                if (result.contains("결과가 없습니다.")) {

                } else {

                    JSONString = result;
                    showResult();

                }
            }
        }
    }

    class UpdateData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ItemUpdateActivity.this, R.style.AlertDialogStyle);
            progressDialog.setTitle("수정중입니다.");
            progressDialog.show();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if (result.contains("상품 수정 에러")) {

                Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();

            } else if (result.contains("상품의 내용을 수정했습니다.")) {

                String[] classes = new String[allClass.size()];

                for (int i = 0; i < allClass.size(); i++) {

                    if (!allClass.get(i).getText().toString().isEmpty()) {

                        classes[i] = allClass.get(i).getText().toString();
                        String name = classes[i].split("/")[0];
                        String price = classes[i].split("/")[1];
                        Class classObject = new Class(itemName, name, price);
                        classList.add(classObject);

                    }

                }

                try {
                    JSONArray jArray = new JSONArray();
                    for (int j = 0; j < classList.size(); j++) {
                        JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                        sObject.put("itemName", classList.get(j).getItem_Name());
                        sObject.put("className", classList.get(j).getClass_name());
                        sObject.put("classPrice", classList.get(j).getClass_price());
                        jArray.put(sObject);

                        if (j >= classList.size() - 1) {

                            jsonObject.put("class", jArray);

                            System.out.println(jsonObject.toString());
                            UpdateClassData task = new UpdateClassData();
                            task.execute("http://" + IP_ADDRESS + "/ewoman-php/updateClass.php", jsonObject.toString());

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String item_no = (String) params[0];
            String category = (String) params[1];
            String name = (String) params[2];
            String price = (String) params[3];
            String image = (String) params[4];
            String inform = (String) params[5];
            String delivery_method = (String) params[6];
            String delivery_price = (String) params[7];
            String delivery_inform = (String) params[8];
            String minimum_quantity = (String) params[9];
            String maximum_quantity = (String) params[10];

            String serverURL = "http://" + IP_ADDRESS + "/ewoman-php/updateItem.php";
            String postParameters = "item_no=" + item_no + "&category=" + category + "&name=" + name + "&price=" + price
                    + "&image=" + image + "&inform=" + inform + "&deliv_method=" + delivery_method
                    + "&deliv_price=" + delivery_price + "&deliv_inform=" + delivery_inform
                    + "&minimum_quantity=" + minimum_quantity + "&maximum_quantity=" + maximum_quantity;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
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

                Log.d(TAG, "UpdateData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    class UpdateClassData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.contains("클래스 정보를 수정했습니다.")) {

                Toast.makeText(getApplicationContext(), "상품의 내용이 성공적으로 수정되었습니다!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.putExtra("item_no", item_no);

                finish();
                startActivity(intent);

            } else {

                Toast.makeText(getApplicationContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();

            }

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String json = (String) params[1];

            String serverURL = (String) params[0];
            String postParameters = "class=" + json;

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
