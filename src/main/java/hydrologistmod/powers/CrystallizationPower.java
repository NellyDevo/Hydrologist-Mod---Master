package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.actions.TransmuteCardAction;
import hydrologistmod.patches.HydrologistTags;

public class CrystallizationPower extends AbstractPower implements NonStackablePower, CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:CrystallizationPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public CrystallizationPower(AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/CrystallizationPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/CrystallizationPower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        amount = -1;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(owner, owner, this));
        if (card.hasTag(HydrologistTags.ICE)) {
            flash();
            AbstractDungeon.actionManager.addToTop(new TransmuteCardAction());
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new CrystallizationPower(owner);
    }
}
