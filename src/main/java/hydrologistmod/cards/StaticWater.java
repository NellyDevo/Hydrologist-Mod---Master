package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class StaticWater extends AbstractAdaptiveCard {
    public static final String ID = "hydrologistmod:StaticWater";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/static_water.png";
    private static final int COST = 0;
    private static final int ADAPTIVE_AMT = 3;
    private static final int UPGRADE_ADAPTIVE = 1;

    public StaticWater() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.NONE, ADAPTIVE_AMT);
        assignHydrologistSubtype(HydrologistTags.WATER);
        tags.add(HydrologistTags.CARES_ABOUT_SUBTYPES);
    }

    @Override
    public String getDescription() {
        return EXTENDED_DESCRIPTION[0];
    }

    @Override
    public AbstractCard makeCopy() {
        return new StaticWater();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeAdaptiveNumber(UPGRADE_ADAPTIVE);
        }
    }
}
