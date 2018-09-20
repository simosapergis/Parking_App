package helperClasses;

import android.location.Location;

import java.util.Locale;
import java.util.concurrent.TimeUnit;



public class Location2 extends Location {
    private Location location;
    private Location previousLocation;
    private Helper.Scale scale;

    public Location2(String provider) {

        super(provider);
    }

    public Location2 (Location location,Location previousLocation, Helper.Scale scale){
        super(location);
        this. location = location;
        this.previousLocation = (previousLocation==null ? location : previousLocation);
        this.scale = scale;
    }

    public float getSpeed(){
        float speed =0f;
        float distance = this.distanceTo(previousLocation);
        long timeLapse = TimeUnit.MILLISECONDS.toSeconds(this.getTime() - previousLocation.getTime());
        switch (scale){
            case METETERS_PER_SECOND:
                speed =  distance/timeLapse;
                break;
            case METERS_PER_MINUTE:
                speed =  (distance/1000)/((timeLapse/60)/60); //needs fix
                break;
            case KMETERS_PER_HOUR:
                speed = (distance/1000)/((timeLapse/60)/60); // needs fix
                break;
        }
        return Math.round(speed*100.0f)/100.0f;
        //return String.format(Locale.getDefault(),"%.2f",speed);
    }

    @Override
    public boolean hasSpeed() {
        return getSpeed() > Helper.TARGET_SPEED ;
    }
}
