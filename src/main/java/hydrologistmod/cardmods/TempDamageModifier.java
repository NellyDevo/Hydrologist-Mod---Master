package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class TempDamageModifier extends AbstractTemporaryCardmod {
    public static final String ID = "hydrologistmod:TempDamageModifier";
    public int increase;

    public TempDamageModifier(int increase) {
        this.increase = increase;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TempDamageModifier(increase);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.baseDamage += increase;
        card.applyPowers();
    }

    @Override
    public void onRemove(AbstractCard card) {
        card.baseDamage -= increase;
        card.applyPowers();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, ID)) {
            ((TempDamageModifier)CardModifierManager.getModifiers(card, ID).get(0)).increase += increase;
            card.baseDamage += increase;
            card.applyPowers();
            return false;
        }
        return true;
    }
}
