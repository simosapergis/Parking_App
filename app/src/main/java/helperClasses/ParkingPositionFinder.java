package helperClasses;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;

import com.sapergis.parking.R;
import com.sapergis.parking.StartActivity;

import java.util.List;

import objects.ParkingPositionObject;

public class ParkingPositionFinder {
    private Location mLocation;
    private Context context;
    NotificationManager notificationManager;
    private static int NOTIFICATION_ID = 105;

    public ParkingPositionFinder(Context context){
        this.context = context;
        mLocation = new Location("search");
    }

   public void searchForPossibleParkingPlaces(Location location , List<ParkingPositionObject> ppoList){
       for(ParkingPositionObject ppo : ppoList){
           mLocation.reset();
           mLocation.setLongitude(ppo.getLongitude());
           mLocation.setLatitude(ppo.getLatitude());
           if(mLocation.distanceTo(location) < Helper.TARGET_DISTANCE ){
                sendNotification();
           }
       }
   }

   private void sendNotification() {
       String id = "my_channel";
       CharSequence name = context.getString(R.string.channel_name);
       String discription = context.getString(R.string.channel_discription);
       NotificationCompat.Builder mBuilder =
               new NotificationCompat.Builder(context , id)
                       .setSmallIcon(R.drawable.ic_parking_nearby)
                       .setContentTitle(name)
                       .setContentText(discription)
                       .setTicker("ticker");
       Intent resultIntent = new Intent(context, StartActivity.class);
       TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
       taskStackBuilder.addParentStack(StartActivity.class);
       taskStackBuilder.addNextIntent(resultIntent);
       PendingIntent resultPendingIntent  = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
       mBuilder.setContentIntent(resultPendingIntent);
       NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       mNotificationManager.notify(NOTIFICATION_ID,mBuilder.build());
   }

}
