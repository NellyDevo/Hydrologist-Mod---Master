package hydrologistmod.cards;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class HeatBlast extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:HeatBlast";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/HeatBlast.png";
    private static final int COST = 2;
    private static final int DAMAGE_AMT = 10;
    private static final int UPGRADE_DAMAGE = 3;
    private static final int VULN_AMT = 2;
    private static final int UPGRADE_VULN = 1;

    public HeatBlast() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        damage = baseDamage = DAMAGE_AMT;
        magicNumber = baseMagicNumber = VULN_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, magicNumber, false), magicNumber));
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) { //TODO calculate this properly I guess
        super.calculateCardDamage(mo);
        if (mo.hasPower(VulnerablePower.POWER_ID)) {
            float tmp = damage;
            VulnerablePower power = (VulnerablePower)mo.getPower(VulnerablePower.POWER_ID);
            for (int i = 1; i < power.amount; ++i) {
                tmp = power.atDamageReceive(tmp, damageTypeForTurn);
            }
            damage = MathUtils.floor(tmp);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new HeatBlast();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
            upgradeMagicNumber(UPGRADE_VULN);
        }
    }
}
