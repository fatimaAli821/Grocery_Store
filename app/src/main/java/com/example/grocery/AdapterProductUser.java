package com.example.grocery;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductuser> {

    private Context context;
    private ArrayList<ModelProduct>productList;
   private ModelProduct product;
   private String shopid;
   private AdapterCartItem adapterCartItem;
    int count=0;



    public AdapterProductUser(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public HolderProductuser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductuser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductuser holder, int position) {


        //get data
        final ModelProduct modelProduct=productList.get(position);
        String product_id=modelProduct.getProduct_id();
        shopid=modelProduct.getUuid();
        String Title=modelProduct.getProduct_title();
        String Quantity=modelProduct.getProduct_Quantity();
        String timestamp=modelProduct.getTimestamp();
        String disscount_Avalible=modelProduct.getDisscount_Avalible();
        String disscount_note=modelProduct.getDiscoutnt_Note();
        String disscount_price=modelProduct.getDiscount_price();
        String orignal_price=modelProduct.getOrignal_price();
        String product_category=modelProduct.getProduct_Category();
        String product_discription=modelProduct.getProduct_Category();
        String icon=modelProduct.getProfile_Image();

        // set data

        holder.tile.setText(Title);
        holder.description.setText(product_discription);
        holder.disscount_note.setText(disscount_note);
        holder.discount_priceTv.setText("$"+disscount_price);
        holder.Orignal_price.setText("$"+orignal_price);

        if (disscount_Avalible.equals("true")){
            //product is on discount
            holder.discount_priceTv.setVisibility(View.VISIBLE);
            holder.disscount_note.setVisibility(View.VISIBLE);
            holder.Orignal_price.setPaintFlags(holder.Orignal_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);// add strike through on orignal price
        }else {
            //product is not on discount

            holder.discount_priceTv.setVisibility(View.GONE);
            holder.disscount_note.setVisibility(View.GONE);
        }

        try {
            Picasso.get().load(icon).placeholder(R.drawable.ic_person_grey).into(holder.product_icon);
        }catch (Exception e){
            holder.product_icon.setImageResource(R.drawable.ic_person_grey);
        }

        holder.Add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add product to cart
                showQuantityDialouge(modelProduct);

            }
        });
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // show product Details

        }
    });



    }

    private double cost=0;
    private double finalcost=0;
    private int quantity=0;

    private void showQuantityDialouge(ModelProduct modelProduct) {
        //inflate layout for dialouge
        View view=LayoutInflater.from(context).inflate(R.layout.dialouge_quantity,null);
        // init view of layout
        ImageView productIv;
        final TextView titleIv,pQuantityTv,descriptionTv,DiscountedNoteTv,orignalpriceTv,priceDiscountedTv,finalTv,di_quantity;
        ImageButton decrementButton,incrementButton;
        Button continueBtn;

        productIv=view.findViewById(R.id.productIv);
        titleIv=view.findViewById(R.id.titleIv);
        pQuantityTv=view.findViewById(R.id.pQuantityTv);
        descriptionTv=view.findViewById(R.id.descriptionTv);
        DiscountedNoteTv=view.findViewById(R.id.DiscountedNoteTv);
        orignalpriceTv=view.findViewById(R.id.orignalpriceTv);
        priceDiscountedTv=view.findViewById(R.id.priceDiscountedTv);
        finalTv=view.findViewById(R.id.finalTv);
        di_quantity=view.findViewById(R.id.di_quantity);
        decrementButton=view.findViewById(R.id.decrementButton);
        incrementButton=view.findViewById(R.id.incrementButton);
        continueBtn=view.findViewById(R.id.continueBtn);
        // get data from model
        final String productid=modelProduct.getProduct_id();
        String title=modelProduct.getProduct_title();
        String productQuantity=modelProduct.getProduct_Quantity();
        String description=modelProduct.getProduct_Description();
        String DiscountNote=modelProduct.getDiscoutnt_Note();
        String image=modelProduct.getProfile_Image();

        final String price;
        if (modelProduct.getDisscount_Avalible().equals("true")){
            // product have discount
            price=modelProduct.getDiscount_price();
            DiscountedNoteTv.setVisibility(View.VISIBLE);
            orignalpriceTv.setPaintFlags(orignalpriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            // product do not have discount
            DiscountedNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
            price=modelProduct.getOrignal_price();
        }

        //to perform calculations we have to remove $ from price/cost/amount

        cost=Double.parseDouble(price.replaceAll("$",""));
        finalcost=Double.parseDouble(price.replaceAll("$",""));
        quantity=1;

        // dialouge
        final AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);

        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_person_grey).into(productIv);

        }catch (Exception e){
            productIv.setImageResource(R.drawable.ic_person_grey);

        }

        titleIv.setText(""+title);
        descriptionTv.setText(""+description);
        finalTv.setText("$"+finalcost);
        di_quantity.setText(""+quantity);
        pQuantityTv.setText(""+productQuantity);
        DiscountedNoteTv.setText(""+DiscountNote);
        orignalpriceTv.setText("$"+modelProduct.getOrignal_price());
        priceDiscountedTv.setText("$"+modelProduct.getDiscount_price());

     final  AlertDialog dialog= builder.create();
        dialog.show();
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalcost=finalcost + cost;
                quantity++;
                finalTv.setText("$"+finalcost);
                di_quantity.setText(""+quantity);
            }
        });

        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (quantity>1){
                 finalcost=finalcost - cost;
                 quantity--;
                 finalTv.setText("$"+finalcost);
                 di_quantity.setText(""+quantity);
             }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=titleIv.getText().toString().trim();
                String priceEach=price;
                String TotalPrice=finalTv.getText().toString().trim().replaceAll("","");
                String quantity=di_quantity.getText().toString().trim();
                // add to db sqlite
                add_to_cart(productid,title,priceEach,TotalPrice,quantity);
                dialog.dismiss();

            }
        });



    }

    private void add_to_cart(String productid, String title, String priceEach, String totalPrice, String quantity) {
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        String id= databaseReference.push().getKey();
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        Map<String,Object>map=new HashMap<>();
        map.put("uuid",""+shopid);
        map.put("UserId",firebaseAuth.getUid());
        map.put("productid",productid);
        map.put("item_title",title);
        map.put("priceEach",priceEach);
        map.put("totalPrice",totalPrice);
        map.put("quantity",quantity);
        map.put("item_id",""+id);


        try {
            databaseReference.child(shopid).child("Items").
                    child(id).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context, "Added to cart...", Toast.LENGTH_SHORT).show();
                  /*  if (adapterCartItem.cartItemList.size()<=0){
                        ((ShopDetail_Activity)context).cartcount.setVisibility(View.GONE);
                    }else {
                        ((ShopDetail_Activity)context).cartcount.setVisibility(View.VISIBLE);
                        count = count + adapterCartItem.cartItemList.size();
                        ((ShopDetail_Activity) context).cartcount.setText(String.valueOf(count));
                    }
                    
                   */

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                }
            });


        }catch (NullPointerException ignored){

        }


    }


  /*  private  int itemid=1;
    private void add_to_cart(String productid, String title, String priceEach, String price, String quantity) {
        itemid++;

        EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn("ItemId",new String[]{"text","unique"})
                .addColumn("Item_PID",new String[]{"text","not null"})
                .addColumn("Item_Name",new String[]{"text","not null"})
                .addColumn("Item_Price_Each",new String[]{"text","not null"})
                .addColumn("Item_Price",new String[]{"text","not null"})
                .addColumn("Item_Quantity",new String[]{"text","not null"})
                .doneTableColumn();

        Boolean b=easyDB.addData("ItemId",itemid)
                .addData("Item_PID",productid)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",price)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();
        Toast.makeText(context, "Added to cart...", Toast.LENGTH_SHORT).show();



    }*/

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class HolderProductuser extends RecyclerView.ViewHolder{

        // hold views
        private ImageView product_icon,nextIv;
        private TextView disscount_note,tile,description,Add_to_cart,discount_priceTv
                ,Orignal_price;

        public HolderProductuser(@NonNull View itemView) {
            super(itemView);
            product_icon=itemView.findViewById(R.id.product_icon);
            nextIv=itemView.findViewById(R.id.nextIv);
            disscount_note=itemView.findViewById(R.id.disscount_note);
            tile=itemView.findViewById(R.id.tile);
            description=itemView.findViewById(R.id.description);
            Add_to_cart=itemView.findViewById(R.id.Add_to_cart);
            discount_priceTv=itemView.findViewById(R.id.discount_priceTv);
            Orignal_price=itemView.findViewById(R.id.Orignal_price);


        }
    }


}
