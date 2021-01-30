package hydrologistmod.cardmods;


import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static hydrologistmod.cards.HeatBlast.EXTENDED_DESCRIPTION;

public class HeatBlastModifier extends AbstractCardModifier {
    public int amount;
    public static final String ID = "hydrologistmod:HeatBlastModifier";

    public HeatBlastModifier(int amount) {
        this.amount = amount;
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage + amount;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + EXTENDED_DESCRIPTION[0] + amount + EXTENDED_DESCRIPTION[1];
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new HeatBlastModifier(amount);
    }
}