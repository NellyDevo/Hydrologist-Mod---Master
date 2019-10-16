package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.unique.LoseEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.Arrays;
import java.util.LinkedList;

public class WaterWhip extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:WaterWhip";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/WaterWhip.png";
    private static final int COST = 1;
    public static final int ATTACK_DMG = 7;
    private static final int UPGRADE_PLUS_DMG = 3;
    private static final int ENERGY_LOSS_ON_SWAP = 1;
    private String cantSwapMessage = EXTENDED_DESCRIPTION[0];

    public WaterWhip() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                AbstractCard.CardRarity.BASIC, AbstractCard.CardTarget.ENEMY);
        damage = baseDamage = ATTACK_DMG;
        magicNumber = baseMagicNumber = ENERGY_LOSS_ON_SWAP;
        assignHydrologistSubtype(HydrologistTags.WATER);
        SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new SteamLash())));
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_DIAGONAL));
    }

    @Override
    public AbstractCard makeCopy() {
        return new WaterWhip();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_PLUS_DMG);
        }
    }

    @Override
    public boolean canSwap() {
        return EnergyPanel.totalCount >= magicNumber;
    }

    @Override
    public String getUnableToSwapString() {
        return cantSwapMessage;
    }

    @Override
    public void onSwapOut() {
        addToBot(new LoseEnergyAction(magicNumber));
    }
}
