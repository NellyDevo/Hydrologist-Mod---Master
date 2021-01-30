package hydrologistmod.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;
import hydrologistmod.cardmods.effects.AbstractExtraEffectModifier;

import java.util.ArrayList;

public interface TransmutableCard {
    default ArrayList<AbstractExtraEffectModifier> getMutableAbilities() {
        return new ArrayList<>();
    }

    default void onTransmuted(AbstractCard newCard, boolean firstTime) {}
}
