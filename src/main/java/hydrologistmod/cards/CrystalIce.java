package hydrologistmod.cards;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.Arrays;
import java.util.LinkedList;

public class CrystalIce extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:CrystalIce";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/CrystalIce.png";
    private static final int COST = 1;
    private static final int BLOCK_AMT = 4;
    private static final int UPGRADE_BLOCK_AMT = 2;

    public CrystalIce() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.ICE);
        SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new LaminarFlow())));
        block = baseBlock = BLOCK_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        AbstractCard pairCard = SwapperHelper.getNextCard(this);
        if (pairCard != null) {
            CardModifierManager.addModifier(pairCard, new LaminarFlowModifier(block));
            pairCard.applyPowers();
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new CrystalIce();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeBlock(UPGRADE_BLOCK_AMT);
        }
    }

    public static class LaminarFlowModifier extends AbstractCardModifier {
        public int increase;

        public LaminarFlowModifier(int increase) {
            this.increase = increase;
        }

        public AbstractCardModifier makeCopy() {
            return new LaminarFlowModifier(increase);
        }
    }
}
