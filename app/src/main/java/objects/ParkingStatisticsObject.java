package objects;

public class ParkingStatisticsObject extends ParkingPositionObject {
    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    double percentage;

}
