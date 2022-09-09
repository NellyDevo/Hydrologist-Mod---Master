package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.FlowAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class Cascade extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Cascade";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/cascade.png";
    private static final int COST = 1;
    private static final int BLOCK_AMT = 4;
    private static final int UPGRADE_BLOCK_AMT = 1;

    public Cascade() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.SELF);
        assignHydrologistSubtype(HydrologistTags.WATER);
        block = baseBlock = BLOCK_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new FlowAction((ArrayList<AbstractCard> cardsDiscarded) -> {
            for (int i = 0; i < cardsDiscarded.size(); ++i) {
                addToTop(new GainBlockAction(p, p, block));
            }
        }));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Cascade();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(UPGRADE_BLOCK_AMT);
        }
    }
}
