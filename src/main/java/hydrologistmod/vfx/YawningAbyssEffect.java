package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import hydrologistmod.HydrologistMod;
import hydrologistmod.actions.YawningAbyssAction;

public class YawningAbyssEffect extends AbstractGameEffect {
    private static final Texture CRACK_TEXTURE = new Texture("hydrologistmod/images/vfx/YawningAbyssCrack.png");
    private static final Texture MASK_TEXTURE = new Texture("hydrologistmod/images/vfx/YawningAbyssMask.png");
    private static final Texture EFFECT_TEXTURE = new Texture("hydrologistmod/images/vfx/YawningAbyssEffect.png");
    private static final int CRACK_WIDTH = 568;
    private static final int CRACK_HEIGHT = 240;
    private static final int MASK_WIDTH = 568;
    private static final int MASK_HEIGHT = 280;
    private static final int CRACK_HORIZONTAL = 5;
    private static final int CRACK_VERTICAL = 5;
    private static final int MASK_HORIZONTAL = 5;
    private static final int MASK_VERTICAL = 5;
    private static TextureRegion[] crackAnimation = null;
    private static TextureRegion[] maskAnimation = null;
    private static TextureRegion effect = null;
    private static FrameBuffer fbo = null;
    private static OrthographicCamera camera = null;

    private static final int BASE_FPS = 5;
    private static final int WIND_UP_FRAMES = 4;

    private float x;
    private float y;
    private YawningAbyssAction parent;
    private int currentFrame = 0;
    private int nextFrame = 1;
    private Stage stage = Stage.EXPANDING;
    private static final float EFFECT_DURATION = 1.0f;
    private float frameDuration = 0.0f;
    private float effectDuration = EFFECT_DURATION;
    private float currentFrameAlpha = 1.0f;
    private float effectScale;
    private static final float EFFECT_FADE_OUT = 1.0f;
    private float effectFadeOut = 0.0f;

