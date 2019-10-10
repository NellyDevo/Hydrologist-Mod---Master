package hydrologistmod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import hydrologistmod.actions.TransmuteCardAction;

public interface TransmutableAffectingPower {
    default void onTransmute(TransmuteCardAction action) {}

    default void affectTransmutedCard(AbstractCard newCard) {}
}
