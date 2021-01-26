package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import hydrologistmod.actions.FlowAction;

@AbstractCardModifier.SaveIgnore
public class FlowEffect extends AbstractExtraEffectModifier {
    public static final String ID = "hydrologistmod:FlowEffect";

    public FlowEffect(AbstractCard card, boolean isMutable, int times) {
        super(card, VariableType.DAMAGE, isMutable, times);
        priority = 0;
    }

    @Override
    public void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m) {
        for (int i = 0; i < amount; ++i) {
            addToBot(new FlowAction());
        }
    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard card) {
        String s = " hydrologistmod:Flow";
        if (amount == 1) {
            s += ".";
        } else {
            s += " " + amount + " times.";
        }
        if (isMutable) {
            s = " hydrologistmod:Mutable:" + s;
        }
        return rawDescription + " NL " + s;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, ID)) {
            ((AbstractExtraEffectModifier)CardModifierManager.getModifiers(card, ID).get(0)).amount++;
            card.applyPowers();
            card.initializeDescription();
            return false;
        }
        return true;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public boolean shouldRenderValue() {
        return false;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new FlowEffect(attachedCard, isMutable, amount);
    }
}
