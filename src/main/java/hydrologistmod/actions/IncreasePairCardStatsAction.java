package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;

public class IncreasePairCardStatsAction extends AbstractGameAction {
    private static final String ID = "hydrologistmod:IncreasePairCardStatsAction";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractCard card;
    private AbstractCard pairCard;
    private int damageIncrease;
    private int blockIncrease;

    public IncreasePairCardStatsAction(AbstractCard card, AbstractCard pairCard, int damageIncrease, int blockIncrease) {
        this.duration = DURATION;
        this.card = card;
        this.pairCard = pairCard;
        this.damageIncrease = damageIncrease;
        this.blockIncrease = blockIncrease;
    }

    @Override
    public void update() { //TODO add pizzaz
        pairCard.baseDamage += damageIncrease;
        pairCard.baseBlock += blockIncrease;
        pairCard.applyPowers();
        isDone = true;
    }
}