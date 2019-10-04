package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.interfaces.HydrologistSubtypeAffectingPower;
import hydrologistmod.patches.HydrologistTags;

public class HighPressurePower extends AbstractPower implements HydrologistSubtypeAffectingPower, CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:HighPressurePower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HighPressurePower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/HighPressurePower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/HighPressurePower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        this.amount = amount;
        updateDescription();
    }

    @Override
    public void updateDescription() {

    }

    @Override
    public AbstractPower makeCopy() {
        return new HighPressurePower(owner, amount);
    }

    @Override
    public void beforeApplyPowers(AbstractCard card) {
        if (card.hasTag(HydrologistTags.STEAM) && card.baseDamage > -1) {
            card.baseDamage += amount;
        }
    }
}
