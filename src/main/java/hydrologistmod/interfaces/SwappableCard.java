package hydrologistmod.interfaces;

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
}
