
package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import static java.lang.Integer.parseInt;

public class Administration extends AppCompatActivity {

    private static final String TAG = "Administration";
    private static final int REQUEST_CODE = 1001;

    private static String IP_ADDRESS = "IP ADDRESS";

    private DBOpenHelper dbOpenHelper;

    private RelativeLayout rl_image_container;

    private RadioGroup rg_category;

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

    private ImageButton ib_image_remove;

    private Button btn_item_save;

    private String itemCategory;
    private String itemName;
    private String itemPrice;
    private String itemImage;
    private String itemInform;
    private String itemDeliveryMethod;
    private String itemDeliveryPrice;
    private String itemDeliveryInform;
    private String itemMinimumQuantity;
    private String itemMaximumQuantity;
    private String[] itemClass;

    private Bitmap img;

    private String username = null;
    private String useremail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administration);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorWhite)); //툴바 배경색

        InitAllComponent();

        rg_category.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_category_ecollege :
                        itemCategory = "ecollege";
                        break;
                    case R.id.rb_category_eproduct :
                        itemCategory = "eproduct";
                        break;
                    default:
                        Log.d(TAG, "Error : Category is not Exist.");
                }
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
        String tempClass = et_class_data.getText().toString();
        itemClass = new String[tempClass.split("/").length];
        itemClass = tempClass.split("/").clone();

        btn_item_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (iv_image_data.getDrawable() != null) {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    itemImage = byteArrayToBinaryString(bytes);

                }

                Date date = new Date();
                date.getTime();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "ewoman-php/insertCollege.php", itemCategory, itemName, itemPrice, itemImage, itemInform,
                        itemDeliveryMethod, itemDeliveryPrice, itemDeliveryInform, itemMinimumQuantity, itemMaximumQuantity);
            }
        });

        tv_upload_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        ib_image_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_image_data.setImageDrawable(null);
                rl_image_container.setVisibility(View.GONE);
            }
        });

    }

    public void InitAllComponent() {

        username = "developer";
        useremail = "develop@naver.com";

        rl_image_container = findViewById(R.id.rl_image_container);

        rg_category = findViewById(R.id.rg_category);

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

    private Bitmap resize(Bitmap bm){
        Configuration config=getResources().getConfiguration();
        if(config.smallestScreenWidthDp>=800)
            bm = Bitmap.createScaledBitmap(bm, 500, 400, true);
        else if(config.smallestScreenWidthDp>=600)
            bm = Bitmap.createScaledBitmap(bm, 400, 300, true);
        else if(config.smallestScreenWidthDp>=400)
            bm = Bitmap.createScaledBitmap(bm, 300, 200, true);
        else if(config.smallestScreenWidthDp>=360)
            bm = Bitmap.createScaledBitmap(bm, 200, 150, true);
        else
            bm = Bitmap.createScaledBitmap(bm, 160, 96, true);
        return bm;
    }

    @Override
    public void onBackPressed() {

        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(username == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if(username != null) {
            getMenuInflater().inflate(R.menu.toolbar_al_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_login :
                Intent main_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(main_to_login);
                return true;
            case R.id.menu_signup :
                Intent main_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(main_to_signup);
                return true;
            case R.id.menu_logout :

                username = null;
                useremail = null;
                dbOpenHelper.deleteAllColumns();

                final ProgressDialog mDialog = new ProgressDialog(Administration.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_main = new Intent(getApplicationContext(), MainActivity.class);
                mDialog.dismiss();

                finish();
                startActivity(logout_to_main);
                return true;
            case R.id.menu_cart :
                startActivity(new Intent(getApplicationContext(), CartActivity.class));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Administration.this,
                    "저장중입니다.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "POST response  - " + result);

            Toast.makeText(getApplicationContext(), "저장 완료되었습니다.", Toast.LENGTH_SHORT);
        }


        @Override
        protected String doInBackground(String... params) {

            String category = (String)params[1];
            String name = (String)params[2];
            String price = (String)params[3];
            String image = (String)params[4];
            String inform = (String)params[5];
            String delivery_method = (String)params[6];
            String delivery_price = (String)params[7];
            String delivery_inform = (String)params[8];
            String minimum_quantity = (String)params[9];
            String maximum_quantity = (String)params[10];

            String serverURL = (String)params[0];
            String postParameters = "category=" + category + "&name=" + name + "&price=" + price
                    + "&image=" + image + "&inform=" + inform + "&deliv_method=" + delivery_method
                    + "&deliv_price=" + delivery_price + "&deliv_inform=" + delivery_inform
                    + "&minimum_quantity=" + minimum_quantity + "&maximum_quantity=" + maximum_quantity;

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
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
