package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;

public class LaminarFlowModifier extends AbstractCardModifier {
    public int increase;

    public LaminarFlowModifier(int increase) {
        this.increase = increase;
    }

    public AbstractCardModifier makeCopy() {
        return new LaminarFlowModifier(increase);
    }
}
