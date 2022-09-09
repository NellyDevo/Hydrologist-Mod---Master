package hydrologistmod.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.SetCardTargetCoordinatesAction;
import hydrologistmod.actions.TidalWaveAction;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.Arrays;
import java.util.LinkedList;

public class TidalWave extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:TidalWave";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/tidal_wave.png";
    private static final int COST = 1;
    private static final int DAMAGE_AMT = 7;
    private static final int UPGRADE_DAMAGE_AMT = 3;

    public TidalWave(boolean makeSwappable) {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.COMMON, CardTarget.ALL_ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        isMultiDamage = true;
        baseDamage = damage = DAMAGE_AMT;
        if (makeSwappable) {
            SwapperHelper.makeSwappableGroup(new LinkedList<>(Arrays.asList(this, new IceSpike(false))));
        }
    }

    public TidalWave() {
        this(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new SetCardTargetCoordinatesAction(this, -1.0f, Settings.HEIGHT / 2.0f + 300f * Settings.scale));
        addToBot(new TidalWaveAction(p, multiDamage, DamageInfo.DamageType.NORMAL));
    }

    @Override
    public AbstractCard makeCopy() {
        return new TidalWave();
    }

    @Override
    public void upgrade() {
        SwapperHelper.upgrade(this);
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE_AMT);
        }
    }
}
