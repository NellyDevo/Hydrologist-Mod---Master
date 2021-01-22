package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import hydrologistmod.actions.TransmuteCardAction;

@AbstractCardModifier.SaveIgnore
public class TransmuteSelfEffect extends AbstractExtraEffectModifier {
    public String ID = "hydrologistmod:TransmuteSelfEffect";

    public TransmuteSelfEffect(AbstractCard card, boolean isMutable) {
        super(card, VariableType.MAGIC, isMutable);
        priority = 0;
    }

    @Override
    public void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m) {
        addToBot(new TransmuteCardAction(card));
    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard card) {
        String s = " hydrologistmod:Transmute this card.";
        if (isMutable) {
            s = " hydrologistmod:Mutable:" + s;
        }
        return rawDescription + " NL " + s;
    }

    @Override
    public boolean shouldRenderValue() {
        return false;
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !CardModifierManager.hasModifier(card, ID);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TransmuteSelfEffect(attachedCard, isMutable);
    }
}
