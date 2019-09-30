package hydrologistmod.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.OnCardDrawPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class AqueductsPower extends AbstractPower implements OnCardDrawPower, CloneablePowerInterface {
    public static final String POWER_ID = "hydrologistmod:AqueductsPower";
    public static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private int cardsDrawnThisTurn = 0;

    public AqueductsPower(AbstractCreature owner, int amount) {
        name = NAME;
        ID = POWER_ID;
        this.owner = owner;
        region128 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/AqueductsPower84.png"), 0, 0, 84, 84);
        region48 = new TextureAtlas.AtlasRegion(ImageMaster.loadImage("hydrologistmod/images/powers/AqueductsPower32.png"), 0, 0, 32, 32);
        type = PowerType.BUFF;
        this.amount = amount;
        updateDescription();
        cardsDrawnThisTurn = AbstractDungeon.player.gameHandSize;
    }

    @Override
    public void updateDescription() {

    }

    @Override
    public void onCardDraw(AbstractCard c) {
        ++cardsDrawnThisTurn;
        if (cardsDrawnThisTurn > AbstractDungeon.player.gameHandSize) {
            flashWithoutSound();
            int[] array = new int[AbstractDungeon.getMonsters().monsters.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = amount;
            }
            AbstractDungeon.actionManager.addToTop(new DamageAllEnemiesAction(owner, array, DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.NONE));
        }
    }

    @Override
    public void atStartOfTurn() {
        cardsDrawnThisTurn = 0;
    }

    @Override
    public AbstractPower makeCopy() {
        return new AqueductsPower(owner, amount);
    }
}
