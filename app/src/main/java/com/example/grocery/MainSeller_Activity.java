package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
 import androidx.appcompat.widget.SearchView;
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

public class MainSeller_Activity extends AppCompatActivity {
    private TextView nameTv,tabProducts,tabOrders,filterProductTv,filterOrderTv;
    private TextView ShopName;
    private TextView Email;
    private SearchView searchProductEt;
    private ImageButton Btnlogout,edit_profile_Seller,Add_product,filterProductBtn,filterOrderBtn,morebtn,Setting;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ImageView profileIv;
    private RelativeLayout productsRl,ordersRl;
    private RecyclerView productsRv,OrderRv_seller;
    private ArrayList<ModelProduct>productList;
    private AdapterProductSeller adapterProductSeller;
    private ArrayList<ModelOrderShop>modelOrderShopArrayList;
    private AdapterOrderShop adapterOrderShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller_);
        nameTv = findViewById(R.id.nameIv);

        ShopName=findViewById(R.id.Shop);
        Email=findViewById(R.id.Email);
        Add_product=findViewById(R.id.Add_product);
        Btnlogout = findViewById(R.id.Btnlogout);
        edit_profile_Seller=findViewById(R.id.edit_profile_Seller);
        profileIv=findViewById(R.id.profileIv);
        tabProducts=findViewById(R.id.tabProducts);
        tabOrders=findViewById(R.id.tabOrders);
        productsRl=findViewById(R.id.productsRl);
        ordersRl=findViewById(R.id.ordersRl);
        searchProductEt=findViewById(R.id.searchProductEt);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        productsRv=findViewById(R.id.productsRv);
        filterProductTv=findViewById(R.id.filterProductTv);
        filterOrderTv=findViewById(R.id.filterOrderTv);
        filterOrderBtn=findViewById(R.id.filterOrderBtn);
        OrderRv_seller=findViewById(R.id.OrderRv_seller);
        morebtn=findViewById(R.id.morebtn);
        Setting=findViewById(R.id.Setting);



        //initalize firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        checkUser();
        loadAllProducts();
        loadAllOrders();
        showProductsUi();
        try {
            Btnlogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeMeofflion();

                }
            });



        }catch (NullPointerException ignored){

        }

        tabProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load products

                showProductsUi();

            }
        });

        tabOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load orders
                ShowOrderUi();



            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainSeller_Activity.this);
                builder.setTitle("Choose Category");
                builder.setItems(Contants.product_Category1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected=Contants.product_Category1[which];
                        filterProductTv.setText(selected);
                        if (selected.equals("All")){
                            loadAllProducts();
                        }else {
                            loadSelectedProducts(selected);
                        }
                    }
                }).show();
            }
        });


        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(MainSeller_Activity.this);
                builder.setTitle("filter Order");
                builder.setItems(Contants.Options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       // String  select=Contants.Options[which];
                       final String Options[]={"All","Completed","In Progress","Cancelled"};

                        if (which==0){
                            loadAllOrders();
                            filterOrderTv.setText("Showing All Orders");
                        }else {
                            String optionclicked=Options[which];
                            filterOrderTv.setText("Showing"+""+optionclicked+""+ "Orders");
                            loadSellectedOrders(optionclicked);
                        }

                    }
                }).show();

            }
        });


        //pop menu
        final PopupMenu popupMenu=new PopupMenu(MainSeller_Activity.this,morebtn);
        popupMenu.getMenu().add("Setting");
        popupMenu.getMenu().add("Reviews");
        popupMenu.getMenu().add("Promotion code");
        //handel click
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle()=="Setting"){
                    Intent intent=new Intent(MainSeller_Activity.this,SettingActivity.class);
                    startActivity(intent);

                }else if (item.getTitle()=="Reviews"){
                    //open same review activity as used in user main page
                    Intent intent=new Intent(MainSeller_Activity.this,ShopeReview_Activity.class);
                    intent.putExtra("shop_uid",firebaseAuth.getUid());
                    startActivity(intent);

                }else if (item.getTitle()=="Promotion code"){
                    Intent intent=new Intent(MainSeller_Activity.this,PromotionCodeActivity.class);
                    startActivity(intent);

                }
                return true;
            }
        });

        //show more option:Setting/Rewies/More Options
        morebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show popup Menu
                popupMenu.show();

            }
        });




    }

    private void loadSellectedOrders(final String optionclicked) {
        modelOrderShopArrayList=new ArrayList<>();
        //load order of Shop
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelOrderShopArrayList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    String order_status=dataSnapshot1.child("OrderStatus").getValue().toString();
                    if (optionclicked.equals(order_status)) {
                        ModelOrderShop modelOrderShop = dataSnapshot1.getValue(ModelOrderShop.class);
                        modelOrderShopArrayList.add(modelOrderShop);
                    }

                    adapterOrderShop=new AdapterOrderShop(MainSeller_Activity.this,modelOrderShopArrayList);
                    OrderRv_seller.setAdapter(adapterOrderShop);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadAllOrders() {
        modelOrderShopArrayList=new ArrayList<>();
        //load order of Shop
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Orders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelOrderShopArrayList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelOrderShop modelOrderShop=ds.getValue(ModelOrderShop.class);
                    modelOrderShopArrayList.add(modelOrderShop);
                }

                adapterOrderShop=new AdapterOrderShop(MainSeller_Activity.this,modelOrderShopArrayList);
                OrderRv_seller.setAdapter(adapterOrderShop);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadSelectedProducts(final String selected) {

        productList=new ArrayList<>();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    String productCategory=ds.child("Product_Category").getValue().toString();
                    // if selected category matches product category then add in list
                    if (selected.equals(productCategory)){
                        ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                        productList.add(modelProduct);
                    }

                }
                //setup Adapter
                adapterProductSeller=new AdapterProductSeller(MainSeller_Activity.this,productList);
                //set adapter
                productsRv.setAdapter(adapterProductSeller);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadAllProducts() {
        productList=new ArrayList<>();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                    productList.add(modelProduct);
                }
                //setup Adapter
                adapterProductSeller=new AdapterProductSeller(MainSeller_Activity.this,productList);
                //set adapter
                productsRv.setAdapter(adapterProductSeller);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showProductsUi() {
        ordersRl.setVisibility(View.GONE);
        OrderRv_seller.setVisibility(View.GONE);
        productsRl.setVisibility(View.VISIBLE);
        productsRv.setVisibility(View.VISIBLE);

        tabProducts.setTextColor(getResources().getColor(R.color.colorBlack));
        tabProducts.setBackgroundResource(R.drawable.shape_rect04);

        tabOrders.setTextColor(getResources().getColor(R.color.color_white));
        tabOrders.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }


    private void ShowOrderUi() {
        productsRl.setVisibility(View.GONE);
        productsRv.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);
        OrderRv_seller.setVisibility(View.VISIBLE);
        tabProducts.setTextColor(getResources().getColor(R.color.color_white));
        tabProducts.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrders.setTextColor(getResources().getColor(R.color.colorBlack));
        tabOrders.setBackgroundResource(R.drawable.shape_rect04);
    }


    private void makeMeofflion() {
        // after logout make user offlion
        progressDialog.setMessage("logging out....");
        Map<String,Object> map=new HashMap<>();
        map.put("onlion","false");

        //update value to db
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
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
                            Toast.makeText(MainSeller_Activity.this,"Logout",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainSeller_Activity.this,Login_Activity.class);
                            startActivity(intent);
                            finish();

                        }
                        else
                        {
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
                Toast.makeText(MainSeller_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(MainSeller_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish();
        } else {
            loadMyInfo();
        }

    }

    private void loadMyInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uuid").equalTo(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String name = ds.child("name").getValue().toString();
                            String accountType = ds.child("accountType").getValue().toString();
                            String ShopName_s=ds.child("ShopeName").getValue().toString();
                            String email_s=ds.child("email").getValue().toString();
                            String profile_img=ds.child("profileImage").getValue().toString();

                            //set the values
                            try {
                                nameTv.setText(name+"("+accountType+")");
                                ShopName.setText(ShopName_s);
                                Email.setText(email_s);
                            }catch (NullPointerException ignored){

                            }


                            try {
                                Picasso.get().load(profile_img).placeholder(R.drawable.ic_person_grey).into(profileIv);
                            }catch (Exception e){
                                profileIv.setImageResource(R.drawable.ic_person_grey);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public void edit_profile_Sellerr(View view) {
        Intent intent=new Intent(this,Seller_profile_Activity.class);
        startActivity(intent);
    }

    public void Add_product(View view) {
        Intent intent=new Intent(this,Add_product_Activity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (searchProductEt!=null){
            searchProductEt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }


    }

           private void search(String newText) {
            ArrayList<ModelProduct>mylist=new ArrayList<>();
            for (ModelProduct obj:productList){
                if (obj.getProduct_title().toUpperCase().contains(newText.toUpperCase())||
                        obj.getProduct_Category().toUpperCase().contains(newText.toUpperCase())){
                    mylist.add(obj);
                }



                AdapterProductSeller adapterProductSeller=new AdapterProductSeller(this,mylist);
                productsRv.setAdapter(adapterProductSeller);

            }

    }


}
