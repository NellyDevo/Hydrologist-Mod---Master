package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.ArrayList;

@AbstractCardModifier.SaveIgnore
public class ExtraPurityEffect extends AbstractExtraEffectModifier {
    public static final String ID = "hydrologistmod:ExtraPurityEffect";

    public ExtraPurityEffect(AbstractCard card, boolean isMutable) {
        super(card, VariableType.MAGIC, isMutable, 1);
        priority = 1;
    }

    @Override
    public void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m) {

    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, ID)) {
            ArrayList<AbstractCardModifier> list = CardModifierManager.getModifiers(card, ID);
            for (AbstractCardModifier mod : list) {
                AbstractCard c = ((AbstractExtraEffectModifier)mod).attachedCard;
                if (c.baseMagicNumber == attachedCard.baseMagicNumber) {
                    ((AbstractExtraEffectModifier)mod).amount++;
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        super.onApplyPowers(card);
        value *= amount;
    }

    @Override
    public boolean shouldRenderValue() {
        return true;
    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard card) {
        String s =  "When hydrologistmod:Transmuted, this gains " + key + " hydrologistmod:Purity.";
        if (isMutable) {
            s = "hydrologistmod:Mutable: " + s;
        }
        return rawDescription + " NL " + s;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        CardModifierManager.addModifier(card, new PurityModifier(value));
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ExtraPurityEffect(attachedCard, isMutable);
    }
}
