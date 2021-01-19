package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupFormActivity extends AppCompatActivity {

    private static final String TAG = "SignupFormActivity";

    private static String IP_ADDRESS = "IP ADDRESS";

    private EditText et_user_email;
    private EditText et_user_passwd;
    private EditText et_user_passwd_check;
    private EditText et_user_name;
    private EditText et_user_pnumber;

    private TextView tv_alert_passwd;
    private TextView tv_signup_try;

    private RadioGroup rg_user_gender;
    private RadioButton rb_user_male;
    private RadioButton rb_user_female;

    private FirebaseAuth firebaseAuth;

    private int State = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE)); //툴바 배경색

        InitAllComponent();

        //파이어베이스 접근 설정
        // user = firebaseAuth.getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        //firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        et_user_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        et_user_passwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                check_Password(et_user_passwd.getText().toString(), et_user_passwd_check.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_user_passwd_check.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                check_Password(et_user_passwd.getText().toString(), et_user_passwd_check.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        tv_signup_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_user_email.getText().toString().isEmpty() && !et_user_passwd.getText().toString().isEmpty()
                        && !et_user_passwd_check.getText().toString().isEmpty() && !et_user_name.getText().toString().isEmpty()
                        && !et_user_pnumber.getText().toString().isEmpty()
                        && et_user_passwd.getText().toString().equals(et_user_passwd_check.getText().toString())) {

                    String email = et_user_email.getText().toString();
                    String pwd = et_user_passwd.getText().toString();
                    String name = et_user_name.getText().toString().trim();
                    String phone = et_user_pnumber.getText().toString().trim();
                    phone = phone.split("-")[0] + phone.split("-")[1] + phone.split("-")[2];
                    boolean gender_f = rb_user_female.isChecked();
                    boolean gender_m = rb_user_male.isChecked();
                    String gender = null;

                    if (gender_f == true) {
                        gender = "female";
                    } else if (gender_m == true) {
                        gender = "male";
                    }

                    InsertData Itask = new InsertData();
                    Itask.execute("http://" + IP_ADDRESS + "/ewoman-php/insertUser.php", email, name, pwd, gender, phone);

                    //가입이 이루어졌을시 가입 화면을 빠져나감.
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

                    startActivity(intent);
                    finish();
                    Toast.makeText(SignupFormActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    if (et_user_email.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "이메일을 작성해야 합니다.", Toast.LENGTH_SHORT).show();
                    } else if (et_user_passwd.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "비밀번호를 작성해야 합니다.", Toast.LENGTH_SHORT).show();
                    } else if (et_user_name.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "이름을 작성해야 합니다.", Toast.LENGTH_SHORT).show();
                    } else if (et_user_pnumber.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "연락처를 작성해야 합니다.", Toast.LENGTH_SHORT).show();
                    } else if (!et_user_passwd.getText().toString().equals(et_user_passwd_check.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "비밀번호가 일치해야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        et_user_pnumber.addTextChangedListener(new TextWatcher() {

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
                    et_user_pnumber.getText().delete(s.length() - 1, s.length());
                    Log.d("addTextChangedListener", "onTextChanged: Intput text is wrong (Type : Number)");
                    return;
                }

                _afterLenght = s.length();

                // 삭제 중
                if (_beforeLenght > _afterLenght) {
                    // 삭제 중에 마지막에 -는 자동으로 지우기
                    if (s.toString().endsWith("-")) {
                        et_user_pnumber.setText(s.toString().substring(0, s.length() - 1));
                    }
                }
                // 입력 중
                else if (_beforeLenght < _afterLenght) {
                    if (_afterLenght == 4 && s.toString().indexOf("-") < 0) {
                        et_user_pnumber.setText(s.toString().subSequence(0, 3) + "-" + s.toString().substring(3, s.length()));
                    } else if (_afterLenght == 9) {
                        et_user_pnumber.setText(s.toString().subSequence(0, 8) + "-" + s.toString().substring(8, s.length()));
                    } else if (_afterLenght == 14) {
                        et_user_pnumber.setText(s.toString().subSequence(0, 13) + "-" + s.toString().substring(13, s.length()));
                    }
                }
                et_user_pnumber.setSelection(et_user_pnumber.length());

//                if(s.length() == 18) {
//                    et_user_pnumber.setBackground(
//                            ContextCompat.getDrawable(OrderActivity.this, R.drawable.btn_active));
//                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 생략
            }
        });
    }

    public void InitAllComponent() {

        et_user_email = findViewById(R.id.et_user_email);
        et_user_passwd = findViewById(R.id.et_user_passwd);
        et_user_passwd_check = findViewById(R.id.et_user_passwd_check);
        et_user_name = findViewById(R.id.et_user_name);
        et_user_pnumber = findViewById(R.id.et_user_pnumber);
        tv_alert_passwd = findViewById(R.id.tv_alert_passwd);
        tv_signup_try = findViewById(R.id.tv_signup_try);

        rg_user_gender = findViewById(R.id.rg_user_gender);
        rb_user_male = findViewById(R.id.rb_user_male);
        rb_user_female = findViewById(R.id.rb_user_female);

    }

    public void check_Password(String upw, String upw_chk) {
        if (upw.equals(upw_chk)) {
            tv_alert_passwd.setText("비밀번호가 일치합니다.");
            tv_alert_passwd.setVisibility(View.VISIBLE);
        } else {
            tv_alert_passwd.setText("비밀번호가 일치하지 않습니다.");
            tv_alert_passwd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);

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
        }
        return super.onOptionsItemSelected(item);
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(SignupFormActivity.this, R.style.AlertDialogStyle);
            progressDialog.setTitle("회원가입 중입니다.");
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            //가입이 이루어졌을시 가입 화면을 빠져나감.
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

            startActivity(intent);
            finish();

            Toast.makeText(SignupFormActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String email = (String) params[1];
            String name = (String) params[2];
            String password = (String) params[3];
            String gender = (String) params[4];
            String phone = (String) params[5];

            String serverURL = (String) params[0];
            String postParameters = "email=" + email +  "&name=" + name + "&password=" + password + "&gender=" + gender + "&phone=" + phone;

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

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}