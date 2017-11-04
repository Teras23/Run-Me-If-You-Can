package thegame.thegame;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Debug;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
//import com.here.android.mpa.mapping.customization.*;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapFragment mapFragment;
    private ImageView mapImage;
    private Map map;

    private final double ZOOM = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<String> requiredSDKPermissions = new ArrayList<String>();
        requiredSDKPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredSDKPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredSDKPermissions.add(Manifest.permission.INTERNET);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

        ActivityCompat.requestPermissions(this,
                requiredSDKPermissions.toArray(new String[requiredSDKPermissions.size()]),
                1);

        mapImage = (ImageView)this.findViewById(R.id.mapimage);
        mapFragment = (MapFragment)this.getFragmentManager().findFragmentById(R.id.mapfragment);

        if(mapFragment != null) {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    map = mapFragment.getMap();
                    Log.d("tg", map.getMinZoomLevel() + " " + map.getMaxZoomLevel());
                    map.setMapScheme(Map.Scheme.SATELLITE_DAY);
                    map.setZoomLevel(ZOOM);
                }
            });
        }

        ImageRequest imageRequest = new ImageRequest();
        String request = "mapview?c=52.5159%2C13.3777&" +
                "z=14&" +
                "w=2048&h=2048&" +
                "f=1&" +
                "app_id=DemoAppId01082013GAL&app_code=AJKnXv84fjrb0KIHawS0Tg";
        Bitmap map = imageRequest.getImage();
    }
}
