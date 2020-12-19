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
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private final static String TAG = "CartActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private ListView lv_cart_product;
    private ListCartAdapter adapter;
    private ListCart listCart;

    private int count = 0;
    private Drawable storage_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.common_backspace); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); //툴바 배경색

        initAllComponent();

        listviewSetting();

        lv_cart_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);

                int number = adapter.getItem(position).getPdnumber();

                intent.putExtra("pdnumber", Integer.toString(number));
                intent.putExtra("DBpath", adapter.getItem(position).getCategory());
                if (adapter.getItem(position).getCategory().equals("ecollege")) {
                    intent.putExtra("category", "e-College");
                } else {
                    intent.putExtra("category", "e-Product");
                }

                adapter.notifyDataSetChanged();

                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        String uid = firebaseAuth.getCurrentUser().getUid();
//
//        firebaseDatabase.getReference("cart/" + uid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                count = Integer.parseInt(Long.toString(snapshot.getChildrenCount()));
//                Log.d(TAG, "count : " + count);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
////        adapter.clearAllItems();
//        listviewSetting();
//
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        adapter.clearAllItems();
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        adapter.clearAllItems();
//
//    }

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        lv_cart_product = findViewById(R.id.lv_cart_product);
        adapter = new ListCartAdapter();

    }

    private void listviewSetting() {

        final String uid = firebaseAuth.getCurrentUser().getUid();

                firebaseDatabase.getReference("cart/" + uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    firebaseDatabase.getInstance().getReference("cart/" + uid + "/" + i).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int pdnumber = 0;
                            String d_name = " ";
                            String d_price = " ";
                            String d_date = " ";
                            int d_count = 0;
                            String d_category = " ";

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                if (dataSnapshot.getKey().equals("pdnumber")) {
                                    pdnumber = Integer.parseInt(dataSnapshot.getValue().toString());
                                } else if (dataSnapshot.getKey().equals("name")) {
                                    d_name = dataSnapshot.getValue().toString();
                                } else if (dataSnapshot.getKey().equals("price")) {
                                    d_price = dataSnapshot.getValue().toString();
                                } else if (dataSnapshot.getKey().equals("date")) {
                                    d_date = dataSnapshot.getValue().toString();
                                } else if (dataSnapshot.getKey().equals("count")) {
                                    d_count = Integer.parseInt(dataSnapshot.getValue().toString());
                                } else if (dataSnapshot.getKey().equals("category")) {

                                }

                            }

                            listCart = new ListCart(pdnumber, d_name, d_date, d_count, d_price, d_category);
                            Log.d(TAG, listCart.getName() + ", " + listCart.getPrice() + ", " + listCart.getDate()
                                    + ", " + listCart.getCount());
                            adapter.addItem(listCart);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    ++i;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        lv_cart_product.setAdapter(adapter);

    }

//    public Drawable downloadInLocal(int i) {
//
//        StorageReference storageReference;
//        storageReference = FirebaseStorage.getInstance().getReference("poduct/" + uid  + "/" + i);
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
//                if (storage_image == null) {
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
                Intent cart_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(cart_to_login);
                return true;
            case R.id.menu_signup:
                Intent cart_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(cart_to_signup);
                return true;
            case R.id.menu_logout:

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(CartActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_cart = new Intent(getApplicationContext(), CartActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_cart);
                return true;
            case R.id.menu_cart:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
