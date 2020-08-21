package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class mem_SignupFormActivity extends AppCompatActivity {

    private static final String TAG = "mem_SignupFormActivity";

    public static class User {
        public String uid;
        public String email;
        public String phone;
        public String gender;
        public String name;

        public User(String uid, String email, String phone, String gender, String name) {
            this.uid = uid;
            this.email = email;
            this.phone = phone;
            this.gender = gender;
            this.name = name;
        }

    }

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
        setContentView(R.layout.activity_mem__signup_form);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GRAY)); //툴바 배경색

        InitAllComponent();

        //파이어베이스 접근 설정
        // user = firebaseAuth.getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        //firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        et_user_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER) {
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
                        && !et_user_pnumber.getText().toString().isEmpty() && (rb_user_female.isChecked() || rb_user_male.isChecked())
                        && et_user_passwd.getText().toString().equals(et_user_passwd_check.getText().toString())) {

                    String email = et_user_email.getText().toString();
                    String pwd = et_user_passwd.getText().toString();

                    System.out.println(email + "\n" + pwd);

                    Log.d(TAG, "등록 버튼 " + email + " , " + pwd);
                    final ProgressDialog mDialog = new ProgressDialog(mem_SignupFormActivity.this);
                    mDialog.setMessage("회원가입 중입니다.");
                    mDialog.show();

                    //파이어베이스에 신규계정 등록하기
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(mem_SignupFormActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //가입 성공시
                            if (task.isSuccessful()) {
                                mDialog.dismiss();

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                String name = et_user_name.getText().toString().trim();
                                String pnumber = et_user_pnumber.getText().toString();
                                boolean gender_f = rb_user_female.isChecked();
                                boolean gender_m = rb_user_male.isChecked();
                                String gender = null;

                                if (gender_f == true) {
                                    gender = "female";
                                } else if (gender_m == true) {
                                    gender = "male";
                                }

                                //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
//                                HashMap<String, Object> hashMap = new HashMap<>();
//
//                                hashMap.put(uid, new User(uid, email, pnumber, gender, name));

                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("name", name);
                                hashMap.put("gender", gender);
                                hashMap.put("phone", pnumber);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("users");
                                reference.child(uid).setValue(hashMap);

                                //가입이 이루어졌을시 가입 화면을 빠져나감.
                                Intent intent = new Intent(getApplicationContext(), mem_LoginActivity.class);

                                startActivity(intent);
                                finish();
                                Toast.makeText(mem_SignupFormActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                            } else {
                                mDialog.dismiss();
                                Toast.makeText(mem_SignupFormActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                return;  //해당 메소드 진행을 멈추고 빠져나감.

                            }
                        }
                    });
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
                Intent main_to_login = new Intent(getApplicationContext(), mem_LoginActivity.class);

                startActivity(main_to_login);
                return true;
            case R.id.menu_signup:
                Intent main_to_signup = new Intent(getApplicationContext(), mem_SignupActivity.class);

                startActivity(main_to_signup);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}