package thegame.thegame;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

/**
 * Created by teras on 4.11.17.
 */

public class MapFiller extends AsyncTask<Void, Void, Bitmap> {

    final static double size = 0.0075;
    static Pair<Double, Double> lastCenter = new Pair<>(0.0, 0.0);

    private ImageView image;

    public MapFiller(ImageView image) {
        this.image = image;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;

        try {
            double centerX = 58.3734098;
            double centerY = 26.7061349;

            lastCenter = new Pair<>(centerX, centerY);

            ImageRequest imageRequest = new ImageRequest();
            String request = "?bbox=" + (centerX + size) + "," + (centerY - size) + "," + (centerX - size) + "," + (centerY + size) + "&" +
                    "w=1080&h=840&" +
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
            image.setImageBitmap(bitmap);
        }
    }
}
