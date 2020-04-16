package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import hydrologistmod.HydrologistMod;

import java.util.ArrayList;

public class FreezeAction extends AbstractGameAction {
    private DamageInfo info;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractCard card;

    public FreezeAction(AbstractCreature target, DamageInfo info) {
        this.setValues(target, this.info = info);
        actionType = ActionType.DAMAGE;
        duration = DURATION;
    }
    
    @Override
    public void update() {
        if (duration == DURATION && target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, AttackEffect.NONE));
            target.damage(info);
            if ((((AbstractMonster)target).isDying || target.currentHealth <= 0) && !target.halfDead && HydrologistMod.isCool(target)) {
                    final ArrayList<AbstractCard> possibleCards = new ArrayList<>();
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c.canUpgrade()) {
                            possibleCards.add(c);
                        }
                    }
                    if (!possibleCards.isEmpty()) {
                        card = possibleCards.get(AbstractDungeon.miscRng.random(0, possibleCards.size() - 1));
                        card.upgrade();
                        AbstractDungeon.player.bottledCardUpgradeCheck(card);
                    }
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
        if (isDone && card != null) {
            AbstractDungeon.effectsQueue.add(new UpgradeShineEffect(Settings.WIDTH / 2f, Settings.HEIGHT / 2f));
            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy()));
            addToTop(new WaitAction(Settings.ACTION_DUR_MED));
        }
    }
}
