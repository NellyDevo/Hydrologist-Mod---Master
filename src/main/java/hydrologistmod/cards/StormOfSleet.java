package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.StormOfSleetAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class StormOfSleet extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:StormOfSleet";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/storm_of_sleet.png";
    private static final int COST = 2;
    private static final int UPGRADED_COST = 1;

    public StormOfSleet() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.ICE);
        cardsToPreview = new RazorIce();
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new StormOfSleetAction());
    }

    @Override
    public AbstractCard makeCopy() {
        return new StormOfSleet();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
        }
    }
}
