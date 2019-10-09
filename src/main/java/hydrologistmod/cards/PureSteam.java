package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class PureSteam extends AbstractHydrologistCard implements TransmutableCard {
    public static final String ID = "hydrologistmod:PureSteam";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/PureSteam.png";
    private static final int COST = 0;
    private static final int DAMAGE_AMT = 4;
    private static final int UPGRADE_DAMAGE = 2;

    public PureSteam() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.ENEMY);
        tags.add(HydrologistTags.STEAM);
        damage = baseDamage = DAMAGE_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
    }

    @Override
    public AbstractCard makeCopy() {
        return new PureSteam();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
        }
    }

    @Override
    public void onTransmuted(AbstractCard newCard) {
        AbstractMonster m = AbstractDungeon.getMonsters().getRandomMonster(true);
        AbstractPlayer p = AbstractDungeon.player;
        if (m != null) {
            calculateCardDamage(m);
            addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
        }
    }
}
