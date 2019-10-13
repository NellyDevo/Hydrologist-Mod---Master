package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import hydrologistmod.patches.HydrologistTags;

public class HailAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:HailAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractCard callingCard;

    public HailAction(AbstractCard card, int amount) {
        duration = DURATION;
        callingCard = card;
        this.amount = amount;
    }

    @Override
    public void update() {
        if (duration == DURATION) {
            AbstractPlayer p = AbstractDungeon.player;
            callingCard.baseDamage += amount;
            callingCard.applyPowers();
            for (AbstractCard c : p.discardPile.group) {
                if (c.hasTag(HydrologistTags.ICE)) {
                    c.baseDamage += amount;
                    c.applyPowers();
                }
            }
            for (AbstractCard c : p.drawPile.group) {
                if (c.hasTag(HydrologistTags.ICE)) {
                    c.baseDamage += amount;
                    c.applyPowers();
                }
            }
            for (AbstractCard c : p.hand.group) {
                if (c.hasTag(HydrologistTags.ICE)) {
                    c.baseDamage += amount;
                    c.applyPowers();
                }
            }
        }
        this.tickDuration();
    }
}