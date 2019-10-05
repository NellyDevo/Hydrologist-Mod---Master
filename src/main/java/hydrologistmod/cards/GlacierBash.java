package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.CardIgnore;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

@CardIgnore
public class GlacierBash extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:GlacierBash";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/GlacierBash.png";
    private static final int COST = 4;
    private static final int UPGRADED_COST = 3;
    private static final int DAMAGE_AMT = 25;
    private static final int UPGRADE_DAMAGE = 7;
    private static final int COST_REDUCE = 1;

    public GlacierBash() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.SPECIAL, CardTarget.ENEMY);
        tags.add(HydrologistTags.ICE);
        damage = baseDamage = DAMAGE_AMT;
        magicNumber = baseMagicNumber = COST_REDUCE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
    }

    @Override
    public AbstractCard makeCopy() {
        return new GlacierBash();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
            upgradeDamage(UPGRADE_DAMAGE);
        }
    }

    @Override
    public void onSwapOut() {
        modifyCostForCombat(-magicNumber);
    }
}