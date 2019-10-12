package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.EquilibriumPower;
import hydrologistmod.HydrologistMod;
import hydrologistmod.actions.RetainFilteredCardAction;

public class GiraffePower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:GiraffePower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GiraffePower(AbstractCreature owner, int amount) {
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

    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && !AbstractDungeon.player.hand.isEmpty() && !AbstractDungeon.player.hasPower(EquilibriumPower.POWER_ID)) {
            AbstractDungeon.actionManager.addToBottom(new RetainFilteredCardAction(HydrologistMod::isThisCorporeal, amount));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new GiraffePower(owner, amount);
    }
}