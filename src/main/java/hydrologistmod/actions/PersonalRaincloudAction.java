package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import helpers.CardProxyHelper;
import helpers.SwapperHelper;
import hydrologistmod.cards.Raincloud;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;

public class PersonalRaincloudAction extends AbstractGameAction {
    private static final String ID = "hydrologistmod:PersonalRaincloudAction";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;

    public PersonalRaincloudAction() {
        this.duration = DURATION;
    }

    @Override
    public void update() {
        if (duration == DURATION) {
            AbstractDungeon.handCardSelectScreen.open("Choose a card to Transmute", 1, false, false);
            tickDuration();
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            AbstractCard oldCard = AbstractDungeon.handCardSelectScreen.selectedCards.group.get(0);
            MethodHandler newBehaviour = (Object self, Method m, Method proceed, Object[] args) -> {
                    SwapperHelper.upgrade((AbstractCard)self);
                    return proceed.invoke(self, args);
                };
            MethodFilter filter = (Method m) -> m.getName().equals("upgrade");
            AbstractCard newCard = CardProxyHelper.createSameInstanceProxy(oldCard, filter, newBehaviour);
            AbstractDungeon.player.hand.removeCard(oldCard);
            AbstractCard raincloud = new Raincloud();
            AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(raincloud));
            SwapperHelper.registerPair(raincloud, newCard);
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
        }
        tickDuration();
    }
}