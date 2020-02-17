package hydrologistmod.interfaces;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public interface HeatAndColdPower {
    default boolean heatAndColdOnApplyPower(AbstractPower powerToApply, AbstractCreature target, AbstractCreature source) {
        return true;
    }
}
