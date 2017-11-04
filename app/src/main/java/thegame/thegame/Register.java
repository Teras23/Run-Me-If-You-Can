package thegame.thegame;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by teras on 4.11.17.
 */

public class Register extends AsyncTask<Void, Void, String> {

    private String name;
    private MainActivity mainActivity;
    private Pair<Double, Double> loc;

    public Register(String name, MainActivity mainActivity, Pair<Double, Double> loc) {
        this.name = name;
        this.mainActivity = mainActivity;
        this.loc = loc;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String response = "{}";
        try {
            JSONRequest request = new JSONRequest();
            response = request.getJSON("/user/register?name=" + name + "&latitude=" + loc.first + "&longitude=" + loc.second + "&location=null");
            Log.d("tg", response);
        } catch (Exception e) {
            Log.e("tg", "Error reading JSON:\n" + e.toString());
        }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {
        try {
            JSONObject jsonArray = new JSONObject(response);
            JSONObject user1 = jsonArray.getJSONObject("user1");
            /*String user1 = user1.getString("id");
            boolean waiting = jsonArray.getBoolean("waiting");
            if(waiting) {
                mainActivity.setUserId(user1);
            }*/
        } catch (Exception e) {
            Log.e("tg", "Error reading JSON:\n" + e.toString());
        }
    }
}
