package hydrologistmod.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;
import hydrologistmod.helpers.SwapperHelper;

import java.util.UUID;

public class IncreasePairedMiscAction extends AbstractGameAction {
    private int miscIncrease;
    private UUID uuid;

    public IncreasePairedMiscAction(UUID targetUUID, final int miscIncrease) {
        this.miscIncrease = miscIncrease;
        this.uuid = targetUUID;
    }

    @Override
    public void update() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (!c.uuid.equals(this.uuid)) {
                continue;
            }
            SwapperHelper.getMasterDeckPair(c).misc += this.miscIncrease;
            SwapperHelper.getMasterDeckPair(c).applyPowers();
        }
        for (AbstractCard c : GetAllInBattleInstances.get(this.uuid)) {
            SwapperHelper.getPairedCard(c).misc += this.miscIncrease;
            SwapperHelper.getPairedCard(c).applyPowers();
        }
        this.isDone = true;
    }
}
