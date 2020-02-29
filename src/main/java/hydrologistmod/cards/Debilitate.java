package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;

public class Debilitate extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Debilitate";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/Debilitate.png";
    private static final int COST = 2;
    private static final int AMOUNT_TO_APPLY = 7;
    private static final int UPGRADE_AMOUNT = 2;

    public Debilitate() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.ALL_ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        magicNumber = baseMagicNumber = AMOUNT_TO_APPLY;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        for (final AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (mo.hasPower(ColdPower.POWER_ID)) {
                addToBot(new ApplyPowerAction(mo, p, new ColdPower(mo, p, magicNumber), magicNumber, true));
                addToBot(new ApplyPowerAction(mo, p, new HeatPower(mo, p, magicNumber), magicNumber, true));
            } else {
                addToBot(new ApplyPowerAction(mo, p, new HeatPower(mo, p, magicNumber), magicNumber, true));
                addToBot(new ApplyPowerAction(mo, p, new ColdPower(mo, p, magicNumber), magicNumber, true));
            }
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Debilitate();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_AMOUNT);
        }
    }
}
