package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegisterUser_Activity extends AppCompatActivity implements LocationListener {
    // Ui views
    private ImageButton backbtn, gpskbtn;
    private ImageView profileIv;
    private EditText nameEt, phoneEt, countryEt, stateEt, cityEt,
            addressEt, emailEt, passwordEt, cpasswordEt;
    private Button BtnRegister;
    private TextView registerSellerTv;

    //permission constant
    public static final int LOCATION_REQUEST_CODE = 100;
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

    private LocationManager locationManager=null;
    private Double latitude;
    private Double longitude;
    // Firebase
   private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_);

        //init UI views

        backbtn = findViewById(R.id.backbtn);
        gpskbtn = findViewById(R.id.gpskbtn_User);
        profileIv = findViewById(R.id.profileIv_User);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        countryEt = findViewById(R.id.countryEt);
        stateEt = findViewById(R.id.stateEt);
        cityEt = findViewById(R.id.cityEt);
        addressEt = findViewById(R.id.addressEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        cpasswordEt = findViewById(R.id.cpasswordEt);
        BtnRegister = findViewById(R.id.BtnRegister);
        registerSellerTv = findViewById(R.id.registerSellerTv);

        // intalize firebase instanence
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //initalize permission array
        locationpermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        camerapermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gpskbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //find current location

                if (checkpermissionlocation()) {
                    //already allow
                    detectLocation();
                } else {
                    //not allowed,request
                    requestLocationPermission();
                }
            }
        });


        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick the img

                showImgPickDialog();
            }

            private void showImgPickDialog() {
                String options[] = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUser_Activity.this);
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


        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();

            }
        });

        registerSellerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterUser_Activity.this, RegisterSeller_Activity.class);
                startActivity(intent);

            }
        });


    }
    String fullName,PhoneNumber,country,state,city,address,password,Confirmpassword,email;
    private void inputData() {
        fullName=nameEt.getText().toString().trim();
        PhoneNumber=phoneEt.getText().toString().trim();
        country=countryEt.getText().toString().trim();
        state=stateEt.getText().toString().trim();
        city=cityEt.getText().toString().trim();
        address=addressEt.getText().toString().trim();
        password=passwordEt.getText().toString().trim();
       final String Confirmpassword=cpasswordEt.getText().toString().trim();
        email=emailEt.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)){
            nameEt.setError("Please Enter name");
            nameEt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(PhoneNumber)){
            phoneEt.setError("please entery Shope Name");
            phoneEt.requestFocus();
            return;
        }



        if (latitude==0.0||longitude==0.0){
            Toast.makeText(this, "Please click gps to detect location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEt.setError("invalid email pattren");
            emailEt.requestFocus();
            return;

        }
        if (TextUtils.isEmpty(password)){
            passwordEt.setError("please enter the Password");
            passwordEt.requestFocus();
            return;
        }
        if (password.length()<6){
            passwordEt.setError("password must be at least 6 character long");
            passwordEt.requestFocus();
            return;
        }
        if (!password.equals(Confirmpassword)){
            cpasswordEt.setError("password is not match");
            cpasswordEt.requestFocus();
            return;

        }


        CreatAcccount();


    }

    private void CreatAcccount() {
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        //creat Account
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //account created
                saveFirebaseData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed account creating
                progressDialog.dismiss();
                Toast.makeText(RegisterUser_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void saveFirebaseData() {
        progressDialog.setMessage("Saving account info...");
        final String timestamp=""+System.currentTimeMillis();

        if (img_Uri==null){
            //saving info without image
            //setup data to save
            Map<String,Object> map=new HashMap<>();
            map.put("uuid",""+firebaseAuth.getUid());
            map.put("email",""+email);
            map.put("name",""+fullName);
            map.put("phone",PhoneNumber);
            map.put("latitude",""+latitude);
            map.put("longitude",""+longitude);
            map.put("timestamp",""+timestamp);
            map.put("country",country);
            map.put("State",""+state);
            map.put("city",""+city);
            map.put("address",""+address);
            map.put("accountType","User");
            map.put("onlion","true");
            map.put("profileImage","");

            //save to db
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //db updated
                    progressDialog.dismiss();
                    Intent intent=new Intent(RegisterUser_Activity.this,MainUser_Activity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Intent intent=new Intent(RegisterUser_Activity.this,MainUser_Activity.class);
                    startActivity(intent);
                    finish();

                }
            });

        }else {
            //saving info with image

            //name and Path of image
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
                        //setup data to save
                        Map<String,Object>map=new HashMap<>();
                        map.put("uuid",""+firebaseAuth.getUid());
                        map.put("email",""+email);
                        map.put("name",""+fullName);
                        map.put("phone",PhoneNumber);
                        map.put("latitude",""+latitude);
                        map.put("longitude",""+longitude);
                        map.put("timestamp",""+timestamp);
                        map.put("country",country);
                        map.put("State",""+state);
                        map.put("city",""+city);
                        map.put("address",""+address);
                        map.put("accountType","User");
                        map.put("onlion","true");
                        map.put("profileImage",""+downloadImageUri);

                        //save to db
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference.child(firebaseAuth.getUid()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //db updated
                                progressDialog.dismiss();
                                Intent intent=new Intent(RegisterUser_Activity.this,MainUser_Activity.class);
                                startActivity(intent);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Intent intent=new Intent(RegisterUser_Activity.this,MainUser_Activity.class);
                                startActivity(intent);
                                finish();

                            }
                        });
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterUser_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
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
            Geocoder geocoder=new Geocoder(getBaseContext(),Locale.getDefault());
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

  /*  private void findAdresss(Location location) {
        //find adreess ,country,state,city;


    }*/

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        //location/gps disabled

        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(i);

    }






    private boolean checkpermissionlocation() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
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


    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationpermission, LOCATION_REQUEST_CODE);
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
}
