package hydrologistmod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.status.Wound;

public interface SwappableCard {
    default boolean canSwap() {
        return true;
    }

    default String getUnableToSwapString() {
        return "";
    }

    default void onSwapIn() {

    }

    default void onSwapOut() {

    }

    default boolean hasDefaultPair() {
        return false;
    }

    default AbstractCard createDefaultPair() {
        System.out.println("WARNING: Wound pairing created. How did we get here?");
        return new Wound();
    }
}
