package helpers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;
import java.util.HashMap;

public class SwapperHelper {
    private static HashMap<AbstractCard, AbstractCard> cardPairs = new HashMap<>(); //any swappable pairings in combat
    private static HashMap<AbstractCard, AbstractCard> masterDeckPairs = new HashMap<>(); //only to store in the format of <card in master deck, card it swaps to>
    private static HashMap<AbstractCard, ArrayList<AbstractCard>> masterDeckChains = new HashMap<>(); //<card in deck, list of cards in swap order before going back to card in deck>
    public static boolean preventUpgradeLoop = false;

    public static void registerPair(AbstractCard card1, AbstractCard card2) {
        registerPair(card1, card2, false);
    }

    public static void registerPair(AbstractCard card1, AbstractCard card2, boolean shouldOverride) {
        if (!shouldOverride) {
            boolean card1Found = false;
            boolean card2Found = false;
            if (isCardRegistered(card1)) {
                card1Found = true;
                System.out.println("SwapperHelper: ERROR: card1," + card1 + " is already paired with " + cardPairs.get(card1));
            }
            if (isCardRegistered(card2)) {
                card2Found = true;
                System.out.println("SwapperHelper: ERROR: card2," + card2 + " is already paired with " + cardPairs.get(card2));
            }
            if (card1Found || card2Found) {
                System.out.println("SwapperHelper: WARNING: card pairing NOT registered.");
            } else {
                cardPairs.put(card1, card2);
                cardPairs.put(card2, card1);
                card1.cardsToPreview = card2;
                card2.cardsToPreview = card1;
                System.out.println("SwapperHelper: card pairing between " + card1 + " and " + card2 + " successfully registered.");
            }
        } else {
            AbstractCard existingPair1 = null;
            AbstractCard existingPair2 = null;
            if (cardPairs.get(card1) != null) {
                existingPair1 = cardPairs.get(card1);
                System.out.println("SwapperHelper: WARNING: card1, " + card1 + " is already paired with " + existingPair1);
            }
            if (cardPairs.get(card2) != null) {
                existingPair2 = cardPairs.get(card2);
                System.out.println("SwapperHelper: WARNING: card2, " + card2 + " is already paired with " + existingPair2);
            }
            if (existingPair1 != null) {
                cardPairs.remove(card1);
                cardPairs.remove(existingPair1);
                System.out.println("SwapperHelper: existing pairing between " + card1 + " and " + existingPair1 + " removed.");
            }
            if (existingPair2 != null) {
                cardPairs.remove(card2);
                cardPairs.remove(existingPair2);
                System.out.println("SwapperHelper: existing pairing between " + card2 + " and " + existingPair2 + " removed.");
            }
            cardPairs.put(card1, card2);
            cardPairs.put(card2, card1);
            card1.cardsToPreview = card2;
            card2.cardsToPreview = card1;
            System.out.println("SwapperHelper: card pairing between " + card1 + " and " + card2 + " successfully registered.");
        }
    }

    public static void registerOneWayPair(AbstractCard card1, AbstractCard card2) { //for creating chains, trios, etc.
        if (!isCardRegistered(card1) && (!isCardRegistered(card2))) {
            cardPairs.put(card1, card2);
            card1.cardsToPreview = card2;
        }
    }

    public static void registerMasterDeckChain(AbstractCard mainCard, ArrayList<AbstractCard> otherCards) {
        masterDeckChains.put(mainCard, otherCards);
    }

    public static boolean isCardRegistered(AbstractCard card) {
        return cardPairs.containsKey(card);
    }

    public static AbstractCard getPairedCard(AbstractCard card) {
        return cardPairs.get(card);
    }

    public static void registerMasterDeckPair(AbstractCard card1, AbstractCard card2) {
        if (!masterDeckPairs.containsKey(card1) && !masterDeckPairs.containsKey(card2)) {
            masterDeckPairs.put(card1, card2);
            card1.cardsToPreview = card2;
            card2.cardsToPreview = card1;
        } else {
            System.out.println("SwapperHelper: ERROR: card already has a pair, pairing not made.");
        }
    }

