package hydrologistmod.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import hydrologistmod.HydrologistMod;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class BottledWaterAction extends DiscoveryAction {
    public BottledWaterAction(int copies) {
        super(null, copies);
    }

    @SpireOverride
    protected ArrayList<AbstractCard> generateCardChoices(AbstractCard.CardType type) {
        ArrayList<AbstractCard> list = new ArrayList<>();
        list.add(HydrologistMod.returnTrulyRandomCardWithTagInCombat(HydrologistTags.ICE));
        list.add(HydrologistMod.returnTrulyRandomCardWithTagInCombat(HydrologistTags.WATER));
        list.add(HydrologistMod.returnTrulyRandomCardWithTagInCombat(HydrologistTags.STEAM));
        return list;
    }
}
