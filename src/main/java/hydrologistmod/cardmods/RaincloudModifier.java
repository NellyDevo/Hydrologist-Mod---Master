package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class RaincloudModifier extends AbstractCardModifier {
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + " NL hydrologistmod:Swappable.";
    }

    public AbstractCardModifier makeCopy() {
        return new RaincloudModifier();
    }
}
