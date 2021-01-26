package hydrologistmod.powers;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class AbstractHeatAndColdPower extends AbstractPower implements HealthBarRenderPower {
    private static final int DAMAGE_MULTIPLIER = 3;
    protected AbstractCreature source;
    protected Color hbColor;

    protected int damageMultiplier() {
        return DAMAGE_MULTIPLIER;
    }

    public void dealDamage() {
        flash();
        addToTop(new DamageAction(owner, new DamageInfo(source, amount * damageMultiplier(), DamageInfo.DamageType.HP_LOSS)));
    }

    @Override
    public int getHealthBarAmount() {
        return amount * damageMultiplier();
    }

    @Override
    public Color getColor() {
        return hbColor;
    }

    @Override
    public void atEndOfRound() {
        addToBot(new ReducePowerAction(owner, owner, this, 1));
    }
}
