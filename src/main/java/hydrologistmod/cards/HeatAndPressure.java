package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.MultiplyPowerAction;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.HeatAndColdRelic;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.HeatPower;

import java.util.Arrays;
import java.util.LinkedList;

public class HeatAndPressure extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:HeatAndPressure";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/HeatAndPressure.png";
    private static final int COST = 1;
    private static final int MULTIPLIER = 2;
    private static final int UPGRADE_MULTIPLIER = 1;

    public HeatAndPressure(boolean makeSwappable) {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        magicNumber = baseMagicNumber = MULTIPLIER;
        tags.add(HydrologistTags.TEMPERATURE);
        exhaust = true;
        if (makeSwappable) {
            SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new Ripcurrent(false))));
        }
    }

    public HeatAndPressure() {
        this(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m.hasPower(HeatPower.POWER_ID)) {
            addToBot(new MultiplyPowerAction(m.getPower(HeatPower.POWER_ID), m, p, magicNumber));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new HeatAndPressure();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_MULTIPLIER);
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public boolean isPairCard() {
        return true;
    }
}
