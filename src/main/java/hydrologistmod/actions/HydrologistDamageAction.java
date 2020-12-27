package hydrologistmod.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import hydrologistmod.character.HydrologistCharacter;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.vfx.HydrologistParticle;

import java.util.HashMap;

public class HydrologistDamageAction extends AbstractGameAction {
    private static final HashMap<AbstractCard.CardTags, String> sfxMap = initializeSfxMap();
    private static final HashMap<AbstractCard.CardTags, Color> colorMap = initializeColorMap();
    private static final HashMap<AbstractCard.CardTags, Boolean> playSoundAtStartMap = initializeBooleanMap();
    private static final float POST_ATTACK_WAIT_DUR = 0.1f;
    private static final float DURATION = 0.3f;
    private static final float EFFECT_DURATION = 0.5f;
    private DamageInfo info;
    private AbstractCard.CardTags tag;
    private boolean secondParticle = false;
    private HydrologistCharacter player = null;
    private Vector2 startPosition = null;
    private Vector2 targetPosition = null;

    public HydrologistDamageAction(AbstractCard.CardTags tag, AbstractCreature target, DamageInfo info) {
        setValues(target, info);
        this.info = info;
        this.tag = tag;
        if (info.owner instanceof HydrologistCharacter) {
            player = (HydrologistCharacter)info.owner;
            duration = EFFECT_DURATION;
            targetPosition = new Vector2(target.hb.cX, target.hb.cY);
        } else {
            duration = DURATION;
        }
        startDuration = duration;
    }

    @Override
    public void update() {
        if (shouldCancelAction() && info.type != DamageInfo.DamageType.THORNS) {
            isDone = true;
            return;
        }

        if (player == null) {
            if (duration == startDuration) {
                if (info.type != DamageInfo.DamageType.THORNS) {
                    if (info.owner.isDying || info.owner.halfDead) {
                        isDone = true;
                        return;
                    }
                }
                generateParticle(tag, target);
                CardCrawlGame.sound.playV(sfxMap.get(tag), 2.0f);
            }

            if (duration < startDuration / 2 && !secondParticle) {
                secondParticle = true;
                generateParticle(tag, target);
            }
            tickDuration();

            if (isDone) {
                doDamage();
                addToTop(new WaitAction(POST_ATTACK_WAIT_DUR));
            }
        } else {
            if (startPosition == null) {
                startPosition = player.waterCoords.cpy();
                CardCrawlGame.sound.playV("ATTACK_WHIFF_1", 2.0f);
                if (playSoundAtStartMap.get(tag)) {
                    CardCrawlGame.sound.playV(sfxMap.get(tag), 2.0f);
                }
            }
            Vector2 interPosition = new Vector2();
            if (duration > startDuration / 2f) {
                //interpolate new vector2 coordinates to center of target
                interPosition.x = Interpolation.linear.apply(startPosition.x, targetPosition.x, 1F - ((duration - (startDuration / 2.0F)) / (startDuration / 2.0F)));
                interPosition.y = Interpolation.swingIn.apply(startPosition.y, targetPosition.y, 1F - ((duration - (startDuration / 2.0F)) / (startDuration / 2.0F)));
                player.waterbending.override = interPosition;
                duration -= Gdx.graphics.getDeltaTime();
                //if duration is <= startDuration/2, set result coordinates to startPosition
                if (duration <= startDuration / 2f) {
                    doDamage();
                    startPosition = interPosition.cpy();
                }
            } else {
                //interpolate new vector2 coordinates to waterbending position
                interPosition.x = Interpolation.linear.apply(startPosition.x, player.waterCoords.x, 1F - (duration / (startDuration / 2.0F)));
                interPosition.y = Interpolation.swingIn.apply(startPosition.y, player.waterCoords.y, 1F - (duration / (startDuration / 2.0F)));
                player.waterbending.override = interPosition;
                duration -= Gdx.graphics.getDeltaTime();
                if (duration <= 0) {
                    isDone = true;
                }
            }
        }
    }

    private void doDamage() {
        target.tint.color.set(colorMap.get(tag).cpy());
        target.tint.changeColor(Color.WHITE.cpy());
        target.damage(info);
        generateParticle(tag, target);
        if (!playSoundAtStartMap.get(tag) && player != null) {
            CardCrawlGame.sound.playV(sfxMap.get(tag), 2.0f);
        }
        if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
            AbstractDungeon.actionManager.clearPostCombatActions();
        }
    }

    private static HashMap<AbstractCard.CardTags, String> initializeSfxMap() {
        HashMap<AbstractCard.CardTags, String> retVal = new HashMap<>();
        retVal.put(HydrologistTags.ICE, "hydrologistmod:ICE");
        retVal.put(HydrologistTags.WATER, "hydrologistmod:WATER"); //TODO
        retVal.put(HydrologistTags.STEAM, "hydrologistmod:STEAM");
        return retVal;
    }

    private static HashMap<AbstractCard.CardTags, Color> initializeColorMap() {
        HashMap<AbstractCard.CardTags, Color> retVal = new HashMap<>();
        retVal.put(HydrologistTags.ICE, Color.CYAN);
        retVal.put(HydrologistTags.WATER, Color.BLUE);
        retVal.put(HydrologistTags.STEAM, Color.GRAY);
        return retVal;
    }

    private static HashMap<AbstractCard.CardTags, Boolean> initializeBooleanMap() {
        HashMap<AbstractCard.CardTags, Boolean> retVal = new HashMap<>();
        retVal.put(HydrologistTags.WATER, true);
        retVal.put(HydrologistTags.ICE, false);
        retVal.put(HydrologistTags.STEAM, false);
        return retVal;
    }

    private static void generateParticle(AbstractCard.CardTags tag, AbstractCreature target) {
        float leftBound = target.hb.cX - (target.hb.width / 2);
        float rightBound = target.hb.cX + (target.hb.width / 2);
        float upBound = target.hb.cY + (target.hb.width / 2);
        float downBound = target.hb.cY - (target.hb.width / 2);
        float x = MathUtils.random(leftBound, rightBound);
        float y = MathUtils.random(downBound, upBound);
        float rotation = MathUtils.random(0.0f, 360.0f);
        float scale = MathUtils.random(1.1f, 1.4f);
        AbstractDungeon.effectList.add(new HydrologistParticle(tag, x, y, rotation, scale));
    }
}