package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

public class FlowingFormAction extends AbstractGameAction {
    private static final String ID = "hydrologistmod:FlowingFormAction";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private int drawAmount;

    public FlowingFormAction(int drawAmount) {
        this.duration = DURATION;
        this.drawAmount = drawAmount;
    }

    @Override
    public void update() {
        if (duration == DURATION) {
            AbstractDungeon.handCardSelectScreen.open("Choose a card to Transmute", 1, true, true);
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            if (AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                isDone = true;
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                return;
            } else {
                AbstractCard card = AbstractDungeon.handCardSelectScreen.selectedCards.group.get(0);
                AbstractDungeon.player.hand.moveToDiscardPile(card);
                card.triggerOnManualDiscard();
                GameActionManager.incrementDiscard(false);
                AbstractDungeon.actionManager.addToTop(new DrawCardAction(AbstractDungeon.player, drawAmount));
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            }
        }
        tickDuration();
    }
}