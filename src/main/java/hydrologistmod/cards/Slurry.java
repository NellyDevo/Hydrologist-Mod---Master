package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.AbstractHeatAndColdPower;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;

public class Slurry extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Slurry";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/slurry.png";
    private static final int COST = 2;

    public Slurry() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractHeatAndColdPower power = null;
                if (m.hasPower(HeatPower.POWER_ID)) {
                    AbstractPower pow = m.getPower(HeatPower.POWER_ID);
                    power = (AbstractHeatAndColdPower)pow;
                }
                if (m.hasPower(ColdPower.POWER_ID)) {
                    AbstractPower pow = m.getPower(ColdPower.POWER_ID);
                    power = (AbstractHeatAndColdPower)pow;
                }
                if (power != null) {
                    power.dealDamage();
                }
                isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new Slurry();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
            exhaust = false;
        }
    }
}
