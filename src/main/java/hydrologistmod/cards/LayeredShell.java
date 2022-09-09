package hydrologistmod.cards;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.cardmods.TempDamageModifier;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.Arrays;
import java.util.LinkedList;

public class LayeredShell extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:LayeredShell";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/layered_shell.png";
    private static final int COST = 1;
    private static final int BLOCK_AMT = 7;
    private static final int UPGRADE_BLOCK = 2;
    private static final int DAMAGE_INCREASE = 4;
    private static final int UPGRADE_DAMAGE_INC = 3;

    public LayeredShell(boolean makeSwappable) {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.SELF);
        assignHydrologistSubtype(HydrologistTags.ICE);
        block = baseBlock = BLOCK_AMT;
        magicNumber = baseMagicNumber = DAMAGE_INCREASE;
        if (makeSwappable) {
            SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new FrigidLash(false))));
        }
    }

    public LayeredShell() {
        this(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, p, block));
        AbstractCard pairCard = SwapperHelper.getNextCard(this);
        if (pairCard != null) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    CardModifierManager.addModifier(pairCard, new TempDamageModifier(magicNumber));
                    isDone = true;
                }
            });
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new LayeredShell();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeBlock(UPGRADE_BLOCK);
            upgradeMagicNumber(UPGRADE_DAMAGE_INC);
        }
    }
}
