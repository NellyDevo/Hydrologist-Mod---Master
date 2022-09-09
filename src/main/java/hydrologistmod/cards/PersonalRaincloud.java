package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.PersonalRaincloudAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class PersonalRaincloud extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:PersonalRaincloud";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/personal_raincloud.png";
    private static final int COST = 0;
    private static final int BLOCK_AMT = 4;
    private static final int DRAW_AMT = 1;

    public PersonalRaincloud() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.SELF);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        block = baseBlock = BLOCK_AMT;
        magicNumber = baseMagicNumber = DRAW_AMT;
        cardsToPreview = new Raincloud();
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new PersonalRaincloudAction(upgraded));
    }

    @Override
    public AbstractCard makeCopy() {
        return new PersonalRaincloud();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
            cardsToPreview.upgrade();
        }
    }
}
