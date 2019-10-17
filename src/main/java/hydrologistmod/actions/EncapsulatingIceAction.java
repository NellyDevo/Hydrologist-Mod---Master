package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import hydrologistmod.helpers.SwapperHelper;

import java.util.UUID;

public class EncapsulatingIceAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:EncapsulatingIceAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private UUID originalUuid;
    private UUID pairedUuid;
    private int energyOnUse;
    private boolean freeToPlayOnce;
    private boolean upgraded;

    public EncapsulatingIceAction(UUID originalUuid, UUID pairedUuid, int energyOnUse, boolean upgraded, boolean freeToPlayOnce) {
        this.duration = DURATION;
        this.originalUuid = originalUuid;
        this.pairedUuid = pairedUuid;
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
            for (AbstractCard card : GetAllInBattleInstances.get(originalUuid)) {
                SwapperHelper.getNextCard(card).misc += effect;
                SwapperHelper.getNextCard(card).applyPowers();
            }
            for (AbstractCard card : GetAllInBattleInstances.get(pairedUuid)) {
                card.misc += effect;
                card.applyPowers();
            }
            if (!freeToPlayOnce) {
                AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
            }
        }

        isDone = true;
    }
}