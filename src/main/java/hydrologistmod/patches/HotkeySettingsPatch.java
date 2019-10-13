package hydrologistmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.options.InputSettingsScreen;
import com.megacrit.cardcrawl.screens.options.RemapInputElement;
import hydrologistmod.helpers.SwapperInputActionSet;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz=InputSettingsScreen.class,
        method="refreshData"
)
public class HotkeySettingsPatch
{
    private static class Strings
    {
        private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("hydrologistmod:InputSettingsScreen");
    }

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"elements"}
    )
    public static void Insert(InputSettingsScreen __instance, ArrayList<RemapInputElement> elements)
    {
        if (!Settings.isControllerMode) {
            elements.add(new RemapInputElement(__instance, Strings.uiStrings.TEXT[0], SwapperInputActionSet.swapCard));
        }
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(InputSettingsScreen.class, "maxScrollAmount");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
