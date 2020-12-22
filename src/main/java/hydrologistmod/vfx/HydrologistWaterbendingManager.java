package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import hydrologistmod.HydrologistMod;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;
import java.util.HashMap;

public class HydrologistWaterbendingManager {
    private ArrayList<Vector2> spline = new ArrayList<>();
    private HashMap<Vector2, Float> times = new HashMap<>();
    public static float SPLINE_LENGTH = 1.0F; //in seconds
    public static int LINE_WIDTH = (int)(20F * Settings.scale);
    private ShapeRenderer shape = new ShapeRenderer();
    private static final int WATER_TILE_WIDTH = 160;
    private static final int WATER_TILE_HEIGHT = 64;
    private static final int WATER_ANIMATION_HORIZONTAL_FRAMES = 3;
    private static final int WATER_ANIMATION_VERTICAL_FRAMES = 7;
    private static final int ICE_TILE_WIDTH = 64;
    private static final int ICE_TILE_HEIGHT = 64;
    private static final Texture WATER_TILE_SHEET = new Texture("hydrologistmod/images/vfx/waterbending/Water_Tile_Sheet.png");
    private static final Texture ICE_TILE = new Texture("hydrologistmod/images/vfx/waterbending/Ice_Tile.png");
    private static TextureRegion[] waterTiles;
    private static TextureRegion iceTile;
    private int waterTileCount = WATER_ANIMATION_HORIZONTAL_FRAMES * WATER_ANIMATION_VERTICAL_FRAMES;
    private int currentWaterTile = waterTileCount;
    private FrameBuffer maskBuffer;
    private FrameBuffer tileBuffer;
    public Vector2 override;
    private AbstractCard.CardTags current = HydrologistTags.WATER;
    private AbstractCard.CardTags target = HydrologistTags.WATER;
    private static final float TRANSITION_TIME = 0.5f;
    private float transitionTimer = 0.0f;
    private static HashMap<AbstractCard.CardTags, TextureGetter> textureMap;
    private int currentTileWidth;
    private int currentTileHeight;

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
        textureMap = new HashMap<>();
        textureMap.put(HydrologistTags.WATER, () -> {
            currentTileWidth = WATER_TILE_WIDTH;
            currentTileHeight = WATER_TILE_HEIGHT;
            return waterTiles[currentWaterTile];
        });
        textureMap.put(HydrologistTags.ICE, () -> {
            currentTileWidth = ICE_TILE_WIDTH;
            currentTileHeight = ICE_TILE_HEIGHT;
            return iceTile;
        });
        textureMap.put(HydrologistTags.STEAM, () -> {
            System.out.println("What are you doing here? This has yet to be implemented");
            return null; //TODO
        });
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
        updateWater();
        updateSteam();
        if (transitionTimer > 0) {
            transitionTimer -= Gdx.graphics.getDeltaTime();
        }
        if (transitionTimer <= 0) {
            current = target;
        }
    }

    private void updateWater() {
        currentWaterTile++;
        if (currentWaterTile >= waterTileCount) {
            currentWaterTile = 0;
        }
    }

    private void updateSteam() {

    }

    private GridInfo findGridPoints() {
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
        float scaleWidth = currentTileWidth * Settings.scale;
        float scaleHeight = currentTileHeight * Settings.scale;
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
        //create the mask
        sb.end();
        TextureRegion mask = createMask();

        //create the tiles
        Color c = Color.WHITE.cpy();
        TextureRegion renderThis = textureMap.get(current).getTexture();
        HydrologistMod.beginBuffer(tileBuffer);
        sb.begin();
        sb.setColor(c);
        sb.disableBlending();
        renderTiles(sb, renderThis, findGridPoints());

        //if necessary, overlay with the top set of tiles
        if (transitionTimer > 0) {
            renderThis = textureMap.get(target).getTexture();
            sb.setColor(new Color(1f, 1f, 1f, Interpolation.linear.apply(1F, 0F, transitionTimer / TRANSITION_TIME)));
            sb.enableBlending();
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            renderTiles(sb, renderThis, findGridPoints());
        }

        //mask the tiles
        sb.enableBlending();
        sb.setBlendFunction(0, GL20.GL_SRC_ALPHA);
        sb.setColor(c);
        sb.draw(mask, 0, 0);
        sb.end();
        tileBuffer.end();
        TextureRegion texture = HydrologistMod.getBufferTexture(tileBuffer);

        //render the effect on the main camera
        sb.begin();
        sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sb.draw(texture, 0,0);
        sb.setColor(Color.WHITE);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
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
        return HydrologistMod.getBufferTexture(maskBuffer);
    }

    private void renderTiles(SpriteBatch sb, TextureRegion renderThis, GridInfo info) {
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

    public void changeBackground(AbstractCard.CardTags newTag) {
        if (!HydrologistMod.subTypes.contains(newTag)) {
            System.out.println("WATERBENDING_MANAGER: attempted to change to invalid tag");
            return;
        }
        if (newTag != target) {
            current = target;
            target = newTag;
            transitionTimer = TRANSITION_TIME;
        }
    }

    private interface TextureGetter {
        TextureRegion getTexture();
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
