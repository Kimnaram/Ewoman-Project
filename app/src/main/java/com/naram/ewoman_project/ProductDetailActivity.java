package com.naram.ewoman_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private final static String TAG = "ProductDetailActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private TextView tv_toolbar_title;
    private TextView tv_category_home;
    private TextView tv_category_pd;
    private TextView tv_category_name;
    private ImageView iv_item_image;
    private TextView tv_item_name;
    private TextView tv_item_price;
    private TextView tv_item_detail;
    private TextView tv_item_origin_is;
    private TextView tv_item_delivery_is;
    private TextView tv_option_1;
    private TextView tv_option_2;
    private TextView tv_option_3;
    private TextView tv_buy_try;
    private TextView tv_cart_try;
    private TextView tv_wishlist_try;
    private Spinner sp_option_1;
    private Spinner sp_option_2;
    private Spinner sp_option_3;

    private Drawable d_image = null;
    private String d_name = " ";
    private int t_price = 0;
    private String d_price = " ";
    private String d_detail = " ";
    private String d_origin = " ";
    private String d_delivery = " ";
    private String d_wishlist = " ";
    private List<String> d_option1_list = new ArrayList<String>();
    private List<String> d_option2_list = new ArrayList<String>();
    private List<String> d_option3_list = new ArrayList<String>();

    private int pdnumber = 0;
    private int pdwishlist = 0;
    private String category = " ";
    private String path = " ";

    private boolean state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        initAllComponent();

        Intent intent = getIntent();
        if (intent != null) {
            pdnumber = Integer.parseInt(intent.getStringExtra("pdnumber"));
            category = intent.getStringExtra("category");
            path = intent.getStringExtra("DBpath");

            if(category.equals("e-College")) {
                tv_buy_try.setText("예약하기");
            } else {
                tv_buy_try.setText("구매하기");
            }

            tv_category_name.setText(category);
            tv_category_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (category.equals("e-College")) {
                        Intent intent = new Intent(getApplicationContext(), eCollegeActivity.class);

                        finish();
                        startActivity(intent);
                    } else if(category.equals("e-Product")) {
                        Intent intent = new Intent(getApplicationContext(), eProductActivity.class);

                        finish();
                        startActivity(intent);
                    }
                }
            });

            tv_category_home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    finish();
                    startActivity(intent);
                }
            });

            firebaseDatabase.getReference("product/" + path + "/" + pdnumber).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        if (dataSnapshot.getKey().equals("name")) {
                            d_name = dataSnapshot.getValue().toString();
                        } else if (dataSnapshot.getKey().equals("price")) {
                            t_price = Integer.parseInt(dataSnapshot.getValue().toString());
                            DecimalFormat format = new DecimalFormat("###,###");
                            d_price = format.format(t_price);
                        } else if (dataSnapshot.getKey().equals("detail")) {
                            d_detail = dataSnapshot.getValue().toString();
                        } else if (dataSnapshot.getKey().equals("wishlist")) {
                            d_wishlist = dataSnapshot.getValue().toString();
                        } else if (dataSnapshot.getKey().equals("origin")) {
                            d_origin = dataSnapshot.getValue().toString();
                            if (d_origin.equals("입력 필요")) {
                                d_origin = " ";
                            }
                        } else if (dataSnapshot.getKey().equals("delivery")) {
                            d_delivery = dataSnapshot.getValue().toString();
                        } else if (dataSnapshot.getKey().equals("option")) {
                            firebaseDatabase.getReference("product/" + path + "/" + pdnumber + "/option/0").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int count = Integer.parseInt(Long.toString(snapshot.getChildrenCount()));
                                    Log.d(TAG, "count : " + count);
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.getKey().equals("title")) {
                                            tv_option_1.setText(dataSnapshot.getValue().toString() + "   *");
                                        } else {
                                            for(int i = 0; i < count - 1; i++) {
                                                firebaseDatabase.getReference("product/" + path + "/" + pdnumber + "/option/0").child("i").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.getKey().equals("option")) {
                                                            boolean sameflag = false;
                                                            String d_option_list_thing = snapshot.getValue().toString();
                                                            for (int j = 0; j < d_option1_list.size(); j++) {
                                                                String samethingchk = d_option1_list.get(j);
                                                                if (samethingchk.equals(d_option_list_thing)) {
                                                                    sameflag = true;
                                                                    break;
                                                                }
                                                            }
                                                            if (sameflag == false) {
                                                                d_option1_list.add(snapshot.getValue().toString());
                                                            }
                                                        } else {
                                                            // price 일 때
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }
                                            sp_option_1.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                            getApplicationContext(), android.R.layout.simple_spinner_item, d_option1_list);

                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    sp_option_1.setAdapter(adapter);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            firebaseDatabase.getReference("product/" + path + "/" + pdnumber + "/option/1").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int count = Integer.parseInt(Long.toString(snapshot.getChildrenCount()));
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.getKey().equals("title")) {
                                            tv_option_2.setText(dataSnapshot.getValue().toString());
                                            tv_option_2.setVisibility(View.VISIBLE);
                                        } else {
                                            for(int i = 0; i < count; i++) {
                                                firebaseDatabase.getReference("product/" + path + "/" + pdnumber + "/option/1").child("i").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.getKey().equals("option")) {
                                                            boolean sameflag = false;
                                                            String d_option_list_thing = snapshot.getValue().toString();
                                                            for (int j = 0; j < d_option2_list.size(); j++) {
                                                                String samethingchk = d_option2_list.get(j);
                                                                if (samethingchk.equals(d_option_list_thing)) {
                                                                    sameflag = true;
                                                                    break;
                                                                }
                                                            }
                                                            if (sameflag == false) {
                                                                d_option2_list.add(snapshot.getValue().toString());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }
                                            sp_option_2.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                            getApplicationContext(), android.R.layout.simple_spinner_item, d_option2_list);

                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    sp_option_2.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            firebaseDatabase.getReference("product/" + path + "/" + pdnumber + "/option/2").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        if (dataSnapshot.getKey().equals("title")) {
                                            tv_option_3.setText(dataSnapshot.getValue().toString() + "   *");
                                            tv_option_3.setVisibility(View.VISIBLE);
                                        } else {
                                            boolean sameflag = false;
                                            String d_option_list_thing = dataSnapshot.getValue().toString();
                                            for(int i = 0; i < d_option3_list.size(); i++) {
                                                String samethingchk = d_option3_list.get(i);
                                                if(samethingchk.equals(d_option_list_thing)) {
                                                    sameflag = true;
                                                    break;
                                                }
                                            }
                                            if(sameflag == false) {
                                                d_option3_list.add(dataSnapshot.getValue().toString());
                                            }
                                            sp_option_3.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                            getApplicationContext(), android.R.layout.simple_spinner_item, d_option3_list);

                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    sp_option_3.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }

                    tv_category_pd.setText(d_name);
                    tv_toolbar_title.setText(d_name);

                    downloadInLocal(pdnumber);
                    tv_item_name.setText(d_name);
                    tv_item_price.setText(d_price);
                    tv_item_detail.setText(d_detail);
                    tv_item_origin_is.setText(d_origin);
                    tv_item_delivery_is.setText(d_delivery);
                    tv_wishlist_try.setText(d_wishlist);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            tv_wishlist_try.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(state == false) {
                        state = true;
                        int wishlist = Integer.parseInt(tv_wishlist_try.getText().toString());
                        wishlist += 1;
                        tv_wishlist_try.setText(Integer.toString(wishlist));
                    } else {
                        state = false;
                        int wishlist = Integer.parseInt(tv_wishlist_try.getText().toString());
                        wishlist -= 1;
                        tv_wishlist_try.setText(Integer.toString(wishlist));
                    }
                }
            });

        }

    }

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);

        tv_category_home = findViewById(R.id.tv_category_home);
        tv_category_name = findViewById(R.id.tv_category_name);
        tv_category_pd = findViewById(R.id.tv_category_pd);

        iv_item_image = findViewById(R.id.iv_item_image);
        tv_item_name = findViewById(R.id.tv_item_name);
        tv_item_price = findViewById(R.id.tv_item_price);
        tv_item_detail = findViewById(R.id.tv_item_detail);
        tv_item_origin_is = findViewById(R.id.tv_item_origin_is);
        tv_item_delivery_is = findViewById(R.id.tv_item_delivery_is);

        tv_option_1 = findViewById(R.id.tv_option_1);
        tv_option_2 = findViewById(R.id.tv_option_2);
        tv_option_3 = findViewById(R.id.tv_option_3);

        tv_buy_try = findViewById(R.id.tv_buy_try);
        tv_cart_try = findViewById(R.id.tv_cart_try);
        tv_wishlist_try = findViewById(R.id.tv_wishlist_try);

        sp_option_1 = findViewById(R.id.sp_option_1);
        sp_option_2 = findViewById(R.id.sp_option_2);
        sp_option_3 = findViewById(R.id.sp_option_3);

    }

    public void downloadInLocal(int i) {

        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReference("product/" + path + "/" + i);
        StorageReference pathReference = storageReference.child("image.png");

        pathReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                d_image = Drawable.createFromStream(byteArrayInputStream, "Product Image");
                if (d_image == null) {
                    d_image = getResources().getDrawable(R.drawable.ewoman_main_logo);
                }
                iv_item_image.setImageDrawable(d_image);
            }
        });

    }

    @Override
    public void onBackPressed() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("product/" + path + "/" + pdnumber);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.getKey().equals("wishlist")) {
                        pdwishlist = Integer.parseInt(dataSnapshot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        int final_wishlist = Integer.parseInt(tv_wishlist_try.getText().toString());
        if(pdnumber != final_wishlist) {
            reference.child("wishlist").setValue(final_wishlist);
        }

        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if (firebaseAuth.getCurrentUser() != null) {
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
                Intent product_to_login = new Intent(getApplicationContext(), mem_LoginActivity.class);

                finish();
                startActivity(product_to_login);
                return true;
            case R.id.menu_signup:
                Intent product_to_signup = new Intent(getApplicationContext(), mem_SignupActivity.class);

                finish();
                startActivity(product_to_signup);
                return true;
            case R.id.menu_logout:

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(ProductDetailActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_product = new Intent(getApplicationContext(), ProductDetailActivity.class);
                logout_to_product.putExtra("pdnumber", pdnumber);
                logout_to_product.putExtra("category", category);
                logout_to_product.putExtra("DBpath", path);
                mDialog.dismiss();

                finish();
                startActivity(logout_to_product);
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(getApplicationContext(), CartActivity.class));

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
