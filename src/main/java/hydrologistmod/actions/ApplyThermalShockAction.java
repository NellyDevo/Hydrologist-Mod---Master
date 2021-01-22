package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;
import hydrologistmod.powers.ThermalShockPower;

public class ApplyThermalShockAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:ApplyThermalShockAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractCreature m;
    private AbstractPlayer p;

    public ApplyThermalShockAction(AbstractCreature monster, AbstractPlayer source, int amount) {
        duration = DURATION;
        m = monster;
        p = source;
        this.amount = amount;
    }

    @Override
    public void update() {
        if (m.hasPower(ColdPower.POWER_ID)) {
            addToTop(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, amount - 1), amount - 1));
            addToTop(new ApplyPowerAction(m, p, new HeatPower(m, p, 1), 1));
        } else if (m.hasPower(HeatPower.POWER_ID)) {
            addToTop(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, amount - 1), amount - 1));
            addToTop(new ApplyPowerAction(m, p, new ColdPower(m, p, 1), 1));
        } else {
            addToTop(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, amount), amount));
        }
        isDone = true;
    }
}