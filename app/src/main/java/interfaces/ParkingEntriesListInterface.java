package interfaces;

import java.util.List;

import objects.ParkingPositionObject;

public interface ParkingEntriesListInterface {
    public void sendList(List<ParkingPositionObject> entriesList);
}
