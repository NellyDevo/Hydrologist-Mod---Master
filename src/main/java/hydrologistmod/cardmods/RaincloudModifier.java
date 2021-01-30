package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

public class RaincloudModifier extends AbstractCardModifier {
    protected static final String ID = "hydrologistmod:RaincloudModifier";
    protected static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    protected static final String[] TEXT = cardStrings.EXTENDED_DESCRIPTION;

    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + TEXT[0];
    }

    public AbstractCardModifier makeCopy() {
        return new RaincloudModifier();
    }
}
