package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;

import java.lang.reflect.Field;

public class SwapWhenPlayedAction extends AbstractGameAction {
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractCard card1;
    private AbstractCard card2;

    public SwapWhenPlayedAction(AbstractCard card) {
        this.duration = DURATION;
        card1 = card;
        if (SwapperHelper.isCardSwappable(card)) {
            card2 = SwapperHelper.getNextCard(card);
        } else {
            isDone = true;
            System.out.println("ERROR: why is SwapWhenPlayedAction being created on a non-paired card?");
        }
    }

    @Override
    public void update() {
        if (!isDone) {
            AbstractPlayer p = AbstractDungeon.player;
            // find action, put card 2 into action, mark card 2 as card in use
            UseCardAction useCardAction = null;
            for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
                if (action instanceof UseCardAction) {
                    useCardAction = (UseCardAction)action;
                    break;
                }
            }
            if (useCardAction != null) {
                try {
                    Field targetCardField = UseCardAction.class.getDeclaredField("targetCard");
                    targetCardField.setAccessible(true);
                    if (targetCardField.get(useCardAction) == card1 && p.cardInUse == card1) {
                        if (card1 instanceof SwappableCard) {
                            ((SwappableCard) card1).onSwapOut();
                        }
                        targetCardField.set(useCardAction, card2);
                        p.cardInUse = card2;
                        card2.current_x = card1.current_x;
                        card2.current_y = card1.current_y;
                        card2.target_x = card1.target_x;
                        card2.target_y = card1.target_y;
                        if (card2 instanceof SwappableCard) {
                            ((SwappableCard) card2).onSwapIn();
                        }
                        card2.isGlowing = card1.isGlowing;
                        card1.isGlowing = false;
                        card2.flash();
                        for (AbstractCard handCard : p.hand.group) {
                            handCard.applyPowers();
                        }
                    } else {
                        System.out.println("ERROR: useCardAction targetCard and/or player.cardInUse is not the main card. Unable to swap.");
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.out.println("ERROR: target card reflection failed, unable to swap.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("ERROR: could not find a useCardAction! unable to Swap.");
            }
            this.isDone = true;
        }
    }
}