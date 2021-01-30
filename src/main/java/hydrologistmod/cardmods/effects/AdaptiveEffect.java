package hydrologistmod.cardmods.effects;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.ApplyTemperatureAction;
import hydrologistmod.cards.AbstractAdaptiveCard;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;

import java.util.ArrayList;

@AbstractCardModifier.SaveIgnore
public class AdaptiveEffect extends AbstractExtraEffectModifier {
    private static final String ID = "hydrologistmod:AdaptiveEffect";
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    private static final String[] TEXT = cardStrings.EXTENDED_DESCRIPTION;
    private AbstractAdaptiveCard card;
    private AbstractCard.CardTarget defaultTarget = null;

    public AdaptiveEffect(AbstractCard c, boolean isMutable, int times) {
        super(c, null, isMutable, times);
        priority = 1;
        if (c instanceof AbstractAdaptiveCard) {
            card = (AbstractAdaptiveCard)c;
        } else {
            card = null;
            System.out.println("ERROR: sent a non-adaptive card to AdaptiveEffect");
        }
        setValues();
    }

    @Override
    protected void setValues() {
        if (card != null) {
            value = card.adaptiveNumber;
            baseValue = card.baseAdaptiveNumber;
            isValueModified = card.isAdaptiveNumberModified;
        }
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        super.onApplyPowers(card);
        card.initializeDescription();
        setTarget(card);
    }

    @Override
    public void onCalculateAttachedCardDamage(AbstractCard card, AbstractMonster mo) {
        super.onCalculateAttachedCardDamage(card, mo);
        card.initializeDescription();
        setTarget(card);
    }

    private void setTarget(AbstractCard c) {
        switch(card.mode) {
            case ICE:
            case STEAM:
                if (card.target == AbstractCard.CardTarget.SELF_AND_ENEMY) {
                    c.target = AbstractCard.CardTarget.SELF_AND_ENEMY;
                } else {
                    c.target = AbstractCard.CardTarget.ENEMY;
                }
                break;
            default:
                c.target = defaultTarget;
                break;
        }
    }

    @Override
    public void doExtraEffects(AbstractCard c, AbstractPlayer p, AbstractCreature m) {
        for (int i = 0; i < amount; ++i) {
            switch (card.mode) {
                case ICE:
                    addToBot(new ApplyTemperatureAction(m, p, new ColdPower(m, p, value)));
                    break;
                case WATER:
                    addToBot(new GainBlockAction(p, p, value));
                    break;
                case STEAM:
                    addToBot(new ApplyTemperatureAction(m, p, new HeatPower(m, p, value)));
                    break;
                case NONE:
                    break;
            }
        }
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (this.card == null) {
            return false;
        }
        if (CardModifierManager.hasModifier(card, ID)) {
            ArrayList<AbstractCardModifier> list = CardModifierManager.getModifiers(card, ID);
            for (AbstractCardModifier mod : list) {
                AbstractAdaptiveCard c = ((AdaptiveEffect)mod).card;
                if (c.baseAdaptiveNumber == this.card.baseAdaptiveNumber) {
                    ((AdaptiveEffect)mod).amount++;
                    card.applyPowers();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public boolean shouldRenderValue() {
        return true;
    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard c) {
        String s;
        if (card != null) {
            switch (card.mode) {
                case ICE:
                    s =  TEXT[1] + key + TEXT[3];
                    break;
                case WATER:
                    s =  TEXT[2] + key + TEXT[4];
                    break;
                case STEAM:
                    s = TEXT[1] + key + TEXT[5];
                    break;
                default:
                    s = TEXT[0] + key + AbstractExtraEffectModifier.TEXT[3];
                    break;
            }
        } else {
            s =  TEXT[0] + key + AbstractExtraEffectModifier.TEXT[3];
        }
        if (!(card == null || card.mode == AbstractAdaptiveCard.Mode.NONE)) {
            s = applyTimes(s);
        }
        s = applyMutable(s);
        return rawDescription + " NL " + s;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        defaultTarget = card.target;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AdaptiveEffect(attachedCard, isMutable, amount);
    }
}
