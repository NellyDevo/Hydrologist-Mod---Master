package hydrologistmod.helpers;

import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.cards.AbstractCard;
import hydrologistmod.patches.SwapperCardPatch;

import java.util.LinkedList;

public class SwapperHelper {
    public static boolean preventUpgradeLoop = false;

    //create a new swappable chain.
    public static void makeSwappableGroup(LinkedList<AbstractCard> swappableLink) {
        if (swappableLink.size() < 2) {
            System.out.println("Error: trying to create swappable group with fewer than 2 cards");
        } else {
            for (AbstractCard card : swappableLink) {

                //disassemble any existing swappable chains
                LinkedList<AbstractCard> preExistingList = SwapperCardPatch.SwappableChainField.swappableCards.get(card);
                if (preExistingList != null) {
                    for (AbstractCard otherListCard : preExistingList) {
                        SwapperCardPatch.SwappableChainField.swappableCards.set(otherListCard, null);
                    }
                }

                //set card's relevant fields
                SwapperCardPatch.SwappableChainField.swappableCards.set(card, swappableLink);
            }
        }
    }

    public static boolean isCardSwappable(AbstractCard card) {
        return SwapperCardPatch.SwappableChainField.swappableCards.get(card) != null;
    }

    public static AbstractCard getNextCard(AbstractCard card) {
        LinkedList<AbstractCard> list = SwapperCardPatch.SwappableChainField.swappableCards.get(card);
        return list.get((list.indexOf(card) + 1) % list.size());
    }

    public static void upgrade(AbstractCard source) {
        if (isCardSwappable(source)) {
            if (!preventUpgradeLoop) {
                preventUpgradeLoop = true;
                AbstractCard bufferCard = getNextCard(source);
                bufferCard.upgrade();
                while (getNextCard(bufferCard) != source) {
                    bufferCard = getNextCard(bufferCard);
                    bufferCard.upgrade();
                }
                preventUpgradeLoop = false;
            }
        }
    }

    public static AbstractCard findMasterDeckEquivalent(AbstractCard card) {
        AbstractCard masterDeckCard = StSLib.getMasterDeckEquivalent(card);
        if (masterDeckCard == null) {
            if (isCardSwappable(card)) {
                AbstractCard nextCard = getNextCard(card);
                while (nextCard != card) {
                    masterDeckCard = StSLib.getMasterDeckEquivalent(nextCard);
                    if (masterDeckCard != null) {
                        return masterDeckCard;
                    }
                    nextCard = SwapperHelper.getNextCard(nextCard);
                }
            }
        }
        return masterDeckCard;
    }

    private static boolean justPressedButtonLast = false;

    public static boolean handleInput(){
        boolean isButtonPressed = SwapperInputActionSet.swapCard.isPressed();
        if(isButtonPressed && !justPressedButtonLast) {
            justPressedButtonLast = true;
            return true;
        }

        if(!isButtonPressed && justPressedButtonLast)
            justPressedButtonLast = false;

        return false;
    }
}
