package hydrologistmod.actions;


import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GetAllInBattleInstances;

import java.util.UUID;

public class SkeletalIncreaseMiscAction extends AbstractGameAction {
    private int miscIncrease;
    private UUID uuid;

    public SkeletalIncreaseMiscAction(UUID targetUUID, final int miscIncrease) {
        this.miscIncrease = miscIncrease;
        this.uuid = targetUUID;
    }

    @Override
    public void update() {
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (!c.uuid.equals(this.uuid)) {
                continue;
            }
            c.misc += this.miscIncrease;
            c.applyPowers();
        }
        for (AbstractCard c : GetAllInBattleInstances.get(this.uuid)) {
            c.misc += this.miscIncrease;
            c.applyPowers();
        }
        this.isDone = true;
    }
}
