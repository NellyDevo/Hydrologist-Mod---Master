package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.actions.TransmuteCardAction;
import hydrologistmod.interfaces.TransmutableAffectingPower;

public class ExothermicReactionsPower extends AbstractPower implements TransmutableAffectingPower, CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:ExothermicReactionsPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public ExothermicReactionsPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/ExothermicReactionsPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/ExothermicReactionsPower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        this.amount = amount;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new ExothermicReactionsPower(owner, amount);
    }

    @Override
    public void onTransmute(TransmuteCardAction action) {
        flash();
        AbstractPlayer p = AbstractDungeon.player;
        AbstractMonster m = AbstractDungeon.getMonsters().getRandomMonster(true);
        if (m != null) {
            AbstractDungeon.actionManager.addToTop(new DamageAction(m, new DamageInfo(p, amount, DamageInfo.DamageType.THORNS)));
        }
    }
}
