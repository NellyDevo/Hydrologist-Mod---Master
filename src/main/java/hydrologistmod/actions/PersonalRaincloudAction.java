package hydrologistmod.actions;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import hydrologistmod.cards.Raincloud;
import hydrologistmod.helpers.CardProxyHelper;
import hydrologistmod.helpers.SwapperHelper;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class PersonalRaincloudAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:PersonalRaincloudAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private ArrayList<AbstractCard> cannotPair = new ArrayList<>();
    private boolean upgraded;

    public PersonalRaincloudAction(boolean upgraded) {
        this.duration = DURATION;
        this.upgraded = upgraded;
    }

    @Override
    public void update() {
        AbstractPlayer p = AbstractDungeon.player;
        if (duration == Settings.ACTION_DUR_FAST) {
            for (AbstractCard c : p.hand.group) {
                if (SwapperHelper.isCardSwappable(c)) {
                    cannotPair.add(c);
                }
            }
            if (cannotPair.size() == p.hand.group.size()) {
                AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(new Raincloud()));
                isDone = true;
                return;
            } else if (p.hand.group.size() - cannotPair.size() == 1) {
                for (AbstractCard c : p.hand.group) {
                    if (!SwapperHelper.isCardSwappable(c)) {
                        pairWithRaincloud(c);
                        isDone = true;
                        return;
                    }
                }
            }
            p.hand.group.removeAll(cannotPair);
            if (p.hand.group.size() > 1) {
                AbstractDungeon.handCardSelectScreen.open("pair with your Raincloud.", 1, false, false, false, false);
                tickDuration();
                return;
            } else if (p.hand.group.size() == 1) {
                pairWithRaincloud(p.hand.getTopCard());
                isDone = true;
                return;
            }
            for (AbstractCard c : cannotPair) {
                p.hand.addToTop(c);
            }
            p.hand.refreshHandLayout();
            AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(new Raincloud()));
            isDone = true;
            return;
        }
        if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
            pairWithRaincloud(AbstractDungeon.handCardSelectScreen.selectedCards.group.get(0));
            for (AbstractCard c : cannotPair) {
                p.hand.addToTop(c);
            }
            p.hand.refreshHandLayout();
            AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
            AbstractDungeon.handCardSelectScreen.selectedCards.group.clear();
            isDone = true;
        }
        tickDuration();
    }

    private void pairWithRaincloud(AbstractCard oldCard) {
        MethodHandler newBehaviour = (Object self, Method m, Method proceed, Object[] args) -> {
            SwapperHelper.upgrade((AbstractCard)self);
            return proceed.invoke(self, args);
        };
        MethodFilter filter = (Method m) -> m.getName().equals("upgrade");
        AbstractCard newCard = CardProxyHelper.createSameInstanceProxy(oldCard, filter, newBehaviour);
        AbstractDungeon.player.hand.removeCard(oldCard);
        CardModifierManager.addModifier(newCard, new RaincloudModifier());
        AbstractCard raincloud = new Raincloud();
        if (upgraded) {
            raincloud.upgrade();
            if (newCard.canUpgrade()) {
                newCard.upgrade();
            }
        }
        AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(raincloud));
        SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(raincloud, newCard)));
    }

    public static class RaincloudModifier extends AbstractCardModifier {
        public String modifyDescription(String rawDescription, AbstractCard card) {
            return rawDescription + " NL hydrologistmod:Swappable.";
        }

        public AbstractCardModifier makeCopy() {
            return new RaincloudModifier();
        }
    }
}