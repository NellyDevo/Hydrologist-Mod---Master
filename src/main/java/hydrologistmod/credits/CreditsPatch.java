package hydrologistmod.credits;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class CreditsPatch {
    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, String.class, int.class, String.class, AbstractCard.CardType.class, AbstractCard.CardColor.class, AbstractCard.CardRarity.class, AbstractCard.CardTarget.class, DamageInfo.DamageType.class}
    )
    public static class SetArtToCreditedPatch {
        public static void Postfix(AbstractCard __instance, String id, String name, String imgUrl, int cost, String rawDescription, AbstractCard.CardType type, AbstractCard.CardColor color, AbstractCard.CardRarity rarity, AbstractCard.CardTarget target, DamageInfo.DamageType dtype) {
            if (CreditsHelper.isArtCredited(__instance.cardID)) {
                CreditsInfo defaultInfo = CreditsHelper.getDefaultInfo(__instance.cardID);
                if (defaultInfo.smallImage == null) {
                    defaultInfo.smallImage = ReflectionHacks.getPrivate(__instance, AbstractCard.class, "portrait");
                }
                TextureAtlas.AtlasRegion img = CreditsHelper.getCurrentInfo(__instance.cardID).getSmallImage();
                CreditsHelper.setImage(__instance, img);
            }
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "loadPortraitImg"
    )
    public static class SetSingleViewArtToCreditedPatch {
        public static void Postfix(SingleCardViewPopup __instance) {
            AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            if (CreditsHelper.isArtCredited(card.cardID)) {
                CreditsInfo defaultInfo = CreditsHelper.getDefaultInfo(card.cardID);
                if (defaultInfo.largeImage == null) {
                    defaultInfo.largeImage = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "portraitImg");
                }
                Texture img = CreditsHelper.getCurrentInfo(card.cardID).getLargeImage();
                CreditsHelper.setSingleViewScreenImage(__instance, img);
            }
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "open",
            paramtypez = {AbstractCard.class, CardGroup.class}
    )
    public static class SingleCardViewOpenWithGroupPatch {
        public static void Postfix(SingleCardViewPopup __instance, AbstractCard card, CardGroup group) {
            CreditsHelper.onScreenOpen(card.cardID);
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "open",
            paramtypez = {AbstractCard.class}
    )
    public static class SingleCardViewOpenPatch {
        public static void Postfix(SingleCardViewPopup __instance, AbstractCard card) {
            CreditsHelper.onScreenOpen(card.cardID);
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "close"
    )
    public static class SingleCardViewClosePatch {
        public static void Postfix(SingleCardViewPopup __instance) {
            CreditsHelper.onScreenClose(__instance);
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "updateInput"
    )
    public static class SingleCardViewUpdateInputPatch {
        public static SpireReturn Prefix(SingleCardViewPopup __instance) {
            if (CreditsHelper.updateInput()) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "updateArrows"
    )
    public static class SingleCardViewUpdateArrowsPatch {
        public static void Prefix(SingleCardViewPopup __instance) {
            CreditsHelper.update(__instance);
        }
    }
}
