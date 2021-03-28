package hydrologistmod.credits;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class CreditsPatch {
    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, String.class, int.class, String.class, AbstractCard.CardType.class, AbstractCard.CardColor.class, AbstractCard.CardRarity.class, AbstractCard.CardTarget.class, DamageInfo.DamageType.class}
    )
    public static class setArtToCreditedPatch {
        public static void Postfix(AbstractCard __instance, String id, String name, String imgUrl, int cost, String rawDescription, AbstractCard.CardType type, AbstractCard.CardColor color, AbstractCard.CardRarity rarity, AbstractCard.CardTarget target, DamageInfo.DamageType dtype) {
            if (CreditsHelper.isArtCredited(__instance.cardID)) {
                CreditsInfo defaultInfo = CreditsHelper.getDefaultInfo(__instance.cardID);
                if (defaultInfo.defaultSmallImage == null) {
                    defaultInfo.defaultSmallImage = ReflectionHacks.getPrivate(__instance, AbstractCard.class, "portrait");
                }
                String imgPath = CreditsHelper.getCurrentInfo(__instance.cardID).getImgPath();
                CreditsHelper.loadImage(__instance, imgPath);
            }
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "loadPortraitImg"
    )
    public static class setSingleViewArtToCreditedPatch {
        public static void Postfix(SingleCardViewPopup __instance) {
            AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            if (CreditsHelper.isArtCredited(card.cardID)) {
                CreditsInfo defaultInfo = CreditsHelper.getDefaultInfo(card.cardID);
                if (defaultInfo.defaultLargeImage == null) {
                    defaultInfo.defaultLargeImage = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "portraitImg");
                }
                CreditsHelper.setSingleViewScreenPortrait(__instance);
            }
        }
    }
}
