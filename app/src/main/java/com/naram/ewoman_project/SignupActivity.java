package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignupActivity";

    private static String IP_ADDRESS = "IP ADDRESS";

    private TextView tv_naver_signup;
    private TextView tv_google_signup;
    private TextView tv_kakao_signup;
    private TextView tv_facebook_signup;
    private TextView tv_normal_signup;

    private static final int RC_SIGN_IN = 9001;
    public GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GRAY)); //툴바 배경색

        InitAllComponent();

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.tv_google_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signIn();
                Intent google_signIn_Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(google_signIn_Intent, RC_SIGN_IN);
            }
        });

        tv_normal_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup_to_form = new Intent(getApplicationContext(), SignupAgreementActivity.class);

                startActivity(signup_to_form);
            }
        });
    }

    public void InitAllComponent() {

        tv_naver_signup = findViewById(R.id.tv_naver_signup);
        tv_google_signup = findViewById(R.id.tv_google_signup);
        tv_kakao_signup = findViewById(R.id.tv_kakao_signup);
        tv_facebook_signup = findViewById(R.id.tv_facebook_signup);
        tv_normal_signup = findViewById(R.id.tv_normal_signup);

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
        final ProgressDialog mDialog = new ProgressDialog(SignupActivity.this);
        mDialog.setMessage("구글 인증 중입니다.");
        mDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            mDialog.dismiss();

                            Toast.makeText(getApplicationContext(), "인증 실패", Toast.LENGTH_SHORT).show();

                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                for (UserInfo profile : user.getProviderData()) {
                                    if(profile.getUid() == user.getUid()) {
                                        // Id of the provider (ex: google.com)
                                        String providerId = profile.getProviderId();

                                        // Name, email address, and profile photo Url
                                        String name = profile.getDisplayName();
                                        String email = profile.getEmail();
                                        String phone = profile.getPhoneNumber();
                                        String pwd = "000000";
                                        String gender = "입력 필요";

                                        if(phone == null) {
                                            phone = "입력 필요";
                                        }

                                        InsertData Itask = new InsertData();
                                        Itask.execute("http://" + IP_ADDRESS + "/ewoman-php/insertUser.php", email, name, pwd, gender, phone);
                                    }
                                }
                            }

                            mDialog.dismiss();

                            //가입이 이루어졌을시 가입 화면을 빠져나감.
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                            startActivity(intent);
                            finish();
                            Toast.makeText(getApplicationContext(), "인증 성공", Toast.LENGTH_SHORT).show();
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
                Intent main_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(main_to_login);
                return true;
            case R.id.menu_signup :
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

            progressDialog = ProgressDialog.show(SignupActivity.this,
                    "회원가입 중입니다.", null, true, true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            //가입이 이루어졌을시 가입 화면을 빠져나감.
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

            startActivity(intent);
            finish();

            Toast.makeText(SignupActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

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

