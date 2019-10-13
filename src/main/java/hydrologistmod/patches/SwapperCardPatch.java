package hydrologistmod.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.actions.SwapCardAction;
import hydrologistmod.interfaces.SwappableCard;

import java.util.ArrayList;

public class SwapperCardPatch {

    @SpirePatch(
            clz = AbstractCard.class,
            method = "update"
    )
    public static class AbstractCardUpdatePatch {

        public static void Prefix(AbstractCard __instance) {
            if (AbstractDungeon.player != null && (AbstractDungeon.player.isDraggingCard || AbstractDungeon.player.inSingleTargetMode) && __instance == AbstractDungeon.player.hoveredCard && SwapperHelper.isCardRegistered(__instance) && AbstractDungeon.actionManager.isEmpty()) {
                boolean pressed = SwapperHelper.handleInput();
                if (pressed) {
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
                                AbstractDungeon.actionManager.addToBottom(new SwapCardAction(__instance, SwapperHelper.getPairedCard(__instance), index));
                            } else {
                                AbstractDungeon.effectList.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0f, swappableCard.getUnableToSwapString(), true));
                            }
                        } else {
                            AbstractDungeon.actionManager.addToBottom(new SwapCardAction(__instance, SwapperHelper.getPairedCard(__instance), index));
                        }
                    } else {
                        System.out.println("How is clicked/hovered card not in hand?");
                    }
                }
            }
        }
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "initializeDeck"
    )
    public static class CardGroupInitializeDeckPatch {

        public static void Postfix(CardGroup __instance, CardGroup group) {
            SwapperHelper.initializeCombatList();
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeSameInstanceOf"
    )
    public static class AbstractCardMakeSameInstanceOfPatch {
        private static boolean disableLoop = false;

        public static AbstractCard Postfix(AbstractCard __result, AbstractCard __instance) {
            if (SwapperHelper.isCardRegistered(__instance)) {
                if (!disableLoop) {
                    disableLoop = true;
                    AbstractCard bufferCard = SwapperHelper.getPairedCard(__instance);
                    ArrayList<AbstractCard> listBuffer = new ArrayList<>();
                    listBuffer.add(__instance.makeSameInstanceOf());
                    listBuffer.add(bufferCard.makeSameInstanceOf());
                    while (SwapperHelper.getPairedCard(bufferCard) != __instance) {
                        bufferCard = SwapperHelper.getPairedCard(bufferCard);
                        listBuffer.add(bufferCard);
                    }
                    for (int i = 0; i < listBuffer.size(); ++i) {
                        SwapperHelper.registerOneWayPair(listBuffer.get(i), listBuffer.get((i + 1) % listBuffer.size()));
                    }
                    System.out.println("Swapper card detected as duplicated by make same instance of: duplicate pairing created");
                    disableLoop = false;
                }
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "makeStatEquivalentCopy"
    )
    public static class AbstractCardMakeStatEquivalentCopyPatch {
        private static boolean disableLoop = false;

        public static AbstractCard Postfix(AbstractCard __result, AbstractCard __instance) {
            if (SwapperHelper.isCardRegistered(__instance)) {
                if (!disableLoop) {
                    disableLoop = true;
                    AbstractCard bufferCard = SwapperHelper.getPairedCard(__instance);
                    ArrayList<AbstractCard> listBuffer = new ArrayList<>();
                    listBuffer.add(__instance.makeStatEquivalentCopy());
                    listBuffer.add(bufferCard.makeStatEquivalentCopy());
                    while (SwapperHelper.getPairedCard(bufferCard) != __instance) {
                        bufferCard = SwapperHelper.getPairedCard(bufferCard);
                        listBuffer.add(bufferCard);
                    }
                    for (int i = 0; i < listBuffer.size(); ++i) {
                        SwapperHelper.registerOneWayPair(listBuffer.get(i), listBuffer.get((i + 1) % listBuffer.size()));
                    }
                    System.out.println("Swapper card detected as duplicated by make same instance of: duplicate pairing created");
                    disableLoop = false;
                }
            }
            return __result;
        }
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "addToTop"
    )
    public static class CardGroupAddToTopMasterDeckPatch {

        public static void Postfix(CardGroup __instance, AbstractCard card) {
            if (__instance == AbstractDungeon.player.masterDeck) {
                System.out.println("addToTop postfix: group is masterDeck");
                if (card instanceof SwappableCard) {
                    SwappableCard swappableCard = (SwappableCard)card;
                    if (swappableCard.hasDefaultPair()) {
                        System.out.println(card + " is a pre-fab swappable. Generating master deck pair");
                        SwapperHelper.registerMasterDeckPair(card, swappableCard.createDefaultPair());
                    }
                    if (swappableCard.isChainSwapper()) {
                        System.out.println(card + " is a pre-fab chain-swappable. Generating master deck chain");
                        SwapperHelper.registerMasterDeckChain(card, swappableCard.createChain());
                    }
                }
            }
        }
    }
}
