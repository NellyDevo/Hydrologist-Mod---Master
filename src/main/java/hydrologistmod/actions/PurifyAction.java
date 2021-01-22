package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.HydrologistMod;

import java.util.ArrayList;

public class PurifyAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:PurifyAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    AbstractMonster target;
    private boolean firstFrame = true;

    public PurifyAction(AbstractMonster m) {
        this.duration = DURATION;
        if (m == null) {
            target = AbstractDungeon.getRandomMonster();
        } else {
            target = m;
        }
    }

    @Override
    public void update() {
        if (target.isDeadOrEscaped()) {
            isDone = true;
            return;
        }

        if (firstFrame) {
            firstFrame = false;
            ArrayList<AbstractCard> cardsToMove = new ArrayList<>();
            float xOffset = 0.0f;
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (HydrologistMod.isThisCorporeal(c)) {
                    boolean con = false;
                    for (CardQueueItem q : AbstractDungeon.actionManager.cardQueue) {
                        if (q.card == c) {
                            con = true;
                            break;
                        }
                    }
                    if (con) {
                        continue;
                    }
                    c.freeToPlayOnce = true;
                    AbstractDungeon.actionManager.cardQueue.add(new CardQueueItem(c, target));
                    c.target_x = Settings.WIDTH / 2.0f + xOffset * Settings.scale;
                    c.target_y = Settings.HEIGHT / 2.0f;
                    xOffset -= 300.0f;
                    cardsToMove.add(c);
                }
            }
            AbstractDungeon.player.hand.group.removeAll(cardsToMove);
        }
        tickDuration();
    }
}