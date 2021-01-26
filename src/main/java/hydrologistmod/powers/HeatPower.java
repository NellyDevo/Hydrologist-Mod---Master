package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import hydrologistmod.interfaces.HeatAndColdRelic;

public class HeatPower extends AbstractHeatAndColdPower implements CloneablePowerInterface, HealthBarRenderPower {
    public static final String POWER_ID = "hydrologistmod:HeatPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final int VULN_PERCENTAGE = 30;
    private static final Color HEALTH_BAR_COLOR = Color.valueOf("d860e6");

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
        hbColor = HEALTH_BAR_COLOR;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + (int)((calculateMultiplier() * 100) - 100) + DESCRIPTIONS[1] + amount + (amount > 1 ? DESCRIPTIONS[3] : DESCRIPTIONS[2]) + DESCRIPTIONS[4] + (amount * damageMultiplier()) + DESCRIPTIONS[5];
    }

    @Override
    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * calculateMultiplier();
        } else {
            return damage;
        }
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
    public AbstractPower makeCopy() {
        return new HeatPower(owner, source, amount);
    }
}
