package thegame.thegame;

import android.widget.ImageView;

import com.here.android.mpa.mapping.MapMarker;

/**
 * Created by teras on 5.11.17.
 */

public class GamePoint {
    private String id;
    private double longitude;
    private double latitude;
    private int reward;
    private boolean collected;
    private MapMarker mapMarker;
    private ImageView imageView;

    public GamePoint(String id, double longitude, double latitude, int reward, boolean collected) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.reward = reward;
        this.collected = collected;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getPointId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getReward() {
        return reward;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public MapMarker getMapMarker() {
        return mapMarker;
    }

    public void setMapMarker(MapMarker mapMarker) {
        this.mapMarker = mapMarker;
    }
}
