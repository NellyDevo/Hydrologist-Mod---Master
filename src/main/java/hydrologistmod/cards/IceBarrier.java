package hydrologistmod.cards;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.TransmuteCardAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.patches.IceBarrierExternalBlock;

public class IceBarrier extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:IceBarrier";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/IceBarrier.png";
    private static final int COST = 1;
    public static final int BLOCK_AMT = 5;
    private static final int UPGRADE_BLOCK_AMT = 3;
    private static final int COLD_AMT = 3;
    private static final int UPGRADE_COLD_AMT = 1;

    public IceBarrier() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.SELF);
        block = baseBlock = BLOCK_AMT;
        magicNumber = baseMagicNumber = COLD_AMT;
        assignHydrologistSubtype(HydrologistTags.ICE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new TransmuteCardAction(this, (AbstractCard c) -> {
            CardModifierManager.addModifier(c, new GainBlockModifier(BLOCK_AMT, UPGRADE_BLOCK_AMT));
            if (upgraded) {
                c.upgrade();
            }
            c.applyPowers();
        }));
    }

    @Override
    public AbstractCard makeCopy() {
        return new IceBarrier();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_COLD_AMT);
            upgradeBlock(UPGRADE_BLOCK_AMT);
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    public static class GainBlockModifier extends AbstractCardModifier {
        private int baseExtraBlock;
        private int upgradeExtraBlock;

        public GainBlockModifier(int baseExtraBlock, int upgradeExtraBlock) {
            this.baseExtraBlock = baseExtraBlock;
            this.upgradeExtraBlock = upgradeExtraBlock;
        }

        @Override
        public void onApplyPowers(AbstractCard card) {
            int extraBlock = baseExtraBlock;
            if (card.upgraded) {
                extraBlock += upgradeExtraBlock;
            }
            IceBarrierExternalBlock.DynamicVariableFields.iceBarrierBaseBlock.set(card, extraBlock);
            AbstractCard c = new IceBarrier();
            if (card.upgraded) {
                c.upgrade();
            }
            c.applyPowers();
            IceBarrierExternalBlock.DynamicVariableFields.iceBarrierBlock.set(card, c.block);
            IceBarrierExternalBlock.DynamicVariableFields.iceBarrierIsBlockModified.set(card, c.isBlockModified);
        }

        @Override
        public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
            AbstractDungeon.actionManager.addToTop(new GainBlockAction(AbstractDungeon.player, IceBarrierExternalBlock.DynamicVariableFields.iceBarrierBlock.get(card)));
        }

        @Override
        public String modifyDescription(String rawDescription, AbstractCard card) {
            return "Gain !hydrologistmod:ICE! Block. NL " + rawDescription;
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new GainBlockModifier(baseExtraBlock, upgradeExtraBlock);
        }
    }
}
