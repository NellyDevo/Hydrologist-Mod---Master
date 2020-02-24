package hydrologistmod.vfx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.HashMap;

public class TransmuteParticle extends AbstractGameEffect {
    private static final Texture ICE_TEXTURE = new Texture("hydrologistmod/images/vfx/IceSprite.png");
    private static final Texture WATER_TEXTURE = new Texture("hydrologistmod/images/vfx/WaterSprite.png");
    private static final Texture STEAM_TEXTURE = new Texture("hydrologistmod/images/vfx/SteamSprite.png");
    private static final TextureRegion[] ICE = new TextureRegion[6];
    private static final TextureRegion[] WATER = new TextureRegion[8];
    private static final TextureRegion[] STEAM = new TextureRegion[9];
    private static HashMap<ParticleType, TextureRegion[]> regions;
    private float x;
    private float y;
    private int index = 0;
    private float rotation;
    private float scale;
    private TextureRegion[] region;

    public TransmuteParticle(ParticleType type, float x, float y, float rotation, float scale) {
        region = regions.get(type);
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.scale = scale;
    }

    public static void initializeRegions() {
        //Ice texture: 6 x 1, 70x70 sections
        for (int i = 0; i < 6; ++i) {
            ICE[i] = new TextureRegion(ICE_TEXTURE, 70 * i, 0, 70, 70);
        }
        //Water texture: 4 x 2, 72x72 sections
        int wa = 0;
        for (int h = 0; h < 2; ++h) {
            for (int w = 0; w < 4; ++w) {
                WATER[wa] = new TextureRegion(WATER_TEXTURE, 72 * w, 72 * h, 72, 72);
                ++wa;
            }
        }
        //Steam texture: 3 x 3, 75x75 sections
        int s = 0;
        for (int h = 0; h < 3; ++h) {
            for (int w = 0; w < 3; ++w) {
                STEAM[s] = new TextureRegion(STEAM_TEXTURE, 75 * w, 75 * h, 75, 75);
                ++s;
            }
        }
        regions = new HashMap<>();
        regions.put(ParticleType.ICE, ICE);
        regions.put(ParticleType.WATER, WATER);
        regions.put(ParticleType.STEAM, STEAM);
    }

    public void update() {
        ++index;
        if (index == region.length) {
            isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        TextureRegion img = region[index];
        float w = img.getRegionWidth();
        float h = img.getRegionHeight();
        sb.draw(img, x - (w / 2.0f), y - (h / 2.0f), (w / 2.0f), (h / 2.0f), w, h, scale * Settings.scale, scale * Settings.scale, rotation);
    }

    public void dispose() {

    }

    public enum ParticleType {
        ICE,
        WATER,
        STEAM
    }
}
