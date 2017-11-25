package interfaces;

import com.sapergis.parking.ParkingStatistics;

import java.util.List;

import objects.ParkingPositionObject;

public interface ParkingEntriesInterface {
    public List<ParkingPositionObject> parkingEntriesList();
    public List<ParkingStatistics> parkingStatisticsList();
}
