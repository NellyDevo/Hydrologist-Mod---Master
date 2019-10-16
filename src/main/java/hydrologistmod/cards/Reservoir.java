package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.Arrays;
import java.util.LinkedList;

public class Reservoir extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:Reservoir";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/Reservoir.png";
    private static final int COST = 1;
    private static final int UPGRADED_COST = 0;
    private static final int ENERGY_LOSS = 2;
    private static final int UPGRADE_ENERGY_LOSS = -1;
    private static String cantSwapMessage = EXTENDED_DESCRIPTION[0];

    public Reservoir() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.WATER);
        isInnate = true;
        isEthereal = true;
        magicNumber = baseMagicNumber = ENERGY_LOSS;
        SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new Eruption())));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public AbstractCard makeCopy() {
        return new Reservoir();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
            upgradeMagicNumber(UPGRADE_ENERGY_LOSS);
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public void onSwapOut() {
        addToBot(new LoseEnergyAction(magicNumber));
    }

    @Override
    public boolean canSwap() {
        return EnergyPanel.totalCount >= magicNumber;
    }

    @Override
    public String getUnableToSwapString() {
        return cantSwapMessage;
    }
}
