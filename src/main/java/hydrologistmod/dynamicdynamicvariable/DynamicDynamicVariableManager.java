package hydrologistmod.dynamicdynamicvariable;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import hydrologistmod.cardmods.effects.AbstractExtraEffectModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DynamicDynamicVariableManager {
    public static HashMap<String, DynamicDynamicVariable> variableDatabase;

    public static void clearVariables() {
        for (String id : variableDatabase.keySet()) {
            BaseMod.cardDynamicVariableMap.remove(id);
        }
        variableDatabase.clear();
    }

    public static void registerVariable(AbstractCard card, AbstractExtraEffectModifier mod) {
        String id = generateKey(card, mod);
        if (!variableDatabase.containsKey(id)) {
            DynamicDynamicVariable variable = new DynamicDynamicVariable(id, mod);
            variableDatabase.put(id, variable);
            BaseMod.cardDynamicVariableMap.put(id, variable);
        }
    }

    public static String generateKey(AbstractCard card, AbstractExtraEffectModifier mod) {
        return "hydrologistmod:" + card.uuid + ":" + mod.attachedCard.uuid;
    }
}
