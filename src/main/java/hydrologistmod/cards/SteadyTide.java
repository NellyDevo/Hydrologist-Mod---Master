package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DrawCardNextTurnPower;
import hydrologistmod.actions.FlowAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class SteadyTide extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:SteadyTide";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/SteadyTide.png";
    private static final int COST = 1;
    private static final int UPGRADED_COST = 0;

    public SteadyTide() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.SELF);
        assignHydrologistSubtype(HydrologistTags.WATER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new FlowAction((amount) -> addToTop(new ApplyPowerAction(p, p, new DrawCardNextTurnPower(p, amount)))));
    }

    @Override
    public AbstractCard makeCopy() {
        return new SteadyTide();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
        }
    }
}
