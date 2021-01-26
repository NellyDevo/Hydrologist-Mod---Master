package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import hydrologistmod.interfaces.HeatAndColdRelic;

public class ColdPower extends AbstractHeatAndColdPower implements CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:ColdPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    public static final int WEAK_PERCENTAGE = 15;
    private static final Color HEALTH_BAR_COLOR = new Color(0.0F, 1.0F, 1.0F, 1.0F);

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
        hbColor = HEALTH_BAR_COLOR;
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + (int)(100 - (calculateMultiplier() * 100)) + DESCRIPTIONS[1] + amount + (amount > 1 ? DESCRIPTIONS[3] : DESCRIPTIONS[2]) + DESCRIPTIONS[4] + (amount * damageMultiplier()) + DESCRIPTIONS[5];
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type) {
        if (type == DamageInfo.DamageType.NORMAL) {
            return damage * calculateMultiplier();
        } else {
            return damage;
        }
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
    public AbstractPower makeCopy() {
        return new ColdPower(owner, source, amount);
    }
}
