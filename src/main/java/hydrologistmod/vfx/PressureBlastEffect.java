package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import hydrologistmod.actions.PressureBlastAction;

public class PressureBlastEffect extends AbstractGameEffect {
    private static final float LOOP_TARGET_VOLUME = 0.2f;
    private static final float VOLUME_FADE_DURATION = 0.5f;
    private static final Texture CRACK_TEXTURE = new Texture("hydrologistmod/images/vfx/PressureBlastCrack.png");
    private static final Texture MASK_TEXTURE = new Texture("hydrologistmod/images/vfx/PressureBlastCrackMask.png");
    private static final float CRACK_OFFSET_X = 80.0f;
    private static final float CRACK_OFFSET_Y = 30.0f;
    private static TextureAtlas.AtlasRegion beamImg;
    private static TextureRegion crackImg = new TextureRegion(CRACK_TEXTURE);
    private static TextureRegion maskImg = new TextureRegion(MASK_TEXTURE);
    private float loopVolume;
    private float volumeDuration = 0.5f;
    private float centerX;
    private float centerY;
    private float fadeDuration;
    private float fadeStartDuration;
    private boolean crackSoundPlayed = false;
    private float crackFormDuration;
    private float crackFormStartDuration;
    private float crackMaskSize = 0.0f;
    private float crackMaskTargetSize = 1.0f;
    private float crackRotation;
    private long soundID;
    private boolean loopStarted = false;
    public boolean doneBlasting = false;
    private float iceTransparency = 0.0f;
    private float iceTargetTransparency;
    private float crackTransparency = 1.0f;
    private Color crackRenderColor = Color.WHITE.cpy();
    private Color iceRenderColor = Color.WHITE.cpy();
    private PressureBlastAction parent;
    private Phase phase = Phase.FADE_IN;
    private boolean renderCrack = false;
    private boolean renderBeam = false;
    private FrameBuffer fb;

    public PressureBlastEffect(float x, float y, float iceTransparency, float fadeDuration, float crackFormDuration, PressureBlastAction parent) {
        if (beamImg == null) {
            beamImg = ImageMaster.vfxAtlas.findRegion("combat/laserThick");
        }
        centerX = x;
        centerY = y;
        iceTargetTransparency = iceTransparency;
        iceRenderColor.a = iceTransparency;
        this.fadeDuration = fadeStartDuration = fadeDuration;
        this.crackFormDuration = crackFormStartDuration = crackFormDuration;
        this.parent = parent;
        crackRotation = MathUtils.random(0.0f, 360.0f);
        fb = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
    }

    public void update() {
        switch (phase) {
            //phase 1: fade in the ice sphere.
            case FADE_IN:
                fadeDuration -= Gdx.graphics.getDeltaTime();
                if (fadeDuration <= 0) {
                    fadeDuration = 0;
                    phase = Phase.CRACKING;
                }
                iceTransparency = Interpolation.linear.apply(0.0f, iceTargetTransparency, (fadeStartDuration - fadeDuration) / fadeStartDuration);
                iceRenderColor.a = iceTransparency;
                break;
            //phase 2: render expanding cracks in front of the ice sphere
            case CRACKING:
                if (!crackSoundPlayed) {
                    renderCrack = true;
                    crackSoundPlayed = true;
                    CardCrawlGame.sound.playV("hydrologistmod:ICE_CRACK", 3.0f);
                }
                crackFormDuration -= Gdx.graphics.getDeltaTime();
                if (crackFormDuration <= 0) {
                    crackFormDuration = 0;
                    phase = Phase.BLASTING;
                }
                crackMaskSize = Interpolation.linear.apply(0.1f, crackMaskTargetSize, (crackFormStartDuration - crackFormDuration) / crackFormStartDuration);
                break;
            //phase 3: render hyperbeam-like effect under ice sphere, loop sound
            case BLASTING:
                if (!loopStarted) {
                    soundID = CardCrawlGame.sound.playAndLoop("hydrologistmod:STEAM_LOOP", 0.05f);
                    parent.beginBlasting = true;
                    loopStarted = true;
                    renderBeam = true;
                }
                if (!doneBlasting) {
                    if (loopVolume < LOOP_TARGET_VOLUME) {
                        volumeDuration += Gdx.graphics.getDeltaTime();
                        if (volumeDuration >= VOLUME_FADE_DURATION) {
                            volumeDuration = VOLUME_FADE_DURATION;
                        }
                        loopVolume = Interpolation.linear.apply(0.05f, LOOP_TARGET_VOLUME, volumeDuration / VOLUME_FADE_DURATION);
                        CardCrawlGame.sound.adjustVolume("hydrologistmod:STEAM_LOOP", soundID, loopVolume);
                    }
                } else {
                    volumeDuration -= Gdx.graphics.getDeltaTime();
                    if (volumeDuration <= 0) {
                        volumeDuration = 0;
                        phase = Phase.FADE_OUT;
                        renderBeam = false;
                        CardCrawlGame.sound.stop("hydrologistmod:STEAM_LOOP");
                    }
                    loopVolume = Interpolation.linear.apply(LOOP_TARGET_VOLUME, 0.05f, (VOLUME_FADE_DURATION - volumeDuration) / VOLUME_FADE_DURATION);
                    CardCrawlGame.sound.adjustVolume("hydrologistmod:STEAM_LOOP", soundID, loopVolume);
                }
                break;
            //phase 4: fade out the ice sphere.
            case FADE_OUT:
                fadeDuration += Gdx.graphics.getDeltaTime();
                if (fadeDuration >= fadeStartDuration) {
                    isDone = true;
                    fadeDuration = fadeStartDuration;
                }
                crackTransparency = Interpolation.linear.apply(1.0f, 0.0f, fadeDuration / fadeStartDuration);
                iceTransparency = Interpolation.linear.apply(iceTargetTransparency, 0.0f, fadeDuration / fadeStartDuration);
                crackRenderColor.a = crackTransparency;
                iceRenderColor.a = iceTransparency;
                break;
        }
    }

