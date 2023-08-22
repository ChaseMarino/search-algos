public class City {
    private final String name;
    private final double latitude;
    private final double longitude;

    public City(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double distanceTo(City otherCity) {
        double lat1 = this.latitude;
        double lon1 = this.longitude;
        double lat2 = otherCity.latitude;
        double lon2 = otherCity.longitude;

        double distance = Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2)) * 100;
        return distance;
    }

    @Override
    public String toString() {
        return String.format(" %s %s %s", name, latitude, longitude);
    }
}