    public YawningAbyssEffect(float x, float y, YawningAbyssAction parent, float scale) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        effectScale = scale * Settings.scale;
    }

    @Override
    public void update() {
        float time = Gdx.graphics.getDeltaTime();
        effectDuration -= time;
        if (effectDuration < 0) {
            effectDuration += EFFECT_DURATION;
        }
        frameDuration += time;
        if (stage == Stage.SHRINKING) {
            frameDuration += time;
        }
        currentFrameAlpha = 1.0f - (frameDuration / (1.0f / BASE_FPS));
        if (frameDuration > 1.0f / BASE_FPS) {
            currentFrameAlpha = 1.0f;
            frameDuration = 0;
            currentFrame = nextFrame;
            if (currentFrame >= WIND_UP_FRAMES && !parent.doingDamage) {
                parent.startDamage();
            }
            switch (stage) {
                case EXPANDING:
                    effectFadeOut += time;
                    if (effectFadeOut > EFFECT_FADE_OUT) {
                        effectFadeOut = EFFECT_FADE_OUT;
                    }
                    if (nextFrame + 1 < CRACK_HORIZONTAL * CRACK_VERTICAL) {
                        ++nextFrame;
                    } else {
                        stage = Stage.STABLE;
                    }
                    break;
                case SHRINKING:
                    effectFadeOut -= time;
                    if (effectFadeOut < 0.0f) {
                        effectFadeOut = 0.0f;
                    }
                    if (nextFrame - 1 >= 0) {
                        --nextFrame;
                    } else {
                        isDone = true;
                    }
                    break;
            }
        }
        currentFrameAlpha = 1.0f - (frameDuration / (1.0f / BASE_FPS));
    }

    @Override
    public void render(SpriteBatch sb) {
        //render the crack at x/y
        Color frame1Color = new Color(1.0f, 1.0f, 1.0f, currentFrameAlpha);
        Color frame2Color = new Color(1.0f, 1.0f, 1.0f, 1.0f - currentFrameAlpha);

        sb.setColor(frame1Color);
        TextureRegion r = crackAnimation[currentFrame];
        float crackWidth = r.getRegionWidth();
        float crackHeight = r.getRegionHeight();
        sb.draw(r, x - (crackWidth / 2.0f), y - (crackHeight / 2.0f),
                crackWidth / 2.0f, crackHeight / 2.0f,
                crackWidth, crackHeight,
                effectScale, effectScale, 0.0f);

        sb.setColor(frame2Color);
        r = crackAnimation[nextFrame];
        sb.draw(r, x - (crackWidth / 2.0f), y - (crackHeight / 2.0f),
                crackWidth / 2.0f, crackHeight / 2.0f,
                crackWidth, crackHeight,
                effectScale, effectScale, 0.0f);

        if (effectFadeOut > 0.0f) {
            //turn on the frame buffer, change camera
            sb.end();
            HydrologistMod.beginBuffer(fbo);
            Matrix4 tmp = sb.getProjectionMatrix();
            sb.setProjectionMatrix(camera.combined);
            sb.begin();

            //render aurora borealis in the buffer
            //TODO: find out how to apply a color oscillating shader
            sb.setColor(Color.WHITE.cpy());
            r = effect;
            float scaleY = 1.0f - (((EFFECT_DURATION / 2.0f) / Math.abs(effectDuration - (EFFECT_DURATION / 2.0f))) / 5.0f);
            sb.draw(r, 0.0f, 0.0f,
                    crackWidth / 2.0f, crackHeight / 2.0f,
                    r.getRegionWidth(), r.getRegionHeight(),
                    1.0f, scaleY * (effectFadeOut / EFFECT_FADE_OUT), 0.0f);

            //mask borealis
            r = maskAnimation[currentFrame];
            sb.setBlendFunction(0, GL20.GL_SRC_ALPHA);
            sb.draw(r, 0.0f, 0.0f);

            //turn off frame buffer, capture texture, change camera back
            sb.end();
            fbo.end();
            TextureRegion texture = HydrologistMod.getBufferTexture(fbo);
            sb.setProjectionMatrix(tmp);
            sb.begin();

            //render captured texture at x/y
            sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
            sb.draw(texture, x, y,
                    crackWidth / 2.0f, crackHeight / 2.0f,
                    texture.getRegionWidth(), texture.getRegionHeight(),
                    effectScale, effectScale, 0.0f);

            //reset blend function
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    @Override
    public void dispose() {

    }

    public static void initializeRegions() {
        crackAnimation = new TextureRegion[CRACK_VERTICAL * CRACK_HORIZONTAL];
        int i = 0;
        for (int h = 0; h < CRACK_VERTICAL; ++h) {
            for (int w = 0; w < CRACK_HORIZONTAL; ++w) {
                crackAnimation[i++] = new TextureRegion(CRACK_TEXTURE, CRACK_WIDTH * w, CRACK_HEIGHT * h, CRACK_WIDTH, CRACK_HEIGHT);
            }
        }
        maskAnimation = new TextureRegion[MASK_VERTICAL * MASK_HORIZONTAL];
        i = 0;
        for (int h = 0; h < MASK_VERTICAL; ++h) {
            for (int w = 0; w < MASK_HORIZONTAL; ++w) {
                maskAnimation[i++] = new TextureRegion(MASK_TEXTURE, MASK_WIDTH * w, MASK_HEIGHT * h, MASK_WIDTH, MASK_HEIGHT);
            }
        }
        effect = new TextureRegion(EFFECT_TEXTURE);
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, MASK_WIDTH, MASK_HEIGHT, false, false);
        camera = new OrthographicCamera(MASK_WIDTH, MASK_HEIGHT);
        camera.position.x = fbo.getWidth() / 2.0f;
        camera.position.y = fbo.getHeight() / 2.0f;
        camera.update();
    }

    public void finish() {
        if (stage == Stage.EXPANDING) {
            int tmp = currentFrame;
            currentFrame = nextFrame;
            nextFrame = tmp;
            duration = ((1.0f / BASE_FPS) - duration);
        } else if (stage == Stage.STABLE) {
            nextFrame = currentFrame - 1;
        }
        stage = Stage.SHRINKING;
    }

    private enum Stage {
        EXPANDING,
        STABLE,
        SHRINKING
    }
}
