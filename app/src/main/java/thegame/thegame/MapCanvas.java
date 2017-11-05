package thegame.thegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Debug;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by teras on 5.11.17.
 */

public class MapCanvas extends View {
    private Paint lineColor;
    private MainActivity mainActivity;

    public MapCanvas(Context context, MainActivity mainActivity) {
        super(context);
        lineColor = new Paint(Color.BLUE);
        lineColor.setStrokeWidth(8);
        lineColor.setStyle(Paint.Style.STROKE);
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if(mainActivity.isGameRunning()) {
            lineColor.setColor(Color.MAGENTA);
            ArrayList<Pair<Double, Double>> opponentHistory = mainActivity.getOpponentHistory();

            for (int i = 0; i < opponentHistory.size() - 1; i++) {

                Pair<Double, Double> curLoc = mainActivity.getScreenCoords(opponentHistory.get(i).second, opponentHistory.get(i).first);
                Pair<Double, Double> nextLoc = mainActivity.getScreenCoords(opponentHistory.get(i + 1).second, opponentHistory.get(i + 1).first);
//                Log.d("tg", opponentHistory.get(i).second + " " + opponentHistory.get(i).first);
//                Log.d("tg", curLoc.first + " " + curLoc.second);

                canvas.drawLine((float) (double) curLoc.first, (float) (double) curLoc.second,
                        (float) (double) nextLoc.first, (float) (double) nextLoc.second, lineColor);
            }
        }
    }
}
