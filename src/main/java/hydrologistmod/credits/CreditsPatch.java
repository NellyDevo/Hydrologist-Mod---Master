package hydrologistmod.credits;

import basemod.ReflectionHacks;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

public class CreditsPatch {
    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = {String.class, String.class, String.class, int.class, String.class, AbstractCard.CardType.class, AbstractCard.CardColor.class, AbstractCard.CardRarity.class, AbstractCard.CardTarget.class, DamageInfo.DamageType.class}
    )
    public static class setArtToCreditedPatch {
        public static void Postfix(AbstractCard __instance, String id, String name, String imgUrl, int cost, String rawDescription, AbstractCard.CardType type, AbstractCard.CardColor color, AbstractCard.CardRarity rarity, AbstractCard.CardTarget target, DamageInfo.DamageType dtype) {
            String imgPath = null;
            if (CreditsHelper.isArtCredited(__instance.cardID) && !CreditsHelper.artIsDefault(__instance.cardID)) {
                imgPath = CreditsHelper.getCurrentInfo(__instance.cardID).getImgPath();
            }
            if (imgPath != null) {
                Texture cardTexture;
                if (CustomCard.imgMap.containsKey(imgPath)) {
                    cardTexture = CustomCard.imgMap.get(imgPath);
                } else {
                    cardTexture = ImageMaster.loadImage(imgPath);
                    CustomCard.imgMap.put(imgPath, cardTexture);
                }
                cardTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                int tw = cardTexture.getWidth();
                int th = cardTexture.getHeight();
                TextureAtlas.AtlasRegion cardImg = new TextureAtlas.AtlasRegion(cardTexture, 0, 0, tw, th);
                ReflectionHacks.setPrivate(__instance, AbstractCard.class, "portrait", cardImg);
            }
        }
    }

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "loadPortraitImg"
    )
    public static class setSingleViewArtToCreditedPatch {
        public static void Postfix(SingleCardViewPopup __instance) {
            String imgPath = null;
            AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            if (CreditsHelper.isArtCredited(card.cardID) && !CreditsHelper.artIsDefault(card.cardID)) {
                imgPath = CreditsHelper.getCurrentInfo(card.cardID).getLargeImgPath();
            }
            if (imgPath != null) {
                Texture cardTexture;
                if (CustomCard.imgMap.containsKey(imgPath)) {
                    cardTexture = CustomCard.imgMap.get(imgPath);
                } else {
                    cardTexture = ImageMaster.loadImage(imgPath);
                    CustomCard.imgMap.put(imgPath, cardTexture);
                }
                ReflectionHacks.setPrivate(__instance, SingleCardViewPopup.class, "portraitImg", cardTexture);
            }
        }
    }
}
