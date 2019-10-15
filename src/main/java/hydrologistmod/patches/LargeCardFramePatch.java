package hydrologistmod.patches;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.screens.options.InputSettingsScreen;
import com.megacrit.cardcrawl.screens.options.RemapInputElement;
import hydrologistmod.cards.AbstractHydrologistCard;
import hydrologistmod.helpers.SwapperInputActionSet;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SpirePatch(
        clz= SingleCardViewPopup.class,
        method="renderFrame"
)
public class LargeCardFramePatch {
    private static Field cardField = null;

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"tmpImg"}
    )
    public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, @ByRef TextureAtlas.AtlasRegion[] tmpImg) {
        if (cardField == null) {
            try {
                cardField = SingleCardViewPopup.class.getDeclaredField("card");
                cardField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        try {
            AbstractCard reflectedCard = (AbstractCard)cardField.get(__instance);
            switch (reflectedCard.type) {
                case ATTACK:
                    if (reflectedCard.hasTag(HydrologistTags.ICE)) {
                        tmpImg[0] = AbstractHydrologistCard.ICE_LARGE_ATTACK_FRAME;
                    } else if (reflectedCard.hasTag(HydrologistTags.WATER)) {
                        tmpImg[0] = AbstractHydrologistCard.WATER_LARGE_ATTACK_FRAME;
                    } else if (reflectedCard.hasTag(HydrologistTags.STEAM)) {
                        tmpImg[0] = AbstractHydrologistCard.STEAM_LARGE_ATTACK_FRAME;
                    }
                case SKILL:
                    if (reflectedCard.hasTag(HydrologistTags.ICE)) {
                        tmpImg[0] = AbstractHydrologistCard.ICE_LARGE_SKILL_FRAME;
                    } else if (reflectedCard.hasTag(HydrologistTags.WATER)) {
                        tmpImg[0] = AbstractHydrologistCard.WATER_LARGE_SKILL_FRAME;
                    } else if (reflectedCard.hasTag(HydrologistTags.STEAM)) {
                        tmpImg[0] = AbstractHydrologistCard.STEAM_LARGE_SKILL_FRAME;
                    }
                case POWER:
                    if (reflectedCard.hasTag(HydrologistTags.ICE)) {
                        tmpImg[0] = AbstractHydrologistCard.ICE_LARGE_POWER_FRAME;
                    } else if (reflectedCard.hasTag(HydrologistTags.WATER)) {
                        tmpImg[0] = AbstractHydrologistCard.WATER_LARGE_POWER_FRAME;
                    } else if (reflectedCard.hasTag(HydrologistTags.STEAM)) {
                        tmpImg[0] = AbstractHydrologistCard.STEAM_LARGE_POWER_FRAME;
                    }
                default:
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(SingleCardViewPopup.class, "renderHelper");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