    public void render(SpriteBatch sb) {
        //render crack
        if (renderCrack) {
            float crackX = centerX + (CRACK_OFFSET_X * Settings.scale);
            float crackY = centerY + (CRACK_OFFSET_Y * Settings.scale);
            if (crackFormDuration == 0.0f) {
                sb.setColor(crackRenderColor);
                sb.draw(crackImg, crackX - (crackImg.getRegionWidth() / 2.0f), crackY - (crackImg.getRegionHeight() / 2.0f), crackImg.getRegionWidth() / 2.0f, crackImg.getRegionHeight() / 2.0f, crackImg.getRegionWidth(), crackImg.getRegionHeight(), scale, scale, crackRotation);
            } else {
                //draw the crack in fb
                sb.end();
                fb.begin();
                Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                Gdx.gl.glColorMask(true,true,true,true);
                sb.begin();
                sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                sb.setColor(Color.WHITE);
                sb.draw(crackImg, crackX - (crackImg.getRegionWidth() / 2.0f), crackY - (crackImg.getRegionHeight() / 2.0f), crackImg.getRegionWidth() / 2.0f, crackImg.getRegionHeight() / 2.0f, crackImg.getRegionWidth(), crackImg.getRegionHeight(), scale, scale, crackRotation);
                //draw the mask over the crack

                sb.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_ALPHA);
                sb.setColor(new Color(1,1,1,1));
                sb.draw(maskImg, crackX - (maskImg.getRegionWidth() / 2.0f), crackY - (maskImg.getRegionHeight() / 2.0f), maskImg.getRegionWidth() / 2.0f, maskImg.getRegionHeight() / 2.0f, maskImg.getRegionWidth(), maskImg.getRegionHeight(), scale * crackMaskSize, scale * crackMaskSize, 0.0f);
                //capture the image and draw outside of fb
                sb.end();
                fb.end();

                TextureRegion tmpImg = new TextureRegion(fb.getColorBufferTexture());
                tmpImg.flip(false, true);
                sb.begin();
                sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                sb.draw(tmpImg, 0, 0);
            }
        }
        //render beam
        if (renderBeam) {
            sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE);
            sb.setColor(Color.GRAY.cpy());
            float beamX = centerX;
            float beamY = centerY + (CRACK_OFFSET_Y * Settings.scale);
            sb.draw(beamImg, beamX, beamY - beamImg.packedHeight / 2.0f, 0.0f, beamImg.packedHeight / 2.0f, beamImg.packedWidth, beamImg.packedHeight, scale * 2.0f + MathUtils.random(-0.05f, 0.05f), scale * 1.5f + MathUtils.random(-0.1f, 0.1f), MathUtils.random(-4.0f, 4.0f));
            sb.draw(beamImg, beamX, beamY - beamImg.packedHeight / 2.0f, 0.0f, beamImg.packedHeight / 2.0f, beamImg.packedWidth, beamImg.packedHeight, scale * 2.0f + MathUtils.random(-0.05f, 0.05f), scale * 1.5f + MathUtils.random(-0.1f, 0.1f), MathUtils.random(-4.0f, 4.0f));
            sb.setColor(new Color(0.3f, 0.3f, 0.3f, 1.0f));
            sb.draw(beamImg, beamX, beamY - beamImg.packedHeight / 2.0f, 0.0f, beamImg.packedHeight / 2.0f, beamImg.packedWidth, beamImg.packedHeight, scale * 2.0f, scale / 2.0f, MathUtils.random(-2.0f, 2.0f));
            sb.draw(beamImg, beamX, beamY - beamImg.packedHeight / 2.0f, 0.0f, beamImg.packedHeight / 2.0f, beamImg.packedWidth, beamImg.packedHeight, scale * 2.0f, scale / 2.0f, MathUtils.random(-2.0f, 2.0f));
            sb.setBlendFunction(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        }
        //render ice
        sb.setColor(iceRenderColor);
        TextureRegion iceImg = EncapsulatingIceEffect.iceImg;
        float w = iceImg.getRegionWidth();
        float h = iceImg.getRegionHeight();
        sb.draw(iceImg, centerX - (w / 2), centerY - (h / 2), w / 2, h / 2, w, h, Settings.scale, Settings.scale, 0.0f);
        sb.setColor(Color.WHITE);
    }

    public void dispose() {
        fb.dispose();
    }

    private enum Phase {
        FADE_IN,
        CRACKING,
        BLASTING,
        FADE_OUT
    }
}