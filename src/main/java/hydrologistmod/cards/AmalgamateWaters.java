package hydrologistmod.cards;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.FlowAction;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.cardmods.AbstractExtraEffectModifier;
import hydrologistmod.interfaces.TransmutableCard;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;

public class AmalgamateWaters extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:AmalgamateWaters";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/AmalgamateWaters.png";
    private static final int COST = 2;
    private static final int DAMAGE = 20;
    private static final int UPGRADE_DAMAGE = 5;

    public AmalgamateWaters() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.RARE, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.WATER);
        damage = baseDamage = DAMAGE;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
        addToBot(new FlowAction((cards) -> {
            ArrayList<AbstractExtraEffectModifier> mods = new ArrayList<>();
            for (AbstractCard card : cards) {
                for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
                    if (mod instanceof AbstractExtraEffectModifier) {
                        AbstractExtraEffectModifier effect = (AbstractExtraEffectModifier)mod;
                        if (effect.isMutable) {
                            mods.add(effect);
                        }
                    }
                }
                if (card instanceof TransmutableCard) {
                    mods.addAll(((TransmutableCard)card).getMutableAbilities());
                }
            }
            for (AbstractExtraEffectModifier mod : mods) {
                CardModifierManager.addModifier(this, mod.makeCopy());
            }
        }));
    }

    @Override
    public AbstractCard makeCopy() {
        return new AmalgamateWaters();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeDamage(UPGRADE_DAMAGE);
        }
    }
}
