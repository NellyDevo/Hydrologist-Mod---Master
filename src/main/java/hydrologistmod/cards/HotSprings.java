package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.HotSpringsPower;

public class HotSprings extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:HotSprings";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/hot_springs.png";
    private static final int COST = 1;
    private static final int POWER_AMOUNT = 6;
    private static final int UPGRADE_POWER_AMOUNT = 2;

    public HotSprings() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.WATER);
        magicNumber = baseMagicNumber = POWER_AMOUNT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new HotSpringsPower(p, magicNumber)));
    }

    @Override
    public AbstractCard makeCopy() {
        return new HotSprings();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_POWER_AMOUNT);
        }
    }
}
