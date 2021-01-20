package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;

public class ExtraPurityEffect extends AbstractExtraEffectModifier {
    public ExtraPurityEffect(AbstractCard card, boolean isMutable) {
        super(card, VariableType.MAGIC, isMutable);
        priority = 1;
    }

    @Override
    public void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m) {

    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard card) {
        String s =  "When hydrologistmod:Transmuted, gains " + key + " hydrologistmod:Purity.";
        if (isMutable) {
            s = "NL hydrologistmod:Mutable: " + s;
        }
        return rawDescription + s;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        CardModifierManager.addModifier(card, new PurityModifier(value));
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return null;
    }
}
