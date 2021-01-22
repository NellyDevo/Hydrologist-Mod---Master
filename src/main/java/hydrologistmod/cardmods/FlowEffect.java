package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import hydrologistmod.actions.FlowAction;

@AbstractCardModifier.SaveIgnore
public class FlowEffect extends AbstractExtraEffectModifier {
    public FlowEffect(AbstractCard card, boolean isMutable) {
        super(card, VariableType.DAMAGE, isMutable);
        priority = 0;
    }

    @Override
    public void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m) {
        addToBot(new FlowAction());
    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard card) {
        String s = " hydrologistmod:Flow.";
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
    public AbstractCardModifier makeCopy() {
        return new FlowEffect(attachedCard, isMutable);
    }
}
