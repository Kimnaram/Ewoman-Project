package com.naram.ewoman_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class mem_SignupAgreementActivity extends AppCompatActivity {

    private String termsofuse_text;
    private String personalinfo_text;

    private CheckBox cb_termsofuse_form;
    private CheckBox cb_personalinfo_form;

    private TextView tv_termsofuse_form;
    private TextView tv_personalinfo_form;

    private Button btn_signup_cancel;
    private Button btn_signup_try;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mem__signup_agreement);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GRAY)); //툴바 배경색

        InitAllComponent();

        termsofuse_text = "제 1조 목적\n\n" +
                "본 이용약관은 \"사이트명\"(이하 \"사이트\")의 서비스의 이용조건과 운영에 관한 제반사항 규정을 목적으로 합니다.\n\n" +
                "제 2조 용어의 정의\n" +
                "본 약관에서 사용되는 주요한 용어의 정의는 다음과 같습니다.\n\n" +
                "① 회원 : 사이트의 약관에 동의하고 개인정보를 제공하여 회원등록을 한 자로서, 사이트와의 이용계약을 체결하고 사이트를 이용하는 이용자를 말합니다.\n" +
                "② 이용계약 : 사이트 이용과 관련하여 사이트와 회원간에 체결하는 계약을 말합니다." +
                "③ 회원 아이디(이하 \"ID\") : 회원의 식별과 회원의 서비스 이용을 위하여 회원별로 부여하는 고유한 문자와 숫자의 조합을 말합니다.\n" +
                "④ 운영자 : 서비스에 홈페이지를 개설하여 운영하는 운영자를 말합니다.\n" +
                "⑤ 해지 : 회원이 이용계약을 해약하는 것을 말합니다.\n\n" +
                "제 3조 약관외 준칙\n\n" +
                "운영자는 필요한 경우 별도로 운영정책을 공지 안내할 수 있으며, 본 약관과 운영정책이 중첩될 경우 운영정책이 우선 적용됩니다.\n\n" +
                "제 4조 이용계약 체결\n\n" +
                "① 이용계약은 회원으로 등록하여 사이트를 이용하려는 자의 본 약관 내용에 대한 동의와 가입신청에 대하여 운영자의 이용승낙으로 성립합니다.\n" +
                "② 회원으로 등록하여 서비스를 이용하려는 자는 사이트 가입신청시 본 약관을 읽고 위에 있는 \"동의합니다\"를 선택하는 것으로 본 약관에 대한 동의 의사 표시를 합니다.\n\n" +
                "제 5조 서비스 이용 신청\n\n";

        tv_termsofuse_form.setText(termsofuse_text);
        tv_termsofuse_form.setMovementMethod(new ScrollingMovementMethod());

        cb_termsofuse_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_termsofuse_form.isChecked() && cb_personalinfo_form.isChecked()) {
                    btn_signup_try.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent agreement_to_form = new Intent(getApplicationContext(), mem_SignupFormActivity.class);

                            startActivity(agreement_to_form);
                        }
                    });
                } else {
                    btn_signup_try.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "모두 동의하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        cb_personalinfo_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_termsofuse_form.isChecked() && cb_personalinfo_form.isChecked()) {
                    btn_signup_try.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent agreement_to_form = new Intent(getApplicationContext(), mem_SignupFormActivity.class);

                            startActivity(agreement_to_form);
                        }
                    });
                } else {
                    btn_signup_try.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "모두 동의하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    public void InitAllComponent() {

        tv_termsofuse_form = findViewById(R.id.tv_termsofuse_form);
        tv_personalinfo_form = findViewById(R.id.tv_personalinfo_form);
        cb_termsofuse_form = findViewById(R.id.cb_termsofuse_form);
        cb_personalinfo_form = findViewById(R.id.cb_personalinfo_form);
        btn_signup_try = findViewById(R.id.btn_signup_try);
        btn_signup_cancel = findViewById(R.id.btn_signup_cancel);

        termsofuse_text = null;
        personalinfo_text = null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login :
                Intent main_to_login = new Intent(getApplicationContext(), mem_LoginActivity.class);

                startActivity(main_to_login);
                return true;

            case R.id.menu_signup :
                Intent main_to_signup = new Intent(getApplicationContext(), mem_SignupActivity.class);

                startActivity(main_to_signup);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

