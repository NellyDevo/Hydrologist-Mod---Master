package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.EvaporationPower;

public class Evaporation extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Evaporation";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/Evaporation.png";
    private static final int COST = 0;
    private static final int BLOCK_AMT = 2;
    private static final int UPGRADE_BLOCK = 2;
    private static final int VULNERABLE_AMT = 2;
    private static final int UPGRADE_VULNERABLE_AMT = 1;

    public Evaporation() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.SELF);
        assignHydrologistSubtype(HydrologistTags.WATER);
        block = baseBlock = BLOCK_AMT;
        magicNumber = baseMagicNumber = VULNERABLE_AMT;
        tags.add(HydrologistTags.TEMPERATURE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new ApplyPowerAction(p, p, new EvaporationPower(p, magicNumber)));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Evaporation();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(UPGRADE_BLOCK);
            upgradeMagicNumber(UPGRADE_VULNERABLE_AMT);
        }
    }
}
