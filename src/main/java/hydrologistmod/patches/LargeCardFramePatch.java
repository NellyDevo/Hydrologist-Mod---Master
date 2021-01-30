package hydrologistmod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import hydrologistmod.cards.AbstractHydrologistCard;
import javassist.CtBehavior;

@SpirePatch(
        clz= SingleCardViewPopup.class,
        method="renderFrame"
)
public class LargeCardFramePatch {

    @SpireInsertPatch(
            locator=Locator.class,
            localvars={"tmpImg"}
    )
    public static void Insert(SingleCardViewPopup __instance, SpriteBatch sb, @ByRef TextureAtlas.AtlasRegion[] tmpImg) {
        AbstractCard reflectedCard = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
        switch (reflectedCard.type) {
            case ATTACK:
                if (reflectedCard.hasTag(HydrologistTags.ICE)) {
                    renderHelper(__instance, sb, AbstractHydrologistCard.ICE_LARGE_ATTACK_BACKGROUND);
                    tmpImg[0] = AbstractHydrologistCard.ICE_LARGE_ATTACK_FRAME;
                } else if (reflectedCard.hasTag(HydrologistTags.WATER)) {
                    renderHelper(__instance, sb, AbstractHydrologistCard.WATER_LARGE_ATTACK_BACKGROUND);
                    tmpImg[0] = AbstractHydrologistCard.WATER_LARGE_ATTACK_FRAME;
                } else if (reflectedCard.hasTag(HydrologistTags.STEAM)) {
                    renderHelper(__instance, sb, AbstractHydrologistCard.STEAM_LARGE_ATTACK_BACKGROUND);
                    tmpImg[0] = AbstractHydrologistCard.STEAM_LARGE_ATTACK_FRAME;
                }
                break;
            case SKILL:
                if (reflectedCard.hasTag(HydrologistTags.ICE)) {
                    renderHelper(__instance, sb, AbstractHydrologistCard.ICE_LARGE_SKILL_BACKGROUND);
                    tmpImg[0] = AbstractHydrologistCard.ICE_LARGE_SKILL_FRAME;
                } else if (reflectedCard.hasTag(HydrologistTags.WATER)) {
                    renderHelper(__instance, sb, AbstractHydrologistCard.WATER_LARGE_SKILL_BACKGROUND);
                    tmpImg[0] = AbstractHydrologistCard.WATER_LARGE_SKILL_FRAME;
                } else if (reflectedCard.hasTag(HydrologistTags.STEAM)) {
                    renderHelper(__instance, sb, AbstractHydrologistCard.STEAM_LARGE_SKILL_BACKGROUND);
                    tmpImg[0] = AbstractHydrologistCard.STEAM_LARGE_SKILL_FRAME;
                }
                break;
            case POWER:
                if (reflectedCard.hasTag(HydrologistTags.ICE)) {
                    renderHelper(__instance, sb, AbstractHydrologistCard.ICE_LARGE_POWER_BACKGROUND);
                    tmpImg[0] = AbstractHydrologistCard.ICE_LARGE_POWER_FRAME;
                } else if (reflectedCard.hasTag(HydrologistTags.WATER)) {
                    renderHelper(__instance, sb, AbstractHydrologistCard.WATER_LARGE_POWER_BACKGROUND);
                    tmpImg[0] = AbstractHydrologistCard.WATER_LARGE_POWER_FRAME;
                } else if (reflectedCard.hasTag(HydrologistTags.STEAM)) {
                    renderHelper(__instance, sb, AbstractHydrologistCard.STEAM_LARGE_POWER_BACKGROUND);
                    tmpImg[0] = AbstractHydrologistCard.STEAM_LARGE_POWER_FRAME;
                }
                break;
            }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(SingleCardViewPopup.class, "renderHelper");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    private static void renderHelper(SingleCardViewPopup __instance, SpriteBatch sb, TextureAtlas.AtlasRegion texture) {
        ReflectionHacks.privateMethod(SingleCardViewPopup.class, "renderHelper", SpriteBatch.class, float.class, float.class, TextureAtlas.AtlasRegion.class).invoke(__instance, sb, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f, texture);
    }
}
