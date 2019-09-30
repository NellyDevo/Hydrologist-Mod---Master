package hydrologistmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import hydrologistmod.HydrologistMod;

public class CorporealPatch {

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "applyStartOfTurnRelics"
    )
    public static class AbstractPlayerApplyStartOfTurnRelicsPatch {

        public static void Prefix(AbstractPlayer __instance) {
            HydrologistMod.determineNonCorporealCards();
        }
    }
}
