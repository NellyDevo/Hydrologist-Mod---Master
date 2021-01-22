package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import hydrologistmod.cards.RazorIce;

public class StormOfSleetAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:StormOfSleetAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private boolean firstFrame = true;

    public StormOfSleetAction() {
        this.duration = DURATION;
    }

    @Override
    public void update() {
        if (firstFrame) {
            if (AbstractDungeon.getMonsters().areMonstersBasicallyDead() || AbstractDungeon.player.hand.size() == 0) {
                isDone = true;
                return;
            }
            AbstractDungeon.handCardSelectScreen.open("discard.", 99, true, true);
            AbstractDungeon.player.hand.applyPowers();
            tickDuration();
            firstFrame = false;
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            int cardsDiscarded = 0;
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                AbstractDungeon.player.hand.moveToDiscardPile(c);
                ++cardsDiscarded;
                c.triggerOnManualDiscard();
                GameActionManager.incrementDiscard(false);
            }
            addToTop(new MakeTempCardInHandAction(new RazorIce(), cardsDiscarded));
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }
        tickDuration();
    }
}