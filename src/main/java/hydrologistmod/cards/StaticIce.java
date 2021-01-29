package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.TransmuteCardAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class StaticIce extends AbstractAdaptiveCard {
    public static final String ID = "hydrologistmod:StaticIce";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/StaticIce.png";
    private static final int COST = 0;
    private static final int ADAPTIVE_AMT = 2;
    private static final int UPGRADE_ADAPTIVE = 1;

    public StaticIce() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.NONE, ADAPTIVE_AMT);
        assignHydrologistSubtype(HydrologistTags.ICE);
        tags.add(HydrologistTags.CARES_ABOUT_SUBTYPES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new TransmuteCardAction());
        super.use(p, m);
    }

    @Override
    public String getDescription() {
        return EXTENDED_DESCRIPTION[0];
    }

    @Override
    public AbstractCard makeCopy() {
        return new StaticIce();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeAdaptiveNumber(UPGRADE_ADAPTIVE);
        }
    }
}
