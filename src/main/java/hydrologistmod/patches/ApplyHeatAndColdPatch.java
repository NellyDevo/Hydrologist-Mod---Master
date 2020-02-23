package hydrologistmod.patches;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.TextAboveCreatureAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.interfaces.HeatAndColdPower;
import javassist.CtBehavior;

@SpirePatch(
        clz= ApplyPowerAction.class,
        method="update"
)
public class ApplyHeatAndColdPatch {

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"duration", "powerToApply"}
    )
    public static SpireReturn<Void> Insert(ApplyPowerAction __instance, @ByRef float[] duration, AbstractPower powerToApply) {
        return checkPower(__instance, __instance.target, __instance.source, duration, powerToApply);
    }

    private static SpireReturn<Void> checkPower(ApplyPowerAction action, AbstractCreature target, AbstractCreature source, float[] duration, AbstractPower powerToApply) {
        for (AbstractPower power : target.powers) {
            if (power instanceof HeatAndColdPower) {
                boolean apply = ((HeatAndColdPower)power).heatAndColdOnApplyPower(powerToApply, target, source);
                if (!apply) {
                    AbstractDungeon.actionManager.addToTop(new TextAboveCreatureAction(target, "Shocked"));
                    duration[0] -= Gdx.graphics.getDeltaTime();
                    CardCrawlGame.sound.play("NULLIFY_SFX");
                    return SpireReturn.Return(null);
                }
            }
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "effectList");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
