package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class EncapsulatingIceEffect extends AbstractGameEffect {
    private static Texture ICE_ORB_TEXTURE = new Texture("hydrologistmod/images/vfx/EncapsulatingIceGraphic.png");
    private static Texture ICE_ORB_MASK = new Texture("hydrologistmod/images/vfx/EncapsulatingIceMask.png");
    public static TextureRegion iceImg = new TextureRegion(ICE_ORB_TEXTURE);
    private static TextureRegion maskImg = new TextureRegion(ICE_ORB_MASK);
    private float startAlpha;
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;
    private float fadeOutDuration;
    private float fadeOutStartDuration;
    private float alpha;
    private Color color = Color.WHITE.cpy();
    private FrameBuffer fb;
    private boolean soundPlayed = false;
    private float centerX;
    private float centerY;

    public EncapsulatingIceEffect(float x, float y, float mainDuration, float fadeDuration, float transparency) {
        centerX = x;
        centerY = y;
        duration = startingDuration = mainDuration;
        fadeOutDuration = fadeOutStartDuration = fadeDuration;
        alpha = startAlpha = transparency;
        fb = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
        color.a = alpha;
    }

    public void update() {
        if (duration > 0.0f) {
            if (!soundPlayed) {
                CardCrawlGame.sound.play("hydrologistmod:ENCAPSULATING_ICE");
                soundPlayed = true;
            }
            offsetX = Interpolation.linear.apply(0.0f, iceImg.getRegionWidth() * 0.75f, (startingDuration - duration) / startingDuration);
            offsetY = Interpolation.linear.apply(0.0f, iceImg.getRegionHeight() * 0.75f, (startingDuration - duration) / startingDuration);
            duration -= Gdx.graphics.getDeltaTime();
        } else {
            alpha = Interpolation.linear.apply(startAlpha, 0.0f, (fadeOutStartDuration - fadeOutDuration) / fadeOutStartDuration);
            fadeOutDuration -= Gdx.graphics.getDeltaTime();
            color.a = alpha;
            if (fadeOutDuration <= 0) {
                isDone = true;
            }
        }
    }

    public void render(SpriteBatch sb) {
        AbstractPlayer p = AbstractDungeon.player;
        if (duration > 0.0f) {
            sb.end();
            fb.begin();
            Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glColorMask(true,true,true,true);
            //render the ice
            sb.begin();
            sb.setColor(Color.WHITE);
            sb.draw(iceImg, centerX - (iceImg.getRegionWidth() / 2.0f), centerY - (iceImg.getRegionHeight() / 2.0f), iceImg.getRegionWidth() / 2.0f, iceImg.getRegionHeight() / 2.0f, iceImg.getRegionWidth(), iceImg.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);

            //render the mask at offset
            sb.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);
            sb.setColor(Color.BLACK);
            float x = centerX - (maskImg.getRegionWidth() / 2.0f) - (offsetX * Settings.scale);
            float y = centerY - (maskImg.getRegionHeight() / 2.0f) + (offsetY * Settings.scale);
            sb.draw(maskImg, x, y, maskImg.getRegionWidth() / 2.0f, maskImg.getRegionHeight() / 2.0f, maskImg.getRegionWidth(), maskImg.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
            sb.end();
            fb.end();

            //capture texture and render in normal batch
            TextureRegion img = new TextureRegion(fb.getColorBufferTexture());
            img.flip(false, true);

            sb.begin();
            sb.setColor(color);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            sb.draw(img, 0, 0);
        } else {
            sb.setColor(color);
            sb.draw(iceImg, centerX - (iceImg.getRegionWidth() / 2.0f), centerY - (iceImg.getRegionHeight() / 2.0f), iceImg.getRegionWidth() / 2.0f, iceImg.getRegionHeight() / 2.0f, iceImg.getRegionWidth(), iceImg.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
            sb.setColor(Color.WHITE);
        }
    }

    public void dispose() {
        fb.dispose();
    }
}
