package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.DiscardSpecificCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.actions.SwapWhenPlayedAction;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.Arrays;
import java.util.LinkedList;

public class Eruption extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:Eruption";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/eruption.png";
    private static final int COST = 1;
    private static final int DAMAGE_AMT = 20;
    private static final int UPGRADE_DAMAGE = 5;
    private static final int ENERGY_GAIN = 3;

    public Eruption(boolean makeSwappable) {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        damage = baseDamage = DAMAGE_AMT;
        magicNumber = baseMagicNumber = ENERGY_GAIN;
        if (makeSwappable) {
            SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new Reservoir(false))));
        }
    }

    public Eruption() {
        this(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        addToBot(new GainEnergyAction(magicNumber));
        addToBot(new SwapWhenPlayedAction(this));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Eruption();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
        }
    }

    @Override
    public void onSwapIn() {
        addToBot(new DiscardSpecificCardAction(this));
    }

    @Override
    public boolean isPairCard() {
        return true;
    }
}
