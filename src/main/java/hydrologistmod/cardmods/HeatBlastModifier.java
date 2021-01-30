package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class HeatBlastModifier extends AbstractCardModifier {
    public int amount;
    public static final String ID = "hydrologistmod:HeatBlastModifier";

    public HeatBlastModifier(int amount) {
        this.amount = amount;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        card.baseDamage += amount;
        card.applyPowers();
    }

    @Override
    public void onRemove(AbstractCard card) {
        card.baseDamage -= amount;
        card.applyPowers();
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, ID)) {
            ((HeatBlastModifier)CardModifierManager.getModifiers(card, ID).get(0)).amount += amount;
            card.baseDamage += amount;
            card.applyPowers();
            return false;
        }
        return true;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HeatBlastModifier(amount);
    }
}