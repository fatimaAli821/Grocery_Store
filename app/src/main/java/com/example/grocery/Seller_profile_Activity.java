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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Seller_profile_Activity extends AppCompatActivity implements LocationListener {
    //UI views
    private ImageButton backbtn, gpskbtn;
    private ImageView profileIv;
    private EditText nameEt, phoneEt, countryEt, stateEt, cityEt,
            addressEt, ShopNameEt, DeliveryFeeEt;
    private Button update;
    private SwitchCompat switchCompat;

   // Firebase Auth
    FirebaseAuth firebaseAuth;
    //progress dialouge
    ProgressDialog progressDialog;
    //permission constant
    public static final int LOCATION_REQUEST_CODE=100;
    public static final int CAMERA_REQUEST_CODE = 200;
    public static final int STORAGE_REQUEST_CODE = 300;

    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    // pick img uri
    Uri img_Uri;

    //permission array
    private String[] locationpermission;
    private String[] camerapermission;
    private String[] storagepermission;
    //location MENEGER
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double latitude=0.0, longitude=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initalize ui views
        backbtn=findViewById(R.id.backbtn);
        gpskbtn=findViewById(R.id.gpskbtn_seller);
        setContentView(R.layout.activity_seller_profile_);
        profileIv=findViewById(R.id.profileIv_Seller);
        nameEt=findViewById(R.id.nameEt);
        phoneEt=findViewById(R.id.phoneEt);
        countryEt=findViewById(R.id.countryEt);
        stateEt=findViewById(R.id.stateEt);
        cityEt=findViewById(R.id.cityEt);
        addressEt=findViewById(R.id.addressEt);
        ShopNameEt=findViewById(R.id.ShopNameEt);
        DeliveryFeeEt=findViewById(R.id.DeliveryFeeEt);
        switchCompat=findViewById(R.id.ShopOpen);
        update=findViewById(R.id.update);

        //initalize firebase aauth
        firebaseAuth=FirebaseAuth.getInstance();
        img_Uri=null;
        checkUser();
        //initaize progress dialouge
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);


        //initalize permission array
        locationpermission=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        camerapermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};




        try {
            gpskbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //find current location

                    if (checkpermissionlocation()) {
                        //already allow
                        detectLocation();
                    } else {
                        //not allowed,request
                        requestLocationpermission();
                    }
                }
            });
        }catch (NullPointerException ignored){

        }

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgPickDialog();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }
  private String fullName,ShpeName,PhoneNumber,deliveryFee,country,state,city,address;
    private Boolean ShopOpen;

    private void inputData() {

        try {
            fullName=nameEt.getText().toString().trim();
            ShpeName=ShopNameEt.getText().toString().trim();
            PhoneNumber=phoneEt.getText().toString().trim();
            deliveryFee=DeliveryFeeEt.getText().toString().trim();
            country=countryEt.getText().toString().trim();
            state=stateEt.getText().toString().trim();
            city=cityEt.getText().toString().trim();
            address=addressEt.getText().toString().trim();
            ShopOpen=switchCompat.isChecked();//true or false

        }catch (NullPointerException ignored){

        }

        UpdateProfile();
    }

    private void UpdateProfile() {
        progressDialog.setMessage("Updating profile");
        progressDialog.show();
        if (img_Uri==null){
            //update without image
            Map<String,Object>map=new HashMap<>();
            map.put("name",""+fullName);
            map.put("ShopeName",""+ShpeName);
            map.put("phone",PhoneNumber);
            map.put("delivery_fee",""+deliveryFee);
            map.put("address",""+address);
            map.put("latitude",""+latitude);
            map.put("longitude",""+longitude);
            map.put("country",""+country);
            map.put("State",""+state);
            map.put("city",""+city);
            map.put("ShopOpen",""+ShopOpen);

            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast.makeText(Seller_profile_Activity.this, "profile is updated...", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Seller_profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }else {
            //update with image
            String filePathandName="Profileimages/"  + ""+firebaseAuth.getUid();
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathandName);
            storageReference.putFile(img_Uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //get url of uploaded image
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadImageUri=uriTask.getResult();
                    if (uriTask.isSuccessful()){
                        Map<String,Object>map=new HashMap<>();
                        map.put("name",""+fullName);
                        map.put("ShopeName",""+ShpeName);
                        map.put("phone",PhoneNumber);
                        map.put("delivery_fee",""+deliveryFee);
                        map.put("latitude",""+latitude);
                        map.put("longitude",""+longitude);
                        map.put("country",""+country);
                        map.put("address",""+address);
                        map.put("State",""+state);
                        map.put("city",""+city);
                        map.put("ShopOpen",""+ShopOpen);
                        map.put("profileImage",""+downloadImageUri);


                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference.child(firebaseAuth.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(Seller_profile_Activity.this, "profile is updated...", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Seller_profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Seller_profile_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }


    }

    private void checkUser() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user==null){
            Intent intent=new Intent(Seller_profile_Activity.this,Login_Activity.class);
            startActivity(intent);
            finish();
        }else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        //load user info and set the views
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uuid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    String accountType=""+ds.child("accountType").getValue();
                    String address=""+ds.child("address").getValue();
                    String city=""+ds.child("city").getValue();
                    String State=""+ds.child("State").getValue();
                    String country=""+ds.child("country").getValue();
                    String onlion=""+ds.child("onlion").getValue();
                    String delivery_fee=""+ds.child("delivery_fee").getValue();
                    String ShopeName=""+ds.child("ShopeName").getValue();
                    String email=""+ds.child("email").getValue();
                    String name=""+ds.child("name").getValue();
                     latitude= Double.parseDouble(""+ds.child("latitude").getValue());
                     longitude= Double.parseDouble(""+ds.child("longitude").getValue());
                    String profileImage=""+ds.child("profileImage").getValue();
                    String timestamp=""+ds.child("timestamp").getValue();
                    String phone=""+ds.child("phone").getValue();
                    String uuid=""+ds.child("uuid").getValue();
                    String ShopOpen=""+ds.child("ShopOpen").getValue();
                    try {
                        nameEt.setText(name);
                        phoneEt.setText(phone);
                        countryEt.setText(country);
                        stateEt.setText(State);
                        cityEt.setText(city);
                        addressEt.setText(address);
                        DeliveryFeeEt.setText(delivery_fee);
                        ShopNameEt.setText(ShopeName);
                    }catch (NullPointerException ignored){

                    }


                    if (ShopOpen.equals("true")){
                        switchCompat.setChecked(true);
                    }else {
                        switchCompat.setChecked(false);
                    }


                    try {
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_person_grey).into(profileIv);
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

    private void showImgPickDialog() {
        String[]options={"Camera","Gallery"};
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(Seller_profile_Activity.this);
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

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagepermission,STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,camerapermission,CAMERA_REQUEST_CODE);
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
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkCAMERAPermission() {
        Boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        Boolean result1=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private boolean checkStoragePermission() {
        Boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationpermission() {
        ActivityCompat.requestPermissions(this,locationpermission,LOCATION_REQUEST_CODE);
    }


    private boolean checkpermissionlocation() {
        Boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void detectLocation() {
        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
       /* Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);*/

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }catch (NullPointerException ignored){

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //location detected
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        // findAdresss(location);
        try {
            Geocoder geocoder=new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            // geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size()>0){
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String State = addresses.get(0).getAdminArea();
                String Country = addresses.get(0).getCountryName();

                //set Adresss
                countryEt.setText(Country);
                cityEt.setText(city);
                addressEt.setText(address);
                stateEt.setText(State);

            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(i);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {

                        //permission allowed

                        detectLocation();

                    } else {

                        //permission denied

                        Toast.makeText(this, "Location permission is necessary", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            break;
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK){

            if (requestCode==IMAGE_PICK_GALLERY_CODE){
                img_Uri=data.getData();
                profileIv.setImageURI(img_Uri);
            }else if (requestCode==IMAGE_PICK_CAMERA_CODE){
                profileIv.setImageURI(img_Uri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void backpress(View view) {
        onBackPressed();
    }
}

