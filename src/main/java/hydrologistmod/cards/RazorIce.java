package hydrologistmod.cards;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.cardmods.AbstractExtraEffectModifier;
import hydrologistmod.cardmods.DamageEffect;
import hydrologistmod.cardmods.GainBlockEffect;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class RazorIce extends AbstractHydrologistCard implements TransmutableCard {
    public static final String ID = "hydrologistmod:RazorIce";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/RazorIce.png";
    private static final int COST = 0;
    private static final int DAMAGE = 3;
    private static final int BLOCK = 3;
    private static final int UPGRADE_DAMAGE = 1;
    private static final int UPGRADE_BLOCK = 1;

    public RazorIce() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, CardColor.COLORLESS,
                CardRarity.SPECIAL, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.ICE);
        damage = baseDamage = DAMAGE;
        block = baseBlock = BLOCK;
        isEthereal = true;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new GainBlockAction(p, block));
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
    }

    //ebbing
    @Override
    public void triggerOnManualDiscard() {
        AbstractDungeon.player.discardPile.removeCard(this);
        AbstractDungeon.player.limbo.addToTop(this);
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                target_y = Settings.HEIGHT / 2.0f + AbstractDungeon.miscRng.random(-100.0f, 300.0f);
                target_x = Settings.WIDTH / 2.0f + AbstractDungeon.miscRng.random(-Settings.WIDTH / 4.0f, Settings.WIDTH / 4.0f);
                targetAngle = 0;
                isDone = true;
            }
        });
        for (Soul soul : (ArrayList<Soul>)ReflectionHacks.getPrivate(AbstractDungeon.getCurrRoom().souls, SoulGroup.class, "souls")) {
            if (soul.card == this) {
                soul.isDone = true;
                soul.isReadyForReuse = true;
                break;
            }
        }
        AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(this, true, energyOnUse, true, true), true);
    }

    @Override
    public AbstractCard makeCopy() {
        return new RazorIce();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
            upgradeBlock(UPGRADE_BLOCK);
        }
    }

    @Override
    public ArrayList<AbstractExtraEffectModifier> getMutableAbilities() {
        ArrayList<AbstractExtraEffectModifier> list = new ArrayList<>();
        list.add(new GainBlockEffect(this, true));
        list.add(new DamageEffect(this, true));
        return list;
    }
}
