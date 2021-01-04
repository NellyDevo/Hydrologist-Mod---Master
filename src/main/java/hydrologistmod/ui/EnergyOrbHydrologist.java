package hydrologistmod.ui;

import basemod.abstracts.CustomEnergyOrb;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import hydrologistmod.HydrologistMod;
import hydrologistmod.patches.HydrologistTags;

import java.util.HashMap;

public class EnergyOrbHydrologist extends CustomEnergyOrb {
    private static TextureRegion HYDROLOGIST_ORB_BACKGROUND = new TextureRegion(new Texture("hydrologistmod/images/char/orb/background.png"));
    private static TextureRegion HYDROLOGIST_ORB_LAYER_2 = new TextureRegion(new Texture("hydrologistmod/images/char/orb/layer2.png"));
    private static TextureRegion HYDROLOGIST_ORB_LAYER_3 = new TextureRegion(new Texture("hydrologistmod/images/char/orb/layer3.png"));
    private static TextureRegion HYDROLOGIST_ORB_LAYER_4 = new TextureRegion(new Texture("hydrologistmod/images/char/orb/layer4.png"));
    private static TextureRegion HYDROLOGIST_ORB_LAYER_5 = new TextureRegion(new Texture("hydrologistmod/images/char/orb/layer5.png"));
    private static TextureRegion HYDROLOGIST_ORB_LAYER_2D = new TextureRegion(new Texture("hydrologistmod/images/char/orb/layer2d.png"));
    private static TextureRegion HYDROLOGIST_ORB_LAYER_3D = new TextureRegion(new Texture("hydrologistmod/images/char/orb/layer3d.png"));
    private static TextureRegion HYDROLOGIST_ORB_LAYER_4D = new TextureRegion(new Texture("hydrologistmod/images/char/orb/layer4d.png"));
    private static TextureRegion HYDROLOGIST_ORB_LAYER_5D = new TextureRegion(new Texture("hydrologistmod/images/char/orb/layer5d.png"));
    private static TextureRegion HYDROLOGIST_ORB_MASK = new TextureRegion(new Texture("hydrologistmod/images/char/orb/mask.png"));
    private static TextureRegion HYDROLOGIST_ORB_LAYER_6 = new TextureRegion(new Texture("hydrologistmod/images/char/orb/border.png"));
    private static float SPIN_OFFSET_X = 32.0f * Settings.scale;
    private static float SPIN_OFFSET_Y = -32.0f * Settings.scale;
    private static float OFFSET_X = 0.0f * Settings.scale;
    private static float OFFSET_Y = 20.0f * Settings.scale;
    private static float ORB_SCALE = 1.15f * Settings.scale;

    private FrameBuffer fbo;
    private float angle2;
    private float angle3;
    private float angle4;
    private float angle5;

    private static Color WATER_COLOR = Color.BLUE.cpy();
    private static Color ICE_COLOR = Color.CYAN.cpy();
    private static Color STEAM_COLOR = Color.LIGHT_GRAY.cpy();
    private static HashMap<AbstractCard.CardTags, Color> colorsMap;
    private static HashMap<AbstractCard.CardTags, Float> speedsMap;
    private static float TRANSITION_TIME = 1.0f;
    private AbstractCard.CardTags current = HydrologistTags.WATER;
    private AbstractCard.CardTags target = HydrologistTags.WATER;
    private float transitionTimer = 0.0f;
    private Color color = WATER_COLOR.cpy();

    public EnergyOrbHydrologist() {
        super(null, null, null);
        fbo = HydrologistMod.createBuffer();

        orbVfx = ImageMaster.loadImage("hydrologistmod/images/char/orb/vfx.png");

        if (colorsMap == null) {
            colorsMap = new HashMap<>();
            colorsMap.put(HydrologistTags.ICE, ICE_COLOR);
            colorsMap.put(HydrologistTags.WATER, WATER_COLOR);
            colorsMap.put(HydrologistTags.STEAM, STEAM_COLOR);
        }
        if (speedsMap == null) {
            speedsMap = new HashMap<>();
            speedsMap.put(HydrologistTags.ICE, 0.5f);
            speedsMap.put(HydrologistTags.WATER, 1.0f);
            speedsMap.put(HydrologistTags.STEAM, 1.5f);
        }
    }

    @Override
    public void updateOrb(int energyCount) {
        float time = Gdx.graphics.getDeltaTime();
        float multiplier;

        //update color and spin speed
        if (transitionTimer > 0.0f) {
            color.r = Interpolation.linear.apply(colorsMap.get(target).r, colorsMap.get(current).r, transitionTimer / TRANSITION_TIME);
            color.g = Interpolation.linear.apply(colorsMap.get(target).g, colorsMap.get(current).g, transitionTimer / TRANSITION_TIME);
            color.b = Interpolation.linear.apply(colorsMap.get(target).b, colorsMap.get(current).b, transitionTimer / TRANSITION_TIME);
            multiplier = Interpolation.linear.apply(speedsMap.get(target), speedsMap.get(current),transitionTimer / TRANSITION_TIME);
            transitionTimer -= time;
        } else {
            current = target;
            color = colorsMap.get(current).cpy();
            multiplier = speedsMap.get(current);
        }

        if (energyCount == 0) {
            angle5 += time * 5.0f * multiplier;
            angle4 += time * -5.0f * multiplier;
            angle3 += time * 8.0f * multiplier;
            angle2 += time * -8.0f * multiplier;
        } else {
            angle5 += time * 20.0f * multiplier;
            angle4 += time * -20.0f * multiplier;
            angle3 += time * 40.0f * multiplier;
            angle2 += time * -40.0f * multiplier;
        }
    }

