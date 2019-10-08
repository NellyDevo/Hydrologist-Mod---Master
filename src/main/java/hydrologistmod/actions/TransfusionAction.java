package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;

public class TransfusionAction extends AbstractGameAction {
    private DamageInfo info;
    private int hpIncrease;
    private static final float DURATION = Settings.ACTION_DUR_FAST;

    public TransfusionAction(AbstractCreature target, DamageInfo info, int hpIncrease) {
        this.setValues(target, this.info = info);
        actionType = ActionType.DAMAGE;
        duration = DURATION;
        this.hpIncrease = hpIncrease;
    }
    
    @Override
    public void update() {
        if (duration == DURATION && target != null) {
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, AttackEffect.NONE));
            target.damage(info);
            if ((((AbstractMonster)target).isDying || target.currentHealth <= 0) && !target.halfDead && target.hasPower(VulnerablePower.POWER_ID)) {
                AbstractDungeon.player.increaseMaxHp(hpIncrease, false);
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
    }
}
