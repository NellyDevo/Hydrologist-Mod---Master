package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.CardIgnore;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.FleetingFormPower;

@CardIgnore
public class FleetingForm extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:FleetingForm";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/FleetingForm.png";
    private static final int COST = 3;
    private static final int DAMAGE_AMT = 6;
    private static final int UPGRADE_DAMAGE = 3;
    FrigidForm nextCard;

    public FleetingForm() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.SPECIAL, CardTarget.NONE);
        tags.add(HydrologistTags.STEAM);
        magicNumber = baseMagicNumber = DAMAGE_AMT;
    }

    public FleetingForm(FlowingForm previousCard) {
        this();
        nextCard = new FrigidForm(previousCard);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new FleetingFormPower(p, magicNumber)));
    }

    @Override
    public AbstractCard makeCopy() {
        return new FleetingForm();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_DAMAGE);
        }
    }
}
