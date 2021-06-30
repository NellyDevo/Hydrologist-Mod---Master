package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.EncapsulatingIceAction;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.Arrays;
import java.util.LinkedList;

public class EncapsulatingIce extends AbstractHydrologistCard implements SwappableCard {
    public static final String ID = "hydrologistmod:EncapsulatingIce";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/EncapsulatingIce.png";
    private static final int COST = -1;

    public EncapsulatingIce(boolean makeSwappable) {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.SELF);
        assignHydrologistSubtype(HydrologistTags.ICE);
        if (makeSwappable) {
            SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new PressureBlast(false))));
        }
    }

    public EncapsulatingIce() {
        this(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new EncapsulatingIceAction(energyOnUse, upgraded, freeToPlayOnce));
        AbstractCard card = this;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                AbstractCard c = SwapperHelper.getNextCard(card);
                if (c != null) {
                    c.applyPowers();
                }
                isDone = true;
            }
        });
    }

    @Override
    public AbstractCard makeCopy() {
        return new EncapsulatingIce();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public boolean isPairCard() {
        return true;
    }
}
