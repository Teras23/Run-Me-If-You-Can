package thegame.thegame;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.here.android.mpa.common.GeoCoordinate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by teras on 4.11.17.
 */

public class MapFiller extends AsyncTask<Void, Void, Bitmap> {

    final static double size = 0.0025;
    static Pair<Double, Double> lastCenter = new Pair<>(0.0, 0.0);

    private MainActivity mainActivity;
    private GeoCoordinate coordinate;

    public MapFiller(MainActivity mainActivity, GeoCoordinate coords) {
        this.mainActivity = mainActivity;
        coordinate = coords;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;

        try {
            double longitude = coordinate.getLongitude();
            double latitude = coordinate.getLatitude();

            lastCenter = new Pair<>(longitude, latitude);

            ImageRequest imageRequest = new ImageRequest();
            String request = "?bbox=" + (longitude + size) + "," + (latitude - size) + "," + (longitude - size) + "," + (latitude + size) + "&" +
                    "w=" + mainActivity.getMapImageSize().first + "&" + "" +
                    "h=" +  + mainActivity.getMapImageSize().second + "&" +
                    "f=0&" + //PNG fomat
                    "t=1&" + //Satellite
                    "app_id=g80UNADO9xYUvzX5nnVO&" +
                    "app_code=FNieWGLVaUYUCucll8gezA";
            Log.d("tg", request);
            bitmap = imageRequest.getImage(request);
        } catch (Exception e) {
            Log.e("tg", "Error reading JSON:\n" + e.toString());
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null) {
            mainActivity.startGame(bitmap);
        }
    }
}