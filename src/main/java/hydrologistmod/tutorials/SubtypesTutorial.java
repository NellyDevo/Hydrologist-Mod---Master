package hydrologistmod.tutorials;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.FtueTip;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import com.megacrit.cardcrawl.ui.buttons.SkipCardButton;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class SubtypesTutorial extends FtueTip {
    public static final String ID = "hydrologistmod:SubtypesTutorial";
    public static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(ID);
    public static final String[] LABEL = tutorialStrings.LABEL;
    public static final String[] TEXT = tutorialStrings.TEXT;
    public static final TextureRegion arrows = new TextureRegion(new Texture("hydrologistmod/images/tutorials/subtype_tutorial_arrows.png"));
    public static final Color textColor = Color.LIME.cpy();

    public SubtypesTutorial(String label, String message, float x, float y, AbstractCard c) {
        super(label, message, x, y, c);
        type = TipType.POWER;
        //never show SubType tip again TODO
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        AbstractCard card = ReflectionHacks.getPrivateInherited(this, SubtypesTutorial.class, "c");
        sb.draw(arrows, card.current_x - arrows.getRegionWidth() / 2.0f, card.current_y - arrows.getRegionHeight() / 2.0f, arrows.getRegionWidth() / 2.0f, arrows.getRegionHeight() / 2.0f, arrows.getRegionWidth(), arrows.getRegionHeight(), Settings.scale, Settings.scale, 0.0f);
        FontHelper.renderSmartText(
                sb,
                FontHelper.tipBodyFont,
                TEXT[1],
                card.current_x - 20.0f * Settings.scale,
                card.current_y + 276.0f * Settings.scale,
                textColor);
        FontHelper.renderSmartText(
                sb,
                FontHelper.tipBodyFont,
                TEXT[2],
                card.current_x + 217.0f * Settings.scale,
                card.current_y + 70.0f * Settings.scale,
                textColor);
        FontHelper.renderSmartText(
                sb,
                FontHelper.tipBodyFont,
                TEXT[3],
                card.current_x - 87.0f * Settings.scale,
                card.current_y - 254.0f * Settings.scale,
                textColor);
    }

//    @SpirePatch(
//            clz = CardRewardScreen.class,
//            method = "open"
//    )
//    public static class ShowSubtypesTutorialPatch {
//        public static void Postfix(CardRewardScreen __instance, ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
//            if (AbstractDungeon.ftue == null) {
//                for (AbstractCard c : cards) {
//                    if (c.hasTag(HydrologistTags.CARES_ABOUT_SUBTYPES /*&& !subtypeShown*/)) { //TODO
//                        AbstractDungeon.ftue = new SubtypesTutorial(LABEL[0], TEXT[0], Settings.WIDTH / 2.0f - (500.0f * Settings.scale), Settings.HEIGHT / 2.0f, c);
//                        ((SkipCardButton)ReflectionHacks.getPrivate(__instance, CardRewardScreen.class, "skipButton")).hide();
//                        ((SingingBowlButton)ReflectionHacks.getPrivate(__instance, CardRewardScreen.class, "bowlButton")).hide();
//                        break;
//                    }
//                }
//            }
//        }
//    }

}
