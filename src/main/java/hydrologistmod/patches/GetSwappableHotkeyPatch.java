package hydrologistmod.patches;

import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.helpers.TipHelper;
import hydrologistmod.cards.AbstractHydrologistCard;
import hydrologistmod.helpers.SwapperInputActionSet;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.util.TreeMap;

public class GetSwappableHotkeyPatch {

    @SpirePatch(
            clz = TipHelper.class,
            method = "renderKeywords"
    )
    public static class TipHelperRenderKeywordsPatch {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(TreeMap.class.getName()) && m.getMethodName().equals("get")) {
                        m.replace("$_ = hydrologistmod.patches.GetSwappableHotkeyPatch.getKeywordString($$);");
                    }
                }
            };
        }
    }

    @SpirePatch(
            clz = TipHelper.class,
            method = "renderBox"
    )
    public static class TipHelperRenderBoxPatch {
        public static ExprEditor Instrument() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getClassName().equals(TreeMap.class.getName()) && m.getMethodName().equals("get")) {
                        m.replace("$_ = hydrologistmod.patches.GetSwappableHotkeyPatch.getKeywordString($$);");
                    }
                }
            };
        }
    }

    public static String getKeywordString(Object s) {
        String body = GameDictionary.keywords.get(s);
        if (s.equals(BaseMod.getKeywordPrefix(AbstractHydrologistCard.swappable.toLowerCase()) + BaseMod.getKeywordUnique(AbstractHydrologistCard.swappable.toLowerCase()))) {
            body += SwapperInputActionSet.swapCard.getKeyString() + ".";
        }
        return body;
    }
}
