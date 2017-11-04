package thegame.thegame;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by teras on 4.11.17.
 */

public class MapFiller extends AsyncTask<Void, Void, Bitmap> {

    private ImageView image;

    public MapFiller(ImageView image) {
        this.image = image;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;

        try {
            ImageRequest imageRequest = new ImageRequest();
            String request = "?c=58.3776%2C26.7290&" +
                    "z=" + MainActivity.ZOOM/18*19 + "&" +
                    "w=1080&h=840&" +
                    "f=0&" +
                    "t=1&" +
                    "app_id=g80UNADO9xYUvzX5nnVO&" +
                    "app_code=FNieWGLVaUYUCucll8gezA";
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
