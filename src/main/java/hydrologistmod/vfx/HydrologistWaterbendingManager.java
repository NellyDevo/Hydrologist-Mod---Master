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
    //mask variables
    public static final float SPLINE_LENGTH = 1.0F; //in seconds
    public static final int LINE_WIDTH = (int)(20F * Settings.scale);
    private ArrayList<Coordinates> spline;
    private HashMap<Coordinates, Float> times;
    private ShapeRenderer shape;

    //GDX variables
    private FrameBuffer maskBuffer;
    private FrameBuffer tileBuffer;

    //water variables
    private static final Texture WATER_TILE_SHEET = new Texture("hydrologistmod/images/vfx/waterbending/Water_Tile_Sheet.png");
    private static TextureRegion[] waterTiles;
    private static final int WATER_ANIMATION_HORIZONTAL_FRAMES = 3;
    private static final int WATER_ANIMATION_VERTICAL_FRAMES = 7;
    private static final float WATER_ANIMATION_DURATION = 1.0f;
    private int waterTileCount;
    private float waterTimer;
    private int currentWaterTile;

    //ice variables
    private static final Texture ICE_TILE = new Texture("hydrologistmod/images/vfx/waterbending/Ice_Tile.png");
    private static TextureRegion iceTile;

    //steam variables
    private static final Texture STEAM_TILE = new Texture("hydrologistmod/images/vfx/waterbending/Steam_Tile.png");
    private static TextureRegion steamTile;
    private static final float STEAM_SCROLL_DURATION = 8.0F;
    private float steamTimer;

    //list of tiles to render and how to render them
    private ArrayList<RenderInstructions> renderInstructions;

    //transition effect variables
    private static final float TRANSITION_TIME = 0.5f;
    private AbstractCard.CardTags current;
    private AbstractCard.CardTags target;
    private float transitionTimer;

    //effect control
    public Vector2 override;

    //add to this to define tile modes
    private static HashMap<AbstractCard.CardTags, BehaviourPackage> effectsMap;

    public HydrologistWaterbendingManager() {
        //texture initialization
        waterTileCount = WATER_ANIMATION_HORIZONTAL_FRAMES * WATER_ANIMATION_VERTICAL_FRAMES;
        waterTiles = new TextureRegion[waterTileCount];
        int i = 0;
        for (int w = 0; w < WATER_ANIMATION_HORIZONTAL_FRAMES; ++w) {
            for (int h = 0; h < WATER_ANIMATION_VERTICAL_FRAMES; ++h) {
                waterTiles[i] = new TextureRegion(WATER_TILE_SHEET,
                        w * (WATER_TILE_SHEET.getWidth() / WATER_ANIMATION_HORIZONTAL_FRAMES),
                        h * (WATER_TILE_SHEET.getHeight() / WATER_ANIMATION_VERTICAL_FRAMES),
                        (WATER_TILE_SHEET.getWidth() / WATER_ANIMATION_HORIZONTAL_FRAMES),
                        (WATER_TILE_SHEET.getHeight() / WATER_ANIMATION_VERTICAL_FRAMES));
                ++i;
            }
        }
        iceTile = new TextureRegion(ICE_TILE);
        steamTile = new TextureRegion(STEAM_TILE);

        //Gdx initialization
        shape = new ShapeRenderer();
        maskBuffer = HydrologistMod.createBuffer();
        tileBuffer = HydrologistMod.createBuffer();

        //list initialization
        spline = new ArrayList<>();
        times = new HashMap<>();
        renderInstructions = new ArrayList<>();
        effectsMap = new HashMap<>();

        //transition effect initialization
        current = HydrologistTags.WATER;
        target = HydrologistTags.WATER;
        transitionTimer = 0.0f;

        //water effect declaration
        waterTimer = WATER_ANIMATION_DURATION;
        currentWaterTile = waterTileCount;
        RenderInstructor waterInstructor = () -> {
            renderInstructions.clear();
            renderInstructions.add(new RenderInstructions(waterTiles[currentWaterTile]));
            findGridPoints();
        };
        EffectUpdater waterUpdater = () -> {
            waterTimer += Gdx.graphics.getDeltaTime();
            if (waterTimer >= WATER_ANIMATION_DURATION) {
                waterTimer = 0;
            }
            currentWaterTile = (int)Math.floor((waterTimer / WATER_ANIMATION_DURATION) * waterTileCount);
        };
        effectsMap.put(HydrologistTags.WATER, new BehaviourPackage(waterUpdater, waterInstructor));

        //ice effect declaration
        RenderInstructor iceInstructor = () -> {
            renderInstructions.clear();
            renderInstructions.add(new RenderInstructions(iceTile));
            findGridPoints();
        };
        EffectUpdater iceUpdater = () -> {};
        effectsMap.put(HydrologistTags.ICE, new BehaviourPackage(iceUpdater, iceInstructor));

        //steam effect declaration
        steamTimer = STEAM_SCROLL_DURATION;
        RenderInstructor steamInstructor = () -> {
            renderInstructions.clear();
            renderInstructions.add(new RenderInstructions(steamTile, new Vector2(steamTile.getRegionWidth() * Settings.scale * (steamTimer / STEAM_SCROLL_DURATION), 0), Color.WHITE.cpy(), false, false, 1.0f, 1.0f));
            renderInstructions.add(new RenderInstructions(steamTile, new Vector2(steamTile.getRegionWidth() * Settings.scale * (steamTimer / STEAM_SCROLL_DURATION), 0), Color.WHITE.cpy(), false, true, 1.0f, 1.0f));
            findGridPoints();
        };
        EffectUpdater steamUpdater = () -> {
            steamTimer += Gdx.graphics.getDeltaTime();
            if (steamTimer >= STEAM_SCROLL_DURATION) {
                steamTimer = 0;
            }
        };
        effectsMap.put(HydrologistTags.STEAM, new BehaviourPackage(steamUpdater, steamInstructor));
    }

    public void update(Vector2 coords) {
        Coordinates point = new Coordinates();
        if (override != null) {
            point.x = override.x;
            point.y = override.y;
            override = null;
        } else {
            point.x = coords.x;
            point.y = coords.y;
        }
        spline.add(point);
        times.put(point, Gdx.graphics.getDeltaTime());
        float length = 0.0f;
        for (float time : times.values()) {
            length += time;
        }
        while (length > SPLINE_LENGTH) {
            Coordinates dot = spline.get(0);
            length -= times.get(dot);
            times.remove(dot);
            spline.remove(dot);
        }
        effectsMap.get(current).updater.update();
        if (transitionTimer > 0) {
            effectsMap.get(target).updater.update();
            transitionTimer -= Gdx.graphics.getDeltaTime();
        }
        if (transitionTimer <= 0) {
            current = target;
        }
    }

    public void render(SpriteBatch sb) {
        //create the mask
        sb.end();
        TextureRegion mask = createMask();

        //create the tiles
        HydrologistMod.beginBuffer(tileBuffer);
        sb.begin();
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        effectsMap.get(current).instructor.instruct();
        renderTiles(sb);

        //if necessary, overlay with the top set of tiles
        if (transitionTimer > 0) {
            float alpha = Interpolation.linear.apply(1F, 0F, transitionTimer / TRANSITION_TIME);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            effectsMap.get(target).instructor.instruct();
            for (RenderInstructions instruction : renderInstructions) {
                instruction.color.a = alpha;
            }
            renderTiles(sb);
        }

        //mask the tiles
        sb.setBlendFunction(0, GL20.GL_SRC_ALPHA);
        sb.setColor(Color.WHITE.cpy());
        sb.draw(mask, 0, 0);

        //render the effect on the main camera
        sb.end();
        tileBuffer.end();
        TextureRegion texture = HydrologistMod.getBufferTexture(tileBuffer);
        sb.begin();
        sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sb.draw(texture, 0,0);
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
            Vector2 start = spline.get(i).toVector();
            Vector2 mid = spline.get(i+1).toVector();
            shape.rectLine(start, mid, lineWidth);
            if ((i < spline.size() - 3)) {
                Vector2 end = spline.get(i + 2).toVector();
                shape.rectLine(start, end, lineWidth);
            }
        }
        shape.end();
        maskBuffer.end();
        return HydrologistMod.getBufferTexture(maskBuffer);
    }

    private void findGridPoints() {
        for (RenderInstructions instructions : renderInstructions) {
            Coordinates bottomLeft = spline.get(0).cpy();
            Coordinates topRight = spline.get(0).cpy();
            for (Coordinates point : spline) {
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
            float scaleWidth = instructions.texture.getRegionWidth() * Settings.scale * instructions.scaleX;
            float scaleHeight = instructions.texture.getRegionHeight() * Settings.scale * instructions.scaleY;
            float gridBottom = instructions.offSet.y;
            float gridTop = scaleHeight + instructions.offSet.y;
            if (gridBottom > bottomLeft.y) {
                gridBottom -= scaleHeight;
                gridTop -= scaleHeight;
            }
            float gridLeft = instructions.offSet.x;
            float gridRight = scaleWidth + instructions.offSet.x;
            if (gridLeft > bottomLeft.x) {
                gridLeft -= scaleWidth;
                gridRight -= scaleWidth;
            }
            int verticalTiles = 1;
            int horizontalTiles = 1;
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
            instructions.setInfo(gridBottomLeft, horizontalTiles, verticalTiles);
        }
    }

    private void renderTiles(SpriteBatch sb) {
        for (RenderInstructions instruction : renderInstructions) {
            sb.setColor(instruction.color);
            instruction.texture.flip(instruction.flipHorizontal, instruction.flipVertical);
            for (int x = 0; x < instruction.horizontalTiles; ++x) {
                for (int y = 0; y < instruction.verticalTiles; ++y) {
                    sb.draw(instruction.texture,
                            instruction.origin.x + (x * instruction.texture.getRegionWidth() * Settings.scale * instruction.scaleX),
                            instruction.origin.y + (y * instruction.texture.getRegionHeight() * Settings.scale * instruction.scaleY),
                            0, 0,
                            instruction.texture.getRegionWidth(),
                            instruction.texture.getRegionHeight(),
                            Settings.scale * instruction.scaleX,
                            Settings.scale * instruction.scaleY,
                            0);
                }
            }
            instruction.texture.flip(instruction.flipHorizontal, instruction.flipVertical);
        }
    }

    public void changeBackground(AbstractCard.CardTags newTag) {
        if (!effectsMap.containsKey(newTag)) {
            System.out.println("WATERBENDING_MANAGER: attempted to change to invalid tag");
            return;
        }
        if (newTag != target) {
            current = target;
            target = newTag;
            transitionTimer = TRANSITION_TIME;
        }
    }

    private interface RenderInstructor {
        void instruct();
    }

    private interface EffectUpdater {
        void update();
    }

    private static class BehaviourPackage {
        public EffectUpdater updater;
        public RenderInstructor instructor;

        public BehaviourPackage(EffectUpdater updater, RenderInstructor instructor) {
            this.updater = updater;
            this.instructor = instructor;
        }
    }

    private static class RenderInstructions {
        public TextureRegion texture;
        public Vector2 offSet;
        public boolean flipHorizontal;
        public boolean flipVertical;
        public float scaleX;
        public float scaleY;
        public Color color;
        public int horizontalTiles;
        public int verticalTiles;
        public Vector2 origin;

        public RenderInstructions(TextureRegion texture, Vector2 offSet, Color color, boolean flipHorizontal, boolean flipVertical, float scaleX, float scaleY) {
            this.texture = texture;
            this.offSet = offSet;
            this.flipHorizontal = flipHorizontal;
            this.flipVertical = flipVertical;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            if (this.scaleX == 0.0f) {
                System.out.println("ERROR: invalid waterbending X scale. Scale cannot be 0");
                this.scaleX = 1;
            }
            if (this.scaleY == 0.0F) {
                System.out.println("ERROR: invalid waterbending Y scale. Scale cannot be 0");
                this.scaleY = 1;
            }
            this.color = color;
        }

        public RenderInstructions(TextureRegion texture) {
            this(texture, new Vector2(0, 0), Color.WHITE.cpy(), false, false, 1, 1);
        }

        public void setInfo(Vector2 origin, int horizontalTiles, int verticalTiles) {
            this.origin = origin;
            this.horizontalTiles = horizontalTiles;
            this.verticalTiles = verticalTiles;
        }
    }

    //eat shit Vector2 and it's override of .equals
    public static class Coordinates {
        public float x;
        public float y;

        public Coordinates(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Coordinates() {
            this.x = 0;
            this.y = 0;
        }

        public Coordinates cpy() {
            return new Coordinates(x, y);
        }

        public Vector2 toVector() {
            return new Vector2(x, y);
        }

        public void add(int x, int y) {
            this.x += x;
            this.y += y;
        }
    }
}
