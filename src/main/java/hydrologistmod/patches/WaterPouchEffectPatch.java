package hydrologistmod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.relics.MysticalPouch;
import hydrologistmod.relics.WaterPouch;
import hydrologistmod.vfx.RelicSoul;
import java.util.ArrayList;

@SpirePatch(
        clz = UseCardAction.class,
        method = "update"
)
public class WaterPouchEffectPatch {
    private static final float DELAY_TIMER = 0.3f;
    private static Soul soul = null;
    private static float timer = DELAY_TIMER;

    public static SpireReturn Prefix(UseCardAction __instance) {
        AbstractRoom room = AbstractDungeon.getCurrRoom();
        if (room.monsters.areMonstersBasicallyDead() && hasRelic()) {
            if (soul == null) {
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
                AbstractCard masterDeckCard = SwapperHelper.findMasterDeckEquivalent(card);
                if (masterDeckCard != null) {
                    AbstractCard card2 = card;
                    boolean doSwap = false;
                    while (masterDeckCard.uuid != card2.uuid) {
                        card2 = SwapperHelper.getNextCard(card2);
                        doSwap = true;
                    }
                    if (doSwap) {
                        card2.current_x = card.current_x;
                        card2.current_y = card.current_y;
                        card2.target_x = card.target_x;
                        card2.target_y = card.target_y;
                        card2.isGlowing = card.isGlowing;
                        card.stopGlowing();
                        card.flashVfx = null;
                        card2.flash();
                        card = card2;
                        ReflectionHacks.setPrivate(__instance, UseCardAction.class, "targetCard", card);
                        AbstractDungeon.player.cardInUse = card;
                    }
                }
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
                    if (s.isReadyForReuse && s instanceof RelicSoul) {
                        ((RelicSoul)s).targetRelic = relic;
                        notEmpower(card, s, relic);
                        needMoreSouls = false;
                        soul = s;
                        break;
                    }
                }

                if (needMoreSouls) {
                    RelicSoul s = new RelicSoul();
                    s.targetRelic = relic;
                    notEmpower(card, s, relic);
                    souls.add(s);
                    soul = s;
                }
            } else if (timer == DELAY_TIMER) {
                if (soul.isReadyForReuse) {
                    timer -= Gdx.graphics.getDeltaTime();
                }
            } else {
                timer -= Gdx.graphics.getDeltaTime();
                if (timer <= 0) {
                    timer = DELAY_TIMER;
                    soul = null;
                    __instance.isDone = true;
                    AbstractDungeon.player.cardInUse = null;
                }
            }
            return SpireReturn.Return(null);
        } else {
            return SpireReturn.Continue();
        }
    }

    private static void notEmpower(AbstractCard card, Soul s, AbstractRelic relic) {
        s.empower(card);
        ReflectionHacks.setPrivate(s, Soul.class, "target", new Vector2(relic.hb.cX, relic.hb.cY));
        s.group = RelicSoul.dummyGroup;
    }

    private static boolean hasRelic() {
        return (AbstractDungeon.player.hasRelic(WaterPouch.ID) || AbstractDungeon.player.hasRelic(MysticalPouch.ID));
    }
}
