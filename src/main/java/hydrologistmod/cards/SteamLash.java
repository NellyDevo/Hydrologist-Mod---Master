package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.ApplyTemperatureAction;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.HeatPower;

import java.util.Arrays;
import java.util.LinkedList;

public class SteamLash extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:SteamLash";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/steam_lash.png";
    private static final int COST = 1;
    public static final int ATTACK_DMG = 8;
    private static final int ENERGY_GAIN_ON_SWAP = 1;
    private static final int UPGRADE_PLUS_DMG = 4;
    private static final int HEAT_TO_APPLY = 4;
    private static final int UPGRADE_HEAT = 1;

    public SteamLash(boolean makeSwappable) {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.BASIC, AbstractCard.CardTarget.ENEMY);
        damage = baseDamage = ATTACK_DMG;
        magicNumber = baseMagicNumber = HEAT_TO_APPLY;
        assignHydrologistSubtype(HydrologistTags.STEAM);
        tags.add(HydrologistTags.TEMPERATURE);
        if (makeSwappable) {
            SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new FrostWhip(false))));
        }
    }

    public SteamLash() {
        this(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        addToBot(new ApplyTemperatureAction(m, p, magicNumber, true));
    }

    @Override
    public AbstractCard makeCopy() {
        return new SteamLash();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_PLUS_DMG);
            upgradeMagicNumber(UPGRADE_HEAT);
        }
    }

    @Override
    public void onSwapOut() {
        addToBot(new GainEnergyAction(ENERGY_GAIN_ON_SWAP));
    }

    @Override
    public boolean isPairCard() {
        return true;
    }
}
