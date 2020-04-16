package hydrologistmod.powers;

import basemod.BaseMod;
import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class AccumulatorPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:AccumulatorPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public AccumulatorPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/GiraffePower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/GiraffePower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        this.amount = amount;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public void onInitialApplication() {
        BaseMod.MAX_HAND_SIZE += amount;
    }

    @Override
    public void stackPower(int stackAmount) {
        BaseMod.MAX_HAND_SIZE += stackAmount;
    }

    @Override
    public void reducePower(int reduceAmount) {
        BaseMod.MAX_HAND_SIZE -= reduceAmount;
    }

    @Override
    public void onVictory() {
        BaseMod.MAX_HAND_SIZE -= amount;
    }

    @Override
    public void onRemove() {
        BaseMod.MAX_HAND_SIZE -= amount;
    }

    @Override
    public AbstractPower makeCopy() {
        return new AccumulatorPower(owner, amount);
    }
}
