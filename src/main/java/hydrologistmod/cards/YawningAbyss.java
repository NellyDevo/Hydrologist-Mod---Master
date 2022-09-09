package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.YawningAbyssAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class YawningAbyss extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:YawningAbyss";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/yawning_abyss.png";
    private static final int COST = 2;
    private static final int DAMAGE_AMT = 5;
    private static final int UPGRADE_DAMAGE = 2;

    public YawningAbyss() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        damage = baseDamage = DAMAGE_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new YawningAbyssAction(m, new DamageInfo(p, damage, damageTypeForTurn), m.hb.cX, m.hb.cY - (m.hb_h / 2.0f), 1.0f));
    }

    @Override
    public AbstractCard makeCopy() {
        return new YawningAbyss();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
        }
    }
}
