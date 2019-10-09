package hydrologistmod.cards;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToDiscardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import com.sun.org.apache.bcel.internal.generic.NEW;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class Cryogenics extends AbstractHydrologistCard implements TransmutableCard {
    public static final String ID = "hydrologistmod:Cryogenics";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/Cryogenics.png";
    private static final int COST = -2;
    private static final int NEW_COPIES = 1;
    private static final int UPGRADE_COPIES = 1;

    public Cryogenics() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.NONE);
        tags.add(HydrologistTags.ICE);
        magicNumber = baseMagicNumber = NEW_COPIES;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public boolean canUse(final AbstractPlayer p, final AbstractMonster m) {
        this.cantUseMessage = EXTENDED_DESCRIPTION[0];
        return false;
    }

    @Override
    public AbstractCard makeCopy() {
        return new Cryogenics();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADE_COPIES);
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public void onTransmuted(AbstractCard newCard) {
        AbstractDungeon.actionManager.addToTop(new AbstractGameAction() {
            @Override
            public void update() {
                for (int i = 0; i < magicNumber; ++i) {
                    AbstractCard freeCard = newCard.makeStatEquivalentCopy();
                    freeCard.current_x = -1000.0f * Settings.scale;
                    if (AbstractDungeon.player.hand.size() < BaseMod.MAX_HAND_SIZE) {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToHandEffect(freeCard, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                    }
                    else {
                        AbstractDungeon.effectList.add(new ShowCardAndAddToDiscardEffect(freeCard, Settings.WIDTH / 2.0f, Settings.HEIGHT / 2.0f));
                    }
                    freeCard.setCostForTurn(0);
                }
                isDone = true;
            }
        });
    }
}
