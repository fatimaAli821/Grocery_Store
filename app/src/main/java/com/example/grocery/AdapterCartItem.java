package com.example.grocery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{

    private Context context;
    public ArrayList<ModelCartItem> cartItemList;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout

        View view= LayoutInflater.from(context).inflate(R.layout.row_cartitem,parent,false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderCartItem holder, final int position) {
        // get data
        ModelCartItem modelCartItem=cartItemList.get(position);
        final String id=modelCartItem.getItem_id();
        final String uuid=modelCartItem.getUuid();
        String pid=modelCartItem.getProductid();
        String title=modelCartItem.getItem_title();
        final String cost=modelCartItem.getPriceEach();
        String price=modelCartItem.getTotalPrice();
        final String quantity=modelCartItem.getQuantity();

        //set data
        holder.itemPriceTv.setText(""+price);
        holder.itemPriceEach.setText(""+cost);
        holder.itemTitleTv.setText(""+title);
        holder.itemQuantity.setText(""+"["+quantity+"]");
        // handel remove on click listner
        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {


                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
                databaseReference.child(uuid).child("Items").child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

               /* // creat table if not exsist,but in that case will must exsist
                EasyDB easyDB=EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn("ItemId",new String[]{"text","unique"})
                        .addColumn("Item_PID",new String[]{"text","not null"})
                        .addColumn("Item_Name",new String[]{"text","not null"})
                        .addColumn("Item_Price_Each",new String[]{"text","not null"})
                        .addColumn("Item_Price",new String[]{"text","not null"})
                        .addColumn("Item_Quantity",new String[]{"text","not null"})
                        .doneTableColumn();

                easyDB.deleteRow(1,id);
                Toast.makeText(context, "Remove from cart....", Toast.LENGTH_SHORT).show();

                */

               // refresh list
                cartItemList.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();

              /* double tx=Double.parseDouble((((Cart_Activity)context).allTotalPriceTv.getText().toString().trim().replace("$","")));
                double TotalPrice=tx-Double.parseDouble(cost.replace("$",""));
                double deliveryfee=Double.parseDouble((((Cart_Activity)context).DeliveryFee.replace("$","")));
                double sTotalprice=Double.parseDouble(String.format("%.2f",TotalPrice) )-Double.parseDouble(String.format("%.2f",deliveryfee));
                ((Cart_Activity)context).allTotalPrice=0.00;
                ((Cart_Activity)context).sTotalTv.setText(String.format("%.2f",sTotalprice));
                ((Cart_Activity)context).allTotalPriceTv.setText("$"+String.format("%.2f",Double.parseDouble(String.format("%.2f",TotalPrice))));

               */

              //allTotalPriceTv=sub+delveryfee

              /*  double tx=Double.parseDouble((((Cart_Activity)context).allTotalPriceTv.getText().toString().trim().replace("$","")));
                double TotalPrice=tx-Double.parseDouble(cost.replace("$",""))*Double.parseDouble(quantity);
                double deliveryfee=Double.parseDouble((((Cart_Activity)context).DeliveryFee.replace("$","")));
                double sTotalprice=Double.parseDouble(String.format("%.2f",TotalPrice) )-Double.parseDouble(String.format("%.2f",deliveryfee));
                ((Cart_Activity)context).allTotalPrice=0.00;
                ((Cart_Activity)context).sTotalTv.setText(String.format("%.2f",sTotalprice));
                ((Cart_Activity)context).allTotalPriceTv.setText("$"+String.format("%.2f",Double.parseDouble(String.format("%.2f",TotalPrice))));

               */

              //adjust the sub Total after Product remove


                //once sub total is updated check minimum order price of Promo code.
                try {
                    double SubTotalWithoutdiscount=((Cart_Activity)context).allTotalPrice;
                    double subTotalAfterProductRemove=SubTotalWithoutdiscount-Double.parseDouble(cost.replace("$",""))*Double.parseDouble(quantity);
                    ((Cart_Activity)context).allTotalPrice=subTotalAfterProductRemove;
                    ((Cart_Activity)context).sTotalTv.setText(String.format("%.2f",((Cart_Activity)context).allTotalPrice));


                    double PromoPrice=Double.parseDouble(((Cart_Activity)context).PromoPrice);
                    double deliveryfee=Double.parseDouble((((Cart_Activity)context).DeliveryFee.replace("$","")));

                    //check if promo code Applied
                    if (((Cart_Activity)context).isPtomotionCodeisApplied){
                        //applied
                        if (subTotalAfterProductRemove < Double.parseDouble(((Cart_Activity)context).PromoMinimumOrderPrice)){
                            Toast.makeText(context, "This code is valid for Order with minimum amount : $"+((Cart_Activity)context).PromoMinimumOrderPrice, Toast.LENGTH_SHORT).show();
                            ((Cart_Activity)context).applyButton.setVisibility(View.GONE);
                            ((Cart_Activity)context). PromoDescriptionTv.setVisibility(View.GONE);
                            ((Cart_Activity)context).PromoDescriptionTv.setText("");
                            ((Cart_Activity)context).sdisscountTv.setText("0$");
                            ((Cart_Activity)context).isPtomotionCodeisApplied=false;

                            //show new net Totall After Delivery fee
                            ((Cart_Activity)context).allTotalPriceTv.setText("$"+String.format("%.2f",Double.parseDouble(String.format("%.2f",subTotalAfterProductRemove +deliveryfee))));

                        }else {

                            ((Cart_Activity)context).applyButton.setVisibility(View.VISIBLE);
                            ((Cart_Activity)context). PromoDescriptionTv.setVisibility(View.VISIBLE);
                            ((Cart_Activity)context).PromoDescriptionTv.setText(((Cart_Activity)context).PromoDescription);
                            //Show now Totall Price after Adding delivery fee and subtracting Promo Price
                            ((Cart_Activity)context).isPtomotionCodeisApplied=true;
                            ((Cart_Activity)context).allTotalPriceTv.setText("$"+String.format("%.2f",Double.parseDouble(String.format("%.2f",subTotalAfterProductRemove +deliveryfee-PromoPrice))));



                        }

                    }else {
                        //not applied
                        ((Cart_Activity)context).allTotalPriceTv.setText("$"+String.format("%.2f",Double.parseDouble(String.format("%.2f",subTotalAfterProductRemove+deliveryfee))));
                    }



                }catch (NullPointerException ignored){

                }








            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder{

        // hold the views of layout
        private TextView itemTitleTv,itemPriceTv,itemPriceEach,itemQuantity,
                itemRemoveTv;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);
            itemTitleTv=itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv=itemView.findViewById(R.id.itemPriceTv);
            itemPriceEach=itemView.findViewById(R.id.itemPriceEach);
            itemQuantity=itemView.findViewById(R.id.itemQuantity);
            itemRemoveTv=itemView.findViewById(R.id.itemRemoveTv);



        }
    }
}
