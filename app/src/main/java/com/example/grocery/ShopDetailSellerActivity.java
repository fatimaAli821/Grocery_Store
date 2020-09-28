package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShopDetailSellerActivity extends AppCompatActivity {
    private String Order_id,Order_By;
    private ImageButton backbtn,edit_btm,mapBtn_seller;
    private TextView orderIdSellerTv,orderDateSellerTv,orderStatusSellerTv,BuyerEmailSellerTv,
            BuyerPhoneSellerTv,orderItemsSellerTv,amountSeller_Tv,DeliveryAddressSeller_Tv;
    private RecyclerView Rv_seller;
    private FirebaseAuth firebaseAuth;
   private String Sourcelatitude ,Sourcelongitude,DestinationLatitude,DastinationLongitude;

   private ArrayList<ModelOrderdItem>modelOrderdItemArrayList;
   private AdapterOrderdItem adapterOrderdItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail_seller);
        Order_id=getIntent().getStringExtra("Order_id");
        Order_By=getIntent().getStringExtra("Order_By");

        backbtn=findViewById(R.id.backbtn);
        edit_btm=findViewById(R.id.edit_btm);
        mapBtn_seller=findViewById(R.id.mapBtn_seller);
        orderIdSellerTv=findViewById(R.id.orderIdSellerTv);
        orderDateSellerTv=findViewById(R.id.orderDateSellerTv);
        orderStatusSellerTv=findViewById(R.id.orderStatusSellerTv);
        BuyerEmailSellerTv=findViewById(R.id.BuyerEmailSellerTv);
        BuyerPhoneSellerTv=findViewById(R.id.BuyerPhoneSellerTv);
        orderItemsSellerTv=findViewById(R.id.orderItemsSellerTv);
        amountSeller_Tv=findViewById(R.id.amountSeller_Tv);
        DeliveryAddressSeller_Tv=findViewById(R.id.DeliveryAddressSeller_Tv);
        Rv_seller=findViewById(R.id.Rv_seller);
        firebaseAuth=FirebaseAuth.getInstance();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mapBtn_seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenMap();
            }
        });

        edit_btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit order Status:Complete/In Progress/Cancelled
                
                editOrderStatusDialouge();
            }
        });

        LoadMyInfo();
        LoadBuyerInfo();
        LoadOrderDetails();
        LoadOrderdItems();
    }

    private void editOrderStatusDialouge() {
        //options to display in dialouge
        final String options[]={"In Progress","Cancelled","Completed"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Edit Order Status")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                   // handel item clicks
                   String SelectOption=options[which];
                   editOrderStatus(SelectOption);
                    }
                }).show();
    }

    private void editOrderStatus(final String selectOption) {

        HashMap<String,Object>map=new HashMap<>();
        map.put("OrderStatus",""+selectOption);

        final String message="Order is now"+selectOption;

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Orders").child(Order_id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(ShopDetailSellerActivity.this, message, Toast.LENGTH_SHORT).show();

                PrepareNotificationMessage(Order_id,message);


                
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShopDetailSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();     
            }
        });

    }

    private void LoadOrderdItems() {
        //load the products/items of the order
        modelOrderdItemArrayList=new ArrayList<>();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Orders").child(Order_id).child("OrderedItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelOrderdItemArrayList.clear();
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ModelOrderdItem modelOrderdItem=dataSnapshot1.getValue(ModelOrderdItem.class);
                    modelOrderdItemArrayList.add(modelOrderdItem);
                }

                adapterOrderdItem=new AdapterOrderdItem(ShopDetailSellerActivity.this,modelOrderdItemArrayList);
                Rv_seller.setAdapter(adapterOrderdItem);
                //set Total number of items is ordered
                orderItemsSellerTv.setText(""+dataSnapshot.getChildrenCount());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadOrderDetails() {
        //load detail info of this order based on order id
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Orders").child(Order_id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get order info

                try{
                    String orderBy=""+dataSnapshot.child("OrderBy").getValue().toString();
                    String order_Cost=""+dataSnapshot.child("OrderCost").getValue().toString();
                    String order_Id=""+dataSnapshot.child("OrderId").getValue().toString();
                    String orderStatus=""+dataSnapshot.child("OrderStatus").getValue().toString();
                    String orderTime=""+dataSnapshot.child("OrderTime").getValue().toString();
                    String orderTo=""+dataSnapshot.child("orderTo").getValue().toString();
                    String deliveryfee=""+dataSnapshot.child("deliveryfee").getValue().toString();
                    String latitude=""+dataSnapshot.child("latitude").getValue().toString();
                    String longitude=""+dataSnapshot.child("longitude").getValue().toString();
                    String discount=""+dataSnapshot.child("discount").getValue().toString();

                    if (discount.equals("null")||discount.equals("0")){
                        discount="& Discount $0";

                    }else {
                        discount="& Discount $"+discount;
                    }


                    Calendar calendar=Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(orderTime));
                    String formatDate= DateFormat.format("dd/MM/yyy hh:mm a",calendar).toString();

                    if (orderStatus.equals("In Progress")){
                        orderStatusSellerTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }else if (orderStatus.equals("Completed")){
                        orderStatusSellerTv.setTextColor(getResources().getColor(R.color.Greencolor));
                    }else if (orderStatus.equals("Cancelled")){
                        orderStatusSellerTv.setTextColor(getResources().getColor(R.color.RedColor));
                    }

                    //set data
                    orderIdSellerTv.setText(order_Id);
                    orderStatusSellerTv.setText(orderStatus);
                    amountSeller_Tv.setText("$"+order_Cost+"[Including delivery fee $"+deliveryfee+""+discount+"]");
                    orderDateSellerTv.setText(formatDate);
                    // TotalItemsIdTV.setText(""+dataSnapshot.getChildrenCount());


                    findAddress(latitude,longitude);
                }catch (NullPointerException ignored){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findAddress(String latitude, String longitude) {

        double lati=Double.parseDouble(latitude);
        double longi=Double.parseDouble(longitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder=new Geocoder(this, Locale.getDefault());

        try {
            addresses=geocoder.getFromLocation(lati,longi,1);

            String address=addresses.get(0).getAddressLine(0);

            DeliveryAddressSeller_Tv.setText(address);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void LoadMyInfo() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Sourcelatitude=dataSnapshot.child("latitude").getValue().toString();
                Sourcelongitude=dataSnapshot.child("longitude").getValue().toString();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadBuyerInfo() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(Order_By).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DestinationLatitude=dataSnapshot.child("latitude").getValue().toString();
                DastinationLongitude=dataSnapshot.child("longitude").getValue().toString();
                String email=dataSnapshot.child("email").getValue().toString();
                String Phone=dataSnapshot.child("phone").getValue().toString();

                BuyerEmailSellerTv.setText(email);
                BuyerPhoneSellerTv.setText(Phone);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void OpenMap() {
        String address="https://maps.google.com/maps?saddr="+Sourcelatitude+","+Sourcelongitude+"&daddr="+DestinationLatitude+","+DastinationLongitude;
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private  void PrepareNotificationMessage(String OrderId,String message){
        //when user seller change  order status send notification to byure
        //prep-are notification data

        String NOTIFICATION_TOPIC="/topics/"+Contants.FCM_Topic;
        String NOTIFICATION_TITLE="Your Order"+OrderId;
        String NOTIFICATION_MESSAGE=""+message;
        String NOTIFICATION_TYPE="OrderStatuschanged";

        //prepare json what to snd and where to snd
        JSONObject Notificationjo=new JSONObject();
        JSONObject NotificationBodyjo=new JSONObject();

        try {
            //what to send
            Notificationjo.put("Notification_type",NOTIFICATION_TYPE);
            Notificationjo.put("buyer_uid",Order_By);// we login as  byuer to place order snice current use id is byuer uid;
            Notificationjo.put("seller_uid",firebaseAuth.getUid()) ;
            Notificationjo.put("Order_Uid",OrderId);
            Notificationjo.put("Notification_Title",NOTIFICATION_TITLE);
            Notificationjo.put("Notification_Message",NOTIFICATION_MESSAGE);

            // where to snd

            Notificationjo.put("to",NOTIFICATION_TOPIC);//to all who subscribed to this topic
            Notificationjo.put("data",NotificationBodyjo);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        SendNotification(Notificationjo);

    }

    private void SendNotification(JSONObject notificationjo) {
        //send volley request
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationjo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              //notification sent
               Toast.makeText(ShopDetailSellerActivity.this, "send notification", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //notification failed

                Toast.makeText(ShopDetailSellerActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

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
