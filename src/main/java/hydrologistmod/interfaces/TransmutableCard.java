package hydrologistmod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface TransmutableCard {
    default void onTransmuted(AbstractCard newCard) {}
}
