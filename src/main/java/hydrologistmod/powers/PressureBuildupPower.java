package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.abstracts.TwoAmountPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.HydrologistMod;

public class PressureBuildupPower extends TwoAmountPower implements CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:PressureBuildupPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public PressureBuildupPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/PressureBuildupPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/PressureBuildupPower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        this.amount = amount;
        amount2 = 0;
        updateDescription();
    }

    @Override
    public void updateDescription() {

    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (HydrologistMod.isThisCorporeal(card)) {
            ++amount2;
        } if (amount2 == 10) {
            flash();
            AbstractDungeon.actionManager.addToBottom(new DamageAllEnemiesAction(owner, DamageInfo.createDamageMatrix(amount, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE));
            amount2 = 0;
        }
        updateDescription();
    }

    @Override
    public AbstractPower makeCopy() {
        return new PressureBuildupPower(owner, amount);
    }
}
