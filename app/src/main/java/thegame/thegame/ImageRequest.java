package thegame.thegame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageRequest {
    static String tr;
    static String bl;

    public Bitmap getImage(String path) {
        String serverUrl = "https://image.maps.cit.api.here.com/mia/1.6/mapview";
        URL url;
        HttpURLConnection urlConnection;

        try {
            url = new URL(serverUrl + path);
            Log.i("ImageRequest", "Image Request to: " + url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();

            tr = urlConnection.getHeaderField("Viewport-Top-Right");
            bl = urlConnection.getHeaderField("Viewport-Bottom-Left");

            Log.d("tg", tr + " " + bl);

            Bitmap image = BitmapFactory.decodeStream(in);

            urlConnection.disconnect();
            return image;
        } catch(Exception e) {
            Log.e("ImageRequest", "Error getting image from server:\n" + e.toString());
        }
        return null;
    }
}