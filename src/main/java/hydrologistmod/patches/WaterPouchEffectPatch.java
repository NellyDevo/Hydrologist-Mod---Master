package hydrologistmod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import hydrologistmod.relics.MysticalPouch;
import hydrologistmod.relics.WaterPouch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@SpirePatch(
        clz = UseCardAction.class,
        method = "update"
)
public class WaterPouchEffectPatch {
    public static SpireReturn Prefix(UseCardAction __instance) {
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room.monsters.areMonstersBasicallyDead() && hasRelic()) {
            AbstractRelic relic;
            relic = AbstractDungeon.player.getRelic(WaterPouch.ID);
            if (relic == null) {
                relic = AbstractDungeon.player.getRelic(MysticalPouch.ID);
            }
            if (relic == null) {
                System.out.println("How the eff did you get here?");
                return SpireReturn.Continue();
            }
            AbstractCard card = ReflectionHacks.getPrivate(__instance, UseCardAction.class, "targetCard");
            if (AbstractDungeon.player.hoveredCard == card) {
                AbstractDungeon.player.releaseCard();
            }
            AbstractDungeon.actionManager.removeFromQueue(card);
            card.unhover();
            card.untip();
            card.stopGlowing();
            AbstractDungeon.player.hand.group.remove(card);
            card.shrink();
            SoulGroup soulGroup = room.souls;
            boolean needMoreSouls = true;
            ArrayList<Soul> souls = ReflectionHacks.getPrivate(soulGroup, SoulGroup.class, "souls");
            for (Soul s : souls) {
                if (s.isReadyForReuse) {
                    notEmpower(card, s, relic);
                    needMoreSouls = false;
                    break;
                }
            }

            if (needMoreSouls) {
                Soul s = new Soul();
                notEmpower(card, s, relic);
                souls.add(s);
            }
            __instance.isDone = true;
            return SpireReturn.Return(null);
        } else {
            return SpireReturn.Continue();
        }
    }

    private static void notEmpower(AbstractCard card, Soul s, AbstractRelic relic) {
        CardCrawlGame.sound.play("CARD_POWER_WOOSH", 0.1f);
        s.card = card;
        s.group = null;
        ReflectionHacks.setPrivate(s, Soul.class, "pos", new Vector2(card.current_x, card.current_y));
        ReflectionHacks.setPrivate(s, Soul.class, "target", new Vector2(relic.currentX, relic.currentY));
        try {
            Method m = Soul.class.getDeclaredMethod("setSharedVariables");
            m.setAccessible(true);
            m.invoke(s);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasRelic() {
        return (AbstractDungeon.player.hasRelic(WaterPouch.ID) || AbstractDungeon.player.hasRelic(MysticalPouch.ID));
    }
}
