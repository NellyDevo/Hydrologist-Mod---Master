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
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import hydrologistmod.actions.TransmuteCardAction;

import java.util.HashMap;

public class TransmuteCardEffect extends AbstractGameEffect {
    private HashMap<AbstractCard, AbstractCard> transmutedPairs;
    private TransmuteCardAction action;
    private CardGroup.CardGroupType targetGroup;
    private float offsetPercent;
    private static final int MAX_CARD_COUNT = 8;
    private static final float MAX_LEFT_BOUND = 0.1f;
    private static final float MAX_RIGHT_BOUND = 0.9f;
    private static final float MAX_DOWN_BOUND = 0.2f;
    private static final float MAX_UP_BOUND = 0.8f;
    private HashMap<AbstractCard, TextureRegion> textureMap = new HashMap<>();
    private static TextureRegion mask = new TextureRegion(new Texture("hydrologistmod/images/vfx/TransmuteMask.png"), 512, 512);
    private static TextureRegion line = new TextureRegion(new Texture("hydrologistmod/images/vfx/TransmuteLine.png"), 512, 512);

    public TransmuteCardEffect(HashMap<AbstractCard, AbstractCard> transmutedPairs, CardGroup.CardGroupType targetGroup, TransmuteCardAction action, float duration) {
        this.transmutedPairs = transmutedPairs;
        this.action = action;
        this.targetGroup = targetGroup;
        this.duration = duration;
        this.startingDuration = duration;
    }

    @Override
    public void render(SpriteBatch sb) {
        //generate partial card images for each hash pair, using the mask, and overlay them over the cards positions along with transmute line effect
        //first, for each card, establish a new texture by creating a 512x512 framebuffer, and render the card, followed by the mask, at 256x256
        for (AbstractCard card : transmutedPairs.values()) {
            sb.end();

            FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, 512, 512, false, false);
            fb.begin();
            Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            Gdx.gl.glColorMask(true,true,true,true);
            sb.begin();
            //render the card at 256 x 256 on the frame buffer with a 1x scale, 0f rotation
            sb.setColor(Color.WHITE);
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            setCardAttributes(card);
            card.render(sb);

            //render the mask at offset
            sb.setBlendFunction(0, GL20.GL_SRC_ALPHA);
            sb.setColor(new Color(1,1,1,1));
            sb.draw(mask, 0f, 0f - (512f * offsetPercent), 256f, 256f - (512f * offsetPercent), 512f, 512f, 1f, 1f, 0.0f);

            //render the "water line" and "water effects" at offset
            sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            sb.setColor(Color.WHITE);
            sb.draw(line, 0f, 0f - (512f * offsetPercent), 256f, 256f - (512f * offsetPercent), 512f, 512f, 1f, 1f, 0.0f);
            //

            fb.end();
            sb.end();

            textureMap.put(card, new TextureRegion(fb.getColorBufferTexture()));
            sb.begin();
        }
        //second, render the new textures to the key cards coordinates, angle, and scale. Iterate through limbo to make sure the masking cards have the same render order as the original cards.
        for (AbstractCard card : AbstractDungeon.player.limbo.group) {
            if (transmutedPairs.containsKey(card)) {
                sb.draw(textureMap.get(transmutedPairs.get(card)),card.current_x - 256f,card.current_y - 256f,256f,256f,512f,512f,card.drawScale * Settings.scale,card.drawScale * Settings.scale,card.angle);
            }
        }
    }

    private void setCardAttributes(AbstractCard card) {
        card.current_x = 256.0f;
        card.current_y = 256.0f;
        card.drawScale = 1.0f;
        card.angle = 0.0f;
    }

    @Override
    public void update() {
        if (duration == startingDuration) {
            //Initialize: place the cards along the screen. Centered if only one card, but otherwise scattered similar to other game effects. Set hash pair render-relevant variables to be the same as the hash keys.
            AbstractDungeon.player.limbo.group.addAll(transmutedPairs.keySet());
            float cardCount = transmutedPairs.keySet().size();
            float alpha = Math.min(1.0f, (cardCount - 1) / ((float)MAX_CARD_COUNT - 1));
            float leftBound = Interpolation.circleOut.apply(0.5f, MAX_LEFT_BOUND, alpha);
            float rightBound = Interpolation.circleOut.apply(0.5f, MAX_RIGHT_BOUND, alpha);
            float downBound = Interpolation.circleOut.apply(0.5f, MAX_DOWN_BOUND, alpha);
            float upBound = Interpolation.circleOut.apply(0.5f, MAX_UP_BOUND, alpha);
            for (AbstractCard card : transmutedPairs.keySet()) {
                card.targetDrawScale = 1.0f;
                card.setAngle(0.0f, true);
                card.target_x = MathUtils.random(leftBound, rightBound);
                card.target_y = MathUtils.random(downBound, upBound);
            }
            duration -= Gdx.graphics.getDeltaTime();
        } else if (duration > 0.0f) {
            //Upkeep: control logic of where the mask is to be placed upon the card(s), based on duration of the effect/action
            duration -= Gdx.graphics.getDeltaTime();
            offsetPercent = duration / startingDuration;
        } else {
            //then, when the effect completes, distribute the cards and signal the action to complete.
            if (targetGroup != null) {
                switch (targetGroup) {
                    case HAND:
                        for (AbstractCard card : transmutedPairs.values()) {
                            AbstractDungeon.player.limbo.removeCard(card);
                            AbstractDungeon.player.limbo.moveToHand(card);
                        }
                        break;
                    case DRAW_PILE:
                        for (AbstractCard card : transmutedPairs.values()) {
                            AbstractDungeon.player.limbo.removeCard(card);
                            AbstractDungeon.player.limbo.moveToDeck(card, true);
                        }
                        break;
                    case DISCARD_PILE:
                        for (AbstractCard card : transmutedPairs.values()) {
                            AbstractDungeon.player.limbo.removeCard(card);
                            AbstractDungeon.player.limbo.moveToDiscardPile(card);
                        }
                        break;
                    case EXHAUST_PILE:
                        for (AbstractCard card : transmutedPairs.values()) {
                            AbstractDungeon.player.limbo.removeCard(card);
                            AbstractDungeon.player.limbo.moveToExhaustPile(card);
                        }
                        break;
                    default:
                        System.out.println("TransmuteCardEffect: How was this reached?");
                        break;
                }
            }
            action.isDone = true;
            isDone = true;
        }
    }

    @Override
    public void dispose() {

    }
}
