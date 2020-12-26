package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.interfaces.HeatAndColdPower;

public class ThermalShockPower extends AbstractPower implements CloneablePowerInterface, HeatAndColdPower {
    public static final String POWER_ID = "hydrologistmod:ThermalShockPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private AbstractCreature source;

    public ThermalShockPower(AbstractCreature owner, AbstractCreature source, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/ThermalShockPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/ThermalShockPower32.png"), 0, 0, 32, 32);
        type = PowerType.DEBUFF;
        this.amount = amount;
        updateDescription();
        this.source = source;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + (int)((HeatPower.calculateMultiplier() * 100) - 100) + DESCRIPTIONS[1] + (int)(100 - (ColdPower.calculateMultiplier() * 100)) + DESCRIPTIONS[2];
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * ColdPower.calculateMultiplier();
        } else {
            return damage;
        }
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * HeatPower.calculateMultiplier();
        } else {
            return damage;
        }
    }

    @Override
    public boolean heatAndColdOnApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (target == owner) {
            if (power instanceof HeatPower || power instanceof ColdPower) {
                addToTop(new ApplyPowerAction(owner, source, new ThermalShockPower(owner, source, 1), 1));
                flash();
                return false;
            }
        }
        return true;
    }

    @Override
    public void atEndOfRound() {
        addToBot(new ReducePowerAction(owner, owner, this, 1));
    }

    @Override
    public AbstractPower makeCopy() {
        return new ThermalShockPower(owner, source, amount);
    }
}
