package objects;

public class ParkingPositionObject {
    private String username;
    private String area;
    private Double latitude;
    private Double longitude;
    private Long datetime;

    private String vehicle;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }


    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String toString(){
        return "Username : "+username+" ,Vehicle : "+vehicle+" ,Logitude : "+longitude+" ,Latitude : "+latitude+" ,Area: "+area+" ,Datetime : "+datetime;
    }
}
