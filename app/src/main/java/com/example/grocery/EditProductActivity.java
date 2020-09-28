package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {
    private String productid;
    private ImageButton backbtn;
    private ImageView profileIv_Seller;
    private EditText titleEt,descriptionEt,quantityEt,PriceEt,discount_priceEt,disscount_Note_Et;
    private TextView cateogryTv;
    private SwitchCompat discount;
    private Button update;

    //permission constant
    public static final int CAMERA_REQUEST_CODE=200;
    public static final int STORAGE_REQUEST_CODE=300;
    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    //permission array

    private String[] camerapermission;
    private String[] storagepermission;
    // pick img uri
    Uri img_Uri;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        productid= getIntent().getStringExtra("productid");


        backbtn=findViewById(R.id.backbtn);
        profileIv_Seller=findViewById(R.id.profileIv_Seller);
        titleEt=findViewById(R.id.titleEt);
        descriptionEt=findViewById(R.id.descriptionEt);
        quantityEt=findViewById(R.id.quantityEt);
        PriceEt=findViewById(R.id.PriceEt);
        discount=findViewById(R.id.discount);
        discount_priceEt=findViewById(R.id.discount_priceEt);
        PriceEt=findViewById(R.id.PriceEt);
        disscount_Note_Et=findViewById(R.id.disscount_Note_Et);
        cateogryTv=findViewById(R.id.cateogryTv);
        update=findViewById(R.id.update);

        firebaseAuth=FirebaseAuth.getInstance();
        loadproductDetails();//load product details to set on views
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        discount_priceEt.setVisibility(View.GONE);
        disscount_Note_Et.setVisibility(View.GONE);

        //initalize permission
        camerapermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //initalize image uri;
        img_Uri=null;

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cateogryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(EditProductActivity.this);
                builder.setTitle("Category");
                builder.setItems(Contants.product_Category, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String category=Contants.product_Category[which];
                        cateogryTv.setText(category);
                    }
                }).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flow
                // Input data
                // Validate data
                // add data to db

                InputData();
            }
        });
        discount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    discount_priceEt.setVisibility(View.VISIBLE);
                    disscount_Note_Et.setVisibility(View.VISIBLE);

                }else {

                    discount_priceEt.setVisibility(View.GONE);
                    disscount_Note_Et.setVisibility(View.GONE);


                }


            }
        });

        profileIv_Seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick the img

                showImgPickDialog();
            }

            private void showImgPickDialog() {
                String options[] = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProductActivity.this);
                builder.setTitle("Pick img");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            //camera clicked
                            if (checkCAMERAPermission()) {
                                // camera Permission allowed
                                pickFromCAMERA();

                            } else {
                                //not allowed,request permission
                                requestCameraPermission();
                            }

                        } else {
                            //gallery clicked
                            if (checkStoragePermission()) {
                                // Storage Permission allowed
                                pickFromGallery();

                            } else {
                                //not allowed,request permission
                                requestStoragePermission();

                            }
                        }
                    }


                }).show();
            }


        });
    }



    private void loadproductDetails() {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Products").child(productid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get data
                String product_id=""+dataSnapshot.child("product_id").getValue();
                String uuid=""+dataSnapshot.child("uuid").getValue();
                String Product_Description=""+dataSnapshot.child("Product_Description").getValue();
                String Product_Category=""+dataSnapshot.child("Product_Category").getValue();
                String Product_Quantity=""+dataSnapshot.child("Product_Quantity").getValue();
                String Orignal_price=""+dataSnapshot.child("Orignal_price").getValue();
                String Discount_price=""+dataSnapshot.child("Discount_price").getValue();
                String Product_title=""+dataSnapshot.child("Product_title").getValue();
                String Discoutnt_Note=""+dataSnapshot.child("Discoutnt_Note").getValue();
                String timestamp=""+dataSnapshot.child("timestamp").getValue();
                String disscount_Avalible=""+dataSnapshot.child("disscount_Avalible").getValue();
                String profile_Image=""+dataSnapshot.child("profile_Image").getValue();

                //set data
                if (disscount_Avalible.equals("true")){
                    discount.setChecked(true);
                    discount_priceEt.setVisibility(View.VISIBLE);
                    disscount_Note_Et.setVisibility(View.VISIBLE);
                }else {
                    discount.setChecked(false);
                    discount_priceEt.setVisibility(View.GONE);
                    disscount_Note_Et.setVisibility(View.GONE);
                }

                titleEt.setText(Product_title);
                descriptionEt.setText(Product_Description);
                cateogryTv.setText(Product_Category);
                quantityEt.setText(Product_Quantity);
                PriceEt.setText(Orignal_price);
                discount_priceEt.setText(Discount_price);
                disscount_Note_Et.setText(Discoutnt_Note);

                try {
                    Picasso.get().load(profile_Image).placeholder(R.drawable.ic_person_grey).into(profileIv_Seller);
                }catch (Exception e){
                    profileIv_Seller.setImageResource(R.drawable.ic_person_grey);
                }






            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String Product_title,Product_Description,Product_Category,Product_Quantity,
            Orignal_price,Discount_price,Discoutnt_Note;
    private Boolean disscount_Avalible;
    private void InputData() {

        Product_title=titleEt.getText().toString().trim();
        Product_Description=descriptionEt.getText().toString().trim();
        Product_Category=cateogryTv.getText().toString().trim();
        Product_Quantity=quantityEt.getText().toString().trim();
        Orignal_price=PriceEt.getText().toString().trim();

        disscount_Avalible=discount.isChecked();

        if (TextUtils.isEmpty(Product_title)){
            titleEt.setError("enter the title");
            titleEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Product_Description)){
            descriptionEt.setError("enter the discription");
            descriptionEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Product_Category)){
            cateogryTv.setError("choose the category");
            cateogryTv.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Product_Quantity)){
            quantityEt.setError("enter the quantity");
            quantityEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(Orignal_price)){
            PriceEt.setError("enter the price");
            PriceEt.requestFocus();
            return;
        }


        if (disscount_Avalible){
            Discount_price=discount_priceEt.getText().toString().trim();
            Discoutnt_Note=disscount_Note_Et.getText().toString().trim();

            if (TextUtils.isEmpty(Discount_price)){
                discount_priceEt.setError("enter the discount price");
                discount_priceEt.requestFocus();
                return;
            }

        }else {

            Discount_price="0";
            Discoutnt_Note="";

        }

        Update_Product();


    }

    private void Update_Product() {
        progressDialog.setMessage("Updating product....");
        progressDialog.show();

        final String timestamp=""+System.currentTimeMillis();
        if (img_Uri==null){
            //saving info without image
            //setup data to save
            Map<String,Object> map=new HashMap<>();
            map.put("uuid",""+firebaseAuth.getUid());
            map.put("Product_title",""+Product_title);
            map.put("Product_Description",""+Product_Description);
            map.put("Product_Category",""+Product_Category);
            map.put("Product_Quantity",Product_Quantity);
            map.put("Orignal_price",""+Orignal_price);
            map.put("Discount_price",""+Discount_price);
            map.put("Discoutnt_Note",""+Discoutnt_Note);
            map.put("product_id",""+timestamp);
            map.put("timestamp",""+timestamp);
            map.put("disscount_Avalible",disscount_Avalible.toString());
            map.put("profile_Image","");

            //save to db
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Products").child(productid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //db updated
                    progressDialog.dismiss();
                    Toast.makeText(EditProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
                    ClearData();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProductActivity.this, "Product not Added", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            //saving info with image

            //name and Path of image
            String filePathandName="Product_images/"  + ""+productid;// override previous image using same id
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathandName);
            storageReference.putFile(img_Uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //get url of uploaded image
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadImageUri=uriTask.getResult();
                    if (uriTask.isSuccessful()){
                        //setup data to save
                        Map<String,Object>map=new HashMap<>();
                        map.put("uuid",""+firebaseAuth.getUid());
                        map.put("Product_title",""+Product_title);
                        map.put("Product_Description",""+Product_Description);
                        map.put("Product_Category",""+Product_Category);
                        map.put("Product_Quantity",Product_Quantity);
                        map.put("Orignal_price",""+Orignal_price);
                        map.put("Discount_price",""+Discount_price);
                        map.put("product_id",""+timestamp);
                        map.put("Discoutnt_Note",""+Discoutnt_Note);
                        map.put("timestamp",""+timestamp);
                        map.put("disscount_Avalible",disscount_Avalible.toString());
                        map.put("profile_Image",downloadImageUri.toString());

                        //save to db
                        try {
                            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Products").child(productid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //db updated
                                    progressDialog.dismiss();
                                    Toast.makeText(EditProductActivity.this, "Product Added", Toast.LENGTH_SHORT).show();
                                    ClearData();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditProductActivity.this, "Product not Added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (NullPointerException ignored){

                        }

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }



    }

    private void ClearData() {
        titleEt.setText("");
        descriptionEt.setText("");
        cateogryTv.setText("");
        quantityEt.setText("");
        PriceEt.setText("");
        discount_priceEt.setText("");
        disscount_Note_Et.setText("");
        profileIv_Seller.setImageResource(R.drawable.ic_person_grey);
        img_Uri=null;
    }


    private void pickFromCAMERA() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp image Description");

        img_Uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, img_Uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagepermission, STORAGE_REQUEST_CODE);

    }

    private boolean checkCAMERAPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, camerapermission, CAMERA_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {


            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean CameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (CameraAccepted && StorageAccepted) {

                        //permission allowed
                        pickFromCAMERA();


                    } else {

                        //permission denied

                        Toast.makeText(this, "Camera permission is necessary", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    boolean StorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (StorageAccepted) {

                        //permission allowed
                        pickFromGallery();


                    } else {

                        //permission denied

                        Toast.makeText(this, "Camera permission is necessary", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;

            default:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){

            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                img_Uri=data.getData();
                profileIv_Seller.setImageURI(img_Uri);
            }else if (requestCode==IMAGE_PICK_CAMERA_CODE){
                profileIv_Seller.setImageURI(img_Uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
