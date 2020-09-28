package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderDetailUsersActivity extends AppCompatActivity {

    private String orderid,orderTo;
    private ImageButton backbtn,write_review_btn;
    private TextView orderIdTV,orderDateTv,orderStatusTV,ShopNameIdTV,
            TotalItemsIdTV,amountIdTV,DeliveryAddressTv;
    private RecyclerView ItemsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrderdItem>modelOrderdItemArrayList;
    private AdapterOrderdItem adapterOrderdItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_users);

        Intent intent=getIntent();
        orderTo=intent.getStringExtra("orderTo");//order to contain id of the shop where we placed the order
        orderid=intent.getStringExtra("orderId");
       // latitude=intent.getStringExtra("m_latitude");
        //longitude=intent.getStringExtra("m_laongitude");

        backbtn=findViewById(R.id.backbtn);
        orderIdTV=findViewById(R.id.orderIdTV);
        orderDateTv=findViewById(R.id.orderDateTv);
        orderStatusTV=findViewById(R.id.orderStatusTV);
        ShopNameIdTV=findViewById(R.id.ShopNameIdTV);
        TotalItemsIdTV=findViewById(R.id.TotalItemsIdTV);
        amountIdTV=findViewById(R.id.amountIdTV);
        write_review_btn=findViewById(R.id.write_review_btn);
        write_review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(OrderDetailUsersActivity.this,Write_review_Activity.class);
                intent1.putExtra("s_id",orderTo);// to write review of shop we must have uid of shop
                startActivity(intent1);
            }
        });
        DeliveryAddressTv=findViewById(R.id.DeliveryAddressTv);
        ItemsRv=findViewById(R.id.ItemsRv);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
        loadShopInfo();
        loadOrderDetails();
        loadOrderItems();




    }

    private void loadOrderItems() {
        modelOrderdItemArrayList=new ArrayList<>();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(orderTo).child("Orders").child(orderid).child("OrderedItems")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelOrderdItemArrayList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelOrderdItem modelOrderdItem=ds.getValue(ModelOrderdItem.class);
                    modelOrderdItemArrayList.add(modelOrderdItem);
                }

                adapterOrderdItem=new AdapterOrderdItem(OrderDetailUsersActivity.this,modelOrderdItemArrayList);
                ItemsRv.setAdapter(adapterOrderdItem);
                TotalItemsIdTV.setText(""+dataSnapshot.getChildrenCount());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadOrderDetails() {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(orderTo).child("Orders").child(orderid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                        orderStatusTV.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }else if (orderStatus.equals("Completed")){
                        orderStatusTV.setTextColor(getResources().getColor(R.color.Greencolor));
                    }else if (orderStatus.equals("Cancelled")){
                        orderStatusTV.setTextColor(getResources().getColor(R.color.RedColor));
                    }

                    //set data
                    orderIdTV.setText(order_Id);
                    orderStatusTV.setText(orderStatus);
                    amountIdTV.setText("$"+order_Cost+"[Including delivery fee $"+deliveryfee+""+discount+"]");
                    orderDateTv.setText(formatDate);
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

            DeliveryAddressTv.setText(address);


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void loadShopInfo() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(orderTo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String ShopName=dataSnapshot.child("ShopeName").getValue().toString();
                ShopNameIdTV.setText(ShopName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
