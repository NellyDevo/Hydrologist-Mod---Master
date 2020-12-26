package hydrologistmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import hydrologistmod.cards.UnstableDefend;
import hydrologistmod.cards.UnstableStrike;
import javassist.CtBehavior;

public class FixOldSavePatch {
    @SpirePatch(
            clz = CardCrawlGame.class,
            method = "loadPlayerSave"
    )
    public static class FixUnstableStrikesLoadPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"s"}
        )
        public static void Insert(CardCrawlGame __instance, CardSave s) {
            if (s.id.equals(UnstableStrike.ID)) {
                s.id = UnstableDefend.ID;
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "addToTop");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
