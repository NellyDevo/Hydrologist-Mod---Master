package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import hydrologistmod.HydrologistMod;

public class SaunaAction extends AbstractGameAction {
    private static final String ID = "hydrologistmod:SaunaAction";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private int amount;

    public SaunaAction(int amount) {
        this.duration = DURATION;
        this.amount = amount;
    }

    @Override
    public void update() {
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (HydrologistMod.isThisCorporeal(card)) {
                AbstractDungeon.actionManager.addToTop(new GainBlockAction(AbstractDungeon.player, AbstractDungeon.player, amount));
            }
        }
        isDone = true;
    }
}