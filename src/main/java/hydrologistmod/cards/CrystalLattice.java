package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
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

public class CrystalLattice extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:CrystalLattice";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/crystal_lattice.png";
    private static final int COST = 0;
    private static final int DAMAGE_AMT = 5;
    private static final int UPGRADE_DAMAGE = 3;
    private static final int EXTRA_COPIES = 2;
    private static final int UPGRADE_EXTRA_COPIES = 1;

    public CrystalLattice() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.ICE);
        damage = baseDamage = DAMAGE_AMT;
        exhaust = true;
        tags.add(CardTags.HEALING);
        magicNumber = baseMagicNumber = EXTRA_COPIES;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        addToBot(new TransmuteCardAction((oldCard, newCard, firstTime) -> {
            if (firstTime) {
                for (int i = 0; i < magicNumber; ++i) {
                    addToTop(new MakeTempCardInHandAction(newCard));
                }
            }
        }));
    }

    @Override
    public AbstractCard makeCopy() {
        return new CrystalLattice();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
            upgradeMagicNumber(UPGRADE_EXTRA_COPIES);
        }
    }
}
