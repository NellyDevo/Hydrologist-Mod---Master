package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

public class RetainFilteredCardAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:RetainFilteredCardAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private FilterCards cardFilter;
    private ArrayList<AbstractCard> cannotRetain;
    private AbstractPlayer p;
    private int retainAmount;

    public RetainFilteredCardAction(FilterCards cardFilter, int amount) {
        this.duration = DURATION;
        this.cardFilter = cardFilter;
        p = AbstractDungeon.player;
        retainAmount = amount;
    }

    @Override
    public void update() {
        if (duration == Settings.ACTION_DUR_FAST) {
            for (AbstractCard c : p.hand.group) {
                if (!cardFilter.canRetain(c)) {
                    cannotRetain.add(c);
                }
            }
            if (cannotRetain.size() == p.hand.group.size()) {
                isDone = true;
                return;
            } else if (p.hand.group.size() - cannotRetain.size() <= retainAmount) {
                for (AbstractCard c : p.hand.group) {
                    if (cardFilter.canRetain(c)) {
                        if (!c.isEthereal) {
                            c.retain = true;
                        }
                        isDone = true;
                        return;
                    }
                }
            }
            p.hand.group.removeAll(cannotRetain);
            if (p.hand.group.size() > retainAmount) {
                AbstractDungeon.handCardSelectScreen.open("retain", retainAmount, true, true, false, false);
                tickDuration();
                return;
            } else if (p.hand.group.size() > 0) {
                for (AbstractCard c : p.hand.group) {
                    if (!c.isEthereal) {
                        c.retain = true;
                    }
                }
                for (AbstractCard c : cannotRetain) {
                    p.hand.addToTop(c);
                }
                p.hand.refreshHandLayout();
                isDone = true;
            }
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                if (!c.isEthereal) {
                    c.retain = true;
                }
            }
            for (AbstractCard c : cannotRetain) {
                p.hand.addToTop(c);
            }
            p.hand.refreshHandLayout();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            isDone = true;
        }

        tickDuration();
    }

    public interface FilterCards {
        boolean canRetain(AbstractCard handCard);
    }
}