    @Override
    public void renderOrb(SpriteBatch sb, boolean enabled, float current_x, float current_y) {
        sb.end();

        //render pre-mask in buffer
        HydrologistMod.beginBuffer(fbo);
        sb.begin();
        sb.setColor(color);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sb.draw(HYDROLOGIST_ORB_BACKGROUND, current_x - (HYDROLOGIST_ORB_BACKGROUND.getRegionWidth() / 2.0f) + OFFSET_X, current_y - (HYDROLOGIST_ORB_BACKGROUND.getRegionHeight() / 2.0f) + OFFSET_Y, HYDROLOGIST_ORB_BACKGROUND.getRegionWidth() / 2.0f, HYDROLOGIST_ORB_BACKGROUND.getRegionHeight() / 2.0f, HYDROLOGIST_ORB_BACKGROUND.getRegionWidth(), HYDROLOGIST_ORB_BACKGROUND.getRegionHeight(), ORB_SCALE, ORB_SCALE, 0.0f);
        if (enabled) {
            sb.draw(HYDROLOGIST_ORB_LAYER_2, current_x - (HYDROLOGIST_ORB_LAYER_2.getRegionWidth() / 2.0f) + SPIN_OFFSET_X + OFFSET_X, current_y - (HYDROLOGIST_ORB_LAYER_2.getRegionHeight() / 2.0f) + SPIN_OFFSET_Y + OFFSET_Y, HYDROLOGIST_ORB_LAYER_2.getRegionWidth() / 2.0f, HYDROLOGIST_ORB_LAYER_2.getRegionHeight() / 2.0f, HYDROLOGIST_ORB_LAYER_2.getRegionWidth(), HYDROLOGIST_ORB_LAYER_2.getRegionHeight(), ORB_SCALE, ORB_SCALE, angle2);
            sb.draw(HYDROLOGIST_ORB_LAYER_3, current_x - (HYDROLOGIST_ORB_LAYER_3.getRegionWidth() / 2.0f) + SPIN_OFFSET_X + OFFSET_X, current_y - (HYDROLOGIST_ORB_LAYER_3.getRegionHeight() / 2.0f) + SPIN_OFFSET_Y + OFFSET_Y, HYDROLOGIST_ORB_LAYER_3.getRegionWidth() / 2.0f, HYDROLOGIST_ORB_LAYER_3.getRegionHeight() / 2.0f, HYDROLOGIST_ORB_LAYER_3.getRegionWidth(), HYDROLOGIST_ORB_LAYER_3.getRegionHeight(), ORB_SCALE, ORB_SCALE, angle3);
            sb.draw(HYDROLOGIST_ORB_LAYER_4, current_x - (HYDROLOGIST_ORB_LAYER_4.getRegionWidth() / 2.0f) + SPIN_OFFSET_X + OFFSET_X, current_y - (HYDROLOGIST_ORB_LAYER_4.getRegionHeight() / 2.0f) + SPIN_OFFSET_Y + OFFSET_Y, HYDROLOGIST_ORB_LAYER_4.getRegionWidth() / 2.0f, HYDROLOGIST_ORB_LAYER_4.getRegionHeight() / 2.0f, HYDROLOGIST_ORB_LAYER_4.getRegionWidth(), HYDROLOGIST_ORB_LAYER_4.getRegionHeight(), ORB_SCALE, ORB_SCALE, angle4);
            sb.draw(HYDROLOGIST_ORB_LAYER_5, current_x - (HYDROLOGIST_ORB_LAYER_5.getRegionWidth() / 2.0f) + SPIN_OFFSET_X + OFFSET_X, current_y - (HYDROLOGIST_ORB_LAYER_5.getRegionHeight() / 2.0f) + SPIN_OFFSET_Y + OFFSET_Y, HYDROLOGIST_ORB_LAYER_5.getRegionWidth() / 2.0f, HYDROLOGIST_ORB_LAYER_5.getRegionHeight() / 2.0f, HYDROLOGIST_ORB_LAYER_5.getRegionWidth(), HYDROLOGIST_ORB_LAYER_5.getRegionHeight(), ORB_SCALE, ORB_SCALE, angle5);
        } else {
            sb.draw(HYDROLOGIST_ORB_LAYER_2D, current_x - (HYDROLOGIST_ORB_LAYER_2D.getRegionWidth() / 2.0f) + SPIN_OFFSET_X + OFFSET_X, current_y - (HYDROLOGIST_ORB_LAYER_2D.getRegionHeight() / 2.0f) + SPIN_OFFSET_Y + OFFSET_Y, HYDROLOGIST_ORB_LAYER_2D.getRegionWidth() / 2.0f, HYDROLOGIST_ORB_LAYER_2D.getRegionHeight() / 2.0f, HYDROLOGIST_ORB_LAYER_2D.getRegionWidth(), HYDROLOGIST_ORB_LAYER_2D.getRegionHeight(), ORB_SCALE, ORB_SCALE, angle2);
            sb.draw(HYDROLOGIST_ORB_LAYER_3D, current_x - (HYDROLOGIST_ORB_LAYER_3D.getRegionWidth() / 2.0f) + SPIN_OFFSET_X + OFFSET_X, current_y - (HYDROLOGIST_ORB_LAYER_3D.getRegionHeight() / 2.0f) + SPIN_OFFSET_Y + OFFSET_Y, HYDROLOGIST_ORB_LAYER_3D.getRegionWidth() / 2.0f, HYDROLOGIST_ORB_LAYER_3D.getRegionHeight() / 2.0f, HYDROLOGIST_ORB_LAYER_3D.getRegionWidth(), HYDROLOGIST_ORB_LAYER_3D.getRegionHeight(), ORB_SCALE, ORB_SCALE, angle3);
            sb.draw(HYDROLOGIST_ORB_LAYER_4D, current_x - (HYDROLOGIST_ORB_LAYER_4D.getRegionWidth() / 2.0f) + SPIN_OFFSET_X + OFFSET_X, current_y - (HYDROLOGIST_ORB_LAYER_4D.getRegionHeight() / 2.0f) + SPIN_OFFSET_Y + OFFSET_Y, HYDROLOGIST_ORB_LAYER_4D.getRegionWidth() / 2.0f, HYDROLOGIST_ORB_LAYER_4D.getRegionHeight() / 2.0f, HYDROLOGIST_ORB_LAYER_4D.getRegionWidth(), HYDROLOGIST_ORB_LAYER_4D.getRegionHeight(), ORB_SCALE, ORB_SCALE, angle4);
            sb.draw(HYDROLOGIST_ORB_LAYER_5D, current_x - (HYDROLOGIST_ORB_LAYER_5D.getRegionWidth() / 2.0f) + SPIN_OFFSET_X + OFFSET_X, current_y - (HYDROLOGIST_ORB_LAYER_5D.getRegionHeight() / 2.0f) + SPIN_OFFSET_Y + OFFSET_Y, HYDROLOGIST_ORB_LAYER_5D.getRegionWidth() / 2.0f, HYDROLOGIST_ORB_LAYER_5D.getRegionHeight() / 2.0f, HYDROLOGIST_ORB_LAYER_5D.getRegionWidth(), HYDROLOGIST_ORB_LAYER_5D.getRegionHeight(), ORB_SCALE, ORB_SCALE, angle5);
        }
        sb.setBlendFunction(0, GL20.GL_SRC_ALPHA);
        sb.setColor(new Color(1, 1, 1, 1));
        sb.draw(HYDROLOGIST_ORB_MASK, current_x - (HYDROLOGIST_ORB_MASK.getRegionWidth() / 2.0f) + OFFSET_X, current_y - (HYDROLOGIST_ORB_MASK.getRegionHeight() / 2.0f) + OFFSET_Y, (HYDROLOGIST_ORB_MASK.getRegionWidth() / 2.0f), (HYDROLOGIST_ORB_MASK.getRegionHeight() / 2.0f), HYDROLOGIST_ORB_MASK.getRegionWidth(), HYDROLOGIST_ORB_MASK.getRegionHeight(), ORB_SCALE, ORB_SCALE, 0.0f);
        sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        fbo.end();
        sb.end();
        TextureRegion texture = HydrologistMod.getBufferTexture(fbo);

        //render whole
        sb.begin();
        sb.setColor(Color.WHITE);
        sb.draw(texture, 0.0f, 0.0f);
        sb.draw(HYDROLOGIST_ORB_LAYER_6, current_x - (HYDROLOGIST_ORB_LAYER_6.getRegionWidth() / 2.0f) + OFFSET_X, current_y - (HYDROLOGIST_ORB_LAYER_6.getRegionHeight() / 2.0f) + OFFSET_Y, (HYDROLOGIST_ORB_LAYER_6.getRegionWidth() / 2.0f), (HYDROLOGIST_ORB_LAYER_6.getRegionHeight() / 2.0f), HYDROLOGIST_ORB_LAYER_6.getRegionWidth(), HYDROLOGIST_ORB_LAYER_6.getRegionHeight(), ORB_SCALE, ORB_SCALE, 0.0f);
    }

    public void changeColor(AbstractCard.CardTags tag) {
        if (!colorsMap.containsKey(tag)) {
            System.out.println("WATERBENDING_MANAGER: attempted to change to invalid tag");
            return;
        }
        if (tag != target) {
            current = target;
            target = tag;
            transitionTimer = TRANSITION_TIME;
        }
    }
}
