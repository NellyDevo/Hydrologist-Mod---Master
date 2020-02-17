package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.SteamPowerIcePower;
import hydrologistmod.powers.SteamPowerSteamPower;
import hydrologistmod.powers.SteamPowerWaterPower;

public class SteamPower extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:SteamPower";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/SteamPower.png";
    private static final int COST = 2;
    private static final int CARD_DRAW = 1;
    private static final int UPGRADE_COST = 1;

    public SteamPower() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        magicNumber = baseMagicNumber = CARD_DRAW;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawCardAction(magicNumber, new AbstractGameAction() {
            @Override
            public void update() {
                for (AbstractCard c : DrawCardAction.drawnCards) {
                    if (c.hasTag(HydrologistTags.ICE)) {
                        addToBot(new ApplyPowerAction(p, p, new SteamPowerIcePower(p, magicNumber)));
                    }
                    if (c.hasTag(HydrologistTags.WATER)) {
                        addToBot(new ApplyPowerAction(p, p, new SteamPowerWaterPower(p, magicNumber)));
                    }
                    if (c.hasTag(HydrologistTags.STEAM)) {
                        addToBot(new ApplyPowerAction(p, p, new SteamPowerSteamPower(p, magicNumber)));
                    }
                }
                isDone = true;
            }
        }));
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
