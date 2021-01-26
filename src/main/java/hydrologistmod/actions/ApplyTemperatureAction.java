package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;
import hydrologistmod.powers.ThermalShockPower;

public class ApplyTemperatureAction extends AbstractGameAction {
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractCreature m;
    private AbstractPlayer p;
    private AbstractPower power;

    public ApplyTemperatureAction(AbstractCreature monster, AbstractPlayer source, AbstractPower powerToApply) {
        duration = DURATION;
        m = monster;
        p = source;
        power = powerToApply;
    }

    @Override
    public void update() {
        if ((m.hasPower(HeatPower.POWER_ID) && power.ID.equals(ColdPower.POWER_ID)) || (m.hasPower(ColdPower.POWER_ID) && power.ID.equals(HeatPower.POWER_ID)) || m.hasPower(ThermalShockPower.POWER_ID)) {
            addToTop(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, 1), 1));
        } else {
            addToTop(new ApplyPowerAction(m, p, power, power.amount));
        }
        isDone = true;
    }
}