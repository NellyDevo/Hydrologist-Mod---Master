package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.FlowAction;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.Arrays;
import java.util.LinkedList;

public class HighTide extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:HighTide";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/HighTide.png";
    private static final int COST = 1;
    private static final int DAMAGE_AMT = 7;
    private static final int UPGRADE_DAMAGE = 3;

    public HighTide() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        damage = baseDamage = DAMAGE_AMT;
        SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new SeaFoam())));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        addToBot(new FlowAction());
    }

    @Override
    public AbstractCard makeCopy() {
        return new HighTide();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
        }
    }
}
