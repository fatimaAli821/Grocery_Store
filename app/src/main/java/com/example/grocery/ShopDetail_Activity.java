package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import p32929.androideasysql_library.EasyDB;

public class ShopDetail_Activity extends AppCompatActivity {
    // Declare Ui views
  private ImageView shopImg;
   private TextView ShopNameTv,PhoneTv,EmailTv,AddressTv,OpenClose,DeliveryFeeTv,filterProductTv,no_data;
    private ImageButton CallBtn,mapBtn,cart,backbtn,filterProductBtn,reviewBtn;
    private SearchView searchProductEt;
    private RecyclerView productsRv;
    private ModelShop modelShop;
    public TextView cartcount;
   public String DeliveryFee;
   private RatingBar rating;
   private ArrayList<ModelCartItem>modelCartItemArrayList;
    private String shopuid,Shopname,shop_phone,shop_email,icon;
    private String Shop;
    private String mylatitude,mylongitude;
    private String Shoplatitude,Shoplongitude;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct>productlist;
    private AdapterProductUser adapterProductUser;
    private String   myphone;

    private TextView Shop_NameIt;

    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail_);
        // init Ui views
        shopImg=findViewById(R.id.shopImg);
        ShopNameTv=findViewById(R.id.ShopNameTv);
        PhoneTv=findViewById(R.id.PhoneTv);
        EmailTv=findViewById(R.id.EmailTv);
        AddressTv=findViewById(R.id.AddressTv);
        OpenClose=findViewById(R.id.OpenClose);
        DeliveryFeeTv=findViewById(R.id.DeliveryFeeTv);
        filterProductTv=findViewById(R.id.filterProductTv);
        CallBtn=findViewById(R.id.CallBtn);
        mapBtn=findViewById(R.id.mapBtn);
        cart=findViewById(R.id.cart);
        backbtn=findViewById(R.id.backbtn);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        searchProductEt=findViewById(R.id.searchProductEt);
        cartcount=findViewById(R.id.cartcount);
        rating=findViewById(R.id.rating);
        reviewBtn=findViewById(R.id.reviewBtn);
        cartcount.setVisibility(View.GONE);
        productsRv=findViewById(R.id.productsRv);
        no_data=findViewById(R.id.no_data);
        no_data.setVisibility(View.GONE);


        // get id of shop from intent
        shopuid=getIntent().getStringExtra("ShopUid");
        Shop=getIntent().getStringExtra("Shop");
        firebaseAuth=FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        loaddReviews();//avg rating set on Rating bar
        //each shop have its own products and orders so if user add itemsto cart and go back and open cart in different shop then cart should be different
        //so delete cart data when user open this Activity
        //DeleteCartData();
        loadcartcount();


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ShowCartDialouge();
                Intent intent=new Intent(ShopDetail_Activity.this,Cart_Activity.class);
                intent.putExtra("Shopname",Shopname);
                intent.putExtra("Shopid",shopuid);
                intent.putExtra("DeliveryFee",DeliveryFee);
                intent.putExtra("mylatitude",mylatitude);
                intent.putExtra("mylongitude",mylongitude);
                intent.putExtra("myPhone",myphone);
                startActivity(intent);

            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ShopDetail_Activity.this);
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

        CallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailphone();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenMap();
            }
        });

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShopDetail_Activity.this,ShopeReview_Activity.class);
                intent.putExtra("shop_uid",shopuid);
                startActivity(intent);
            }
        });

    }

    private void loadcartcount() {
        modelCartItemArrayList=new ArrayList<>();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopuid).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelCartItemArrayList.clear();

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelCartItem modelCartItem=dataSnapshot1.getValue(ModelCartItem.class);
                    modelCartItemArrayList.add(modelCartItem);
                    cartcount.setVisibility(View.VISIBLE);
                }


               // String count=""+dataSnapshot.getChildrenCount();
                cartcount.setText(""+dataSnapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private float ratingSum=0;

    private void loaddReviews() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopuid).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ratingSum=0;
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    float rating=Float.parseFloat(""+ds.child("rating").getValue().toString());

                    ratingSum=ratingSum+rating;//for avg rating add all ratings,later will divide it by number of reviews


                }


                long numberOfReviews=dataSnapshot.getChildrenCount();
                float avgrating=ratingSum/numberOfReviews;

                rating.setRating(avgrating);





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



   /* private void DeleteCartData() {

            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
            String id= databaseReference.push().getKey();
            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
            Map<String,Object> map=new HashMap<>();
            map.put("uuid",""+firebaseAuth.getUid());
            map.put("productid",productid);
            map.put("item_title",title);
            map.put("priceEach",priceEach);
            map.put("totalPrice",totalPrice);
            map.put("quantity",quantity);
            map.put("item_id",""+id);

            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Products").
                    removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ShopDetail_Activity.this, "Added to cart...", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ShopDetail_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }*/

