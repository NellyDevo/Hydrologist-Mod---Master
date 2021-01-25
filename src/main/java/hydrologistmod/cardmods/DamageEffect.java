package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.cards.AbstractHydrologistCard;

import java.util.ArrayList;

@AbstractCardModifier.SaveIgnore
public class DamageEffect extends AbstractExtraEffectModifier {
    private AbstractCard.CardTarget oldTarget = null;
    private static final String ID = "hydrologistmod:DamageEffect";

    public DamageEffect(AbstractCard card, boolean isMutable, int times) {
        super(card, VariableType.DAMAGE, isMutable, times);
        priority = 2;
    }

    @Override
    public void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m) {
        if (attachedCard instanceof AbstractHydrologistCard) {
            AbstractHydrologistCard elementalCard = (AbstractHydrologistCard)attachedCard;
            addToBot(new HydrologistDamageAction(elementalCard.getHydrologistSubtype(), m, new DamageInfo(p, value, DamageInfo.DamageType.NORMAL)));
        } else {
            addToBot(new DamageAction(m, new DamageInfo(p, value, DamageInfo.DamageType.NORMAL), AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public String addExtraText(String rawDescription, AbstractCard card) {
        String s = " Deal " + key;
        if (amount == 1) {
            s +=  " damage.";
        } else {
            s +=  " damage " + amount + " times.";
        }
        if (isMutable) {
            s = " hydrologistmod:Mutable:" + s;
        }
        return rawDescription + " NL " + s;
    }

    @Override
    public boolean shouldRenderValue() {
        return true;
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        super.onInitialApplication(card);
        if (card.target != AbstractCard.CardTarget.ENEMY && card.target != AbstractCard.CardTarget.SELF_AND_ENEMY) {
            oldTarget = card.target;
            if (card.target == AbstractCard.CardTarget.SELF) {
                card.target = AbstractCard.CardTarget.SELF_AND_ENEMY;
            } else {
                card.target = AbstractCard.CardTarget.ENEMY;
            }
        }
    }

    @Override
    public void onRemove(AbstractCard card) {
        if (oldTarget != null) {
            card.target = oldTarget;
        }
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, ID)) {
            ArrayList<AbstractCardModifier> list = CardModifierManager.getModifiers(card, ID);
            for (AbstractCardModifier mod : list) {
                AbstractCard c = ((AbstractExtraEffectModifier)mod).attachedCard;
                if (c.baseDamage == attachedCard.baseDamage) {
                    ((AbstractExtraEffectModifier)mod).amount++;
                    card.applyPowers();
                    card.initializeDescription();
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
    public AbstractCardModifier makeCopy() {
        return new DamageEffect(attachedCard, isMutable, amount);
    }
}
