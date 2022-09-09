package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.powers.AbstractHeatAndColdPower;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;
import hydrologistmod.powers.ThermalShockPower;

public class ApplyTemperatureAction extends AbstractGameAction {
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private final AbstractCreature m;
    private final AbstractPlayer p;
    private final int number;
    private final boolean hot;

    public ApplyTemperatureAction(AbstractCreature monster, AbstractPlayer source, int number, boolean hot) {
        duration = DURATION;
        m = monster;
        p = source;
        this.number = number;
        this.hot = hot;
    }

    @Override
    public void update() {
        addToTop(new ApplyPowerAction(m, p, hot ? new HeatPower(m, p, number) : new ColdPower(m, p, number), number));
        AbstractPower heat = m.getPower(HeatPower.POWER_ID);
        AbstractPower cold = m.getPower(ColdPower.POWER_ID);
        if (heat != null && !hot) {
            addToTop(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, 1), 1));
            addToTop(new RemoveSpecificPowerAction(m, p, heat));
            ((AbstractHeatAndColdPower)heat).dealDamage();
        } else if (cold != null && hot) {
            addToTop(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, 1), 1));
            addToTop(new RemoveSpecificPowerAction(m, p, cold));
            ((AbstractHeatAndColdPower)cold).dealDamage();
        }
        isDone = true;
    }
}