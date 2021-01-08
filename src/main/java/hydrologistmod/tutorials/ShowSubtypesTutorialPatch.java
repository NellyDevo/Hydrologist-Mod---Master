package hydrologistmod.tutorials;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.FtueTip;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;
import hydrologistmod.HydrologistMod;
import hydrologistmod.patches.HydrologistTags;

import java.io.IOException;
import java.util.ArrayList;

@SpirePatch(
        clz = CardRewardScreen.class,
        method = "open"
)
public class ShowSubtypesTutorialPatch {
    public static final String ID = "hydrologistmod:SubtypesTutorial";
    public static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(ID);
    public static final String[] LABEL = tutorialStrings.LABEL;
    public static final String[] TEXT = tutorialStrings.TEXT;

    public static void Postfix(CardRewardScreen __instance, ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
        if (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.FTUE) {
            for (AbstractCard c : cards) {
                if (c.hasTag(HydrologistTags.CARES_ABOUT_SUBTYPES) && !HydrologistMod.hydrologistConfig.getBool("Subtype Tutorial Seen")) {
                    AbstractDungeon.ftue = new FtueTip(LABEL[0], TEXT[0], Settings.WIDTH / 2.0f - (500.0f * Settings.scale), Settings.HEIGHT / 2.0f, c);
                    AbstractDungeon.ftue.type = FtueTip.TipType.POWER;
                    HydrologistMod.hydrologistConfig.setBool("Subtype Tutorial Seen", true);
                    try {HydrologistMod.hydrologistConfig.save();} catch (IOException e) {e.printStackTrace();}
                    ((SkipCardButton)ReflectionHacks.getPrivate(__instance, CardRewardScreen.class, "skipButton")).hide();
                    ((SingingBowlButton)ReflectionHacks.getPrivate(__instance, CardRewardScreen.class, "bowlButton")).hide();
                    break;
                }
            }
        }
    }
}
