package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.HydrologistMod;
import hydrologistmod.interfaces.ApplyPowersForHydrologistPower;

public class ManifestationPower extends AbstractPower implements ApplyPowersForHydrologistPower, CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:ManifestationPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ManifestationPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/ManifestationPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/ManifestationPower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        this.amount = amount;
        updateDescription();
    }

    @Override
    public void updateDescription() {

    }

    @Override
    public void beforeApplyPowers(AbstractCard card) {
        if (HydrologistMod.isThisCorporeal(card)) {
            if (card.baseDamage > -1) {
                card.baseDamage += amount;
            }
            if (card.baseBlock > -1) {
                card.baseBlock += amount;
            }
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new ManifestationPower(owner, amount);
    }
}
