package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.HydrologistMod;
import hydrologistmod.actions.SaunaAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class Sauna extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Sauna";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/Sauna.png";
    private static final int COST = 1;
    private static final int BLOCK_AMT = 5;
    private static final int UPGRADE_BLOCK = 2;

    public Sauna() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.SKILL, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.SELF);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        block = baseBlock = BLOCK_AMT;
        tags.add(HydrologistTags.CORPOREAL_EFFECT);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new SaunaAction(block));
    }

    @Override
    public void triggerOnGlowCheck() {
        for (AbstractCard card : AbstractDungeon.player.hand.group) {
            if (HydrologistMod.isThisCorporeal(card)) {
                glowColor = GOLD_BORDER_GLOW_COLOR.cpy();
                return;
            }
        }
        glowColor = BLUE_BORDER_GLOW_COLOR.cpy();
    }

    @Override
    public AbstractCard makeCopy() {
        return new Sauna();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBlock(UPGRADE_BLOCK);
        }
    }
}
