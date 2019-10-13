package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class EncapsulatingIceAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:EncapsulatingIceAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractCard card;
    private int energyOnUse;
    private boolean freeToPlayOnce;
    private boolean upgraded;

    public EncapsulatingIceAction(AbstractCard card, int energyOnUse, boolean upgraded, boolean freeToPlayOnce) {
        this.duration = DURATION;
        this.card = card;
        this.energyOnUse = energyOnUse;
        this.upgraded = upgraded;
        this.freeToPlayOnce = freeToPlayOnce;
    }

    @Override
    public void update() {
        int effect = EnergyPanel.totalCount;
        if (energyOnUse != -1) {
            effect = energyOnUse;
        }
        if (upgraded) {
            ++effect;
        }
        if (AbstractDungeon.player.hasRelic(ChemicalX.ID)) {
            effect += 2;
            AbstractDungeon.player.getRelic(ChemicalX.ID).flash();
        }
        if (effect > 0) {
            card.misc += effect;
            if (!freeToPlayOnce) {// 60
                AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
            }
        }
        isDone = true;
    }
}