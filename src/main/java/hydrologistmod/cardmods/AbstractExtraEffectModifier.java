package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import hydrologistmod.helpers.DynamicDynamicVariableManager;

@AbstractCardModifier.SaveIgnore
public abstract class AbstractExtraEffectModifier extends AbstractCardModifier {
    public AbstractCard attachedCard;
    public boolean isValueModified;
    public int value;
    public int baseValue;
    private VariableType type;
    protected String key;

    public AbstractExtraEffectModifier(AbstractCard card, VariableType type) {
        attachedCard = card;
        this.type = type;
    }

    public boolean isModified(AbstractCard card) {
        return isValueModified;
    }

    public int value(AbstractCard card) {
        return value;
    }

    public int baseValue(AbstractCard card) {
        return baseValue;
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        CardModifierManager.removeAllModifiers(attachedCard, true);
        for (AbstractCardModifier mod : CardModifierManager.modifiers(card)) {
            if (!(mod instanceof AbstractExtraEffectModifier)) {
                CardModifierManager.addModifier(attachedCard, mod.makeCopy());
            }
        }
        attachedCard.applyPowers();
        switch(type) {
            case DAMAGE:
                value = attachedCard.damage;
                baseValue = attachedCard.baseDamage;
                isValueModified = attachedCard.isDamageModified;
                break;
            case BLOCK:
                value = attachedCard.block;
                baseValue = attachedCard.baseBlock;
                isValueModified = attachedCard.isBlockModified;
                break;
            case MAGIC:
                value = attachedCard.magicNumber;
                baseValue = attachedCard.baseMagicNumber;
                isValueModified = attachedCard.isMagicNumberModified;
                break;
        }
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        doExtraEffects(card, AbstractDungeon.player, target);
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return addExtraText(rawDescription, card);
    }

    public abstract void doExtraEffects(AbstractCard card, AbstractPlayer p, AbstractCreature m);

    public abstract String addExtraText(String rawDescription, AbstractCard card);

    @Override
    public void onInitialApplication(AbstractCard card) {
        DynamicDynamicVariableManager.registerVariable(card, this);
        key = DynamicDynamicVariableManager.generateKey(card, this);
    }

    private enum VariableType {
        DAMAGE,
        BLOCK,
        MAGIC
    }
}
