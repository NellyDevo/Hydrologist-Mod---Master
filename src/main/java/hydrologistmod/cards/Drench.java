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
import hydrologistmod.HydrologistMod;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.ColdPower;

public class Drench extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Drench";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/Drench.png";
    private static final int COST = 1;
    private static final int DAMAGE = 7;
    private static final int UPGRADE_DAMAGE = 3;
    private static final int COOL_AMOUNT = 3;
    private static final int UPGRADE_COOL_AMOUNT = 2;

    public Drench() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        tags.add(HydrologistTags.TEMPERATURE);
        damage = baseDamage = DAMAGE;
        magicNumber = baseMagicNumber = COOL_AMOUNT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        if (!HydrologistMod.isHot(m)) {
            addToBot(new ApplyPowerAction(m, p, new ColdPower(m, p, magicNumber), magicNumber));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Drench();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
            upgradeMagicNumber(UPGRADE_COOL_AMOUNT);
        }
    }
}
