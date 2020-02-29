package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.PressureBlastAction;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.EncapsulatingIcePower;

import java.util.Arrays;
import java.util.LinkedList;

public class PressureBlast extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:PressureBlast";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/PressureBlast.png";
    private static final int COST = -1;
    private static final int DAMAGE_AMT = 6;
    private static final int UPGRADE_DAMAGE = 3;

    public PressureBlast() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        damage = baseDamage = DAMAGE_AMT;
        magicNumber = baseMagicNumber = 0;
        SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new EncapsulatingIce())));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new PressureBlastAction(p, m, damage, damageTypeForTurn, freeToPlayOnce, energyOnUse + magicNumber));
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasPower(EncapsulatingIcePower.POWER_ID)) {
            magicNumber = baseMagicNumber + p.getPower(EncapsulatingIcePower.POWER_ID).amount;
            isMagicNumberModified = baseMagicNumber != magicNumber;
        }
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        AbstractPlayer p = AbstractDungeon.player;
        if (p.hasPower(EncapsulatingIcePower.POWER_ID)) {
            magicNumber = baseMagicNumber + p.getPower(EncapsulatingIcePower.POWER_ID).amount;
            isMagicNumberModified = baseMagicNumber != magicNumber;
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new PressureBlast();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
        }
    }
}
