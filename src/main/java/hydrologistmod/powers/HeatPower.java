package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
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

public class HeatPower extends AbstractPower implements CloneablePowerInterface, HeatAndColdPower, HealthBarRenderPower {
    public static final String POWER_ID = "hydrologistmod:HeatPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final int VULN_PERCENTAGE = 30;
    private static final Color HEALTH_BAR_COLOR = new Color((255.0F / 255.0F), (120.0F / 255.0F), 0.0F, 1.0F);
    private AbstractCreature source;

    public HeatPower(AbstractCreature owner, AbstractCreature source, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/HeatPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/HeatPower32.png"), 0, 0, 32, 32);
        type = PowerType.DEBUFF;
        this.amount = amount;
        updateDescription();
        this.source = source;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + (int)((calculateMultiplier() * 100) - 100) + DESCRIPTIONS[1] + amount + (amount > 1 ? DESCRIPTIONS[3] : DESCRIPTIONS[2]) + DESCRIPTIONS[4] + (amount * 2) + DESCRIPTIONS[5];
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
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
        if (power instanceof ColdPower && target == owner) {
            flash();
            addToTop(new DamageAction(owner, new DamageInfo(source, amount * damageMultiplier(), DamageInfo.DamageType.HP_LOSS)));
            addToTop(new ApplyPowerAction(owner, source, new ThermalShockPower(owner, source, 1), 1));
            addToTop(new RemoveSpecificPowerAction(owner, source, this));
            return false;
        }
        return true;
    }

    public static float calculateMultiplier() {
        int percentage = VULN_PERCENTAGE;
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof HeatAndColdRelic) {
                percentage += ((HeatAndColdRelic)relic).increaseHeatPercent();
            }
        }
        return ((float)(100 + percentage)) / 100.0f;
    }

    @Override
    public int getHealthBarAmount() {
        return amount * damageMultiplier();
    }

    @Override
    public Color getColor() {
        return HEALTH_BAR_COLOR.cpy();
    }

    @Override
    public AbstractPower makeCopy() {
        return new HeatPower(owner, source, amount);
    }
}
