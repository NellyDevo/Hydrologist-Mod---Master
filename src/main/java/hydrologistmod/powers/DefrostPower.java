package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.EquilibriumPower;
import com.megacrit.cardcrawl.relics.RunicPyramid;
import hydrologistmod.HydrologistMod;
import hydrologistmod.interfaces.CorporealRelevantObject;

public class DefrostPower extends AbstractPower implements CloneablePowerInterface, CorporealRelevantObject {
    public static final String POWER_ID = "hydrologistmod:DefrostPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public DefrostPower(AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/DefrostPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/DefrostPower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        this.amount = -1;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public AbstractPower makeCopy() {
        return new DefrostPower(owner);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer && !AbstractDungeon.player.hand.isEmpty() && !AbstractDungeon.player.hasRelic(RunicPyramid.ID)
                && !AbstractDungeon.player.hasPower(EquilibriumPower.POWER_ID)) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractCard card : AbstractDungeon.player.hand.group) {
                        if (HydrologistMod.isThisCorporeal(card) && !card.isEthereal) {
                            card.retain = true;
                        }
                    }
                    isDone = true;
                }
            });
        }
    }

    @Override
    public boolean activateGlow(AbstractCard card) {
        return true;
    }
}
