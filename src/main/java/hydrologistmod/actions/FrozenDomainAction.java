package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class FrozenDomainAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:FrozenDomainAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private boolean upgraded;

    public FrozenDomainAction(boolean upgraded) {
        this.duration = DURATION;
        this.upgraded = upgraded;
    }

    @Override
    public void update() {
        if (duration == DURATION) {
            CardGroup list = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (AbstractCard c : AbstractDungeon.player.discardPile.group) {
                if (c.cost > 0) {
                    list.addToTop(c);
                }
            }
            if (!list.isEmpty()) {
                AbstractCard c = list.getRandomCard(AbstractDungeon.cardRandomRng);
                c.cost = 0;
                c.costForTurn = 0;
                c.isCostModified = true;
            }
            if (upgraded) {
                list.clear();
                for (AbstractCard c : AbstractDungeon.player.drawPile.group) {
                    if (c.cost > 0) {
                        list.addToTop(c);
                    }
                }
                if (!list.isEmpty()) {
                    AbstractCard c = list.getRandomCard(AbstractDungeon.cardRandomRng);
                    c.cost = 0;
                    c.costForTurn = 0;
                    c.isCostModified = true;
                }
            }
        }
        tickDuration();
    }
}