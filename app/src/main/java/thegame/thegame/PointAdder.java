package thegame.thegame;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * Created by teras on 4.11.17.
 */

public class PointAdder extends AsyncTask<Void, Void, Void> {

    private double longitude;
    private double latidude;
    private String userId;
    private MainActivity mainActivity;

    public PointAdder(String id, Pair<Double, Double> loc, MainActivity mainActivity) {
        latidude = loc.first;
        longitude = loc.second;
        userId = id;
        this.mainActivity = mainActivity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            JSONRequest request = new JSONRequest();
            String response = request.addPoint(userId, latidude, longitude);
            JSONObject object = new JSONObject(response);
            Log.d("tg", "TOAST MESSAGE " + object.getString("message"));
            mainActivity.showText(object.getString("message"));
        } catch (Exception e) {
            Log.e("tg", "Error reading JSON:\n" + e.toString());
        }
        return null;
    }
}
