package hydrologistmod.cards;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.TransmuteCardAction;
import hydrologistmod.cardmods.effects.AbstractExtraEffectModifier;
import hydrologistmod.cardmods.PurityModifier;
import hydrologistmod.cardmods.effects.TransmuteSelfEffect;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class MeltingIceberg extends AbstractHydrologistCard implements TransmutableCard {
    public static final String ID = "hydrologistmod:MeltingIceberg";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/melting_iceberg.png";
    private static final int COST = 1;
    private static final int UPGRADED_COST = 0;
    private static final int PURITY = 5;

    public MeltingIceberg() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.ICE);
        magicNumber = baseMagicNumber = PURITY;
        tags.add(CardTags.HEALING);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard card = this;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                CardModifierManager.addModifier(card, new PurityModifier(magicNumber));
                isDone = true;
            }
        });
        addToBot(new TransmuteCardAction(this));
    }

    @Override
    public AbstractCard makeCopy() {
        return new MeltingIceberg();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
        }
    }

    @Override
    public ArrayList<AbstractExtraEffectModifier> getMutableAbilities() {
        ArrayList<AbstractExtraEffectModifier> retVal = new ArrayList<>();
        retVal.add(new TransmuteSelfEffect(this, true));
        return retVal;
    }
}
