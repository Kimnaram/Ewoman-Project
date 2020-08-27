package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private FirebaseAuth firebaseAuth;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private Button btn_to_ecollege;
    private Button btn_to_ourstry;
    private Button btn_to_lookth;
    private Button btn_to_bookth;

    private ImageButton ib_instagram;
    private ImageButton ib_facebook;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitAllComponent();

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 메뉴버튼 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_menu); // 메뉴 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        firebaseAuth = FirebaseAuth.getInstance();

        LinearLayout ll_navigation_container = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.navigation_item, null);
        ll_navigation_container.setBackground(getResources().getDrawable(R.color.colorCocoa));
        ll_navigation_container.setPadding(30, 70, 30, 50);
        ll_navigation_container.setOrientation(LinearLayout.VERTICAL);
        ll_navigation_container.setGravity(Gravity.BOTTOM);
        ll_navigation_container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/nanumbarungothicbold.ttf");

        ImageView iv_userpicture = new ImageView(this);
        iv_userpicture.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_ewoman_round));
        param.setMargins(20, 30, 20, 0);
        iv_userpicture.setLayoutParams(param);

        final TextView tv_username = new TextView(this);
        tv_username.setTextColor(getResources().getColor(R.color.colorWhite));
        tv_username.setTextSize(17);
        tv_username.setTypeface(typeface);
        tv_username.setPadding(0, 50, 0, 0);
        param.setMargins(20, 60, 20, 10);
        tv_username.setLayoutParams(param);

        final TextView tv_useremail = new TextView(this);
        tv_useremail.setTextColor(getResources().getColor(R.color.colorWhite));
        tv_useremail.setTextSize(14);
        tv_useremail.setTypeface(typeface);
        param.setMargins(20, 0, 20, 20);
        tv_useremail.setLayoutParams(param);

        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null) {
            String uid = firebaseUser.getUid();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference("users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        if(dataSnapshot.getKey().equals("name")) {
                            username = dataSnapshot.getValue().toString();
                            tv_username.setText(username + " 님");
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            tv_useremail.setText(firebaseUser.getEmail());

        } else if(firebaseUser == null) {

            iv_userpicture.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher_ewoman_round));
            tv_username.setText("로그인이 필요합니다.");
            tv_useremail.setText(" ");

            tv_username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent navi_to_login = new Intent(getApplicationContext(), mem_LoginActivity.class);

                    startActivity(navi_to_login);
                }
            });

        }

        ll_navigation_container.addView(iv_userpicture);
        ll_navigation_container.addView(tv_username);
        ll_navigation_container.addView(tv_useremail);

        navigationView.addHeaderView(ll_navigation_container);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                if(id == R.id.item_our_story) {
                    Intent main_to_ourstry = new Intent(getApplicationContext(), OurstoryActivity.class);

                    startActivity(main_to_ourstry);
                } else if (id == R.id.item_e_college) {
                    Intent main_to_ecollege = new Intent(getApplicationContext(), eCollegeActivity.class);

                    startActivity(main_to_ecollege);
                } else if (id == R.id.item_e_product) {
                    Intent main_to_eproduct = new Intent(getApplicationContext(), eProductActivity.class);

                    startActivity(main_to_eproduct);
                } else if (id == R.id.item_e_review) {
                    Intent main_to_review = new Intent(getApplicationContext(), ReviewListActivity.class);
                    main_to_review.putExtra("username", username);

                    startActivity(main_to_review);
                } else if (id == R.id.item_contact) {
                    Intent main_to_contact = new Intent(getApplicationContext(), ContactActivity.class);

                    startActivity(main_to_contact);
                }

                return true;
            }
        });

        btn_to_ecollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main_to_ecollege = new Intent(getApplicationContext(), eCollegeActivity.class);

                startActivity(main_to_ecollege);
            }
        });

        btn_to_ourstry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main_to_ourstry = new Intent(getApplicationContext(), OurstoryActivity.class);

                startActivity(main_to_ourstry);
            }
        });

        btn_to_lookth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);

                intent.putExtra("pdnumber", "0");
                intent.putExtra("category", "e-College");
                intent.putExtra("DBpath", "ecollege");

                startActivity(intent);
            }
        });

        btn_to_bookth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);

                intent.putExtra("pdnumber", "1");
                intent.putExtra("category", "e-College");
                intent.putExtra("DBpath", "ecollege");

                startActivity(intent);
            }
        });

        ib_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, "https://www.instagram.com/ewoman.kr/");
                // 구글로 검색

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    String msg = "웹페이지로 이동할 수 없습니다.";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        ib_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, "https://www.facebook.com/suntteut.sunny");
                // 구글로 검색

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    String msg = "웹페이지로 이동할 수 없습니다.";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void InitAllComponent() {

        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        btn_to_ecollege = findViewById(R.id.btn_to_ecollege);
        btn_to_ourstry = findViewById(R.id.btn_to_ourstry);
        btn_to_lookth = findViewById(R.id.btn_to_lookth);
        btn_to_bookth = findViewById(R.id.btn_to_bookth);

        ib_instagram = findViewById(R.id.ib_instagram);
        ib_facebook = findViewById(R.id.ib_facebook);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if(firebaseAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.toolbar_al_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home : {
                drawerLayout.openDrawer(GravityCompat.START);
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
            case R.id.menu_logout :

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_main = new Intent(getApplicationContext(), MainActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_main);
                return true;
            case R.id.menu_cart :
                startActivity(new Intent(getApplicationContext(), CartActivity.class));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.

    }
}
