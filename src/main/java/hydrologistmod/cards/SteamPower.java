package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.SteamPowerIcePower;
import hydrologistmod.powers.SteamPowerSteamPower;
import hydrologistmod.powers.SteamPowerWaterPower;
import hydrologistmod.powers.ThermalShockPower;

public class SteamPower extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:SteamPower";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/SteamPower.png";
    private static final int COST = 1;
    private static final int UPGRADE_COST = 0;

    public SteamPower() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        if (m.hasPower(ThermalShockPower.POWER_ID)) {
            AbstractPower power = m.getPower(ThermalShockPower.POWER_ID);
            addToBot(new DrawCardAction(p, power.amount));
            addToBot(new RemoveSpecificPowerAction(m, p, power));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new SteamPower();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADE_COST);
        }
    }
}
