package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class LaminarFlowModifier extends AbstractTemporaryCardmod {
    public static final String ID = "hydrologistmod:LaminarFlowModifier";
    public int increase;

    public LaminarFlowModifier(int increase) {
        this.increase = increase;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new LaminarFlowModifier(increase);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.baseMagicNumber += increase;
        card.magicNumber = card.baseMagicNumber;
    }

    @Override
    public void onRemove(AbstractCard card) {
        card.baseMagicNumber -= increase;
        card.magicNumber = card.baseMagicNumber;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, ID)) {
            ((LaminarFlowModifier)CardModifierManager.getModifiers(card, ID).get(0)).increase += increase;
            card.baseMagicNumber += increase;
            card.magicNumber = card.baseMagicNumber;
            return false;
        }
        return true;
    }
}
