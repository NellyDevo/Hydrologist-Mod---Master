package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.cards.AbstractHydrologistCard;

public class PurityModifier extends AbstractCardModifier {
    public static final String ID = "hydrologistmod:PurityModifier";
    public int amount;

    public PurityModifier(int amount) {
        this.amount = amount;
    }

    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster monster) {
        return damage + amount;
    }

    public float modifyBlock(float block, AbstractCard card) {
        return block + amount;
    }

    public String identifier(AbstractCard card) {
        return ID;
    }

    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + " NL " + AbstractHydrologistCard.purity + " " + amount;
    }

    public boolean shouldApply(AbstractCard card) {
        if (CardModifierManager.hasModifier(card, ID)) {
            PurityModifier mod = (PurityModifier)CardModifierManager.getModifiers(card, ID).get(0);
            mod.amount += amount;
            card.initializeDescription();
            return false;
        }
        return true;
    }

    public AbstractCardModifier makeCopy() {
        return new PurityModifier(amount);
    }
}