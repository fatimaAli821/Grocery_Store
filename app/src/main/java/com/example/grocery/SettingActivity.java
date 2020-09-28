package com.example.grocery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingActivity extends AppCompatActivity {
    private ImageButton backbtn;
    private SwitchCompat fcmSwitch;
    private TextView NotificationStatusTv;
    public static final String enabledMessage="Notification are enabled";
    public static final String dissabledMessage="Notification are disabled";
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditors;
    private FirebaseAuth firebaseAuth;
    private Boolean ischecked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        backbtn=findViewById(R.id.backbtn);
        fcmSwitch=findViewById(R.id.fcmSwitch);
        NotificationStatusTv=findViewById(R.id.NotificationStatusTv);
        firebaseAuth=FirebaseAuth.getInstance();
        //init sharedPreference
        sp=getSharedPreferences("Setting",MODE_PRIVATE);
        //check last selected option true/false
        ischecked=sp.getBoolean("FCM_ENABLED",false);
        fcmSwitch.setChecked(ischecked);
        if (ischecked){
            //was enabled
            NotificationStatusTv.setText(enabledMessage);

        }else {
            // was disabled
            NotificationStatusTv.setText(dissabledMessage);

        }




        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        //add switch check change listner to enable disable notification

        fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    //enable notification
                    SubscriberTopic();
                }else {
                    //disable notification
                    UnSubscriberTopic();


                }

            }
        });

        //we will work with topic Firebase Messaging for a user to recive Topic based message/notification have to subscribe that topic
        //requirements:Need FCM server i am cpy mine you need to copy yurs if there is not click on add server key button
        //now we need to save the state of what user chose for this we will use shred preferences


    }

    private void SubscriberTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Contants.FCM_Topic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            //subscribed sucessfully
                //save setting in shred preference
                spEditors=sp.edit();
                spEditors.putBoolean("FCM_ENABLED",true);
                spEditors.apply();
                Toast.makeText(SettingActivity.this, ""+enabledMessage, Toast.LENGTH_SHORT).show();
                NotificationStatusTv.setText(enabledMessage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
               
            }
        });
    }

    private void UnSubscriberTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Contants.FCM_Topic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //unsubscribed sucessfully
                // subscribed failed
                spEditors=sp.edit();
                spEditors.putBoolean("FCM_ENABLED",false);
                spEditors.apply();
                Toast.makeText(SettingActivity.this, ""+dissabledMessage, Toast.LENGTH_SHORT).show();
                NotificationStatusTv.setText(dissabledMessage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // unsubscribed failed
                Toast.makeText(SettingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
