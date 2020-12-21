package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;

import java.util.ArrayList;
import java.util.HashMap;

public class HydrologistWaterbendingManager {
    ArrayList<Vector2> spline = new ArrayList<>();
    HashMap<Vector2, Float> times = new HashMap<>();
    public static float SPLINE_LENGTH = 1.0F; //in seconds
    public static int LINE_WIDTH = (int)(20F * Settings.scale);
    private ShapeRenderer shape = new ShapeRenderer();
    private Color color = Color.CYAN.cpy();

    public void update(Vector2 coords) {
        Vector2 point = coords.cpy();
        spline.add(point);
        times.put(point, Gdx.graphics.getDeltaTime());
        float length = 0.0f;
        for (float time : times.values()) {
            length += time;
        }
        while (length > SPLINE_LENGTH) {
            Vector2 dot = spline.get(0);
            length -= times.get(dot);
            times.remove(dot);
            spline.remove(dot);
        }
    }

    public void render(SpriteBatch sb) {
        sb.end();
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(color);
        int lineWidth;
        for (int i = 0; i < spline.size() - 1; ++i) {
            if (i < LINE_WIDTH) {
                lineWidth = i + 1;
            } else if (i > spline.size() - LINE_WIDTH) {
                lineWidth = spline.size() - i;
            } else {
                lineWidth = LINE_WIDTH;
            }
            Vector2 start = spline.get(i);
            Vector2 mid = spline.get(i+1);
            shape.rectLine(start, mid, lineWidth);
            if ((i < spline.size() - 3)) {
                Vector2 end = spline.get(i + 2);
                shape.rectLine(start, end, lineWidth);
            }
        }
        shape.end();
        sb.begin();
    }
}
