package hydrologistmod.cards;

import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
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
    private static final int BLOCK = 2;
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
        autoPlayWhenDiscarded();
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
        list.add(new GainBlockEffect(this, true, 1));
        list.add(new DamageEffect(this, true, 1));
        return list;
    }
}
