package hydrologistmod.cards;

import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.interfaces.HydrologistSubtypeAffectingPower;

public abstract class AbstractHydrologistCard extends CustomCard {

    public AbstractHydrologistCard(String id, String name, String img, int cost, String rawDescription,
                                   CardType type, CardColor color,
                                   CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
    }

    @Override
    public void applyPowers() {
        int damageHolder = baseDamage;
        int blockHolder = baseBlock;
        int magicHolder = baseMagicNumber;
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof HydrologistSubtypeAffectingPower) {
                ((HydrologistSubtypeAffectingPower)p).beforeApplyPowers(this);
            }
        }
        super.applyPowers();
        baseDamage = damageHolder;
        baseBlock = blockHolder;
        baseMagicNumber = magicHolder;
        isDamageModified = baseDamage != damage;
        isBlockModified = baseBlock != block;
        isMagicNumberModified = baseMagicNumber != magicNumber;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int damageHolder = baseDamage;
        int blockHolder = baseBlock;
        int magicHolder = baseMagicNumber;
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof HydrologistSubtypeAffectingPower) {
                ((HydrologistSubtypeAffectingPower)p).beforeApplyPowers(this);
            }
        }
        super.calculateCardDamage(mo);
        baseDamage = damageHolder;
        baseBlock = blockHolder;
        baseMagicNumber = magicHolder;
        isDamageModified = baseDamage != damage;
        isBlockModified = baseBlock != block;
        isMagicNumberModified = baseMagicNumber != magicNumber;
    }
}
