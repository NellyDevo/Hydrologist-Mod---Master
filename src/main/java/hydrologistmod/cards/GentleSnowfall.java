package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import hydrologistmod.actions.FlowAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class GentleSnowfall extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:GentleSnowfall";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/GentleSnowfall.png";
    private static final int COST = 1;
    private static final int CARD_REQUIREMENT = 9;
    private static final int UPGRADE_REQUIREMENT = -1;

    public GentleSnowfall() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.SELF);
        assignHydrologistSubtype(HydrologistTags.ICE);
        magicNumber = baseMagicNumber = CARD_REQUIREMENT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new FlowAction((int cardsDiscarded) -> {
            if (cardsDiscarded >= magicNumber) {
                addToTop(new ApplyPowerAction(p, p, new IntangiblePlayerPower(p, 1), 1));
            }
        }));
    }

    @Override
    public AbstractCard makeCopy() {
        return new GentleSnowfall();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_REQUIREMENT);
        }
    }
}
