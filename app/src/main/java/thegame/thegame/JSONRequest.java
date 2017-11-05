package thegame.thegame;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONRequest {
    public static final String serverURL = "http://eucolus.com:8080";
    public String getJSON(String path) {
        URL url;
        HttpURLConnection urlConnection;

        StringBuilder data = new StringBuilder();

        try {
            url = new URL(serverURL + path);
            Log.d("tg", "JSON Request to: " + url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("Cache-Control", "no-cache");
            InputStream in = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = reader.readLine()) != null) {
                data.append(line);
            }

            urlConnection.disconnect();
        } catch(Exception e) {
            Log.e("tg", "Error getting json from server:\n" + e.toString());
        }

        return data.toString();
    }

    public String addPoint(String userId, double latitude, double longitude) {
        return getJSON("/add/point?userId=" + userId + "&latitude=" + longitude + "&longitude=" + latitude);
    }

    public String ping(String userId, double latitude, double longitude) {
        return getJSON("/ping?userId=" + userId + "&latitude=" + longitude + "&longitude=" + latitude);
    }

    public String register(String name, double latitude, double longitude) {
        return getJSON("/user/register?name=" + name + "&latitude=" + longitude + "&longitude=" + latitude);
    }
}
