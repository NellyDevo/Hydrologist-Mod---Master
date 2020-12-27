package hydrologistmod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import hydrologistmod.helpers.SwapperHelper;

public class RenderSwappablePreviewPatch {
    private static Float cardTipPad = null;

    @SpirePatch(
            clz = SingleCardViewPopup.class,
            method = "renderTips"
    )
    public static class renderSwappablesInSingleViewPatch {

        public static void Postfix(SingleCardViewPopup __instance, SpriteBatch sb) {
            AbstractCard card = ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "card");
            if (!card.isLocked && card.isSeen && SwapperHelper.isCardSwappable(card)) {
                float renderX = (1920F * Settings.scale) - (1435.0F * Settings.scale);
                float renderY = 795.0F * Settings.scale;
                if (cardTipPad == null) {
                    cardTipPad = ReflectionHacks.getPrivateStatic(AbstractCard.class, "CARD_TIP_PAD");
                }
                float horizontal = ((AbstractCard.IMG_WIDTH * 0.8F) + cardTipPad);
                float vertical = ((AbstractCard.IMG_HEIGHT * 0.8F) + cardTipPad + ((Hitbox)ReflectionHacks.getPrivate(__instance, SingleCardViewPopup.class, "prevHb")).height);
                boolean verticalOffset = false;
                if (card.cardsToPreview != null) {
                    renderY -= vertical;
                    verticalOffset = true;
                }
                AbstractCard nextCard = SwapperHelper.getNextCard(card);
                do {
                    nextCard.current_x = renderX;
                    nextCard.current_y = renderY;
                    nextCard.drawScale = 0.8F;
                    nextCard.render(sb);
                    if (verticalOffset) {
                        renderY += vertical;
                        renderX -= horizontal;
                        verticalOffset = false;
                    } else {
                        renderY -= vertical;
                        verticalOffset = true;
                    }
                } while ((nextCard = SwapperHelper.getNextCard(nextCard)) != card);
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderCardTip"
    )
    public static class renderSwappablesPreviewPatch {

        public static void Postfix(AbstractCard __instance, SpriteBatch sb) {
            if (!__instance.isLocked && __instance.isSeen && !Settings.hideCards && (boolean)ReflectionHacks.getPrivate(__instance, AbstractCard.class, "renderTip") && SwapperHelper.isCardSwappable(__instance)) {
                boolean rightSide = __instance.current_x > Settings.WIDTH * 0.75F;
                if (cardTipPad == null) {
                    cardTipPad = ReflectionHacks.getPrivateStatic(AbstractCard.class, "CARD_TIP_PAD");
                }
                float renderX = (((AbstractCard.IMG_WIDTH / 2.0F) + ((AbstractCard.IMG_WIDTH / 2.0F) * 0.8F) + (cardTipPad)) * __instance.drawScale);
                float horizontal = ((AbstractCard.IMG_WIDTH * 0.8F) + cardTipPad) * __instance.drawScale;
                if (!rightSide) {
                    horizontal *= -1;
                }
                float vertical = ((AbstractCard.IMG_HEIGHT * 0.8F) + cardTipPad) * __instance.drawScale;
                boolean verticalOffset = false;
                if (rightSide) {
                    renderX = __instance.current_x + renderX;
                } else {
                    renderX = __instance.current_x - renderX;
                }
                float renderY = __instance.current_y + ((AbstractCard.IMG_HEIGHT / 2.0F) - (AbstractCard.IMG_HEIGHT / 2.0F * 0.8F)) * __instance.drawScale;
                if (__instance.cardsToPreview != null) {
                    renderY -= vertical;
                    verticalOffset = true;
                }
                AbstractCard nextCard = SwapperHelper.getNextCard(__instance);
                do {
                    nextCard.current_x = renderX;
                    nextCard.current_y = renderY;
                    nextCard.drawScale = __instance.drawScale * 0.8F;
                    nextCard.render(sb);
                    if (verticalOffset) {
                        renderY += vertical;
                        renderX += horizontal;
                        verticalOffset = false;
                    } else {
                        renderY -= vertical;
                        verticalOffset = true;
                    }
                } while ((nextCard = SwapperHelper.getNextCard(nextCard)) != __instance);
            }
        }
    }
}
