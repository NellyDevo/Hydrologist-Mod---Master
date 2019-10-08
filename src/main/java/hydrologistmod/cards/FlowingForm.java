package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import helpers.SwapperHelper;
import hydrologistmod.CardIgnore;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.FlowingFormPower;

import java.util.ArrayList;

public class FlowingForm extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:FlowingForm";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/FlowingForm.png";
    private static final int COST = 3;
    private static final int CARD_DRAW = 1;
    private static final int UPGRADE_CARD_DRAW = 1;

    public FlowingForm() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.POWER, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.NONE);
        tags.add(HydrologistTags.WATER);
        magicNumber = baseMagicNumber = CARD_DRAW;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new ApplyPowerAction(p, p, new FlowingFormPower(p, magicNumber)));
    }

    @Override
    public AbstractCard makeCopy() {
        return new FlowingForm();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_CARD_DRAW);
        }
    }

    @Override
    public boolean isChainSwapper() {
        return true;
    }

    @Override
    public ArrayList<AbstractCard> createChain() {
        ArrayList<AbstractCard> result = new ArrayList<>();
        result.add(new FleetingForm());
        result.add(new FrigidForm());
        return result;
    }
}
