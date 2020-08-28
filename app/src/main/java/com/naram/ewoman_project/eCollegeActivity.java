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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class eCollegeActivity extends AppCompatActivity {

    private ListView lv_eCollege_product;
    private ListViewAdapter adapter;
    private ListProduct listProduct;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private static final String TAG = "eCollegeActivity";
    private Drawable[] d_image;
//    private Drawable storage_image;

    private EditText et_search_text;
    private TextView tv_search_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_college);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        InitAllComponent();

        lv_eCollege_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);

                int number = adapter.getItem(position).getPdnumber();

                intent.putExtra("pdnumber", Integer.toString(number));
                intent.putExtra("category", "e-College");
                intent.putExtra("DBpath", "ecollege");

                adapter.notifyDataSetChanged();

                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.clearAllItems();
        listviewSetting();

        et_search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        et_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = et_search_text.getText().toString();
                adapter.fillter(search);
            }
        });

        tv_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.fillter(et_search_text.getText().toString());
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        adapter.clearAllItems();

    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.clearAllItems();

        et_search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        et_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = et_search_text.getText().toString();
                adapter.fillter(search);
            }
        });

        tv_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.fillter(et_search_text.getText().toString());
            }
        });

    }

    public void InitAllComponent() {

        lv_eCollege_product = findViewById(R.id.lv_eCollege_product);
        et_search_text = findViewById(R.id.et_search_text);
        tv_search_btn = findViewById(R.id.tv_search_btn);

        adapter = new ListViewAdapter();

        firebaseAuth = FirebaseAuth.getInstance();

    }

//    private int listviewCount() {
//
//        final AtomicInteger count = new AtomicInteger();
//        firebaseDatabase.getInstance().getReference("product/ecollege").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                childCount = count.incrementAndGet();
//                System.out.println("childCount : " + childCount);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//
//        });
//
//        return childCount;
//
//    }

    private void listviewSetting() {

        d_image = new Drawable[2];

        for(int i = 0; i < 2; i++) {
            final int d_pdnumber = i;
            if(i == 0) {
                d_image[i] = getResources().getDrawable(R.drawable.prd_front_look_theraphy);
            } else if(i == 1) {
                d_image[i] = getResources().getDrawable(R.drawable.prd_book_theraphy);
            }

            firebaseDatabase.getInstance().getReference("product/ecollege/" + i).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String d_name = " ";
                    int t_price = 0;
                    String d_price = " ";
                    int d_wishlist = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        if (dataSnapshot.getKey().equals("name")) {
                            d_name = dataSnapshot.getValue().toString();
                        } else if (dataSnapshot.getKey().equals("price")) {
                            t_price = Integer.parseInt(dataSnapshot.getValue().toString());
                            DecimalFormat format = new DecimalFormat("###,###");
                            d_price = format.format(t_price);

                        } else if (dataSnapshot.getKey().equals("wishlist")) {
                            d_wishlist = Integer.parseInt(dataSnapshot.getValue().toString());
                        }


                    }

//                    Drawable d_image = downloadInLocal(product_no);

                    listProduct = new ListProduct(d_pdnumber, d_image[d_pdnumber], d_name, d_price, d_wishlist);
                    adapter.addItem(listProduct);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        lv_eCollege_product.setAdapter(adapter);

    }

//    private Drawable downloadInLocal(int i) {
//
//        StorageReference storageReference;
//        storageReference = FirebaseStorage.getInstance().getReference("product/ecollege/" + i);
//        StorageReference pathReference = storageReference.child("image.png");
//
//        final int number = i;
//
//        long MAX_IMAGE_SIZE = 1024 * 1024;
//
//        pathReference.getBytes(MAX_IMAGE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//
//                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//                storage_image = Drawable.createFromStream(byteArrayInputStream, "Product Image");
//                if(storage_image == null) {
//                    storage_image = getResources().getDrawable(R.drawable.ewoman_main_logo);
//                }
//
//            }
//        });
//
//        return storage_image;
//
//    }

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
            case R.id.menu_logout :

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(eCollegeActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_main = new Intent(getApplicationContext(), eCollegeActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_main);
                return true;
            case R.id.menu_cart :
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
