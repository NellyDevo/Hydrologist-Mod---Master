package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.TransmuteCardAction;
import hydrologistmod.cardmods.effects.AbstractExtraEffectModifier;
import hydrologistmod.cardmods.effects.GainBlockEffect;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class ViscousShell extends AbstractHydrologistCard implements TransmutableCard {
    public static final String ID = "hydrologistmod:ViscousShell";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/viscous_shell.png";
    private static final int COST = 1;
    public static final int BLOCK_AMT = 5;
    private static final int UPGRADE_BLOCK_AMT = 3;

    public ViscousShell() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                AbstractCard.CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                AbstractCard.CardRarity.COMMON, AbstractCard.CardTarget.SELF);
        block = baseBlock = BLOCK_AMT;
        magicNumber = baseMagicNumber = BLOCK_AMT;
        assignHydrologistSubtype(HydrologistTags.WATER);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        addToBot(new TransmuteCardAction(this));
    }

    @Override
    public AbstractCard makeCopy() {
        return new ViscousShell();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(UPGRADE_BLOCK_AMT);
            upgradeMagicNumber(UPGRADE_BLOCK_AMT);
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public ArrayList<AbstractExtraEffectModifier> getMutableAbilities() {
        ArrayList<AbstractExtraEffectModifier> list = new ArrayList<>();
        list.add(new GainBlockEffect(this, true, 1));
        return list;
    }
}
