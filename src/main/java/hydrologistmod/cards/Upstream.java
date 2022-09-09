package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.ApplyTemperatureAction;
import hydrologistmod.actions.UpstreamAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.ColdPower;

public class Upstream extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Upstream";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/upstream.png";
    private static final int COST = 1;
    private static final int WEAK_AMT = 3;
    private static final int UPGRADE_WEAK = 2;
    private static final int BLOCK_AMT = 5;
    private static final int UPGRADE_BLOCK = 3;

    public Upstream() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        magicNumber = baseMagicNumber = WEAK_AMT;
        block = baseBlock = BLOCK_AMT;
        tags.add(HydrologistTags.TEMPERATURE);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyTemperatureAction(m, p, magicNumber, true));
        addToBot(new UpstreamAction(m, block));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Upstream();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_WEAK);
            upgradeBlock(UPGRADE_BLOCK);
        }
    }
}
