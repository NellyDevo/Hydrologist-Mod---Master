package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import hydrologistmod.interfaces.TransmutableAffectingPower;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.TransmutePlayedCardPatch;
import hydrologistmod.vfx.TransmuteCardEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class TransmuteCardAction extends AbstractGameAction {
    private boolean initialized = false;
    private boolean completed = false;
    public int choices = 1;
    public int cards = 1;
    private boolean anyNumber;
    private AfterTransmute followup;
    private AbstractCard storedOldCard;
    private Conditionals conditions;
    private boolean transformPlayed = false;
    private AbstractCard playedCard;
    private HashMap<AbstractCard, AbstractCard> transmutedPairs = new HashMap<>();

    public TransmuteCardAction(boolean anyNumber, AfterTransmute followup, Conditionals conditions) {
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof TransmutableAffectingPower) {
                ((TransmutableAffectingPower)power).onTransmute(this);
            }
        }
        this.followup = followup;
        this.anyNumber = anyNumber;
        this.conditions = conditions;
    }

    public TransmuteCardAction() {
        this(false, null, null);
    }

    public TransmuteCardAction(boolean anyNumber) {
        this(anyNumber, null, null);
    }

    public TransmuteCardAction(AfterTransmute followup) {
        this(false, followup, null);
    }

    public TransmuteCardAction(Conditionals conditions) {
        this(false, null, conditions);
    }

    public TransmuteCardAction(AbstractCard playedCard, AfterTransmute followup) {
        this(false, followup, null);
        this.playedCard = playedCard;
        transformPlayed = true;
    }

    public TransmuteCardAction(AbstractCard playedCard) {
        this(playedCard, null);
    }

    public void update() {
        if (completed) {
            return;
        }
        if (!initialized) {
            initialized = true;
            if (!transformPlayed) {
                if (AbstractDungeon.player.hand.size() < 1) {
                    isDone = true;
                } else {
                    if (anyNumber) {
                        AbstractDungeon.handCardSelectScreen.open("transmute.", 99, true, true);
                    } else {
                        if (AbstractDungeon.player.hand.size() > cards) {
                            AbstractDungeon.handCardSelectScreen.open("transmute.", cards, false, false);
                        } else {
                            AbstractDungeon.handCardSelectScreen.open("transmute.", AbstractDungeon.player.hand.size(), false, false);
                        }
                    }
                }
            } else {
                AbstractCard newCard = getTransmutationResult(playedCard).makeCopy();
                modifyNewCard(playedCard, newCard);
                //modify useCardAction for the current card using related patch
                UseCardAction useCardAction = null;
                for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
                    if (action instanceof UseCardAction) {
                        try {
                            Field targetCardField = UseCardAction.class.getDeclaredField("targetCard");
                            targetCardField.setAccessible(true);
                            if (targetCardField.get(action) == playedCard) {
                                useCardAction = (UseCardAction)action;
                                break;
                            }
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                            isDone = true;
                            return;
                        }
                    }
                }
                TransmutePlayedCardPatch.UseCardActionField.transmuteTargetCard.set(useCardAction, newCard);
                transmutedPairs.put(playedCard, newCard);
                AbstractDungeon.topLevelEffects.add(new TransmuteCardEffect(transmutedPairs, null, this, 0.75f));
                completed = true;
            }
            return;
        } else if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            AbstractCard oldCard = AbstractDungeon.handCardSelectScreen.selectedCards.group.get(0);
            AbstractDungeon.player.hand.group.remove(oldCard);
            if (choices == 0) {
                isDone = true;
                System.out.println("TRANSMUTECARDACTION: Make 0 choices? How did this happen?");
                return;
            } else if (choices == 1) {
                AbstractCard newCard = getTransmutationResult(oldCard).makeCopy();
                UnlockTracker.markCardAsSeen(newCard.cardID);
                modifyNewCard(oldCard, newCard);
                transmutedPairs.put(oldCard, newCard);
                AbstractDungeon.handCardSelectScreen.selectedCards.group.remove(oldCard);
                if (AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                    AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                }
                return;
            } else {
                CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (int i = 0; i < choices; ++i) {
                    AbstractCard choice = getTransmutationResult(oldCard);
                    UnlockTracker.markCardAsSeen(choice.cardID);
                    if (!tmp.contains(choice)) {
                        tmp.addToBottom(choice);
                    } else {
                        --i;
                    }
                }
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                storedOldCard = oldCard;
                AbstractDungeon.gridSelectScreen.open(tmp, 1, "Choose your new card.", false);
                return;
            }
        } else if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
            AbstractCard newCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0).makeCopy();
            modifyNewCard(storedOldCard, newCard);
            transmutedPairs.put(storedOldCard, newCard);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.handCardSelectScreen.selectedCards.group.remove(0);
            if (!AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = false;
            }
            return;
        }
        if (AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty() && initialized) {
            AbstractDungeon.topLevelEffects.add(new TransmuteCardEffect(transmutedPairs, CardGroup.CardGroupType.HAND, this, 0.75f));
            completed = true;
        }
    }

    private AbstractCard getTransmutationResult(AbstractCard oldCard) {
        ArrayList<AbstractCard> targets = new ArrayList<>();
        switch (oldCard.rarity) {
            case RARE:
                for (AbstractCard candidate : AbstractDungeon.srcRareCardPool.group) {
                    if (!candidate.hasTag(AbstractCard.CardTags.HEALING)) {
                        targets.add(candidate);
                    }
                }
                break;
            case UNCOMMON:
                for (AbstractCard candidate : AbstractDungeon.srcUncommonCardPool.group) {
                    if (!candidate.hasTag(AbstractCard.CardTags.HEALING)) {
                        targets.add(candidate);
                    }
                }
                break;
            default:
                for (AbstractCard candidate : AbstractDungeon.srcCommonCardPool.group) {
                    if (!candidate.hasTag(AbstractCard.CardTags.HEALING)) {
                        targets.add(candidate);
                    }
                }
                break;
        }
        AbstractCard result = targets.get(AbstractDungeon.cardRandomRng.random(targets.size()-1));
        if (conditions != null) {
            while (!conditions.filter(result)) {
                result = targets.get(AbstractDungeon.cardRandomRng.random(targets.size()-1));
            }
        }
        return result;
    }

    private void modifyNewCard(AbstractCard oldCard, AbstractCard newCard) {
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof TransmutableAffectingPower) {
                ((TransmutableAffectingPower)power).affectTransmutedCard(newCard);
            }
        }
        if (followup != null) {
            followup.doActions(newCard);
        }
        if (oldCard instanceof TransmutableCard) {
            ((TransmutableCard)oldCard).onTransmuted(newCard);
        }
    }

    public interface AfterTransmute {
        void doActions(AbstractCard newCard);
    }

    public interface Conditionals {
        boolean filter(AbstractCard potentialCard);
    }
}
