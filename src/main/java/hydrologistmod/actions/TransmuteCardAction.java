package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import hydrologistmod.interfaces.TransmutableAffectingPower;
import hydrologistmod.interfaces.TransmutableCard;

import java.util.ArrayList;

public class TransmuteCardAction extends AbstractGameAction {
    private static final float DURATION = Settings.ACTION_DUR_XFAST;
    private boolean retrieveCard = false;
    public int choices = 1;

    public TransmuteCardAction() {
        this.duration = DURATION;
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof TransmutableAffectingPower) {
                ((TransmutableAffectingPower)power).onTransmute(this);
            }
        }
    }

    public void update() {
        if (this.duration == DURATION) {
            AbstractDungeon.handCardSelectScreen.open("Choose a card to Transmute", 1, false, false);
            tickDuration();
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            AbstractCard oldCard = AbstractDungeon.handCardSelectScreen.selectedCards.group.get(0);
            AbstractDungeon.player.hand.group.remove(oldCard);
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            if (choices == 0) {
                isDone = true;
                System.out.println("TRANSMUTECARDACTION: Make 0 choices? How did this happen?");
                return;
            } else if (choices == 1) {
                AbstractCard newCard = getTransmutationResult().makeCopy();
                UnlockTracker.markCardAsSeen(newCard.cardID);
                AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(newCard));
                if (oldCard instanceof TransmutableCard) {
                    ((TransmutableCard) oldCard).onTransmuted();
                }
                isDone = true;
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
                AbstractDungeon.gridSelectScreen.open(tmp, 1, "Choose your new card", false);
                tickDuration();
                return;
            }
        }
        if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(AbstractDungeon.gridSelectScreen.selectedCards.get(0)));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            tickDuration();
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
        return targets.get(AbstractDungeon.cardRandomRng.random(targets.size()-1));
    }
}
