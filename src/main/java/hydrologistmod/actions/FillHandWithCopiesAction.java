package hydrologistmod.actions;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class FillHandWithCopiesAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:FillHandWithCopiesAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractCard card;

    public FillHandWithCopiesAction(AbstractCard card) {
        this.duration = DURATION;
        this.card = card;
    }

    @Override
    public void update() {
        if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
            AbstractDungeon.actionManager.addToTop(new MakeTempCardInHandAction(card, BaseMod.MAX_HAND_SIZE - AbstractDungeon.player.hand.size()));
        }
        isDone = true;

        int x = 10;

        do {
            x += 5;
        } while (x < 20);

        while (x < 30) {
            x += 5;
        }
    }
}