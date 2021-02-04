package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import hydrologistmod.HydrologistMod;
import hydrologistmod.actions.YawningAbyssAction;

public class YawningAbyssEffect extends AbstractGameEffect {
    private static final Texture CRACK_TEXTURE = new Texture("hydrologistmod/images/vfx/YawningAbyssCrack.png");
    private static final Texture MASK_TEXTURE = new Texture("hydrologistmod/images/vfx/YawningAbyssMask.png");
    private static final Texture EFFECT_TEXTURE = new Texture("hydrologistmod/images/vfx/YawningAbyssEffect.png");
    private static final ShaderProgram shader = makeShader();
    private Color hslc = new Color(0.0f, 0.5f, 0.5f, 0.5f);
    private static final int CRACK_WIDTH = 568;
    private static final int CRACK_HEIGHT = 280;
    private static final int CRACK_HORIZONTAL = 5;
    private static final int CRACK_VERTICAL = 5;
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
        float w = effect.getRegionWidth();
        float h = effect.getRegionHeight();
        TextureRegion r = crackAnimation[currentFrame];
        sb.draw(r, x - (w / 2.0f), y - (h / 2.0f),
                w / 2.0f, h / 2.0f,
                w, h,
                effectScale, effectScale, 0.0f);

        sb.setColor(frame2Color);
        r = crackAnimation[nextFrame];
        sb.draw(r, x - (w / 2.0f), y - (h / 2.0f),
                w / 2.0f, h / 2.0f,
                w, h,
                effectScale, effectScale, 0.0f);

        if (effectFadeOut > 0.0f) {
            //turn on the frame buffer, change camera
            sb.end();
            HydrologistMod.beginBuffer(fbo);
            Matrix4 tmp = sb.getProjectionMatrix().cpy();
            sb.setProjectionMatrix(camera.combined);

            //begin shader program
            ShaderProgram tmpShader = sb.getShader();
            sb.setShader(shader);
            hslc.r = effectDuration;
            sb.setColor(hslc);
            sb.begin();

            //render aurora borealis in the buffer
            r = effect;
            float scaleY = 1.0f - Math.abs(Interpolation.linear.apply(-0.1f, 0.1f, effectDuration / EFFECT_DURATION));
            sb.draw(r, 0.0f, 0.0f,
                    0.0f, 0.0f,
                    w, h,
                    1.0f,  scaleY, 0.0f);

            //end shader program
            sb.end();
            sb.setShader(tmpShader);
            sb.setColor(Color.WHITE.cpy());
            sb.begin();

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
            sb.draw(texture, x - (w / 2.0f), y - (h / 2.0f),
                    w / 2.0f, h / 2.0f,
                    w, h,
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
        maskAnimation = new TextureRegion[CRACK_VERTICAL * CRACK_HORIZONTAL];
        int i = 0;
        for (int h = 0; h < CRACK_VERTICAL; ++h) {
            for (int w = 0; w < CRACK_HORIZONTAL; ++w) {
                crackAnimation[i] = new TextureRegion(CRACK_TEXTURE, CRACK_WIDTH * w, CRACK_HEIGHT * h, CRACK_WIDTH, CRACK_HEIGHT);
                maskAnimation[i++] = new TextureRegion(MASK_TEXTURE, CRACK_WIDTH * w, CRACK_HEIGHT * h, CRACK_WIDTH, CRACK_HEIGHT);
            }
        }
        effect = new TextureRegion(EFFECT_TEXTURE);
        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, CRACK_WIDTH, CRACK_HEIGHT, false, false);
        camera = new OrthographicCamera(CRACK_WIDTH, CRACK_HEIGHT);
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

    private static ShaderProgram makeShader() {
        return new ShaderProgram(
                "attribute vec4 a_position;" +
                "\nattribute vec4 a_color;" +
                "\nattribute vec2 a_texCoord0;" +
                "\nuniform mat4 u_projTrans;" +
                "\nvarying vec4 v_color;" +
                "\nvarying vec2 v_texCoords;" +
                "\nvarying float v_lightFix;" +
                "\n" +
                "\nvoid main()" +
                "\n{" +
                "\n   v_color = a_color;" +
                "\n   v_texCoords = a_texCoord0;" +
                "\n   v_color.a = pow(v_color.a * (255.0/254.0) + 0.5, 1.709);" +
                "\n   v_lightFix = 1.0 + pow(v_color.a, 1.41421356);" +
                "\n   gl_Position =  u_projTrans * a_position;" +
                "\n}" +
                "\n", "#ifdef GL_ES" +
                "\n#define LOWP lowp" +
                "\nprecision mediump float;" +
                "\n#else" +
                "\n#define LOWP " +
                "\n#endif" +
                "\nvarying vec2 v_texCoords;" +
                "\nvarying float v_lightFix;" +
                "\nvarying LOWP vec4 v_color;" +
                "\nuniform sampler2D u_texture;" +
                "\nconst float eps = 1.0e-10;" +
                "\nvec4 rgb2hsl(vec4 c)" +
                "\n{" +
                "\n    const vec4 J = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);" +
                "\n    vec4 p = mix(vec4(c.bg, J.wz), vec4(c.gb, J.xy), step(c.b, c.g));" +
                "\n    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));" +
                "\n    float d = q.x - min(q.w, q.y);" +
                "\n    float l = q.x * (1.0 - 0.5 * d / (q.x + eps));" +
                "\n    return vec4(abs(q.z + (q.w - q.y) / (6.0 * d + eps)), (q.x - l) / (min(l, 1.0 - l) + eps), l, c.a);" +
                "\n}" +
                "\n" +
                "\nvec4 hsl2rgb(vec4 c)" +
                "\n{" +
                "\n    const vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);" +
                "\n    vec3 p = abs(fract(c.x + K.xyz) * 6.0 - K.www);" +
                "\n    float v = (c.z + c.y * min(c.z, 1.0 - c.z));" +
                "\n    return vec4(v * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), 2.0 * (1.0 - c.z / (v + eps))), c.w);" +
                "\n}void main()" +
                "\n{" +
                "\n    float hue = (v_color.x - 0.5);" +
                "\n    float saturation = v_color.y * 2.0;" +
                "\n    float brightness = v_color.z - 0.5;" +
                "\n    vec4 tgt = texture2D( u_texture, v_texCoords );" +
                "\n    tgt = rgb2hsl(tgt);" +
                "\n    tgt.r = fract(tgt.r + hue);" +
                "\n    tgt = hsl2rgb(tgt);" +
                "\n    tgt.rgb = vec3(" +
                "\n     (0.5 * pow(dot(tgt.rgb, vec3(0.375, 0.5, 0.125)), v_color.w) * v_lightFix + brightness)," +
                "\n     ((tgt.r - tgt.b) * saturation)," +
                "\n     ((tgt.g - tgt.b) * saturation));" +
                "\n    gl_FragColor = clamp(vec4(" +
                "\n     dot(tgt.rgb, vec3(1.0, 0.625, -0.5))," +
                "\n     dot(tgt.rgb, vec3(1.0, -0.375, 0.5))," +
                "\n     dot(tgt.rgb, vec3(1.0, -0.375, -0.5))," +
                "\n     tgt.a), 0.0, 1.0);" +
                "\n}");
    }
}
