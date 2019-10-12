package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import hydrologistmod.HydrologistMod;

import java.util.ArrayList;

public class UpgradeCorporealAction extends AbstractGameAction {
    private static final String ID = "hydrologistmod:UpgradeCorporealAction";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractPlayer p;
    private boolean upgraded;
    private ArrayList<AbstractCard> cannotUpgrade = new ArrayList<>();

    public UpgradeCorporealAction(boolean upgraded) {
        this.duration = DURATION;
        p = AbstractDungeon.player;
        duration = Settings.ACTION_DUR_FAST;
        this.upgraded = upgraded;
    }

    @Override
    public void update() {
        if (duration == Settings.ACTION_DUR_FAST) {
            if (upgraded) {
                for (AbstractCard c : p.hand.group) {
                    if (c.canUpgrade() && HydrologistMod.isThisCorporeal(c)) {
                        c.upgrade();
                        c.superFlash();
                    }
                }
                isDone = true;
                return;
            }
            for (AbstractCard c : p.hand.group) {
                if (!c.canUpgrade() || !HydrologistMod.isThisCorporeal(c)) {
                    cannotUpgrade.add(c);
                }
            }
            if (cannotUpgrade.size() == p.hand.group.size()) {
                isDone = true;
                return;
            } else if (p.hand.group.size() - cannotUpgrade.size() == 1) {
                for (AbstractCard c : p.hand.group) {
                    if (c.canUpgrade() && HydrologistMod.isThisCorporeal(c)) {
                        c.upgrade();
                        c.superFlash();
                        isDone = true;
                        return;
                    }
                }
            }
            p.hand.group.removeAll(cannotUpgrade);
            if (p.hand.group.size() > 1) {
                AbstractDungeon.handCardSelectScreen.open(TEXT[0], 1, false, false, false, true);
                tickDuration();
                return;
            } else if (p.hand.group.size() == 1) {
                p.hand.getTopCard().upgrade();
                p.hand.getTopCard().superFlash();
                for (AbstractCard c : cannotUpgrade) {
                    p.hand.addToTop(c);
                }
                p.hand.refreshHandLayout();
                isDone = true;
            }
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            for (AbstractCard c : AbstractDungeon.handCardSelectScreen.selectedCards.group) {
                c.upgrade();
                c.superFlash();
                p.hand.addToTop(c);
            }
            for (AbstractCard c : cannotUpgrade) {
                p.hand.addToTop(c);
            }
            p.hand.refreshHandLayout();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            isDone = true;
        }
        tickDuration();
    }
}