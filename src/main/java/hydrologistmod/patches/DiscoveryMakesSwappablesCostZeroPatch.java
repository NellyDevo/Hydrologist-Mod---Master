package hydrologistmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.unique.DiscoveryAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import hydrologistmod.helpers.SwapperHelper;
import javassist.CtBehavior;

@SpirePatch(
        clz = DiscoveryAction.class,
        method = "update"
)
public class DiscoveryMakesSwappablesCostZeroPatch {
    @SpireInsertPatch(
            locator = Locator.class,
            localvars = {"disCard", "disCard2"}
    )
    public static void Insert(DiscoveryAction __instance, AbstractCard disCard, AbstractCard disCard2) {
        if (SwapperHelper.isCardSwappable(disCard)) {
            for (AbstractCard card : SwapperCardPatch.SwappableChainField.swappableCards.get(disCard)) {
                card.setCostForTurn(0);
            }
        }
        if (SwapperHelper.isCardSwappable(disCard2)) {
            for (AbstractCard card : SwapperCardPatch.SwappableChainField.swappableCards.get(disCard2)) {
                card.setCostForTurn(0);
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(DiscoveryAction.class, "amount");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
