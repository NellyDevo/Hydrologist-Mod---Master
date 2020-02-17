package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.FrozenDomainAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class FrozenDomain extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:FrozenDomain";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/FrozenDomain.png";
    private static final int COST = -2;

    public FrozenDomain() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.ICE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public boolean canUse(AbstractPlayer p, AbstractMonster m) {
        cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
        return false;
    }

    @Override
    public void triggerOnManualDiscard() {
        addToTop(new FrozenDomainAction(upgraded));
        addToTop(new ExhaustSpecificCardAction(this, AbstractDungeon.player.discardPile));
    }

    @Override
    public AbstractCard makeCopy() {
        return new FrozenDomain();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
        }
    }
}
