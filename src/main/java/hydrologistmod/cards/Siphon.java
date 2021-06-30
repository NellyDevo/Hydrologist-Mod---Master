package hydrologistmod.cards;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.TransmuteCardAction;
import hydrologistmod.cardmods.effects.AbstractExtraEffectModifier;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Siphon extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Siphon";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/Siphon.png";
    private static final int COST = 1;

    public Siphon(boolean makeSwappable) {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.NONE);
        assignHydrologistSubtype(HydrologistTags.WATER);
        if (makeSwappable) {
            SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new Freeze(false))));
        }
    }

    public Siphon() {
        this(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new TransmuteCardAction(((oldCard, newCard, firstTime) -> {
            if (firstTime) {
                ArrayList<AbstractExtraEffectModifier> list = new ArrayList<>();
                if (oldCard instanceof TransmutableCard) {
                    list.addAll(((TransmutableCard)oldCard).getMutableAbilities());
                }
                for (AbstractCardModifier mod : CardModifierManager.modifiers(oldCard)) {
                    if (mod instanceof  AbstractExtraEffectModifier) {
                        AbstractExtraEffectModifier effect = (AbstractExtraEffectModifier)mod;
                        if (effect.isMutable) {
                            list.add(effect);
                        }
                    }
                }
                AbstractCard card = SwapperHelper.getNextCard(this);
                for (AbstractExtraEffectModifier mod : list) {
                    CardModifierManager.addModifier(card, mod.makeCopy());
                }
            }
        })));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Siphon();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
        }
    }
}
