package objects;

public class ParkingPositionObject {

    private int id;
    private String username;
    private String area;
    private String parked_address;
    private String parked_address_no;
    private Double latitude;
    private Double longitude;
    private Long datetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getParked_address() {
        return parked_address;
    }

    public void setAddress_parked(String parked_address) {
        this.parked_address = parked_address;
    }
    public String getParked_address_no() {
        return parked_address_no;
    }

    public void setParked_address_no(String parked_address_no) {
        this.parked_address_no = parked_address_no;
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
