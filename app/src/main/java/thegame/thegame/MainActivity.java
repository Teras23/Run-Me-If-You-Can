package thegame.thegame;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.GradientDrawable;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapState;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapFragment mapFragment;
    private ImageView mapImage;
    private ImageView coverImage;
    private Map map;

    public static final double ZOOM = 14.2;

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

        mapImage = this.findViewById(R.id.mapimage);
        coverImage = this.findViewById(R.id.coverimage);
        mapFragment = (MapFragment)this.getFragmentManager().findFragmentById(R.id.mapfragment);

        if(mapFragment != null) {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    map = mapFragment.getMap();
                    Log.d("tg", map.getMinZoomLevel() + " " + map.getMaxZoomLevel());
                    map.setMapScheme(Map.Scheme.SATELLITE_DAY);
                    map.setZoomLevel(ZOOM);

                    final GeoCoordinate phys = new GeoCoordinate(58.3664063, 26.6885759);

                    Image im = new Image();
                    try{
                        Bitmap coin = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.the_game_coin);
                        //im.setImageResource(R.drawable.the_game_coin);
                        im.setBitmap(Bitmap.createScaledBitmap(coin, 48, 48, false));
                    } catch (Exception e) {
                        Log.d("rg", e.toString());
                    }

                    MapMarker mapObject = new MapMarker(phys, im);
                    map.addMapObject(mapObject);
                    map.addTransformListener(new Map.OnTransformListener() {
                        @Override
                        public void onMapTransformStart() {
                            map.setZoomLevel(Math.max(map.getZoomLevel(), ZOOM));
                        }

                        @Override
                        public void onMapTransformEnd(MapState mapState) {

                        }
                    });
                }
            });
        }
        //createCoin();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        MapFiller mapFiller = new MapFiller(mapImage);
        mapFiller.execute();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            uncoverMap();
        }
    }

    public void uncoverMap() {
        coverImage.setImageAlpha(0);
    }

    public void createCoin(GeoCoordinate geoCoordinate) {

    }
}
