package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.cards.AbstractHydrologistCard;

@AbstractCardModifier.SaveIgnore
public class DamageEffect extends AbstractExtraEffectModifier {
    public DamageEffect(AbstractCard card, boolean isMutable) {
        super(card, VariableType.DAMAGE, isMutable);
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
        String s = " Deal " + key + " damage.";
        if (isMutable) {
            s = " hydrologistmod:Mutable:" + s;
        }
        return rawDescription + " NL " + s;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new DamageEffect(attachedCard, isMutable);
    }
}
