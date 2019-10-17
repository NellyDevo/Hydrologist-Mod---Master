package hydrologistmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import hydrologistmod.actions.SwapCardAction;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;

import java.util.ArrayList;
import java.util.LinkedList;

public class SwapperCardPatch {

    @SpirePatch(
            clz = AbstractCard.class,
            method = "update"
    )
    public static class AbstractCardUpdatePatch {

        public static void Prefix(AbstractCard __instance) {
            if (AbstractDungeon.player != null && __instance == AbstractDungeon.player.hoveredCard && SwapperHelper.isCardSwappable(__instance) && AbstractDungeon.actionManager.isEmpty()) {
                boolean pressed = SwapperHelper.handleInput();
                if (pressed) {
                    boolean selected = (AbstractDungeon.player.isDraggingCard || AbstractDungeon.player.inSingleTargetMode);
                    System.out.println("SWAPPER CARD CHECKPOINT REACHED");
                    System.out.println("CARD TO SWAP: " + __instance.name);
                    int index = -1;
                    for (int i = 0; i < AbstractDungeon.player.hand.group.size(); i++) {
                        if (__instance == AbstractDungeon.player.hand.group.get(i)) {
                            index = i;
                        }
                    }
                    if (index != -1) {
                        if (__instance instanceof SwappableCard) {
                            SwappableCard swappableCard = (SwappableCard)__instance;
                            if (swappableCard.canSwap()) {
                                AbstractDungeon.actionManager.addToBottom(new SwapCardAction(__instance, SwapperHelper.getNextCard(__instance), index, selected));
                            } else {
                                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0f, swappableCard.getUnableToSwapString(), true));
                            }
                        } else {
                            AbstractDungeon.actionManager.addToBottom(new SwapCardAction(__instance, SwapperHelper.getNextCard(__instance), index, selected));
                        }
                    } else {
                        System.out.println("How is clicked/hovered card not in hand?");
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeStatEquivalentCopy"
    )
    public static class AbstractCardMakeStatEquivalentCopyPatch {
        private static boolean disableLoop = false;

        public static AbstractCard Postfix(AbstractCard __result, AbstractCard __instance) {
            if (SwapperHelper.isCardSwappable(__instance)) {
                if (!disableLoop) {
                    disableLoop = true;
                    //copy all other cards in __instance's swappable list and create a new swappable list for __result. __result may already have a list from normal instantiation
                    LinkedList<AbstractCard> newList = new LinkedList<>(SwappableChainField.swappableCards.get(__instance));
                    for (int i = 0; i < newList.size(); ++i) {
                        AbstractCard card = newList.get(i);
                        AbstractCard newCard = card.makeStatEquivalentCopy();
                        if (__instance == card) {
                            __result = newCard;
                        }
                        newList.set(i, newCard);
                    }
                    SwapperHelper.makeSwappableGroup(newList);
                    disableLoop = false;
                }
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeSameInstanceOf"
    )
    public static class AbstractCardMakeSameInstanceOfPatch {

        public static AbstractCard Postfix(AbstractCard __result, AbstractCard __instance) {
            if (SwapperHelper.isCardSwappable(__instance)) {
                //set the uuid of each swappable group in __result to match the cards in the swappable group of __instance. __result Swappable list should already by copied by above patch.
                LinkedList<AbstractCard> oldList = SwappableChainField.swappableCards.get(__instance);
                LinkedList<AbstractCard> newList = SwappableChainField.swappableCards.get(__result);
                if (oldList.size() != newList.size()) {
                    System.out.println("ERROR: make same instance list sizes are not the same. How did this happen?");
                }
                for (int i = 0; i < oldList.size(); ++i) {
                    newList.get(i).uuid = oldList.get(i).uuid;
                }
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class SwappableChainField {
        public static SpireField<LinkedList<AbstractCard>> swappableCards = new SpireField<>(() -> null);
    }
}
