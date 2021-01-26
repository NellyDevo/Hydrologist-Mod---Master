package hydrologistmod.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
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
    public boolean isMutable;
    protected int amount = 1;

    public AbstractExtraEffectModifier(AbstractCard card, VariableType type, boolean isMutable, int times) {
        attachedCard = card.makeStatEquivalentCopy();
        this.type = type;
        this.isMutable = isMutable;
        amount = times;
        setValues();
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
        setValues();
    }

    private void setValues() {
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

    public abstract boolean shouldRenderValue();

    @Override
    public void onInitialApplication(AbstractCard card) {
        DynamicDynamicVariableManager.registerVariable(card, this);
        key = "!" + DynamicDynamicVariableManager.generateKey(card, this) + "!";
    }

    protected enum VariableType {
        DAMAGE,
        BLOCK,
        MAGIC
    }

    public void onCardTransmuted(AbstractCard card, AbstractCard newCard) {

    }

    protected void addToTop(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToTop(action);
    }

    protected void addToBot(AbstractGameAction action) {
        AbstractDungeon.actionManager.addToBottom(action);
    }
}
