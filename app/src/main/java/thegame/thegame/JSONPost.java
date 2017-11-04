package thegame.thegame;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by teras on 4.11.17.
 */

public class JSONPost {
    private static final String serverURL = JSONRequest.serverURL;
    public String getJSON(String path, JSONObject jsonObject) {
        URL url;
        HttpURLConnection urlConnection;

        StringBuilder data = new StringBuilder();

        try {
            url = new URL(serverURL + path);
            Log.i("JSONRequest", "JSON Request to: " + url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            InputStream in = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while((line = reader.readLine()) != null) {
                data.append(line);
            }

            DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
            os.writeBytes(jsonObject.toString());
            os.flush();
            os.close();

            Log.d("tg", "response code " + urlConnection.getResponseCode());
            Log.d("tg", "response method " + urlConnection.getResponseMessage());

            urlConnection.disconnect();
        } catch(Exception e) {
            Log.e("JSONRequest", "Error getting json from server:\n" + e.toString());
        }

        return data.toString();
    }
}