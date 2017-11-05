package thegame.thegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        lineColor.setColor(Color.BLUE);
        ArrayList<Pair<Double, Double>> opponentHistory = mainActivity.getOpponentHistory();

        for(int i = 0; i < opponentHistory.size() - 1; i++) {
            Pair<Double, Double> curLoc = mainActivity.getScreenCoords(opponentHistory.get(i).first, opponentHistory.get(i).second);
            Pair<Double, Double> nextLoc = mainActivity.getScreenCoords(opponentHistory.get(i + 1).first, opponentHistory.get(i + 1).second);

            canvas.drawLine((float)(double)curLoc.first, (float)(double)curLoc.second,
                    (float)(double)nextLoc.first, (float)(double)nextLoc.second, lineColor);
        }
    }
}
