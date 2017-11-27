package interfaces;

import com.sapergis.parking.ParkingStatisticsActivity;

import java.util.List;

import objects.ParkingPositionObject;

public interface ParkingEntriesInterface {
    public List<ParkingPositionObject> parkingEntriesList();
    public List<String> areasVisited();
    public double[] areaPercentages();
}
