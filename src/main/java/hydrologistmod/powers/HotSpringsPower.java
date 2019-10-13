package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.BetterOnApplyPowerPower;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HotSpringsPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:HotSpringsPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public HotSpringsPower(AbstractCreature owner) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/HotSpringsPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/HotSpringsPower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        amount = -1;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0];
    }

    @Override
    public void onApplyPower(AbstractPower p, AbstractCreature target, AbstractCreature source) {
        if (target != source && source == owner && p.type == PowerType.DEBUFF && source.hasPower(p.ID)) {
            flash();
            AbstractDungeon.actionManager.addToTop(new RemoveSpecificPowerAction(source, source, p.ID));
        }
    }

    @Override
    public AbstractPower makeCopy() {
        return new HotSpringsPower(owner);
    }
}
