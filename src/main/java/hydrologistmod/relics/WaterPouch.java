package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.google.gson.JsonElement;
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
import javassist.CtBehavior;

public class WaterPouch extends CustomRelic implements CustomSavable<WaterPouch.SaveInfo> {
    public static final String ID = "hydrologistmod:WaterPouch";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/WaterPouch.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/WaterPouchOutline.png");
    public static AbstractCard storedCard = null;

    public WaterPouch() {
        super(ID, IMG, OUTLINE, RelicTier.STARTER, LandingSound.MAGICAL);
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
        storedCard = card.makeSameInstanceOf();
        setDescriptionWithCard();
    }

    @Override
    public void atBattleStart() {
        addToBot(new MakeTempCardInHandAction(storedCard, false, true));
    }

    @Override
    public SaveInfo onSave() {
        if (storedCard != null) {
            SaveInfo save = new SaveInfo();
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
    public void onLoad(SaveInfo save) {
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
        } catch (IllegalStateException e) {
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
    public AbstractRelic makeCopy() {
        return new WaterPouch();
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

    public static class SaveInfo {
        public int deckIndex = -1;
        public CardSave save = null;
    }
}