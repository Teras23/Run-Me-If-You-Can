package thegame.thegame;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by teras on 4.11.17.
 */

public class Pinger extends AsyncTask<Void, Void, Void> {

    private double longitude;
    private double latidude;
    private String userId;
    private MainActivity mainActivity;

    public Pinger(String id, Pair<Double, Double> loc, MainActivity mainActivity) {
        latidude = loc.first;
        longitude = loc.second;
        userId = id;
        this.mainActivity = mainActivity;
        Log.d("tg", latidude + " " + longitude);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            JSONRequest request = new JSONRequest();
            String response = request.ping(userId, latidude, longitude);
            JSONObject object = new JSONObject(response);
            Log.d("tg", object.toString());

            boolean waiting = object.getBoolean("waiting");

            if(!waiting) {
                JSONArray user1Points = object.getJSONArray("myPoints");
                JSONArray user2Points = object.getJSONArray("opponentPoints");

                HashMap<String, GamePoint> user1GamePoints = getGamePoints(user1Points);
                HashMap<String, GamePoint> user2GamePoints = getGamePoints(user2Points);

                Opponent opponent = getOpponent(object.getJSONObject("opponent"));
                boolean gameOver = object.getBoolean("gameOver");

                if(gameOver) {
                    mainActivity.gameOver();
                }

                mainActivity.updateUserGamePoints(user1GamePoints);
                mainActivity.updateOpponentGamePoint(user2GamePoints);

                mainActivity.updateOpponent(opponent);
            }

            mainActivity.updateWaitingStatus(waiting);

        } catch (Exception e) {
            Log.e("tg", "Pinger: Error reading JSON:\n" + e.toString());
        }
        return null;
    }

    private HashMap<String, GamePoint> getGamePoints(JSONArray userPoints) {
        HashMap<String, GamePoint> gamePoints = new HashMap<>();
        try {
            for(int i = 0; i < userPoints.length(); i++) {
                JSONObject arrayObject = userPoints.getJSONObject(i);
                String id = arrayObject.getString("id");
                double lon = arrayObject.getDouble("longitude");
                double lat = arrayObject.getDouble("latitude");
                int reward = arrayObject.getInt("reward");
                boolean collected = arrayObject.getBoolean("collected");

                GamePoint gamePoint = new GamePoint(id, lon, lat, reward, collected);
                gamePoints.put(id, gamePoint);
            }
        } catch (JSONException e) {
            Log.e("tg", e.toString());
        }
        return gamePoints;
    }

    private Opponent getOpponent(JSONObject opponent) {
        try {
            String id = opponent.getString("id");
            String name = opponent.getString("name");
            double lon = opponent.getDouble("longitude");
            double lat = opponent.getDouble("latitude");
            int points = opponent.getInt("rewardPoints");
            String location = opponent.getString("location");
            return new Opponent(id, name, lon, lat, points, location);
        } catch (JSONException e) {
            Log.e("tg", e.toString());
        }
        return null;
    }
}
