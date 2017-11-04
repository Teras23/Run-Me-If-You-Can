package thegame.thegame;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

/**
 * Created by teras on 4.11.17.
 */

public class Pinger extends AsyncTask<Void, Void, Void> {

    private double longitude;
    private double latidude;
    private String userId;

    public Pinger(String id, Pair<Double, Double> loc) {
        latidude = loc.first;
        longitude = loc.second;
        userId = id;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            JSONRequest request = new JSONRequest();
            request.ping(userId, latidude, longitude);
        } catch (Exception e) {
            Log.e("tg", "Error reading JSON:\n" + e.toString());
        }
        return null;
    }
}
