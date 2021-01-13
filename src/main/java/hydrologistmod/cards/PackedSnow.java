package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.PackedIcePower;
import hydrologistmod.powers.PackedSteamPower;
import hydrologistmod.powers.PackedWaterPower;

public class PackedSnow extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:PackedSnow";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/PackedSnow.png";
    private static final int COST = 3;
    private static final int UPGRADED_COST = 2;

    public PackedSnow() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.SELF);
        assignHydrologistSubtype(HydrologistTags.ICE);
        tags.add(HydrologistTags.CARES_ABOUT_SUBTYPES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new PackedIcePower(p, 1), 1, true));
        addToBot(new ApplyPowerAction(p, p, new PackedWaterPower(p, 1), 1, true));
        addToBot(new ApplyPowerAction(p, p, new PackedSteamPower(p, 1), 1, true));
    }

    @Override
    public AbstractCard makeCopy() {
        return new PackedSnow();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
        }
    }
}
