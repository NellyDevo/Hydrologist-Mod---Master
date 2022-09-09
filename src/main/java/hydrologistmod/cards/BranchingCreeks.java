package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.BranchingCreeksPower;

public class BranchingCreeks extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:BranchingCreeks";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/branching_creeks.png";
    private static final int COST = 1;
    private static final int POWER_AMOUNT = 1;
    private static final int UPGRADE_AMOUNT = 1;

    public BranchingCreeks() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.NONE);
        magicNumber = baseMagicNumber = POWER_AMOUNT;
        assignHydrologistSubtype(HydrologistTags.WATER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new BranchingCreeksPower(p, magicNumber), magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new BranchingCreeks();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_AMOUNT);
        }
    }
}
