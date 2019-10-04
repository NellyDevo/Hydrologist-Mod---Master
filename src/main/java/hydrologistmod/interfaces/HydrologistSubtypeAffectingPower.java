package hydrologistmod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface HydrologistSubtypeAffectingPower {
    default void beforeApplyPowers(AbstractCard card) {}
}
