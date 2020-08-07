package hydrologistmod.interfaces;

public interface FlowAffectingRelic {
    default int leftoversToKeep(int leftovers) {
        return 0;
    }
}
