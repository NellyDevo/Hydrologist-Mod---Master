package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.actions.TransmuteCardAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class SoggySynthesis extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:SoggySynthesis";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/SoggySynthesis.png";
    private static final int COST = 1;
    private static final int DAMAGE_AMT = 6;
    private static final int BLOCK_AMT = 6;
    private static final int UPGRADE_DAMAGE = 3;
    private static final int UPGRADE_BLOCK = 3;

    public SoggySynthesis() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        damage = baseDamage = DAMAGE_AMT;
        block = baseBlock = BLOCK_AMT;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        addToBot(new TransmuteCardAction((AbstractCard potentialCard) -> potentialCard.hasTag(HydrologistTags.WATER)));
    }

    @Override
    public AbstractCard makeCopy() {
        return new SoggySynthesis();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
            upgradeBlock(UPGRADE_BLOCK);
        }
    }
}
