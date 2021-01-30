package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class TempBlockModifier extends AbstractTemporaryCardmod {
    public static final String ID = "hydrologistmod:TempBlockModifier";
    public int increase;

    public TempBlockModifier(int increase) {
        this.increase = increase;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TempBlockModifier(increase);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.baseBlock += increase;
        card.applyPowers();
    }

    @Override
    public void onRemove(AbstractCard card) {
        card.baseBlock -= increase;
        card.applyPowers();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, ID)) {
            ((TempBlockModifier)CardModifierManager.getModifiers(card, ID).get(0)).increase += increase;
            card.baseBlock += increase;
            card.applyPowers();
            return false;
        }
        return true;
    }
}
