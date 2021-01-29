package hydrologistmod.cards;

import basemod.abstracts.AbstractCardModifier;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class LaminarFlow extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:LaminarFlow";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/LaminarFlow.png";
    private static final int COST = 1;
    private static final int POWER_AMT = 3;
    private static final int UPGRADE_AMT = 1;

    public LaminarFlow() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.WATER);
        magicNumber = baseMagicNumber = POWER_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new MetallicizePower(p, magicNumber), magicNumber));
    }

    @Override
    public AbstractCard makeCopy() {
        return new LaminarFlow();
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        calculateMagicNumber();
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        super.calculateCardDamage(mo);
        calculateMagicNumber();
    }

    private void calculateMagicNumber() {
        int tmp = baseMagicNumber;
        for (AbstractCardModifier mod : CardModifierPatches.CardModifierFields.cardModifiers.get(this)) {
            if (mod instanceof CrystalIce.LaminarFlowModifier) {
                tmp += ((CrystalIce.LaminarFlowModifier)mod).increase;
            }
        }
        magicNumber = tmp;
        isMagicNumberModified = magicNumber != baseMagicNumber;
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_AMT);
        }
    }

    @Override
    public boolean isPairCard() {
        return true;
    }
}
