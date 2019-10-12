package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import hydrologistmod.interfaces.TransmutableAffectingPower;
import hydrologistmod.interfaces.TransmutableCard;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TransmuteCardAction extends AbstractGameAction {
    private static final float DURATION = Settings.ACTION_DUR_XFAST;
    public int choices = 1;
    public int cards = 1;
    private boolean anyNumber;
    private AfterTransmute followup;
    private AbstractCard storedOldCard;
    private Conditionals conditions;
    private boolean transformPlayed = false;
    private AbstractCard playedCard;

    public TransmuteCardAction(boolean anyNumber, AfterTransmute followup, Conditionals conditions) {
        this.duration = DURATION;
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
        if (this.duration == DURATION) {
            if (!transformPlayed) {
                if (anyNumber) {
                    AbstractDungeon.handCardSelectScreen.open("Choose any number of cards to Transmute", 99, true, true);
                } else {
                    AbstractDungeon.handCardSelectScreen.open("Choose a card to Transmute", cards, false, false);
                    tickDuration();
                    return;
                }
            } else {
                AbstractCard newCard = getTransmutationResult().makeCopy();
                UseCardAction useCardAction = null;
                for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
                    if (action instanceof UseCardAction) {
                        useCardAction = (UseCardAction)action;
                        break;
                    }
                }
                if (useCardAction != null) {
                    try {
                        Field targetCardField = UseCardAction.class.getDeclaredField("targetCard");
                        targetCardField.setAccessible(true);
                        if (targetCardField.get(useCardAction) == playedCard && AbstractDungeon.player.cardInUse == playedCard) {
                            targetCardField.set(useCardAction, newCard);
                            AbstractDungeon.player.cardInUse = newCard;
                            newCard.current_x = playedCard.current_x;
                            newCard.current_y = playedCard.current_y;
                            newCard.target_x = playedCard.target_x;
                            newCard.target_y = playedCard.target_y;
                            newCard.isGlowing = playedCard.isGlowing;
                            playedCard.isGlowing = false;
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                modifyNewCard(playedCard, newCard);
                isDone = true;
                return;
            }
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            AbstractCard oldCard = AbstractDungeon.handCardSelectScreen.selectedCards.group.get(0);
            AbstractDungeon.player.hand.group.remove(oldCard);
            if (choices == 0) {
                isDone = true;
                System.out.println("TRANSMUTECARDACTION: Make 0 choices? How did this happen?");
                return;
            } else if (choices == 1) {
                AbstractCard newCard = getTransmutationResult().makeCopy();
                UnlockTracker.markCardAsSeen(newCard.cardID);
                modifyNewCard(oldCard, newCard);
                AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(newCard));
                AbstractDungeon.handCardSelectScreen.selectedCards.group.remove(oldCard);
                if (AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                    AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                }
                return;
            } else {
                CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                for (int i = 0; i < choices; ++i) {
                    AbstractCard choice = getTransmutationResult();
                    UnlockTracker.markCardAsSeen(choice.cardID);
                    if (!tmp.contains(choice)) {
                        tmp.addToBottom(choice);
                    } else {
                        --i;
                    }
                }
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                storedOldCard = oldCard;
                AbstractDungeon.gridSelectScreen.open(tmp, 1, "Choose your new card", false);
                return;
            }
        }
        if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
            AbstractCard newCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            modifyNewCard(storedOldCard, newCard);
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(newCard));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.handCardSelectScreen.selectedCards.group.remove(0);
            if (!AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = false;
            }
            return;
        }
        tickDuration();
    }

    private AbstractCard getTransmutationResult() {
        ArrayList<AbstractCard> targets = new ArrayList<>();
        for (AbstractCard candidate : AbstractDungeon.srcCommonCardPool.group) {
            if (!candidate.hasTag(AbstractCard.CardTags.HEALING)) {
                targets.add(candidate);
            }
        }
        for (AbstractCard candidate : AbstractDungeon.srcUncommonCardPool.group) {
            if (!candidate.hasTag(AbstractCard.CardTags.HEALING)) {
                targets.add(candidate);
            }
        }
        for (AbstractCard candidate : AbstractDungeon.srcRareCardPool.group) {
            if (!candidate.hasTag(AbstractCard.CardTags.HEALING)) {
                targets.add(candidate);
            }
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
