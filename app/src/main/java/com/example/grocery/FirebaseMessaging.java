package com.example.grocery;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    public static final String Notification_Chanell_id = "My_Notification_Channel_id";//required for android o and above

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //all notification will be received here

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //get data from notification
        String NotificationType = remoteMessage.getData().get("Notification_type");
        try {
            if (NotificationType.equals("NewOrder")) {

                String byuer_uid = remoteMessage.getData().get("buyer_uid");
                String Seller_uid = remoteMessage.getData().get("seller_uid");
                String OrderId = remoteMessage.getData().get("Order_Uid");
                String NotificationTitle = remoteMessage.getData().get("Notification_Title");
                String NotificationMessage = remoteMessage.getData().get("Notification_Message");

                if (firebaseUser != null && firebaseAuth.getUid().equals(Seller_uid)) {
                    //user is signed in and is same user to which notification is send

                    ShowNotification(OrderId,Seller_uid,byuer_uid,NotificationTitle,NotificationMessage,NotificationType);
                }


            }


            if (NotificationType.equals("OrderStatuschanged")){

                String byuer_uid=remoteMessage.getData().get("buyer_uid");
                String Seller_uid=remoteMessage.getData().get("seller_uid");
                String OrderId=remoteMessage.getData().get("Order_Uid");
                String NotificationTitle=remoteMessage.getData().get("Notification_Title");
                String NotificationMessage=remoteMessage.getData().get("Notification_Message");

                if (firebaseUser!=null &&firebaseAuth.getUid().equals(byuer_uid)){
                    //user is signed in and is same user to which notification is send

                    ShowNotification(OrderId,Seller_uid,byuer_uid,NotificationTitle,NotificationMessage,NotificationType);

                }


            }
        }catch (NullPointerException ignored){

        }



    }

    private void ShowNotification(String orderid,String Seller_id,String byuerid,String NotificationTitle,String Notification_Message,String Notification_type){

        //Notificaton

        NotificationManager notificationManager=(NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE);

        // id for notification random
        int Notification_id=new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            //check if android version is oreo or above
            
            SetupNotificationChanell(notificationManager);
        }

        //handel notification click start,order activity

        Intent intent = null;
        if (Notification_type.equals("NewOrder")){
            //open ShopDetailSellerActivity
            intent=new Intent(this,ShopDetailSellerActivity.class);
            intent.putExtra("Order_id",orderid);
            intent.putExtra("Order_By",byuerid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);




        }else if (Notification_type.equals("OrderStatuschanged")){
            intent=new Intent(this,OrderDetailUsersActivity.class);
            intent.putExtra("orderTo",Seller_id);
            intent.putExtra("orderId",orderid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }

        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        //large icon
        Bitmap largeIcon= BitmapFactory.decodeResource(getResources(),R.drawable.icon);
        //sound of Notification
        Uri NotificationsoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationbuilder=new NotificationCompat.Builder(this,Notification_Chanell_id);
        notificationbuilder.setSmallIcon(R.drawable.icon)
                .setLargeIcon(largeIcon)
                .setContentTitle(NotificationTitle)
                .setContentText(Notification_Message)
                .setSound(NotificationsoundUri)
                .setAutoCancel(true)//cancel/dismiss when clicked
                .setContentIntent(pendingIntent);

        //show notification
        //notificationManager.notify(Notification_id,notificationbuilder.build());

      //  NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        notificationManager.notify(Notification_id,notificationbuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetupNotificationChanell(NotificationManager notificationManager) {

        CharSequence chanelName="Some Sample Text";
        String ChannelDiscription="Channel Description";
        NotificationChannel notificationChannel=new NotificationChannel(Notification_Chanell_id,chanelName,NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(ChannelDiscription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(R.color.RedColor);
        notificationChannel.enableVibration(true);
        if (notificationManager!=null){

            notificationManager.createNotificationChannel(notificationChannel);

        }


    }
}
