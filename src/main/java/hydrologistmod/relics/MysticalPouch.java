package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardSave;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import hydrologistmod.helpers.SwapperHelper;
import javassist.CtBehavior;

public class MysticalPouch extends CustomRelic implements CustomSavable<WaterPouch.SaveInfo> {
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
        AbstractCard masterDeckCard = SwapperHelper.findMasterDeckEquivalent(card);
        if (masterDeckCard == null) {
            storedCard = card.makeSameInstanceOf();
        } else {
            storedCard = masterDeckCard;
        }
        setDescriptionWithCard();
    }

    @Override
    public void atBattleStart() {
        if (storedCard != null) {
            flash();
            addToBot(new MakeTempCardInHandAction(storedCard, false, true));
            addToBot(new MakeTempCardInHandAction(storedCard, false, true));
        }
    }

    @Override
    public WaterPouch.SaveInfo onSave() {
        if (storedCard != null) {
            WaterPouch.SaveInfo save = new WaterPouch.SaveInfo();
            AbstractCard masterDeckCard = StSLib.getMasterDeckEquivalent(storedCard);
            if (masterDeckCard != null) {
                save.deckIndex = AbstractDungeon.player.masterDeck.group.indexOf(masterDeckCard);
            } else {
                save.save = new CardSave(storedCard.cardID, storedCard.timesUpgraded, storedCard.misc);
            }
            return save;
        } else {
            return null;
        }
    }

    @Override
    public void onLoad(WaterPouch.SaveInfo save) {
        if (save == null) {
            return;
        }
        if (save.deckIndex >= 0) {
            if (save.deckIndex < AbstractDungeon.player.masterDeck.group.size()) {
                storedCard = AbstractDungeon.player.masterDeck.group.get(save.deckIndex).makeSameInstanceOf();
                setDescriptionWithCard();
            } else {
                System.out.println("how is saved index larger?");
                storedCard = new Madness();
            }
        } else if (save.save != null) {
            storedCard = CardLibrary.getCard(save.save.id).makeCopy();
            for (int i = 0; i < save.save.upgrades; ++i) {
                storedCard.upgrade();
            }
            storedCard.misc = save.save.misc;
        } else {
            System.out.println("Water Pouch Failed to load saved card");
            storedCard = new Madness();
        }
    }

    private void oldOnLoad(Integer num) {
        System.out.println("INFO: Mystical Pouch loaded using old save data");
        if (num >= 0 && num < AbstractDungeon.player.masterDeck.group.size()) {
            storedCard = AbstractDungeon.player.masterDeck.group.get(num).makeSameInstanceOf();
        } else {
            storedCard = new Madness();
        }
    }

    public void onLoadRaw(JsonElement value) {
        Object parsed = null;
        try {
            parsed = saveFileGson.fromJson(value, this.savedType());
        } catch (JsonSyntaxException e) {
            parsed = saveFileGson.fromJson(value, Integer.TYPE);
        }
        if (parsed instanceof WaterPouch.SaveInfo) {
            onLoad((WaterPouch.SaveInfo)parsed);
        } else if (parsed instanceof Integer) {
            oldOnLoad((Integer)parsed);
        } else {
            onLoad(null);
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
    public static class RemovePouchCardFromDeckPatch {
        @SpireInsertPatch(
                locator = Locator.class,
                localvars = {"copy"}
        )
        public static void Insert(CardGroup __instance, CardGroup masterDeck, CardGroup copy) {
            if (AbstractDungeon.player.hasRelic(ID) && storedCard != null) {
                for (AbstractCard card : copy.group) {
                    if (card.uuid.equals(storedCard.uuid)) {
                        copy.removeCard(card);
                        break;
                    }
                }
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardGroup.class, "group");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}