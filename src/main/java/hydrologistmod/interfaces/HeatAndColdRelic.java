package hydrologistmod.interfaces;

public interface HeatAndColdRelic {
    default int increaseColdPercent() {
        return 0;
    }

    default int increaseHeatPercent() {
        return 0;
    }
}
