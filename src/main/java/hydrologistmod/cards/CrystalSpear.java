package hydrologistmod.cards;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.FreezeAction;
import hydrologistmod.cardmods.PurityModifier;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class CrystalSpear extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:CrystalSpear";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/CrystalSpear.png";
    private static final int COST = 1;
    private static final int DAMAGE = 8;
    private static final int SCALING_AMT = 2;
    private static final int UPGRADE_SCALING = 1;

    public CrystalSpear() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.ICE);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = SCALING_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new FreezeAction(m, new DamageInfo(p, damage, damageTypeForTurn)));
    }

    @Override
    public void applyPowers() {
        int i = baseDamage + (calculatePurity() * magicNumber);
        int tmp = baseDamage;
        baseDamage = i;
        super.applyPowers();
        baseDamage = tmp;
        isDamageModified = baseDamage != damage;
    }

    @Override
    public void calculateCardDamage(AbstractMonster m) {
        int i = baseDamage + (calculatePurity() * magicNumber);
        int tmp = baseDamage;
        baseDamage = i;
        super.calculateCardDamage(m);
        baseDamage = tmp;
        isDamageModified = baseDamage != damage;
    }

    private int calculatePurity() {
        int i = 0;
        AbstractPlayer p = AbstractDungeon.player;
        ArrayList<AbstractCard> cards = new ArrayList<>();
        cards.addAll(p.hand.group);
        cards.addAll(p.drawPile.group);
        cards.addAll(p.discardPile.group);
        for (AbstractCard card : cards) {
            if (CardModifierManager.hasModifier(card, PurityModifier.ID)) {
                i += ((PurityModifier)CardModifierManager.getModifiers(card, PurityModifier.ID).get(0)).amount;
            }
        }
        return i;
    }

    @Override
    public AbstractCard makeCopy() {
        return new CrystalSpear();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_SCALING);
        }
    }
}
