package thegame.thegame;

/**
 * Created by teras on 5.11.17.
 */

public class Opponent {
    private String id;
    private String name;
    private double lon;
    private double lat;
    private int points;
    private String location;

    public Opponent(String id, String name, double lon, double lat, int points, String location) {
        this.id = id;
        this.name = name;
        this.lon = lon;
        this.lat = lat;
        this.points = points;
        this.location = location;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getOpponentId() {
        return id;
    }

    public String getOpponentName() {
        return name;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public int getPoints() {
        return points;
    }

    public String getLocation() {
        return location;
    }
}
