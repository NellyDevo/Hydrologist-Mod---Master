package hydrologistmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import helpers.SwapperInputActionSet;

public class HotkeysPatch
{
    @SpirePatch(
            clz=InputActionSet.class,
            method="load"
    )
    public static class Load
    {
        public static void Prefix()
        {
            SwapperInputActionSet.load();
        }
    }

    @SpirePatch(
            clz=InputActionSet.class,
            method="save"
    )
    public static class Save
    {
        public static void Prefix()
        {
            SwapperInputActionSet.save();
        }
    }

    @SpirePatch(
            clz=InputActionSet.class,
            method="resetToDefaults"
    )
    public static class Reset
    {
        public static void Prefix()
        {
            SwapperInputActionSet.resetToDefaults();
        }
    }
}