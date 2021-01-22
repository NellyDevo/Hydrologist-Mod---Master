package hydrologistmod.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

import java.lang.reflect.Field;

public class UseCardActionPatch {
    @SpirePatch(
            clz = UseCardAction.class,
            method = SpirePatch.CLASS
    )
    public static class UseCardActionField {
        public static SpireField<AbstractCard> transmuteTargetCard = new SpireField<>(() -> null);
    }

    @SpirePatch(
            clz = UseCardAction.class,
            method = "update"
    )
    public static class UseCardActionInsertPatch {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn Insert(UseCardAction __instance) {
            //quick fix, remove cards from limbo for multi-play effects
            AbstractDungeon.player.limbo.removeCard((AbstractCard)ReflectionHacks.getPrivate(__instance, UseCardAction.class, "targetCard"));

            //if the played card was transmuted, handle separately from regular UseCardAction logic
            if (UseCardActionField.transmuteTargetCard.get(__instance) != null) {
                AbstractCard newCard = UseCardActionField.transmuteTargetCard.get(__instance);
                try {
                    Field targetCardField = UseCardAction.class.getDeclaredField("targetCard");
                    targetCardField.setAccessible(true);
                    AbstractCard card = (AbstractCard)targetCardField.get(__instance);
                    card.freeToPlayOnce = false;
                    card.isInAutoplay = false;
                    card.exhaustOnUseOnce = false;
                    card.dontTriggerOnUseCard = false;
                    AbstractDungeon.player.limbo.removeCard(card);
                    targetCardField.set(__instance, newCard);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                AbstractDungeon.player.hand.moveToDiscardPile(newCard);
                AbstractDungeon.player.limbo.removeCard(newCard);
                __instance.isDone = true;
                return SpireReturn.Return(null);
            } else {
                return SpireReturn.Continue();
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractCard.class, "freeToPlayOnce");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