    public static boolean isCardRegisteredAsMasterDeckPair(AbstractCard card) {
         return masterDeckPairs.containsKey(card);
    }

    public static boolean isCardRegisteredAsMasterDeckChain(AbstractCard card) {
        return masterDeckChains.containsKey(card);
    }

    public static AbstractCard getMasterDeckPair(AbstractCard card) {
        return masterDeckPairs.get(card);
    }

    public static ArrayList<AbstractCard> getMasterDeckChain(AbstractCard card) {
        return masterDeckChains.get(card);
    }

    public static void initializeCombatList() {
        cardPairs.clear();
        ArrayList<AbstractCard> cleanup = new ArrayList<>();
        ArrayList<AbstractCard> chainCleanup = new ArrayList<>();
        for (AbstractCard key : masterDeckPairs.keySet()) {
            if (!AbstractDungeon.player.masterDeck.contains(key)) {
                cleanup.add(key);
                System.out.println("SwapperHelper: " + key + " is no longer in the master deck. Removing master deck pairing of " + key + " and " + masterDeckPairs.get(key) + ".");
            }
        }
        for (AbstractCard key : masterDeckChains.keySet()) {
            if (!AbstractDungeon.player.masterDeck.contains(key)) {
                chainCleanup.add(key);
                System.out.println("SwapperHelper: " + key + " is no longer in the master deck. Removing master deck pairing of " + key + " and " + masterDeckChains.get(key) + ".");
            }
        }
        for (AbstractCard removedCard : cleanup) {
            masterDeckPairs.remove(removedCard);
        }
        for (AbstractCard removedCard : chainCleanup) {
            masterDeckChains.remove(removedCard);
        }
        //find cards in draw pile with same UUID as cards in masterDeckPairs.keySet, then register pairings with sameCard and key.get.makeStatEquivalentCopy();
        for (AbstractCard key : masterDeckPairs.keySet()) {
            for (AbstractCard drawPileCard : AbstractDungeon.player.drawPile.group) {
                if (drawPileCard.uuid.equals(key.uuid)) {
                    registerPair(drawPileCard, masterDeckPairs.get(key).makeStatEquivalentCopy());
                    break;
                }
            }
        }
        //repeat the above process for chains
        for (AbstractCard key : masterDeckChains.keySet()) {
            for (AbstractCard drawPileCard : AbstractDungeon.player.drawPile.group) {
                if (drawPileCard.uuid.equals(key.uuid)) {
                    ArrayList<AbstractCard> pairingsBuffer = new ArrayList<>();
                    pairingsBuffer.add(drawPileCard);
                    for (AbstractCard card : masterDeckChains.get(key)) {
                        pairingsBuffer.add(card.makeSameInstanceOf());
                    }
                    for (int i = 0; i < pairingsBuffer.size(); ++i) {
                        registerOneWayPair(pairingsBuffer.get(i), pairingsBuffer.get((i+1)%pairingsBuffer.size())); //Johnny power!
                    }
                    break;
                }
            }
        }
        System.out.println("SwapperHelper: pre-combat swapper pairings initialized");
    }

    public static void upgrade(AbstractCard source) {
        if (isCardRegistered(source)) {
            if (!preventUpgradeLoop) {
                preventUpgradeLoop = true;
                AbstractCard bufferCard = getPairedCard(source);
                bufferCard.upgrade();
                while (getPairedCard(bufferCard) != source) {
                    bufferCard = getPairedCard(bufferCard);
                    bufferCard.upgrade();
                }
                preventUpgradeLoop = false;
            }
        }
        if (isCardRegisteredAsMasterDeckPair(source)) {
            if (!preventUpgradeLoop) {
                preventUpgradeLoop = true;
                getMasterDeckPair(source).upgrade();
                preventUpgradeLoop = false;
            }
        }
        if (isCardRegisteredAsMasterDeckChain(source)) {
            if (!preventUpgradeLoop) {
                preventUpgradeLoop = true;
                for (AbstractCard card : getMasterDeckChain(source)) {
                    card.upgrade();
                }
                preventUpgradeLoop = false;
            }
        }
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
