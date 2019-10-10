package hydrologistmod.actions;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class MultiplyPowerAction extends AbstractGameAction {
    private static final String ID = "hydrologistmod:MultiplyPowerAction";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractPower power;
    private AbstractCreature target;
    private AbstractCreature source;
    private int multiplier;

    public MultiplyPowerAction(AbstractPower power, AbstractCreature target, AbstractCreature source, int multiplier) {
        this.duration = DURATION;
        this.power = power;
        this.target = target;
        this.source = source;
        this.multiplier = multiplier;
    }

    @Override
    public void update() {
        if (power instanceof CloneablePowerInterface) {
            AbstractPower newPower = ((CloneablePowerInterface)power).makeCopy();
            newPower.amount *= (multiplier - 1);
            AbstractDungeon.actionManager.addToTop(new ApplyPowerAction(target, source, newPower, newPower.amount));
        } else {
            System.out.println("ERROR: " + power + " is not cloneable!");
        }
        isDone = true;
    }
}