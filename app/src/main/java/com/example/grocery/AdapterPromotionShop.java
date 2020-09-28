package com.example.grocery;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class AdapterPromotionShop extends RecyclerView.Adapter<AdapterPromotionShop.holderAdapterPromotion>{

   private ArrayList<ModelPromotion>modelPromotionArrayList;
   private Context context;
   private ProgressDialog progressDialog;
   private FirebaseAuth firebaseAuth;

    public AdapterPromotionShop(Context context, ArrayList<ModelPromotion> modelPromotionArrayList) {
        this.context = context;
        this.modelPromotionArrayList = modelPromotionArrayList;

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
    }


    @NonNull
    @Override
    public holderAdapterPromotion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_promotion_shop,parent,false);

        return new holderAdapterPromotion(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final holderAdapterPromotion holder, int position) {

        //get data
        final ModelPromotion modelPromotion=modelPromotionArrayList.get(position);
        String id=modelPromotion.getId();
        String timestamp=modelPromotion.getTimestamp();
        String Code=modelPromotion.getCode();
        String p_Description=modelPromotion.getP_Description();
        String promoPrice=modelPromotion.getPromoPrice();
        String MiniOrder=modelPromotion.getMiniOrder();
        String expiredate=modelPromotion.getExpiredate();

        //set data
        holder.description_promo.setText(p_Description);
        holder.expireDateTv.setText("Expire Date: "+expiredate);
        holder.minimum_Order_price_label.setText(MiniOrder);
        holder.promo_code.setText("Code: "+Code);
        holder.promo_price_label.setText(promoPrice);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show Edit/Delete Dialog
                editDeleteDialog(modelPromotion,holder);

            }
        });


    }

    private void editDeleteDialog(final ModelPromotion modelPromotion, holderAdapterPromotion holder) {
        String options[]={"Edit","Delete"};
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Choose Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which==0){
                    //edit clicked
                    EditPromoCode(modelPromotion);

                }else if (which==1){
                    //Delete Clicked
                    DeletePromoCode(modelPromotion);
                }
            }
        }).show();
    }

    private void DeletePromoCode(ModelPromotion modelPromotion) {
        //progress dialog
        progressDialog.setMessage("Deleting Promotion Code");
        progressDialog.show();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Promotion Code").child(modelPromotion.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Deleting..", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void EditPromoCode(ModelPromotion modelPromotion) {
        Intent intent=new Intent(context,AddPromotionCodeActivity.class);
        intent.putExtra("promoId",modelPromotion.getId());//will use id to update promo code
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return modelPromotionArrayList.size();
    }

    class holderAdapterPromotion extends RecyclerView.ViewHolder{

        private ImageView prom;
        private TextView promo_code,promo_price_label,minimum_Order_price_label,expireDateTv,description_promo;

        public holderAdapterPromotion(@NonNull View itemView) {
            super(itemView);
            prom=itemView.findViewById(R.id.prom);
            promo_code=itemView.findViewById(R.id.promo_code);
            promo_price_label=itemView.findViewById(R.id.promo_price_label);
            minimum_Order_price_label=itemView.findViewById(R.id.minimum_Order_price_label);
            expireDateTv=itemView.findViewById(R.id.expireDateTv);
            description_promo=itemView.findViewById(R.id.description_promo);
        }
    }
}
