package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.HeatPower;

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
        tags.add(HydrologistTags.TEMPERATURE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        addToBot(new ApplyPowerAction(m, p, new HeatPower(m, p, magicNumber), magicNumber));
    }

    /*@Override
    public void calculateCardDamage(AbstractMonster mo) {
        refer to HeatBlastApplyVulnerablePatch for accurate calculations
    }*/

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
