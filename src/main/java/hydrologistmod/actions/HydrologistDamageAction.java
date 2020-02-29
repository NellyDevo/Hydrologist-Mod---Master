package hydrologistmod.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.vfx.HydrologistParticle;

import java.util.HashMap;

public class HydrologistDamageAction extends AbstractGameAction {
    private static final HashMap<AbstractCard.CardTags, String> sfxMap = initializeSfxMap();
    private static final HashMap<AbstractCard.CardTags, Color> colorMap = initializeColorMap();
    private static final float POST_ATTACK_WAIT_DUR = 0.1f;
    private static final float DURATION = 0.3f;
    private DamageInfo info;
    private AbstractCard.CardTags tag;
    private boolean secondParticle = false;

    public HydrologistDamageAction(AbstractCard.CardTags tag, AbstractCreature target, DamageInfo info) {
        setValues(target, info);
        this.info = info;
        this.tag = tag;
        duration = DURATION;
        startDuration = duration;
    }

    @Override
    public void update() {
        if (shouldCancelAction() && info.type != DamageInfo.DamageType.THORNS) {
            isDone = true;
            return;
        }

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
            target.tint.color.set(colorMap.get(tag).cpy());
            target.tint.changeColor(Color.WHITE.cpy());
            target.damage(info);
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
            addToTop(new WaitAction(POST_ATTACK_WAIT_DUR));
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