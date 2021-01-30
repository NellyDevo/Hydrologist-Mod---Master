package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class WaterWheelAction extends AbstractGameAction {
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private int energyOnUse;
    private boolean freeToPlayOnce;
    private int multiplier;

    public WaterWheelAction(int energyOnUse, boolean freeToPlayOnce, int multiplier) {
        this.duration = DURATION;
        this.energyOnUse = energyOnUse;
        this.freeToPlayOnce = freeToPlayOnce;
        this.multiplier = multiplier;
    }

    @Override
    public void update() {
        int effect = EnergyPanel.totalCount;
        if (energyOnUse != -1) {
            effect = energyOnUse;
        }
        if (AbstractDungeon.player.hasRelic(ChemicalX.ID)) {
            effect += ChemicalX.BOOST;
            AbstractDungeon.player.getRelic(ChemicalX.ID).flash();
        }
        effect *= multiplier;
        final int tmp = effect;
        TransmuteCardAction action = new TransmuteCardAction();
        if (tmp > 0) {
            action.purity += tmp;
        }
        AbstractDungeon.actionManager.addToTop(action);
        if (!freeToPlayOnce) {
            AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
        }
        isDone = true;
    }
}