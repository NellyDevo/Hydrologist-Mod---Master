package hydrologistmod.cards;

import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.FleetingField;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.CardIgnore;
import hydrologistmod.actions.IncreasePairedMiscAction;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

@CardIgnore
public class SoothingWater extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:SoothingWater";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/SoothingWater.png";
    private static final int COST = 1;
    private static final int BASE_HEAL = 6;
    private static final int HEAL_INCREASE = 4;
    private static final int UPGRADED_HEAL_INCREASE = 6;

    public SoothingWater() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.SELF);
        magicNumber = baseMagicNumber = BASE_HEAL + misc;
        assignHydrologistSubtype(HydrologistTags.WATER);
        tags.add(CardTags.HEALING);
        FleetingField.fleeting.set(this, true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HealAction(p, p, magicNumber));
        AbstractCard pair = SwapperHelper.getNextCard(this);
        if (StSLib.getMasterDeckEquivalent(pair) != null) {
            p.masterDeck.removeCard(StSLib.getMasterDeckEquivalent(pair));
        }
    }

    @Override
    public void applyPowers() {
        magicNumber = baseMagicNumber = BASE_HEAL + misc;
    }

    @Override
    public AbstractCard makeCopy() {
        return new SoothingWater();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
        }
    }

    @Override
    public void onSwapOut() {
        addToBot(new IncreasePairedMiscAction(SwapperHelper.getNextCard(this).uuid, upgraded ? UPGRADED_HEAL_INCREASE : HEAL_INCREASE));
    }
}
