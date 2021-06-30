package hydrologistmod.cards;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.cardmods.TempBlockModifier;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.Arrays;
import java.util.LinkedList;

public class FrigidLash extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:FrigidLash";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/FrigidLash.png";
    private static final int COST = 1;
    private static final int DAMAGE_AMT = 8;
    private static final int UPGRADE_DAMAGE = 3;
    private static final int INCREASE_BLOCK = 3;
    private static final int UPGRADE_INCREASE = 2;

    public FrigidLash(boolean makeSwappable) {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        damage = baseDamage = DAMAGE_AMT;
        magicNumber = baseMagicNumber = INCREASE_BLOCK;
        if (makeSwappable) {
            SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new LayeredShell(false))));
        }
    }

    public FrigidLash() {
        this(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        AbstractCard pairCard = SwapperHelper.getNextCard(this);
        if (pairCard != null) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    CardModifierManager.addModifier(pairCard, new TempBlockModifier(magicNumber));
                    isDone = true;
                }
            });
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new FrigidLash();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_INCREASE);
            upgradeDamage(UPGRADE_DAMAGE);
        }
    }

    @Override
    public boolean isPairCard() {
        return true;
    }
}
