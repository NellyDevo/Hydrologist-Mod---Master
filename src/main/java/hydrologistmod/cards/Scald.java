package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.ThermalShockPower;

public class Scald extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Scald";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/Scald.png";
    private static final int COST = 1;
    private static final int DAMAGE = 12;
    private static final int UPGRADE_DAMAGE = 4;
    private static final int POWER_AMT = 2;
    private static final int UPGRADE_POWER_AMT = 1;

    public Scald() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = POWER_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        addToBot(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, magicNumber), magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Scald();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
            upgradeMagicNumber(UPGRADE_POWER_AMT);
        }
    }
}
