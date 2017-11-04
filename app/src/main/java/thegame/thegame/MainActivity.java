package thegame.thegame;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Debug;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

public class MainActivity extends AppCompatActivity implements LocationListener {

    private MapFragment mapFragment;
    private ImageView mapImage;
    private ImageView coverImage;
    private Map map;
    private RelativeLayout relativeLayout;

    private MapMarker locationMarker;

    final GeoCoordinate phys = new GeoCoordinate(58.3664063, 26.6885759);
    final GeoCoordinate chem = new GeoCoordinate(58.3671412,26.6931929);
    final GeoCoordinate museum = new GeoCoordinate(58.3734098,26.7061349);
    final GeoCoordinate louna = new GeoCoordinate(58.3566391,26.6756017);

    final int doubleX = 32;
    final int doubleY = 64;

    private Bitmap pinPoint;
    private Bitmap markerDefault;
    private Bitmap markerRed;
    private Bitmap markerYellow;
    private Bitmap markerPink;

    public static final double ZOOM = 14.2;
    private String userId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final List<String> requiredSDKPermissions = new ArrayList<String>();
        requiredSDKPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredSDKPermissions.add(Manifest.permission.INTERNET);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                requiredSDKPermissions.toArray(new String[requiredSDKPermissions.size()]),
                1);

        pinPoint = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.current_location), 32, 32, false);
        markerDefault = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pin_point), doubleX, doubleY, false);
        markerYellow = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pin_point_yellow), doubleX, doubleY, false);
        markerPink = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pin_point_pink), doubleX, doubleY, false);
        markerRed = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pin_point_red), doubleX, doubleY, false);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try{
            final long pollTime = 1000;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, pollTime, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, pollTime, 0, this);
        } catch (SecurityException e) {
            Log.d("tg", e.toString());
        }

        mapImage = this.findViewById(R.id.mapimage);
        coverImage = this.findViewById(R.id.coverimage);
        mapFragment = (MapFragment)this.getFragmentManager().findFragmentById(R.id.mapfragment);
        relativeLayout = this.findViewById(R.id.relativelayout);

        coverImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                createMarkerStatic(motionEvent.getX(), motionEvent.getY(), markerYellow);
                PointAdder pointAdder = new PointAdder(userId, getRealCoords(motionEvent.getX(), motionEvent.getY()));
                pointAdder.execute();
                return false;
            }
        });

        if(mapFragment != null) {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    map = mapFragment.getMap();
                    map.setZoomLevel(ZOOM);
                    map.setMapScheme(Map.Scheme.SATELLITE_DAY);

                    createMarker(phys, markerDefault);
                    createMarker(chem, markerDefault);
                    createMarker(museum, markerDefault);
                    createMarker(louna, markerDefault);

                    Image im = new Image();
                    try {
                        im.setBitmap(pinPoint);
                    } catch (Exception e) {
                        Log.d("tg", e.toString());
                    }

                    locationMarker = new MapMarker(new GeoCoordinate(0, 0), im);
                    map.addMapObject(locationMarker);

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

    @Override
    public void onProviderEnabled(String s) {
        Log.d("tg", s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("tg", "Status changed " + s);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("tg", "Location " + location.toString());
        locationMarker.setCoordinate(new GeoCoordinate(location.getLatitude(), location.getLongitude()));
        if(userId != null) {
            Pinger pinger  = new Pinger(userId, new Pair<>(location.getLatitude(), location.getLongitude()));
            pinger.execute();
        }
        else {
            Register register = new Register("testname", this, new Pair<>(location.getLatitude(), location.getLongitude()));
            register.execute();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("tg", "Status changed " + s);
    }

    public void uncoverMap() {
        coverImage.setImageAlpha(0);
    }

    public void createMarker(GeoCoordinate geoCoordinate, Bitmap markerBitmap) {
        Image im = new Image();
        try{
            im.setBitmap(markerBitmap);
        } catch (Exception e) {
            Log.e("rg", e.toString());
        }

        MapMarker mapObject = new MapMarker(geoCoordinate, im);
        map.addMapObject(mapObject);
    }

    public void createMarkerStatic(float x, float y, Bitmap markerBitmap) {
        ImageView marker = new ImageView(this);
        marker.setImageBitmap(markerBitmap);

        marker.setX(x - 16);
        marker.setY(y - 32);

        relativeLayout.addView(marker);

        Pair<Double, Double> realCoord = getRealCoords(x, y);

        createMarker(new GeoCoordinate(realCoord.first, realCoord.second), markerPink);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Pair<Double, Double> getRealCoords(double x, double y) {
        double centerX = mapImage.getWidth() / 2;
        double centerY = mapImage.getHeight() / 2;

        double realCenterX = MapFiller.lastCenter.first;
        double realCenterY = MapFiller.lastCenter.second;

        double topRightN = Double.parseDouble(ImageRequest.tr.substring(5, 17));
        double topRightE = Double.parseDouble(ImageRequest.tr.substring(29, 38));

        double bottomLeftN = Double.parseDouble(ImageRequest.bl.substring(5, 17));
        double bottomLeftE = Double.parseDouble(ImageRequest.bl.substring(29, 38));

        double realN = realCenterX + (topRightN - bottomLeftN) / 2 * (centerY - y) / centerY;
        double realE = realCenterY + (topRightE - bottomLeftE) / 2 * (x - centerX) / centerX;

        return

                new Pair<>(realN, realE);
    }
}
