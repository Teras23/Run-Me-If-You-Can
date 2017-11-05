package thegame.thegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapState;


import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private MapFragment mapFragment;
    private ImageView mapImage;
    private MapCanvas mapCanvas;
    private Map map;
    private RelativeLayout relativeLayout;

    private Pair<Integer, Integer> mapImageSize;

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
    private String playerName = null;

    private boolean gameRunning;
    private boolean lastWating;
    private Location lastLocation;
    private LocationManager locationManager;

    private Opponent opponent = null;
    private HashMap<String, GamePoint> userGamePoints;
    private HashMap<String, GamePoint> opponentGamePoints;
    private HashMap<Pair<Double, Double>, ImageView> opponentMarkers;
    private ArrayList<Pair<Double, Double>> opponentHistory;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        lastLocation = null;
        gameRunning = false;
        lastWating = true;

        pinPoint = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.current_location), 32, 32, false);
        markerDefault = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pin_point), doubleX, doubleY, false);
        markerYellow = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pin_point_yellow), doubleX, doubleY, false);
        markerPink = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pin_point_pink), doubleX, doubleY, false);
        markerRed = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.pin_point_red), doubleX, doubleY, false);

        userGamePoints = new HashMap<>();
        opponentGamePoints = new HashMap<>();
        opponentMarkers = new HashMap<>();
        opponentHistory = new ArrayList<>();

        playerName = getIntent().getStringExtra("Name");
        setTitle("Waiting for a game");

        Image im = new Image();
        try {
            im.setBitmap(pinPoint);
        } catch (Exception e) {
            Log.d("tg", e.toString());
        }
        locationMarker = new MapMarker(new GeoCoordinate(0, 0), im);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try{
            final long pollTime = 1000;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, pollTime, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, pollTime, 0, this);
        } catch (SecurityException e) {
            Log.d("tg", e.toString());
        }

        mapImage = this.findViewById(R.id.mapimage);
        relativeLayout = this.findViewById(R.id.relativelayout);
        mapCanvas = new MapCanvas(getApplicationContext(), this);
        relativeLayout.addView(mapCanvas);
        mapFragment = (MapFragment)this.getFragmentManager().findFragmentById(R.id.mapfragment);

        mapImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(gameRunning) {
                    createMarkerStatic(motionEvent.getX(), motionEvent.getY(), markerYellow);
                    PointAdder pointAdder = new PointAdder(userId, getRealCoords(motionEvent.getX(), motionEvent.getY()));
                    pointAdder.execute();
                }
                else {
                    initGame();
                }
                return false;
            }
        });

        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userId != null) {
                    Pinger pinger  = new Pinger(userId, new Pair<>(lastLocation.getLatitude(), lastLocation.getLongitude()), mainActivity);
                    pinger.execute();
                }
                else if (lastLocation != null) {
                    Register register = new Register(playerName, mainActivity, new Pair<>(lastLocation.getLatitude(), lastLocation.getLongitude()));
                    register.execute();
                }
                else {
                    try{
                        final long pollTime = 1000;
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mainActivity);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mainActivity);
                    } catch (SecurityException e) {
                        Log.d("tg", e.toString());
                    }
                }
                ha.postDelayed(this, 1000);
            }
        }, 1000);

        addMap();
    }

    private void addMap() {
        if(mapFragment != null) {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    map = mapFragment.getMap();
                    map.setZoomLevel(ZOOM);
                    map.setMapScheme(Map.Scheme.SATELLITE_DAY);

//                    createMarker(phys, markerDefault);
//                    createMarker(chem, markerDefault);
//                    createMarker(museum, markerDefault);
//                    createMarker(louna, markerDefault);

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

        mapImageSize = null;
        ViewTreeObserver vto = mapImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if(mapImageSize == null) {
                    Log.d("tg", mapImage.getWidth() + " " + mapImage.getHeight());
                    mapImageSize = new Pair<>(mapImage.getWidth(), mapImage.getHeight());
                }
                return true;
            }
        });
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
        lastLocation = location;
        locationMarker.setCoordinate(new GeoCoordinate(location.getLatitude(), location.getLongitude()));
        //TODO: Check last and current coord dif
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("tg", "Status changed " + s);
    }

    public MapMarker createMarker(GeoCoordinate geoCoordinate, Bitmap markerBitmap) {
        Image im = new Image();
        try{
            im.setBitmap(markerBitmap);
        } catch (Exception e) {
            Log.e("rg", e.toString());
        }

        MapMarker mapObject = new MapMarker(geoCoordinate, im);
        map.addMapObject(mapObject);
        return mapObject;
    }

    public ImageView createMarkerStatic(float x, float y, Bitmap markerBitmap) {
        ImageView marker = new ImageView(this);
        marker.setImageBitmap(markerBitmap);

        marker.setX(x - 16);
        marker.setY(y - 32);

        relativeLayout.addView(marker);

        Pair<Double, Double> realCoord = getRealCoords(x, y);

        opponentMarkers.put(realCoord, marker);
        //TODO: remove makes the marker on the player map
//        createMarker(new GeoCoordinate(realCoord.first, realCoord.second), markerPink);
        return marker;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Pair<Double, Double> getRealCoords(double x, double y) {
        final double centerX = mapImage.getWidth() / 2;
        final double centerY = mapImage.getHeight() / 2;

        double realCenterX = MapFiller.lastCenter.first;
        double realCenterY = MapFiller.lastCenter.second;

        final double topRightN = Double.parseDouble(ImageRequest.tr.substring(5, 17));
        final double topRightE = Double.parseDouble(ImageRequest.tr.substring(29, 38));

        final double bottomLeftN = Double.parseDouble(ImageRequest.bl.substring(5, 17));
        final double bottomLeftE = Double.parseDouble(ImageRequest.bl.substring(29, 38));

        double realN = realCenterX + (topRightN - bottomLeftN) / 2 * (centerY - y) / centerY;
        double realE = realCenterY + (topRightE - bottomLeftE) / 2 * (x - centerX) / centerX;

        Log.d("tg", getScreenCoords(realN, realE) + "");

        return new Pair<>(realN, realE);
    }

    public Pair<Double, Double> getScreenCoords(double N, double E) {
        final double centerX = mapImage.getWidth() / 2;
        final double centerY = mapImage.getHeight() / 2;

        double centerN = MapFiller.lastCenter.first;
        double centerE = MapFiller.lastCenter.second;

        final double topRightN = Double.parseDouble(ImageRequest.tr.substring(5, 17));
        final double topRightE = Double.parseDouble(ImageRequest.tr.substring(29, 38));

        final double bottomLeftN = Double.parseDouble(ImageRequest.bl.substring(5, 17));
        final double bottomLeftE = Double.parseDouble(ImageRequest.bl.substring(29, 38));

        double y = centerY - 2 * centerY * (N - centerN) / (topRightN - bottomLeftN);
        double x = mapImage.getWidth() - centerX - 2 * centerX * (E - centerE) / (topRightE - bottomLeftE);

        return new Pair<>(x, y);
    }

    public void initGame() {
        double latitude = 58.3734098;
        double longitude = 26.7061349;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle("Loading map");
                Log.d("tg", opponent.getLat() + " " + opponent.getLon());

                MapFiller mapFiller = new MapFiller(mainActivity, new GeoCoordinate(opponent.getLon(), opponent.getLat()));
                mapFiller.execute();
            }
        });
    }

    public void startGame(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle("Map loaded");
                mapImage.setImageBitmap(bitmap);
            }
        });
        gameRunning = true;
    }

    public ArrayList<Pair<Double, Double>> getOpponentHistory() {
        return opponentHistory;
    }

    public void updateWaitingStatus(boolean waiting) {
        if(lastWating && !waiting) {
            initGame();
            lastWating = waiting;
        }
    }

    public void updateOpponent(final Opponent opponent) {
        this.opponent = opponent;
        opponentHistory.add(new Pair<>(opponent.getLon(), opponent.getLat()));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle("Playing with: " + opponent.getOpponentName());
            }
        });
    }

    public void updateUserGamePoints(HashMap<String, GamePoint> gamePoints) {
        for(String id : gamePoints.keySet()) {
            GamePoint value = gamePoints.get(id);
            if(userGamePoints.containsKey(id)) {
                //TODO:Update that marker
            }
            else {
                Log.d("tg", "Marking new user marker at " + value.getLongitude() + " " + value.getLongitude());
                value.setMapMarker(createMarker(new GeoCoordinate(value.getLongitude(), value.getLatitude()), markerRed));
                userGamePoints.put(id, value);
            }
        }
    }

    public Pair<Integer, Integer> getMapImageSize() {
        return mapImageSize;
    }

    public void updateOpponentGamePoint(HashMap<String, GamePoint> gamePoints) {
        for(String id : gamePoints.keySet()) {
            GamePoint value = gamePoints.get(id);
            if(opponentGamePoints.containsKey(id)) {
                //TODO:Update that marker
            }
            else {
                Pair<Double, Double> coords = new Pair<>(value.getLongitude(), value.getLatitude());
                Log.d("tg", "Marking new opponent game point " + value.getLongitude() + " " + value.getLongitude());
                value.setImageView(opponentMarkers.get(coords));
                opponentGamePoints.put(id, value);
            }
        }
    }
}