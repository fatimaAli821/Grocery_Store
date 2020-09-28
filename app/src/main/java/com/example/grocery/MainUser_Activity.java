package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainUser_Activity extends AppCompatActivity {

    // ui views
    private TextView nameIv, emailIv, phoneIv, filterProductTv;
    private ImageButton Btnlogout, edit_profile, filterProductBtn,Setting;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ImageView profileIv;
    private TextView tabshop, tabOrders;
    private RelativeLayout ShopsRl, ordersRl;
    private RecyclerView shopRv,OrderRv;
    private EditText searchShoptEt;
    private ArrayList<ModelShop> shopArrayList;
    private AdapterShop adapterShop;
    private SearchView searchProductEt;
    private ArrayList<ModelOrederUser>modelOrederUserList;
    private AdapterOrderUser adapterOrderUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init ui views
        setContentView(R.layout.activity_main_user_);
        nameIv = findViewById(R.id.nameIv);
        emailIv = findViewById(R.id.emailIv);
        phoneIv = findViewById(R.id.phoneIv);
        tabshop = findViewById(R.id.tabshop);
        tabOrders = findViewById(R.id.tabOrders);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        filterProductTv = findViewById(R.id.filterProductTv);
        shopRv = findViewById(R.id.shopRv);
        ShopsRl = findViewById(R.id.ShopsRl);
        ordersRl = findViewById(R.id.ordersRl);
        OrderRv=findViewById(R.id.OrderRv);
        profileIv = findViewById(R.id.profileIv);
        searchProductEt = findViewById(R.id.searchProductEt);
        Setting=findViewById(R.id.Setting);


        Btnlogout = findViewById(R.id.Btnlogout);
        edit_profile = findViewById(R.id.edit_profile);
        //initalize firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();
        showShopUi();
        try {
            Btnlogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    makeMeofflion();

                }
            });

        } catch (NullPointerException ignored) {

        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainUser_Activity.this, User_Profile_Activity.class);
                startActivity(intent);
            }
        });

        tabshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShopUi();
            }
        });
        tabOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUi();
            }
        });

        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainUser_Activity.this,SettingActivity.class);
                startActivity(intent);
            }
        });


    }

    private void showShopUi() {
        // show shop ui hide oreders ui
        ShopsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);
        tabshop.setTextColor(getResources().getColor(R.color.colorBlack));
        tabshop.setBackgroundResource(R.drawable.shape_rect04);

        tabOrders.setTextColor(getResources().getColor(R.color.color_white));
        tabOrders.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrdersUi() {
        // show shop ui hide oreders ui
        ShopsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);
        tabOrders.setTextColor(getResources().getColor(R.color.colorBlack));
        tabOrders.setBackgroundResource(R.drawable.shape_rect04);

        tabshop.setTextColor(getResources().getColor(R.color.color_white));
        tabshop.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void makeMeofflion() {
        // after logout make user offlion
        progressDialog.setMessage("logging out....");
        Map<String, Object> map = new HashMap<>();
        map.put("onlion", "false");

        //update value to db
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                firebaseAuth.signOut();
                // this listener will be called when there is change in firebase user session
                FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {

                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user == null) {

                            // user auth state is changed - user is null
                            // launch login activity
                            Toast.makeText(MainUser_Activity.this, "Logout", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainUser_Activity.this, Login_Activity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            loadMyInfo();
                        }
                    }
                };
                authListener.onAuthStateChanged(firebaseAuth);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(MainUser_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(MainUser_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish();
        } else {
            loadMyInfo();
        }

    }

    private void loadMyInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uuid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = ds.child("name").getValue().toString();
                            String accountType = ds.child("accountType").getValue().toString();
                            String email = ds.child("email").getValue().toString();
                            String phone = ds.child("phone").getValue().toString();
                            String profile = ds.child("profileImage").getValue().toString();
                            String city = ds.child("city").getValue().toString();

                            // nameTv.setText(name +"("+accountType+")");
                            nameIv.setText(name + "(" + accountType + ")");
                            emailIv.setText(email);
                            phoneIv.setText(phone);
                            try {
                                Picasso.get().load(profile).placeholder(R.drawable.ic_person_grey).into(profileIv);
                            } catch (Exception e) {
                                try {
                                    profileIv.setImageResource(R.drawable.ic_person_grey);

                                } catch (NullPointerException ignored) {

                                }
                            }
                            // load only that shops tha are in the city of user

                            loadShop(city);
                            loadOrders();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadOrders() {
        //init list
        modelOrederUserList=new ArrayList<>();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear list before adding
                modelOrederUserList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String uid=ds.getRef().getKey();

                    DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference1.child(uid).child("Orders").orderByChild("OrderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                                            ModelOrederUser modelOrederUser=ds.getValue(ModelOrederUser.class);

                                            //add to the list
                                            modelOrederUserList.add(modelOrederUser);
                                        }

                                        //setup Adapter
                                        adapterOrderUser=new AdapterOrderUser(MainUser_Activity.this,modelOrederUserList);
                                        //set adapter to list
                                        OrderRv.setAdapter(adapterOrderUser);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadShop(final String city) {
        //init list
        shopArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("accountType").equalTo("Seller").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shopArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelShop modelShop = ds.getValue(ModelShop.class);
                    String Shopcity = ds.child("city").getValue().toString();


                    if (Shopcity.equals(city)) {
                        shopArrayList.add(modelShop);
                    }

                }
                //set up adapter
                adapterShop = new AdapterShop(MainUser_Activity.this, shopArrayList);
                shopRv.setAdapter(adapterShop);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    }

