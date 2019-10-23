package hydrologistmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import hydrologistmod.cards.HeatBlast;
import javassist.CtBehavior;


@SpirePatch(
        clz= AbstractCard.class,
        method="calculateCardDamage"
)
public class HeatBlastApplyVulnerablePatch {

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"p","tmp"}
    )
    public static void Insert(AbstractCard __instance, AbstractMonster mo, AbstractPower p, @ByRef float[] tmp) {
        if (p instanceof VulnerablePower && __instance instanceof HeatBlast && p.amount > 1) {
            for (int i = 1; i < p.amount; ++i) {
                tmp[0] = p.atDamageReceive(tmp[0], __instance.damageTypeForTurn);
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPower.class, "atDamageReceive");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
