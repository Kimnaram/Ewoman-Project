package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class mem_LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {


    private EditText et_user_email;
    private EditText et_user_passwd;
    private TextView tv_login_try;
    private TextView tv_naver_login;
    private TextView tv_google_login;
    private TextView tv_kakao_login;
    private TextView tv_facebook_login;
    private TextView tv_to_signup;
    private TextView tv_find_account;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private static final int RC_SIGN_IN = 9001;
    public GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;

    private String uname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mem__login);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorWhite)); //툴바 배경색

        InitAllComponent();

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();

        tv_google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent google_signIn_Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(google_signIn_Intent, RC_SIGN_IN);
            }
        });

       tv_login_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_user_email.getText().toString();
                String passwd = et_user_passwd.getText().toString();

                if(!email.isEmpty() && !passwd.isEmpty()) {
                    final ProgressDialog mDialog = new ProgressDialog(mem_LoginActivity.this);
                    mDialog.setMessage("로그인 중입니다.");
                    mDialog.show();

                    firebaseAuth.signInWithEmailAndPassword(email, passwd)
                            .addOnCompleteListener(mem_LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mDialog.dismiss();

                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        String uid = user.getUid();
                                        firebaseDatabase.getInstance().getReference("users/" + uid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    if (dataSnapshot.getKey().equals("name"))
                                                        uname = dataSnapshot.getValue().toString();
                                                    Log.d("mem_LoginActivity", "Value :" + dataSnapshot.getValue());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.putExtra("name", uname);

                                        startActivity(intent);

                                        // 세션 유지 부분 추가 필요
                                    } else {
                                        mDialog.dismiss();

                                        Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호를 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "이메일과 비밀번호를 모두 입력해야 합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        tv_login_try.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        tv_login_try.setBackground(getResources().getDrawable(R.color.colorCocoa));
                        tv_login_try.setTextColor(getResources().getColor(R.color.colorWhite));
                        return false;

                    case MotionEvent.ACTION_UP :
                        tv_login_try.setBackground(getResources().getDrawable(R.drawable.btn_style_border_line));
                        tv_login_try.setTextColor(getResources().getColor(R.color.colorGray));
                        return false;
                }
                return false;
            }
        });

        tv_to_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_to_signup = new Intent(getApplicationContext(), mem_SignupActivity.class);

                startActivity(login_to_signup);
            }
        });

        tv_find_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    public void InitAllComponent() {

        et_user_email = findViewById(R.id.et_user_email);
        et_user_passwd = findViewById(R.id.et_user_passwd);
        tv_login_try = findViewById(R.id.tv_login_try);
        tv_naver_login = findViewById(R.id.tv_naver_login);
        tv_google_login = findViewById(R.id.tv_google_login);
        tv_kakao_login = findViewById(R.id.tv_kakao_login);
        tv_facebook_login = findViewById(R.id.tv_facebook_login);
        tv_to_signup = findViewById(R.id.tv_to_signup);
        tv_find_account = findViewById(R.id.tv_find_account);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //구글 로그인 성공해서 파베에 인증
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else{
                //구글 로그인 실패
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        final ProgressDialog mDialog = new ProgressDialog(mem_LoginActivity.this);
        mDialog.setMessage("구글 로그인 중입니다.");
        mDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            mDialog.dismiss();

                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();

                        } else {
                            mDialog.dismiss();

                            // 로그인 성공 시 가입 화면을 빠져나감.
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);

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
