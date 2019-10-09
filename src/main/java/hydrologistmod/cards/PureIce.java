package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class PureIce extends AbstractHydrologistCard implements TransmutableCard {
    public static final String ID = "hydrologistmod:PureIce";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/PureIce.png";
    private static final int COST = 0;
    private static final int BLOCK_AMT = 3;
    private static final int UPGRADE_BLOCK_AMT = 2;

    public PureIce() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.SELF);
        tags.add(HydrologistTags.ICE);
        block = baseBlock = BLOCK_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
    }

    @Override
    public AbstractCard makeCopy() {
        return new PureIce();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(UPGRADE_BLOCK_AMT);
        }
    }

    @Override
    public void onTransmuted(AbstractCard newCard) {
        addToBot(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, block));
    }
}
