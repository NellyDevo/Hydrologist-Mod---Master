package hydrologistmod.actions;

import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import hydrologistmod.cardmods.effects.AbstractExtraEffectModifier;
import hydrologistmod.cardmods.PurityModifier;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.TransmutableAffectingPower;
import hydrologistmod.interfaces.TransmutableAffectingRelic;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.SwapperCardPatch;
import hydrologistmod.patches.UseCardActionPatch;
import hydrologistmod.vfx.TransmuteCardEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class TransmuteCardAction extends AbstractGameAction {
    private static final String ID = "hydrologistmod:TransmuteCardAction";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    private boolean initialized = false;
    private boolean completed = false;
    public int choices = 1;
    public int cards = 1;
    public int purity = 1;
    private boolean anyNumber;
    private AfterTransmute followup;
    private AbstractCard storedOldCard;
    private Conditionals conditions;
    private boolean transformPlayed = false;
    private AbstractCard playedCard;
    private HashMap<AbstractCard, AbstractCard> transmutedPairs = new HashMap<>();

    public TransmuteCardAction(boolean anyNumber, AfterTransmute followup, Conditionals conditions) {
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof TransmutableAffectingPower) {
                ((TransmutableAffectingPower)power).onTransmute(this);
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof TransmutableAffectingRelic) {
                ((TransmutableAffectingRelic)relic).onTransmute(this);
            }
        }
        this.followup = followup;
        this.anyNumber = anyNumber;
        this.conditions = conditions;
    }

    public TransmuteCardAction() {
        this(false, null, null);
    }

    public TransmuteCardAction(int amount) {
        this(false, null, null);
        cards = amount;
    }

    public TransmuteCardAction(boolean anyNumber) {
        this(anyNumber, null, null);
    }

    public TransmuteCardAction(AfterTransmute followup) {
        this(false, followup, null);
    }

    public TransmuteCardAction(Conditionals conditions) {
        this(false, null, conditions);
    }

    public TransmuteCardAction(AbstractCard playedCard, AfterTransmute followup) {
        this(false, followup, null);
        this.playedCard = playedCard;
        transformPlayed = true;
    }

    public TransmuteCardAction(AbstractCard playedCard) {
        this(playedCard, null);
    }

    public void update() {
        if (completed) {
            return;
        }
        if (!initialized) {
            initialized = true;
            if (!transformPlayed) {
                if (AbstractDungeon.player.hand.size() < 1) {
                    isDone = true;
                } else {
                    if (anyNumber) {
                        AbstractDungeon.handCardSelectScreen.open(TEXT[0], 99, true, true);
                    } else {
                        AbstractDungeon.handCardSelectScreen.open(TEXT[0], Math.min(AbstractDungeon.player.hand.size(), cards), false, false);
                    }
                }
            } else {
                if (choices == 0) {
                    isDone = true;
                    System.out.println("TRANSMUTECARDACTION: Make 0 choices? How did this happen?");
                    return;
                } else if (choices == 1) {
                    AbstractCard newCard = getRandomCard(getTransmutationCandidates(playedCard)).makeCopy();
                    modifyNewCard(playedCard, newCard);
                    //modify useCardAction for the current card using related patch
                    UseCardAction useCardAction = null;
                    for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
                        if (action instanceof UseCardAction) {
                            AbstractCard c = ReflectionHacks.getPrivate(action, UseCardAction.class, "targetCard");
                            if (c != null && c == playedCard) {
                                useCardAction = (UseCardAction) action;
                                break;
                            } else {
                                System.out.println("ERROR: transmute when played, but UseCardAction not pointing at passed card");
                                isDone = true;
                                return;
                            }
                        }
                    }
                    TransmuteCardEffect.copyCardPosition(playedCard, newCard);
                    AbstractDungeon.player.hand.removeCard(playedCard);
                    AbstractDungeon.player.cardInUse = null;
                    UseCardActionPatch.UseCardActionField.transmuteTargetCard.set(useCardAction, newCard);
                    transmutedPairs.put(playedCard, newCard);
                    AbstractDungeon.topLevelEffects.add(new TransmuteCardEffect(transmutedPairs, null, this, 0.75f));
                    completed = true;
                } else {
                    CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                    ArrayList<AbstractCard> targets = getTransmutationCandidates(playedCard);
                    for (int i = 0; i < choices; ++i) {
                        if (!targets.isEmpty()) {
                            AbstractCard choice = getRandomCard(targets);
                            UnlockTracker.markCardAsSeen(choice.cardID);
                            if (!tmp.contains(choice)) {
                                tmp.addToBottom(choice);
                                targets.remove(choice);
                            }
                        }
                    }
                    AbstractDungeon.gridSelectScreen.open(tmp, 1, TEXT[1], false);
                    return;
                }
            }
            return;
        } else if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved && !transformPlayed) {
            AbstractCard oldCard = AbstractDungeon.handCardSelectScreen.selectedCards.group.get(0);
            AbstractDungeon.player.hand.group.remove(oldCard);
            if (choices == 0) {
                isDone = true;
                System.out.println("TRANSMUTECARDACTION: Make 0 choices? How did this happen?");
                return;
            } else if (choices == 1) {
                AbstractCard newCard = getRandomCard(getTransmutationCandidates(oldCard)).makeCopy();
                UnlockTracker.markCardAsSeen(newCard.cardID);
                modifyNewCard(oldCard, newCard);
                transmutedPairs.put(oldCard, newCard);
                AbstractDungeon.handCardSelectScreen.selectedCards.group.remove(oldCard);
                if (AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                    AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                }
                return;
            } else {
                CardGroup tmp = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                ArrayList<AbstractCard> targets = getTransmutationCandidates(oldCard);
                for (int i = 0; i < choices; ++i) {
                    if (!targets.isEmpty()) {
                        AbstractCard choice = getRandomCard(targets);
                        UnlockTracker.markCardAsSeen(choice.cardID);
                        if (!tmp.contains(choice)) {
                            tmp.addToBottom(choice);
                            targets.remove(choice);
                        }
                    }
                }
                AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
                storedOldCard = oldCard;
                AbstractDungeon.gridSelectScreen.open(tmp, 1, TEXT[1], false);
                return;
            }
        } else if (AbstractDungeon.gridSelectScreen.selectedCards.size() != 0) {
            AbstractCard newCard = AbstractDungeon.gridSelectScreen.selectedCards.get(0).makeCopy();
            if (!transformPlayed) {
                modifyNewCard(storedOldCard, newCard);
                transmutedPairs.put(storedOldCard, newCard);
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                AbstractDungeon.handCardSelectScreen.selectedCards.group.remove(0);
                if (!AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty()) {
                    AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = false;
                }
                return;
            } else {
                modifyNewCard(playedCard, newCard);
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                //modify useCardAction for the current card using related patch
                UseCardAction useCardAction = null;
                for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
                    if (action instanceof UseCardAction) {
                        try {
                            Field targetCardField = UseCardAction.class.getDeclaredField("targetCard");
                            targetCardField.setAccessible(true);
                            if (targetCardField.get(action) == playedCard) {
                                useCardAction = (UseCardAction) action;
                                break;
                            }
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                            isDone = true;
                            return;
                        }
                    }
                }
                TransmuteCardEffect.copyCardPosition(playedCard, newCard);
                AbstractDungeon.player.hand.removeCard(playedCard);
                AbstractDungeon.player.cardInUse = null;
                UseCardActionPatch.UseCardActionField.transmuteTargetCard.set(useCardAction, newCard);
                transmutedPairs.put(playedCard, newCard);
                AbstractDungeon.topLevelEffects.add(new TransmuteCardEffect(transmutedPairs, null, this, 0.75f));
                completed = true;
                return;
            }
        }
        if (AbstractDungeon.gridSelectScreen.selectedCards.isEmpty() && AbstractDungeon.handCardSelectScreen.selectedCards.group.isEmpty() && initialized) {
            float duration = 0.75f;
            if (anyNumber && transmutedPairs.keySet().size() > 1) {
                duration *= 2.0f;
            }
            AbstractDungeon.topLevelEffects.add(new TransmuteCardEffect(transmutedPairs, CardGroup.CardGroupType.HAND, this, duration));
            completed = true;
        }
    }

    private ArrayList<AbstractCard> getTransmutationCandidates(AbstractCard oldCard) {
        ArrayList<AbstractCard> targets = new ArrayList<>();
        switch (oldCard.rarity) {
            case RARE:
                for (AbstractCard candidate : AbstractDungeon.srcRareCardPool.group) {
                    if (!candidate.hasTag(AbstractCard.CardTags.HEALING) && (conditions == null || conditions.filter(candidate)) && !candidate.cardID.equals(oldCard.cardID)) {
                        targets.add(candidate.makeCopy());
                    }
                }
                break;
            case UNCOMMON:
                for (AbstractCard candidate : AbstractDungeon.srcUncommonCardPool.group) {
                    if (!candidate.hasTag(AbstractCard.CardTags.HEALING) && (conditions == null || conditions.filter(candidate)) && !candidate.cardID.equals(oldCard.cardID)) {
                        targets.add(candidate.makeCopy());
                    }
                }
                break;
            default:
                for (AbstractCard candidate : AbstractDungeon.srcCommonCardPool.group) {
                    if (!candidate.hasTag(AbstractCard.CardTags.HEALING) && (conditions == null || conditions.filter(candidate)) && !candidate.cardID.equals(oldCard.cardID)) {
                        targets.add(candidate.makeCopy());
                    }
                }
                break;
        }
        return targets;
    }

    private AbstractCard getRandomCard(ArrayList<AbstractCard> targets) {
        return targets.get(AbstractDungeon.cardRandomRng.random(targets.size() - 1));
    }

    private void modifyNewCard(AbstractCard oldCard, AbstractCard newCard) {
        for (AbstractPower power : AbstractDungeon.player.powers) {
            if (power instanceof TransmutableAffectingPower) {
                ((TransmutableAffectingPower)power).affectTransmutedCard(newCard);
            }
        }
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic instanceof TransmutableAffectingRelic) {
                ((TransmutableAffectingRelic)relic).affectTransmutedCard(newCard);
            }
        }
        if (followup != null) {
            if (SwapperHelper.isCardSwappable(newCard)) {
                boolean firstTime = true;
                for (AbstractCard card : SwapperCardPatch.SwappableChainField.swappableCards.get(newCard)) {
                    followup.doActions(oldCard, card, firstTime);
                    firstTime = false;
                }
            } else {
                followup.doActions(oldCard, newCard, true);
            }
        }
        if (oldCard instanceof TransmutableCard) {
            if (SwapperHelper.isCardSwappable(newCard)) {
                boolean firstTime = true;
                for (AbstractCard card : SwapperCardPatch.SwappableChainField.swappableCards.get(newCard)) {
                    if (card instanceof TransmutableCard) {
                        ((TransmutableCard) card).onTransmuted(newCard, firstTime);
                        firstTime = false;
                    }
                }
            } else {
                ((TransmutableCard) oldCard).onTransmuted(newCard, true);
            }
        }
        PurityModifier mod = new PurityModifier(purity);
        ArrayList<AbstractExtraEffectModifier> list = new ArrayList<>();
        if (CardModifierManager.hasModifier(oldCard, PurityModifier.ID)) {
            mod.amount += ((PurityModifier)CardModifierManager.getModifiers(oldCard, PurityModifier.ID).get(0)).amount;
        }
        if (oldCard instanceof TransmutableCard) {
            list.addAll(((TransmutableCard)oldCard).getMutableAbilities());
        }
        for (AbstractCardModifier effect : CardModifierManager.modifiers(oldCard)) {
            if (effect instanceof AbstractExtraEffectModifier) {
                if (((AbstractExtraEffectModifier)effect).isMutable) {
                    list.add((AbstractExtraEffectModifier)effect);
                }
            }
        }
        applyNewBuffs(list, mod, oldCard, newCard);
    }

    private void applyNewBuffs(ArrayList<AbstractExtraEffectModifier> list, PurityModifier purity, AbstractCard oldCard, AbstractCard newCard) {
        AbstractCard card = newCard;
        do {
            CardModifierManager.addModifier(card, purity.makeCopy());
            boolean firstTime = true;
            for (AbstractExtraEffectModifier mod : list) {
                CardModifierManager.addModifier(card, mod.makeCopy());
                mod.onCardTransmuted(oldCard, card, firstTime);
                firstTime = false;
            }
            card.applyPowers();
        } while (SwapperHelper.isCardSwappable(card) && (card = SwapperHelper.getNextCard(card)) != newCard);
    }

    public interface AfterTransmute {
        void doActions(AbstractCard oldCard, AbstractCard newCard, boolean firstTime);
    }

    public interface Conditionals {
        boolean filter(AbstractCard potentialCard);
    }
}
