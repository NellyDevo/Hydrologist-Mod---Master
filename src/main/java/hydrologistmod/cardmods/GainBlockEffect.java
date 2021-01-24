package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.ArrayList;

@AbstractCardModifier.SaveIgnore
public class GainBlockEffect extends AbstractExtraEffectModifier {
    public static final String ID = "hydrologistmod:GainBlockEffect";

    public GainBlockEffect(AbstractCard card, boolean isMutable, int times) {
        super(card, VariableType.BLOCK, isMutable, times);
        priority = -1;
    }

    @Override
    public void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m) {
        for (int i = 0; i < amount; ++i) {
            addToTop(new GainBlockAction(p, p, value));
        }
    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard card) {
        String s = "Gain " + key + " Block";
        if (amount == 1) {
            s += ".";
        } else {
            s += " " + amount + " times.";
        }
        if (isMutable) {
            s = "hydrologistmod:Mutable: " + s;
        }
        return s + " NL " + rawDescription;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, ID)) {
            ArrayList<AbstractCardModifier> list = CardModifierManager.getModifiers(card, ID);
            for (AbstractCardModifier mod : list) {
                AbstractCard c = ((AbstractExtraEffectModifier)mod).attachedCard;
                if (c.baseBlock == attachedCard.baseBlock) {
                    ((AbstractExtraEffectModifier)mod).amount++;
                    card.initializeDescription();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public boolean shouldRenderValue() {
        return true;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GainBlockEffect(attachedCard.makeStatEquivalentCopy(), isMutable, amount);
    }
}
