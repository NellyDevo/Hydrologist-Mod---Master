package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.FreezeAction;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

public class Freeze extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:Freeze";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/Freeze.png";
    private static final int COST = 1;
    private static final int DAMAGE_AMT = 10;
    private static final int UPGRADE_DAMAGE = 5;

    public Freeze() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.ICE);
        damage = baseDamage = DAMAGE_AMT;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard card = this;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                card.target_y = Settings.HEIGHT / 2.0f + 300f * Settings.scale;
                isDone = true;
            }
        });
        addToBot(new FreezeAction(m, new DamageInfo(p, damage, damageTypeForTurn)));
    }

    @Override
    public AbstractCard makeCopy() {
        return new Freeze();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
        }
    }
}
