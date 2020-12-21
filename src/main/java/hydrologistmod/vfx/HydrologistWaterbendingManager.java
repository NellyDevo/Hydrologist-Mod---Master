package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import hydrologistmod.HydrologistMod;

import java.util.ArrayList;
import java.util.HashMap;

public class HydrologistWaterbendingManager {
    ArrayList<Vector2> spline = new ArrayList<>();
    HashMap<Vector2, Float> times = new HashMap<>();
    public static float SPLINE_LENGTH = 1.0F; //in seconds
    public static int LINE_WIDTH = (int)(20F * Settings.scale);
    private ShapeRenderer shape = new ShapeRenderer();
    private Color color = Color.CYAN.cpy();
    private static final int WATER_TILE_WIDTH = 160;
    private static final int WATER_TILE_HEIGHT = 64;
    private static final int WATER_ANIMATION_HORIZONTAL_FRAMES = 3;
    private static final int WATER_ANIMATION_VERTICAL_FRAMES = 7;
    private static final int ICE_TILE_WIDTH = 512;
    private static final int ICE_TILE_HEIGHT = 512;
    private static final Texture WATER_TILE_SHEET = new Texture("hydrologistmod/images/vfx/waterbending/Water_Tile_Sheet.png");
    private static final Texture ICE_TILE = new Texture("hydrologistmod/images/vfx/waterbending/Ice_Tile.png");
    private static TextureRegion[] waterTiles;
    private static TextureRegion iceTile;
    private static GridInfo info;
    private int waterTileCount = WATER_ANIMATION_HORIZONTAL_FRAMES * WATER_ANIMATION_VERTICAL_FRAMES;
    private int currentWaterTile = waterTileCount;
    private FrameBuffer maskBuffer;
    private FrameBuffer tileBuffer;
    public Vector2 override;

    private TextureRegion renderThis = new TextureRegion();

    public HydrologistWaterbendingManager() {
        waterTiles = new TextureRegion[waterTileCount];
        int i = 0;
        for (int w = 0; w < WATER_ANIMATION_HORIZONTAL_FRAMES; ++w) {
            for (int h = 0; h < WATER_ANIMATION_VERTICAL_FRAMES; ++h) {
                waterTiles[i] = new TextureRegion(WATER_TILE_SHEET, w * WATER_TILE_WIDTH, h * WATER_TILE_HEIGHT, WATER_TILE_WIDTH, WATER_TILE_HEIGHT);
                ++i;
            }
        }
        iceTile = new TextureRegion(ICE_TILE);
        maskBuffer = HydrologistMod.createBuffer();
        tileBuffer = HydrologistMod.createBuffer();
    }

    public void update(Vector2 coords) {
        Vector2 point;
        if (override != null) {
            point = override;
            override = null;
        } else {
            point = coords.cpy();
        }
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
        info = findGridPoints(spline, WATER_TILE_WIDTH, WATER_TILE_HEIGHT);
        currentWaterTile++;
        if (currentWaterTile >= waterTileCount) {
            currentWaterTile = 0;
        }
        renderThis = waterTiles[currentWaterTile];
    }

    private GridInfo findGridPoints(ArrayList<Vector2> spline, int tileWidth, int tileHeight) {
        Vector2 bottomLeft = spline.get(0).cpy();
        Vector2 topRight = spline.get(0).cpy();
        for (Vector2 point : spline) {
            if (point.x < bottomLeft.x) {
                bottomLeft.x = point.x;
            }
            if (point.y < bottomLeft.y) {
                bottomLeft.y = point.y;
            }
            if (point.x > topRight.x) {
                topRight.x = point.x;
            }
            if (point.y > topRight.y) {
                topRight.y = point.y;
            }
        }
        bottomLeft.add(-LINE_WIDTH, -LINE_WIDTH);
        topRight.add(LINE_WIDTH, LINE_WIDTH);
        float scaleWidth = tileWidth * Settings.scale;
        float scaleHeight = tileHeight * Settings.scale;
        float gridBottom = 0;
        float gridLeft = 0;
        int verticalTiles = 1;
        int horizontalTiles = 1;
        float gridTop = scaleHeight;
        float gridRight = scaleWidth;
        while (gridTop < topRight.y) {
            gridTop += scaleHeight;
            verticalTiles++;
        }
        while (gridRight < topRight.x) {
            gridRight += scaleWidth;
            horizontalTiles++;
        }
        while (gridBottom + scaleHeight < bottomLeft.y) {
            gridBottom += scaleHeight;
            verticalTiles--;
        }
        while (gridLeft + scaleWidth < bottomLeft.x) {
            gridLeft += scaleWidth;
            horizontalTiles--;
        }
        Vector2 gridBottomLeft = new Vector2(gridLeft, gridBottom);
        return new GridInfo(gridBottomLeft, horizontalTiles, verticalTiles);
    }

    public void render(SpriteBatch sb) {
        sb.end();
        TextureRegion mask = createMask();
        HydrologistMod.beginBuffer(tileBuffer);
        sb.begin();
        renderTiles(sb);
        sb.setBlendFunction(0, GL20.GL_SRC_ALPHA);
        sb.draw(mask, 0, 0);
        sb.end();
        tileBuffer.end();
        TextureRegion texture = HydrologistMod.getBufferTexture(tileBuffer);
        sb.begin();
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sb.draw(texture, 0,0);
    }

    private TextureRegion createMask() {
        HydrologistMod.beginBuffer(maskBuffer);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK.cpy());
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
        maskBuffer.end();
        TextureRegion retVal = new TextureRegion(maskBuffer.getColorBufferTexture());
        retVal.flip(false, true);
        return retVal;
    }

    private void renderTiles(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        for (int x = 0; x < info.horizontalTiles; ++x) {
            for (int y = 0; y < info.verticalTiles; ++y) {
                sb.draw(renderThis,
                        info.origin.x + (x * renderThis.getRegionWidth() * Settings.scale),
                        info.origin.y + (y * renderThis.getRegionHeight() * Settings.scale),
                        renderThis.getRegionWidth() / 2F,
                        renderThis.getRegionHeight() / 2F,
                        renderThis.getRegionWidth(),
                        renderThis.getRegionHeight(),
                        Settings.scale,
                        Settings.scale,
                        0);
            }
        }
    }

    private static class GridInfo {
        public int horizontalTiles;
        public int verticalTiles;
        public Vector2 origin;

        public GridInfo(Vector2 origin, int horizontalTiles, int verticalTiles) {
            this.origin = origin;
            this.horizontalTiles = horizontalTiles;
            this.verticalTiles = verticalTiles;
        }
    }
}
