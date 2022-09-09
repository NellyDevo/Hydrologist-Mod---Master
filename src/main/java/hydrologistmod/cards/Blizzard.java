package hydrologistmod.cards;

import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.cardmods.effects.AbstractExtraEffectModifier;
import hydrologistmod.cardmods.effects.AdaptiveEffect;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class Blizzard extends AbstractAdaptiveCard implements TransmutableCard {
    public static final String ID = "hydrologistmod:Blizzard";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/blizzard.png";
    private static final int COST = 2;
    private static final int ADAPTIVE_AMT = 2;
    private static final int UPGRADE_ADAPTIVE_AMT = 1;

    public Blizzard() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.SELF, ADAPTIVE_AMT);
        assignHydrologistSubtype(HydrologistTags.ICE);
        tags.add(HydrologistTags.CARES_ABOUT_SUBTYPES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        super.use(p, m);
        AbstractCard card = this;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                isDone = true;
                CardModifierManager.addModifier(card, new AdaptiveEffect(card, true, 1));
            }
        });
    }

    @Override
    public String getDescription() {
        return EXTENDED_DESCRIPTION[0];
    }

    @Override
    public AbstractCard makeCopy() {
        return new Blizzard();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeAdaptiveNumber(UPGRADE_ADAPTIVE_AMT);
        }
    }

    @Override
    public ArrayList<AbstractExtraEffectModifier> getMutableAbilities() {
        ArrayList<AbstractExtraEffectModifier> retVal = new ArrayList<>();
        retVal.add(new AdaptiveEffect(this, true, 1));
        return retVal;
    }
}
