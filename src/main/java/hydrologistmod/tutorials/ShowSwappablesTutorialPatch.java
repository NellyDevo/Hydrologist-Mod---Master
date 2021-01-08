package hydrologistmod.tutorials;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.ui.FtueTip;
import hydrologistmod.HydrologistMod;
import hydrologistmod.helpers.SwapperHelper;

import java.io.IOException;

@SpirePatch(
        clz = DrawCardAction.class,
        method = "update"
)
public class ShowSwappablesTutorialPatch {
    public static final String ID = "hydrologistmod:SwappablesTutorial";
    public static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(ID);
    public static final String[] LABEL = tutorialStrings.LABEL;
    public static final String[] TEXT = tutorialStrings.TEXT;

    public static void Postfix(DrawCardAction __instance) {
        if (__instance.isDone && !HydrologistMod.hydrologistConfig.getBool("Swappables Tutorial Seen")) {
            for (AbstractCard c : DrawCardAction.drawnCards) {
                if (SwapperHelper.isCardSwappable(c)) {
                    AbstractDungeon.ftue = new FtueTip(LABEL[0], TEXT[0], Settings.WIDTH / 2.0f - (500.0f * Settings.scale), Settings.HEIGHT / 2.0f, c);
                    AbstractDungeon.ftue.type = FtueTip.TipType.POWER;
                    HydrologistMod.hydrologistConfig.setBool("Swappables Tutorial Seen", true);
                    try { HydrologistMod.hydrologistConfig.save(); } catch (IOException e) { e.printStackTrace(); }
                    break;
                }
            }
        }
    }
}