/*
        EasyDB easyDB=EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn("ItemId",new String[]{"text","unique"})
                .addColumn("Item_PID",new String[]{"text","not null"})
                .addColumn("Item_Name",new String[]{"text","not null"})
                .addColumn("Item_Price_Each",new String[]{"text","not null"})
                .addColumn("Item_Price",new String[]{"text","not null"})
                .addColumn("Item_Quantity",new String[]{"text","not null"})
                .doneTableColumn();
        easyDB.deleteAllDataFromTable();//delete all data in the cart

 */




   /* public  Double allTotalPrice=0.00;
    // need to access these views in adapter so making public
    public  TextView sTotalTv,dFeeTv,allTotalPriceTv;

    private void ShowCartDialouge() {
        //init list
        cartItemList=new ArrayList<>();


        AlertDialog.Builder builder=new AlertDialog.Builder(ShopDetail_Activity.this);
        // inflate layout
        View view= LayoutInflater.from(this).inflate(R.layout.dialouge_cart,null);

        sTotalTv=view.findViewById(R.id.sTotalTv);
        dFeeTv=view.findViewById(R.id.dFeeTv);
        allTotalPriceTv=view.findViewById(R.id.TotalTv);
        Shop_NameIt=view.findViewById(R.id.ss);
        Button Checkoutbtn=view.findViewById(R.id.Checkoutbtn);
        try {
            Shop_NameIt.setText(Shopname);
        }catch (NullPointerException ignored){

        }
        builder.setView(view);

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Products").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItemList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelCartItem modelCartItem=ds.getValue(ModelCartItem.class);
                    cartItemList.add(modelCartItem);

                }
                adapterCartItem=new AdapterCartItem(ShopDetail_Activity.this,cartItemList);
                try {
                    cartitemsRv.setAdapter(adapterCartItem);
                }catch (NullPointerException ignored){

                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        EasyDB easyDB=EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn("ItemId",new String[]{"text","unique"})
                .addColumn("Item_PID",new String[]{"text","not null"})
                .addColumn("Item_Name",new String[]{"text","not null"})
                .addColumn("Item_Price_Each",new String[]{"text","not null"})
                .addColumn("Item_Price",new String[]{"text","not null"})
                .addColumn("Item_Quantity",new String[]{"text","not null"})
                .doneTableColumn();

        //get data from db

        Cursor cursor=easyDB.getAllData();
        while (cursor.moveToNext()){

            String id=cursor.getString(1);
            String pid=cursor.getString(2);
            String name=cursor.getString(3);
            String price=cursor.getString(4);
            String cost=cursor.getString(5);
            String quantity=cursor.getString(6);

            allTotalPrice=allTotalPrice+Double.parseDouble(cost);

            ModelCartItem modelCartItem=new ModelCartItem(""+id,""+pid,""+name,
                    ""+price,""+cost,""+quantity);
            cartItemList.add(modelCartItem);

        }

        //setup Adapter
        adapterCartItem=new AdapterCartItem(this,cartItemList);
        //set adapter to Recyclerview
        try {
            dFeeTv.setText("$"+DeliveryFee);
            sTotalTv.setText("$"+String.format("%.2f"+allTotalPrice));
            allTotalPriceTv.setText("$"+(allTotalPrice+Double.parseDouble(DeliveryFee.replaceAll("$",""))));
            cartitemsRv.setAdapter(adapterCartItem);

        }catch (NullPointerException ignored){

        }




        AlertDialog dialog=builder.create();
        dialog.show();

        //reset Total price on Dialouge Dismiss
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice=0.00;
            }
        });


    }*/

    private void OpenMap() {
        String address="https://maps.google.com/maps?saddr="+mylatitude+","+mylongitude+"&daddr="+Shoplatitude+","+Shoplongitude;
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(address));
        startActivity(intent);
    }

    private void dailphone() {
        Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shop_phone)));
        startActivity(intent);
        Toast.makeText(this,""+ shop_phone, Toast.LENGTH_SHORT).show();
    }

    private void loadSelectedProducts(final String selected) {

        productlist=new ArrayList<>();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopuid).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productlist.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()){

                    String productCategory=ds.child("Product_Category").getValue().toString();
                    // if selected category matches product category then add in list
                    if (selected.equals(productCategory)){

                        ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                        productlist.add(modelProduct);
                        no_data.setVisibility(View.GONE);

                    }else {
                        no_data.setVisibility(View.VISIBLE);
                    }

                }
                //setup Adapter
                adapterProductUser=new AdapterProductUser(ShopDetail_Activity.this,productlist);
                //set adapter
                productsRv.setAdapter(adapterProductUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadAllProducts() {
        productlist=new ArrayList<>();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopuid).child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                no_data.setVisibility(View.GONE);
                productlist.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelProduct modelProduct=ds.getValue(ModelProduct.class);
                    productlist.add(modelProduct);
                }


                //setup Adapter
                adapterProductUser=new AdapterProductUser(ShopDetail_Activity.this,productlist);
                //set adapter
                productsRv.setAdapter(adapterProductUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadShopProducts() {
        try {
            productlist=new ArrayList<>();
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(shopuid).child("Products").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    productlist.clear();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            ModelProduct modelProduct = dataSnapshot1.getValue(ModelProduct.class);
                            productlist.add(modelProduct);
                        }
                        //setup Adapter
                        adapterProductUser = new AdapterProductUser(ShopDetail_Activity.this, productlist);
                        productsRv.setAdapter(adapterProductUser);


                }



                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException ignored){

        }


    }

    private void loadShopDetails() {

        try {
            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(shopuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Shopname=""+dataSnapshot.child("ShopeName").getValue().toString();
                    shop_phone=""+dataSnapshot.child("phone").getValue().toString();
                    shop_email=""+dataSnapshot.child("email").getValue().toString();
                    Shoplatitude=""+dataSnapshot.child("latitude").getValue().toString();
                    String ShopAddress=""+dataSnapshot.child("address").getValue().toString();
                    Shoplongitude=""+dataSnapshot.child("longitude").getValue().toString();
                    DeliveryFee=""+dataSnapshot.child("delivery_fee").getValue().toString();
                    String ProfileImage=""+dataSnapshot.child("profileImage").getValue().toString();
                    String shopOpen=""+dataSnapshot.child("ShopOpen").getValue().toString();

                    //set values
                    ShopNameTv.setText(Shopname);
                    PhoneTv.setText(shop_phone);
                    EmailTv.setText(shop_email);
                    AddressTv.setText(ShopAddress);
                    DeliveryFeeTv.setText("Delivery Fee: $"+DeliveryFee);
                    if (shopOpen.equals("true")){
                        OpenClose.setText("Open");
                    }else {
                        OpenClose.setText("Closed");
                    }



                    try {
                        Picasso.get().load(ProfileImage).placeholder(R.drawable.ic_person_grey).into(shopImg);
                    }catch (Exception e){
                        try {
                            shopImg.setImageResource(R.drawable.ic_person_grey);

                        }catch (NullPointerException ignored){

                        }

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException ignored){

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
                            myphone = ds.child("phone").getValue().toString();
                            String profile = ds.child("profileImage").getValue().toString();
                            String city = ds.child("city").getValue().toString();
                            mylatitude = ds.child("latitude").getValue().toString();
                            mylongitude = ds.child("longitude").getValue().toString();

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
        no_data.setVisibility(View.GONE);
        ArrayList<ModelProduct>mylist=new ArrayList<>();
        for (ModelProduct obj:productlist){
            if (obj.getProduct_title().toUpperCase().contains(newText.toUpperCase())||
                    obj.getProduct_Category().toUpperCase().contains(newText.toUpperCase())){
                mylist.add(obj);
            }else {
                no_data.setVisibility(View.VISIBLE);
            }



            AdapterProductUser adapterProductUser=new AdapterProductUser(this,mylist);
            productsRv.setAdapter(adapterProductUser);

        }

    }


}
