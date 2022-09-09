package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class FreezeDry extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:FreezeDry";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/freeze_dry.png";
    private static final int COST = 1;
    private static final int SHIV_COUNT = 2;
    private static final int UPGRADE_SHIV_COUNT = 1;

    public FreezeDry() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        magicNumber = baseMagicNumber = SHIV_COUNT;
        cardsToPreview = new RazorIce();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new MakeTempCardInHandAction(new RazorIce(), magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new FreezeDry();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_SHIV_COUNT);
        }
    }
}
