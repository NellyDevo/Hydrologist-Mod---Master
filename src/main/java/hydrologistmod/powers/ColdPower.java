package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import hydrologistmod.interfaces.HeatAndColdPower;
import hydrologistmod.interfaces.HeatAndColdRelic;

public class ColdPower extends AbstractPower implements CloneablePowerInterface, HeatAndColdPower, HealthBarRenderPower {
    public static final String POWER_ID = "hydrologistmod:ColdPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final int WEAK_PERCENTAGE = 15;
    private static final Color HEALTH_BAR_COLOR = new Color(0.0F, 1.0F, 1.0F, 1.0F);
    private AbstractCreature source;

    public ColdPower(AbstractCreature owner, AbstractCreature source, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/ColdPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/ColdPower32.png"), 0, 0, 32, 32);
        type = PowerType.DEBUFF;
        this.amount = amount;
        updateDescription();
        this.source = source;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + (100 - (calculateMultiplier() * 100)) + DESCRIPTIONS[1] + amount + (amount > 1 ? DESCRIPTIONS[3] : DESCRIPTIONS[2]) + DESCRIPTIONS[4] + (amount * 2) + DESCRIPTIONS[5];
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * calculateMultiplier();
        } else {
            return damage;
        }
    }

    @Override
    public void atEndOfRound() {
        addToBot(new ReducePowerAction(owner, owner, this, 1));
    }

    @Override
    public boolean heatAndColdOnApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
        if (power instanceof HeatPower && target == owner) {
            flash();
            addToTop(new DamageAction(owner, new DamageInfo(source, amount * 2, DamageInfo.DamageType.THORNS)));
            addToTop(new ApplyPowerAction(owner, owner, new ThermalShockPower(owner, source, 1), 1));
            addToTop(new RemoveSpecificPowerAction(owner, source, this));
            return false;
        }
        return true;
    }

    public static float calculateMultiplier() {
        int percentage = WEAK_PERCENTAGE;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof HeatAndColdRelic) {
                percentage += ((HeatAndColdRelic)relic).increaseColdPercent();
            }
        }
        return ((float)(100 - percentage)) / 100.0f;
    }

    @Override
    public int getHealthBarAmount() {
        return amount * 2;
    }

    @Override
    public Color getColor() {
        return HEALTH_BAR_COLOR.cpy();
    }

    @Override
    public AbstractPower makeCopy() {
        return new ColdPower(owner, source, amount);
    }
}
