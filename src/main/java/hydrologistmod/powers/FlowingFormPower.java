package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.actions.FlowingFormAction;
import hydrologistmod.patches.HydrologistTags;

public class FlowingFormPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:FlowingFormPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public FlowingFormPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/FlowingFormPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/FlowingFormPower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        this.amount = amount;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        if (amount == 1) {
            description = DESCRIPTIONS[0] + DESCRIPTIONS[3];
        } else {
            description = DESCRIPTIONS[0] + DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
        }
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (card.hasTag(HydrologistTags.WATER)) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new FlowingFormAction(amount));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new FlowingFormPower(owner, amount);
    }
}
