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
import hydrologistmod.HydrologistMod;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class GlacialStrike extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:GlacialStrike";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/GlacialStrike.png";
    private static final int COST = 2;
    private static final int DAMAGE_AMT = 10;
    private static final int UPGRADE_DAMAGE = 3;
    private static final int BLOCK_AMT = 8;
    private static final int UPGRADE_BLOCK = 2;

    public GlacialStrike() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.ICE);
        tags.add(CardTags.STRIKE);
        damage = baseDamage = DAMAGE_AMT;
        block = baseBlock = BLOCK_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        if (HydrologistMod.isHot(m)) {
            addToBot(new GainBlockAction(p, p, block));
        }
        if (HydrologistMod.isCool(m)) {
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new GlacialStrike();
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
