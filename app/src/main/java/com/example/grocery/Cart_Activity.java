package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Cart_Activity extends AppCompatActivity {

   private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;
    private FirebaseAuth firebaseAuth;
   private TextView shopname,sTotalLabelTv,dFeeLabelTv,dFeeTv,TotalLabelTv;
   private Button Checkoutbtn;
     public TextView allTotalPriceTv;
    private RecyclerView cartitemsRv;
    private String s_id;
    private String s_name;
    private String mylatitude;
    private String mylongitude;
    private String myPhone;
    public String DeliveryFee;
    public TextView sTotalTv;
    public  Double allTotalPrice=0.00;
    private ProgressDialog progressDialog;
   private Context context;
   private int count=0;
   private FloatingActionButton ValidateBtn;
   private EditText CodeEt;
   public TextView PromoDescriptionTv,sdisscountTv;
   private ShopDetail_Activity shopDetail_activity;
   public Button applyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_);

        if (isPtomotionCodeisApplied){


            PromoDescriptionTv.setVisibility(View.VISIBLE);
            applyButton.setVisibility(View.VISIBLE);
            applyButton.setText("Applied");
            CodeEt.setText(PromoCode);
            PromoDescriptionTv.setText(PromoDescription);
        }else {
            try {
                PromoDescriptionTv.setVisibility(View.GONE);
                applyButton.setVisibility(View.GONE);
                applyButton.setText("Apply");
            }catch (NullPointerException ignored){

            }



        }


        s_id=getIntent().getStringExtra("Shopid");
        s_name=getIntent().getStringExtra("Shopname");
        DeliveryFee=getIntent().getStringExtra("DeliveryFee");
        mylatitude=getIntent().getStringExtra("mylatitude");
        mylongitude=getIntent().getStringExtra("mylongitude");
        myPhone=getIntent().getStringExtra("myPhone");

        cartitemsRv=findViewById(R.id.cartitemsRv);
        firebaseAuth=FirebaseAuth.getInstance();
        shopname=findViewById(R.id.ss);
        shopname.setText(s_name);
        sTotalLabelTv=findViewById(R.id.sTotalLabelTv);
        sTotalTv=findViewById(R.id.sTotalTv);
        dFeeLabelTv=findViewById(R.id.dFeeLabelTv);
        dFeeTv=findViewById(R.id.dFeeTv);
        TotalLabelTv=findViewById(R.id.TotalLabelTv);
        allTotalPriceTv=findViewById(R.id.TotalTv);
        Checkoutbtn=findViewById(R.id.Checkoutbtn);
        CodeEt=findViewById(R.id.CodeEt);
        sdisscountTv=findViewById(R.id.sdisscountTv);
        applyButton=findViewById(R.id.applyButton);
        ValidateBtn=findViewById(R.id.ValidateBtnpromo);
        PromoDescriptionTv=findViewById(R.id.PromoDescriptionTv);
        // init progress dialouge
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        //calculation
        Calculation();



        //place order
        Checkoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first validate delivery address
                if (mylatitude.equals("")||mylatitude.equals(null)||mylongitude.equals("")||mylongitude.equals(null)){
                    //user did not enter address in Profile
                    Toast.makeText(Cart_Activity.this, "Please enter address in your profile before placing Order....", Toast.LENGTH_SHORT).show();
                    return;//do not proceed further
                }
                if (myPhone.equals("")||myPhone.equals(null)){
                    //user did not enter Phone number in Profile
                    Toast.makeText(Cart_Activity.this, "Please enter Phone number in your profile before placing Order....", Toast.LENGTH_SHORT).show();
                    return;//do not proceed further
                }
                
                if (cartItemList.size()==0){
                    Toast.makeText(Cart_Activity.this, "No item in cart...", Toast.LENGTH_SHORT).show();
                    return;
                }

                SubmitOrder();



            }
        });

        //start Validating Promo Code when send button is clicked

        try {
            ValidateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //get code from edit text
                    // 1) if not empty promotion may be applied otherwise no promotion
                    //check code is valid
                    //if avalible promotion may be applied otherwise no promotion
                    //check if expired or not
                    //if not Expired:promotion may be applied otherwise no promotion
                    //check minimum order price
                    //if minimum order price is >= subTotal Price Promotion available otherwise not


                        String PromotionCodeEt=CodeEt.getText().toString().trim();
                        if (TextUtils.isEmpty(PromotionCodeEt)){
                            try {
                                Toast.makeText(Cart_Activity.this, "Please Enter the Promotion code", Toast.LENGTH_SHORT).show();

                            }catch (NullPointerException ignored){

                            }



                        }else {
                            CheckCodeavaliblity(PromotionCodeEt);
                        }



                }
            });
        }catch (NullPointerException ignored){

        }



        //apply code, no need to check if valid or not because this button is visible only if code is valid
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPtomotionCodeisApplied=true;
                applyButton.setText("Applied");
                priceWithDiscount();
            }
        });







    }
    public boolean isPtomotionCodeisApplied=false;
    String PromoId,Promotimestamp,PromoCode,PromoDescription,PromoExpDate,PromoMinimumOrderPrice,PromoPrice;
    private void CheckCodeavaliblity(String PromotionCode){

         isPtomotionCodeisApplied=false;

            priceWithoutDiscount();
            applyButton.setText("Apply");

            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Please wait..");
            progressDialog.setMessage("Check Promo Code..");
            progressDialog.setCanceledOnTouchOutside(false);

            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(s_id).child("Promotion Code").orderByChild("Code").equalTo(PromotionCode)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check if Promo code is Exists
                            if (dataSnapshot.exists()){

                                for (DataSnapshot ds:dataSnapshot.getChildren()){
                                    progressDialog.dismiss();

                                    PromoId=""+ds.child("id").getValue();
                                    Promotimestamp=""+ds.child("timestamp").getValue();
                                    PromoCode=""+ds.child("Code").getValue();
                                    PromoDescription=""+ds.child("p_Description").getValue();
                                    PromoExpDate=""+ds.child("expiredate").getValue();
                                    PromoMinimumOrderPrice=""+ds.child("MiniOrder").getValue();
                                    PromoPrice=""+ds.child("promoPrice").getValue();
                                    PromoId=""+ds.child("id").getValue();


                                    //now check if code is expired or not
                                    try {
                                        CheckCodeEXpireDate();


                                    }catch (NullPointerException ignored){

                                    }

                                }
                            }else {
                                //enter Promo Code does not exist

                                try {
                                    progressDialog.dismiss();
                                    Toast.makeText(Cart_Activity.this, "Invalid Promo Code", Toast.LENGTH_SHORT).show();
                                    applyButton.setVisibility(View.GONE);
                                    PromoDescriptionTv.setVisibility(View.GONE);
                                     PromoDescriptionTv.setText("");
                                     return;
                                }catch (NullPointerException ignore){

                                }


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }






    private void CheckCodeEXpireDate() {
        //get current date


        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);//it start from 0 istead of 1 thats why we did +1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        //concatenate date
        String todayDate=day+"/"+month+"/"+year;

        /*----Check for Expiry */

        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
            Date Currentdate=simpleDateFormat.parse(todayDate);
            Date Expiredate=simpleDateFormat.parse(PromoExpDate);

            if (Expiredate.compareTo(Currentdate)>0){
                //not Expired
                CheckMinimumOrderPrice();

            }else if (Expiredate.compareTo(Currentdate)<0){
                //Expired
                progressDialog.dismiss();
                Toast.makeText(Cart_Activity.this, "The Promotion code is Expired on"+Expiredate, Toast.LENGTH_SHORT).show();


                applyButton.setVisibility(View.GONE);
                PromoDescriptionTv.setVisibility(View.GONE);
                PromoDescriptionTv.setText("");


            }else  if (Expiredate.compareTo(Currentdate)==0) {
                //not Expired
                CheckMinimumOrderPrice();

            }


        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            applyButton.setVisibility(View.GONE);
            PromoDescriptionTv.setVisibility(View.GONE);
            PromoDescriptionTv.setText("");
        }
    }

    private void CheckMinimumOrderPrice() {
        //each promo Code have minimum  order Price requirement,if order price is less then requirement then does not allow to apply code

        if (Double.parseDouble(String.format("%.2f",allTotalPrice))<Double.parseDouble(PromoMinimumOrderPrice)){
            progressDialog.dismiss();
            Toast.makeText(Cart_Activity.this, "This code is valid for order with minimum amount : $"+PromoMinimumOrderPrice, Toast.LENGTH_SHORT).show();


            applyButton.setVisibility(View.GONE);
            PromoDescriptionTv.setVisibility(View.GONE);
            priceWithoutDiscount();



        }else {

            applyButton.setVisibility(View.VISIBLE);
            PromoDescriptionTv.setVisibility(View.VISIBLE);
            PromoDescriptionTv.setText(PromoDescription);

        }



    }

    private void priceWithDiscount() {
        try {
            sdisscountTv.setText("$"+PromoPrice);
            dFeeTv.setText("$"+DeliveryFee);
            sTotalTv.setText("$"+String.format("%.2f",allTotalPrice));
            allTotalPriceTv.setText("$"+(allTotalPrice+Double.parseDouble(DeliveryFee.replace("$",""))-Double.parseDouble(PromoPrice)));

        }catch (NullPointerException ignored){

        }
    }


    private void priceWithoutDiscount() {

        sdisscountTv.setText("$0");
        dFeeTv.setText("$"+DeliveryFee);
        sTotalTv.setText("$"+String.format("%.2f",allTotalPrice));
        allTotalPriceTv.setText("$"+(allTotalPrice+Double.parseDouble(DeliveryFee.replace("$",""))));



    }

    private void SubmitOrder() {
        //show progress dialouge
        progressDialog.setMessage("Placing Order...");
        progressDialog.show();

        //for order id and order time
        final String timestamp=""+System.currentTimeMillis();

        String cost=allTotalPriceTv.getText().toString().replace("$","");//remove $ if contain

        Map<String,Object>map=new HashMap<>();
        map.put("OrderId",timestamp);
        map.put("OrderTime",timestamp);
        map.put("OrderStatus","In Progress");
        map.put("latitude",mylatitude);
        map.put("longitude",mylongitude);
        map.put("OrderCost",""+cost);
        map.put("deliveryfee",DeliveryFee);
        map.put("OrderBy",""+firebaseAuth.getUid());
        map.put("orderTo",""+s_id);
        if (isPtomotionCodeisApplied){
            map.put("discount",PromoPrice);
        }else {
            map.put("discount","0");
        }


        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(s_id).child("Orders");
        databaseReference.child(timestamp).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //order info added now add order items
                for (int i=0;i<cartItemList.size();i++){
                    String id=cartItemList.get(i).getItem_id();
                    String Pid=cartItemList.get(i).getProductid();
                    String priceEach=cartItemList.get(i).getPriceEach();
                    String TotalPrice=cartItemList.get(i).getTotalPrice();
                    String quantity=cartItemList.get(i).getQuantity();
                    String name=cartItemList.get(i).getItem_title();

                    Map<String,Object>map1=new HashMap<>();
                    map1.put("itemId",id);
                    map1.put("ProductId",Pid);
                    map1.put("CoastEach",priceEach);
                    map1.put("CoastTotal",TotalPrice);
                    map1.put("Quantity",quantity);
                    map1.put("Name",name);

                    databaseReference.child(timestamp).child("OrderedItems").child(Pid).setValue(map1);

                }
                progressDialog.dismiss();
                Toast.makeText(Cart_Activity.this, "Order Placed Successfully...", Toast.LENGTH_SHORT).show();

                PrepareNotificationMessage(timestamp);

               /* //after placing order open order details page
                Intent intent=new Intent(Cart_Activity.this,OrderDetailUsersActivity.class);
                intent.putExtra("orderTo",s_id);
                intent.putExtra("orderId",timestamp);
                intent.putExtra("m_latitude",mylatitude);
                intent.putExtra("m_laongitude",mylongitude);
                startActivity(intent);

                */

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Cart_Activity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });


    }

    private void Calculation() {
        cartItemList=new ArrayList<>();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(s_id).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartItemList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelCartItem modelCartItem=ds.getValue(ModelCartItem.class);
                    //oneProductPrice=Integer.valueOf(modelCartItem.getPriceEach().replaceAll("$",""))*Integer.valueOf(modelCartItem.getQuantity().replaceAll("$","")) +Integer.valueOf(DeliveryFee);
                    try {
                        String cost=""+ds.child("priceEach").getValue().toString();
                        String quantity=""+ds.child("quantity").getValue().toString();
                        allTotalPrice=allTotalPrice+Double.valueOf(cost)*Double.valueOf(quantity);

                        //shopDetail_activity.cartcount.setText(cart_count);


                    }catch (NullPointerException ignored){

                    }

                    cartItemList.add(modelCartItem);


                }
                // total_bill=total_bill+oneProductPrice;
              //  String cart_count=""+dataSnapshot.getChildrenCount();
                //((ShopDetail_Activity)context).cartcount.setText(cart_count);

              /*  dFeeTv.setText("$"+Double.parseDouble(DeliveryFee));
                sTotalTv.setText("$"+String.format("%.2f",allTotalPrice));
                allTotalPriceTv.setText("$"+(allTotalPrice+Double.parseDouble(DeliveryFee.replaceAll("$",""))));

               */



                //setup Adapter
                adapterCartItem=new AdapterCartItem(Cart_Activity.this,cartItemList);
                //set adapter
                cartitemsRv.setAdapter(adapterCartItem);

                if (isPtomotionCodeisApplied){
                    priceWithDiscount();
                }else {
                    priceWithoutDiscount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void PrepareNotificationMessage(String OrderId){
        //when user place order send notification to seller
        //prep-are notification data

        String NOTIFICATION_TOPIC="/topics/"+Contants.FCM_Topic;
        String NOTIFICATION_TITLE="New Order"+OrderId;
        String NOTIFICATION_MESSAGE="Congratulation.....You have new Order!";
        String NOTIFICATION_TYPE="NewOrder";

        //prepare json what to snd and where to snd
        JSONObject Notificationjo=new JSONObject();
        JSONObject NotificationBodyjo=new JSONObject();

        try {
            //what to send
            Notificationjo.put("Notification_type",NOTIFICATION_TYPE);
            Notificationjo.put("buyer_uid",firebaseAuth.getUid());// we login as  byuer to place order snice current use id is byuer uid;
            Notificationjo.put("seller_uid",s_id) ;
            Notificationjo.put("Order_Uid",OrderId);
            Notificationjo.put("Notification_Title",NOTIFICATION_TITLE);
            Notificationjo.put("Notification_Message",NOTIFICATION_MESSAGE);

            // where to snd

            Notificationjo.put("to",NOTIFICATION_TOPIC);//to all who subscribed to this topic
            Notificationjo.put("data",NotificationBodyjo);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        SendNotification(OrderId,Notificationjo);

    }

    private void SendNotification(final String orderId, JSONObject notificationjo) {
        //send volley request
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationjo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //after sending fcm start orderadetails Activity

                //after placing order open order details page
                Intent intent=new Intent(Cart_Activity.this,OrderDetailUsersActivity.class);
                intent.putExtra("orderTo",s_id);
                intent.putExtra("orderId",orderId);
                intent.putExtra("m_latitude",mylatitude);
                intent.putExtra("m_laongitude",mylongitude);
                startActivity(intent);
               Toast.makeText(Cart_Activity.this, "send notification", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //if Failled sending,still satrt order Details Activity

                //after placing order open order details page
                Intent intent=new Intent(Cart_Activity.this,OrderDetailUsersActivity.class);
                intent.putExtra("orderTo",s_id);
                intent.putExtra("orderId",orderId);
                intent.putExtra("m_latitude",mylatitude);
                intent.putExtra("m_laongitude",mylongitude);
                startActivity(intent);
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String>headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization","key="+Contants.FCM_KEY);
                return headers;
            }
        };

        //enque the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);


    }
}
