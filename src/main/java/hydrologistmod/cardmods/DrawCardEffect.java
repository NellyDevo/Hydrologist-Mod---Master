package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class DrawCardEffect extends AbstractExtraEffectModifier {
    public DrawCardEffect(AbstractCard card, boolean isMutable) {
        super(card, VariableType.MAGIC, isMutable);
        priority = 0;
    }

    @Override
    public void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m) {
        addToBot(new DrawCardAction(p, value));
    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard card) {
        String s;
        if (value == 1) {
            s = " Draw a card.";
        } else {
            s = " Draw " + key + " cards.";
        }
        if (isMutable) {
            s = "hydrologistmod:Mutable:" + s;
        }
        return rawDescription + " NL " + s;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DrawCardEffect(attachedCard, isMutable);
    }
}
