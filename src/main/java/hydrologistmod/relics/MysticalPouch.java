package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import javassist.CtBehavior;

import java.util.ArrayList;

public class MysticalPouch extends CustomRelic implements CustomSavable<Integer> {
    public static final String ID = "hydrologistmod:MysticalPouch";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/MysticalPouch.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/MysticalPouchOutline.png");
    public static AbstractCard storedCard = null;

    public MysticalPouch() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    public void setDescriptionWithCard() {
        description = DESCRIPTIONS[0];
        tips.clear();
        tips.add(new PowerTip(this.name, this.description));
        tips.add(new PowerTip(DESCRIPTIONS[1], FontHelper.colorString(storedCard.name, "y") + DESCRIPTIONS[2]));
        initializeTips();
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        AbstractCard deckCard = StSLib.getMasterDeckEquivalent(card);
        if (deckCard != null) {
            storedCard = deckCard;
            setDescriptionWithCard();
        }
    }

    @Override
    public void atBattleStart() {
        addToBot(new MakeTempCardInHandAction(storedCard));
    }

    @Override
    public Integer onSave() {
        if (storedCard != null) {
            return AbstractDungeon.player.masterDeck.group.indexOf(storedCard);
        } else {
            return -1;
        }
    }

    @Override
    public void onLoad(Integer cardIndex) {
        if (cardIndex == null) {
            return;
        }
        if (cardIndex >= 0 && cardIndex < AbstractDungeon.player.masterDeck.group.size()) {
            storedCard = AbstractDungeon.player.masterDeck.group.get(cardIndex);
            if (storedCard != null) {
                setDescriptionWithCard();
            }
        }
    }

    @Override
    public void obtain() {
        if (AbstractDungeon.player.hasRelic(WaterPouch.ID)) {
            for (int i = 0; i < AbstractDungeon.player.relics.size(); ++i) {
                if (AbstractDungeon.player.relics.get(i).relicId.equals(WaterPouch.ID)) {
                    storedCard = WaterPouch.storedCard;
                    if (storedCard != null) {
                        setDescriptionWithCard();
                    }
                    instantObtain(AbstractDungeon.player, i, true);
                    break;
                }
            }
        } else {
            super.obtain();
        }
    }

    @Override
    public boolean canSpawn() {
        return AbstractDungeon.player.hasRelic(WaterPouch.ID);
    }

    @Override
    public AbstractRelic makeCopy() {
        return new MysticalPouch();
    }

    @SpirePatch(
            clz = CardGroup.class,
            method = "initializeDeck"
    )
    public static class MakePouchCardInnatePatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"c", "placeOnTop"}
        )
        public static void Insert(CardGroup __instance, CardGroup masterDeck, AbstractCard c, ArrayList<AbstractCard> placeOnTop) {
            if (AbstractDungeon.player.hasRelic(ID) && c.uuid.equals(storedCard.uuid)) {
                placeOnTop.add(c);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "addToTop");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}