package hydrologistmod.interfaces;

import hydrologistmod.actions.TransmuteCardAction;

public interface TransmutableAffectingPower {
    default void onTransmute(TransmuteCardAction action) {}
}
