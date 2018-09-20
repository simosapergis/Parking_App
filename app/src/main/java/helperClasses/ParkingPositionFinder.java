package helperClasses;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sapergis.parking.R;
import com.sapergis.parking.StartActivity;

import java.util.List;
import java.util.Locale;

import objects.ParkingPositionObject;

public class ParkingPositionFinder{
    private Location mLocation;
    private Context context;
    private Uri uri;
    NotificationManager notificationManager;


    public ParkingPositionFinder(Context context){
        this.context = context;
        this.mLocation = new Location(Helper.PARKINGAPP_PROVIDER);
        this.uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

   public void searchForPossibleParkingPlaces(Location location , List<ParkingPositionObject> ppoList){
       for(ParkingPositionObject ppo : ppoList){
           mLocation.reset();
           mLocation.setLongitude(ppo.getLongitude());
           mLocation.setLatitude(ppo.getLatitude());
           if(mLocation.distanceTo(location) < Helper.TARGET_DISTANCE ){
                int result = sendNotification(location);
                Log.d(Helper.TAG, "result is "+result);
                break;
           }
       }

   }
   private int sendNotification(Location location) {
       String id = Helper.PARKING_CHANNEL;
       CharSequence name = context.getString(R.string.app_name);
       String discription = context.getString(R.string.channel_discription);
       NotificationCompat.Builder mBuilder =
               new NotificationCompat.Builder(context , id)
                       .setSmallIcon(R.drawable.ic_parking_nearby)
                       .setContentTitle(name)
                       .setContentText(discription)
                       .setSound(uri)
                       .setPriority(NotificationCompat.PRIORITY_MAX)
                       .setVibrate(new long[]{500,500})
                       .setStyle(new NotificationCompat.Style() {})
                       .setTicker("ticker");
       try{
           //Intent resultIntent = new Intent(context, StartActivity.class);
           //String geoUri = String.format(Locale.ENGLISH, "geo:%f,%f", 37.950124,23.69197);
           Uri gUri = Uri.parse("google.navigation:q="+location.getLatitude()+","+location.getLongitude());
           Intent resultIntent = new Intent(Intent.ACTION_VIEW,gUri);
           resultIntent.setPackage(Helper.GOOGLE_MAPS_PACKAGE);
           TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
           taskStackBuilder.addParentStack(StartActivity.class);
           taskStackBuilder.addNextIntent(resultIntent);
           PendingIntent resultPendingIntent  = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
           mBuilder.setContentIntent(resultPendingIntent);
           NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
           if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
               NotificationChannel channel = new NotificationChannel(id,discription,NotificationManager.IMPORTANCE_DEFAULT);
               mNotificationManager.createNotificationChannel(channel);
           }
           mNotificationManager.notify(Helper.NOTIFICATION_ID,mBuilder.build());
       }catch(NullPointerException nex){
           Log.d(Helper.TAG,nex.getLocalizedMessage());
          return -1;
       }
       return 1;
   }

}
