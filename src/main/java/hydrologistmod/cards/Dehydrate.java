package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.HydrologistMod;
import hydrologistmod.actions.ApplyTemperatureAction;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.HeatPower;

public class Dehydrate extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Dehydrate";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/dehydrate.png";
    private static final int COST = 1;
    private static final int DAMAGE_AMT = 7;
    private static final int HEAT_AMOUNT = 3;
    private static final int UPGRADE_HEAT_AMOUNT = 2;

    public Dehydrate() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        damage = baseDamage = DAMAGE_AMT;
        magicNumber = baseMagicNumber = HEAT_AMOUNT;
        tags.add(HydrologistTags.TEMPERATURE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        if (!HydrologistMod.isCool(m) || upgraded) {
            addToBot(new ApplyTemperatureAction(m, p, new HeatPower(m, p, magicNumber)));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Dehydrate();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_HEAT_AMOUNT);
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
