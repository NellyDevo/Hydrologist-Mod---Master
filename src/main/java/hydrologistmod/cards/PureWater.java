package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.cardmods.AbstractExtraEffectModifier;
import hydrologistmod.cardmods.AdaptiveEffect;
import hydrologistmod.cardmods.DrawCardEffect;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class PureWater extends AbstractAdaptiveCard implements TransmutableCard {
    public static final String ID = "hydrologistmod:PureWater";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/PureWater.png";
    private static final int COST = 0;
    private static final int DRAW_AMT = 1;
    private static final int ADAPTIVE_AMT = 1;
    private static final int UPGRADE_ADAPTIVE = 1;

    public PureWater() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.SELF, ADAPTIVE_AMT);
        assignHydrologistSubtype(HydrologistTags.WATER);
        magicNumber = baseMagicNumber = DRAW_AMT;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new DrawCardAction(p, magicNumber));
        super.use(p, m);
    }

    @Override
    public String getDescription() {
        return EXTENDED_DESCRIPTION[0];
    }

    @Override
    public AbstractCard makeCopy() {
        return new PureWater();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeAdaptiveNumber(UPGRADE_ADAPTIVE);
        }
    }

    @Override
    public ArrayList<AbstractExtraEffectModifier> getMutableAbilities() {
        ArrayList<AbstractExtraEffectModifier> list = new ArrayList<>();
        list.add(new DrawCardEffect(this, true, 1));
        list.add(new AdaptiveEffect(this, true, 1));
        return list;
    }
}
