package hydrologistmod.cards;

import basemod.abstracts.DynamicVariable;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.ApplyTemperatureAction;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;

import java.util.ArrayList;

public abstract class AbstractAdaptiveCard extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:AbstractAdaptiveCard";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;

    public int baseAdaptiveNumber;
    public int adaptiveNumber;
    public boolean isAdaptiveNumberModified = false;
    public boolean upgradedAdaptiveNumber = false;
    public CardTarget defaultTarget;
    public Mode mode;

    public AbstractAdaptiveCard(String id, String name, String img, int cost, String rawDescription,
                                   AbstractCard.CardType type, AbstractCard.CardColor color,
                                   AbstractCard.CardRarity rarity, AbstractCard.CardTarget target, int adaptiveAmount) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        defaultTarget = target;
        baseAdaptiveNumber = adaptiveNumber = adaptiveAmount;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        switch (mode) {
            case ICE:
                addToBot(new ApplyTemperatureAction(m, p, new ColdPower(m, p, adaptiveNumber)));
                break;
            case WATER:
                addToBot(new GainBlockAction(p, p, adaptiveNumber));
                break;
            case STEAM:
                addToBot(new ApplyTemperatureAction(m, p, new HeatPower(m, p, adaptiveNumber)));
                break;
            case NONE:
                break;
        }
    }

    @Override
    public void applyPowers() {
        adaptiveNumber = baseAdaptiveNumber;
        ArrayList<AbstractCard> list = AbstractDungeon.actionManager.cardsPlayedThisCombat;
        if (list.size() != 0) {
            AbstractCard card = list.get(list.size() - 1);
            if (card.hasTag(HydrologistTags.ICE)) {
                rawDescription = getDescription() + EXTENDED_DESCRIPTION[1];
                initializeDescription();
                mode = Mode.ICE;
                if (defaultTarget == CardTarget.SELF_AND_ENEMY) {
                    target = CardTarget.SELF_AND_ENEMY;
                } else {
                    target = CardTarget.ENEMY;
                }
            } else if (card.hasTag(HydrologistTags.WATER)) {
                int tmp = baseBlock;
                baseBlock = baseAdaptiveNumber * 2;
                super.applyPowers();
                baseBlock = tmp;
                adaptiveNumber = block;
                isAdaptiveNumberModified = adaptiveNumber != baseAdaptiveNumber;
                rawDescription = getDescription() + EXTENDED_DESCRIPTION[2];
                initializeDescription();
                mode = Mode.WATER;
                target = defaultTarget;
            } else if (card.hasTag(HydrologistTags.STEAM)) {
                rawDescription = getDescription() + EXTENDED_DESCRIPTION[3];
                initializeDescription();
                mode = Mode.STEAM;
                if (defaultTarget == CardTarget.SELF_AND_ENEMY) {
                    target = CardTarget.SELF_AND_ENEMY;
                } else {
                    target = CardTarget.ENEMY;
                }
            } else {
                rawDescription = getDescription() + EXTENDED_DESCRIPTION[0];
                initializeDescription();
                mode = Mode.NONE;
                target = defaultTarget;
            }
        }
        super.applyPowers();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        adaptiveNumber = baseAdaptiveNumber;
        ArrayList<AbstractCard> list = AbstractDungeon.actionManager.cardsPlayedThisCombat;
        if (list.size() != 0) {
            AbstractCard card = list.get(list.size() - 1);
            if (card.hasTag(HydrologistTags.ICE)) {
                rawDescription = getDescription() + EXTENDED_DESCRIPTION[1];
                initializeDescription();
                mode = Mode.ICE;
                if (defaultTarget == CardTarget.SELF_AND_ENEMY) {
                    target = CardTarget.SELF_AND_ENEMY;
                } else {
                    target = CardTarget.ENEMY;
                }
            } else if (card.hasTag(HydrologistTags.WATER)) {
                int tmp = baseBlock;
                baseBlock = baseAdaptiveNumber;
                super.calculateCardDamage(mo);
                baseBlock = tmp;
                adaptiveNumber = block;
                isAdaptiveNumberModified = adaptiveNumber != baseAdaptiveNumber;
                rawDescription = getDescription() + EXTENDED_DESCRIPTION[2];
                initializeDescription();
                mode = Mode.WATER;
                target = defaultTarget;
            } else if (card.hasTag(HydrologistTags.STEAM)) {
                rawDescription = getDescription() + EXTENDED_DESCRIPTION[3];
                initializeDescription();
                mode = Mode.STEAM;
                if (defaultTarget == CardTarget.SELF_AND_ENEMY) {
                    target = CardTarget.SELF_AND_ENEMY;
                } else {
                    target = CardTarget.ENEMY;
                }
            } else {
                rawDescription = getDescription() + EXTENDED_DESCRIPTION[0];
                initializeDescription();
                mode = Mode.NONE;
                target = defaultTarget;
            }
        }
        super.calculateCardDamage(mo);
    }

    public abstract String getDescription();

    protected void upgradeAdaptiveNumber(int num) {
        baseAdaptiveNumber += num;
        adaptiveNumber = baseAdaptiveNumber;
        upgradedAdaptiveNumber = true;
    }

    public enum Mode {
        ICE,
        WATER,
        STEAM,
        NONE
    }

    public static class AdaptiveVariable extends DynamicVariable {

        @Override
        public int baseValue(AbstractCard card) {
            if (card instanceof AbstractAdaptiveCard) {
                return ((AbstractAdaptiveCard)card).baseAdaptiveNumber;
            } else {
                return -1;
            }
        }

        @Override
        public boolean isModified(AbstractCard card) {
            if (card instanceof AbstractAdaptiveCard) {
                return ((AbstractAdaptiveCard)card).isAdaptiveNumberModified;
            } else {
                return false;
            }
        }

        @Override
        public void setIsModified(AbstractCard card, boolean v) {
            if (card instanceof AbstractAdaptiveCard) {
                ((AbstractAdaptiveCard)card).isAdaptiveNumberModified = v;
            }
        }

        @Override
        public String key() {
            return "hydrologistmod:A";
        }

        @Override
        public boolean upgraded(AbstractCard card) {
            if (card instanceof AbstractAdaptiveCard) {
                return ((AbstractAdaptiveCard)card).upgradedAdaptiveNumber;
            } else {
                return false;
            }
        }

        @Override
        public int value(AbstractCard card) {
            if (card instanceof AbstractAdaptiveCard) {
                return ((AbstractAdaptiveCard)card).adaptiveNumber;
            } else {
                return -1;
            }
        }
    }
}
