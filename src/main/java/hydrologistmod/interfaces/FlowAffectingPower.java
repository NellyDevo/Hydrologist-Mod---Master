package hydrologistmod.interfaces;

public interface FlowAffectingPower {
    default void onFlow(int cardsDiscarded) {}
}
