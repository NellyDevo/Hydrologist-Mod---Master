package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

@AbstractCardModifier.SaveIgnore
public class GainBlockEffect extends AbstractExtraEffectModifier {
    public GainBlockEffect(AbstractCard card, boolean isMutable) {
        super(card, VariableType.BLOCK, isMutable);
        priority = -1;
    }

    @Override
    public void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m) {
        addToTop(new GainBlockAction(p, p, value));
    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard card) {
        return "hydrologistmod:Mutable: Gain " + key + " Block. NL " + rawDescription;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GainBlockEffect(attachedCard.makeStatEquivalentCopy(), isMutable);
    }
}
