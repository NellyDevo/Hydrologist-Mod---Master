package hydrologistmod.helpers;

import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import hydrologistmod.cardmods.AbstractExtraEffectModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DynamicDynamicVariableManager extends DynamicVariable {
    public static DynamicDynamicVariableManager managerInstance;
    private static Color normalColor = Color.valueOf("65ada1");
    private static Color increasedValueColor = Color.valueOf("9ad6b6");
    private static Color decreasedValueColor = Color.valueOf("b799bd");
    public HashMap<AbstractCard, ArrayList<AbstractExtraEffectModifier>> variableDatabase;
    private int timesKeyFoundThisFrame = -1;

    @Override
    public String key() {
        managerInstance = this;
        managerInstance.variableDatabase = new HashMap<>();
        return "hydrologistmod:dynamicdynamic"; //ideally this should never actually be used
    }

    @Override
    public boolean isModified(AbstractCard card) {
        ++timesKeyFoundThisFrame;
        AbstractExtraEffectModifier mod = variableDatabase.get(card).get(timesKeyFoundThisFrame);
        return mod.isModified(card);
    }

    @Override
    public int value(AbstractCard card) {
        if (timesKeyFoundThisFrame == -1) {
            return 0; //prevent a crash
        }
        AbstractExtraEffectModifier mod = variableDatabase.get(card).get(timesKeyFoundThisFrame);
        return mod.value(card);
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (timesKeyFoundThisFrame == -1) {
            return 0; //prevent a crash
        }
        AbstractExtraEffectModifier mod = variableDatabase.get(card).get(timesKeyFoundThisFrame);
        return mod.baseValue(card);
    }

    @Override
    public Color getNormalColor() {
        if (timesKeyFoundThisFrame == -1) {
            return Color.BLACK.cpy();
        }
        return normalColor;
    }

    @Override
    public Color getIncreasedValueColor() {
        if (timesKeyFoundThisFrame == -1) {
            return Color.BLACK.cpy();
        }
        return increasedValueColor;
    }

    @Override
    public Color getDecreasedValueColor() {
        if (timesKeyFoundThisFrame == -1) {
            return Color.BLACK.cpy();
        }
        return decreasedValueColor;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false; //this is never used
    }

    public static void resetTicker() {
        managerInstance.timesKeyFoundThisFrame = -1;
    }

    public static void clearVariables() {
        for (AbstractCard card : managerInstance.variableDatabase.keySet()) {
            ArrayList<AbstractExtraEffectModifier> list = managerInstance.variableDatabase.get(card);
            for (AbstractExtraEffectModifier mod : list) {
                BaseMod.cardDynamicVariableMap.remove(generateKey(card, mod));
            }
        }
        managerInstance.variableDatabase.clear();
    }

    public static void registerVariable(AbstractCard card, AbstractExtraEffectModifier mod) {
        if (!managerInstance.variableDatabase.containsKey(card)) {
            ArrayList<AbstractExtraEffectModifier> list = new ArrayList<>();
            list.add(mod);
            managerInstance.variableDatabase.put(card, list);
        } else {
            ArrayList<AbstractExtraEffectModifier> list = managerInstance.variableDatabase.get(card);
            if (list.contains(mod)) {
                return;
            }
            list.add(mod);
            Collections.sort(list);
        }
        BaseMod.cardDynamicVariableMap.put(generateKey(card, mod), managerInstance);
    }

    public static String generateKey(AbstractCard card, AbstractExtraEffectModifier mod) {
        return "hydrologistmod:" + card.uuid + ":" + mod.attachedCard.uuid;
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderDescription"
    )
    public static class ResetDynamicVariableTickerPowersPatch {
        public static void Prefix(AbstractCard __instance, SpriteBatch sb) {
            resetTicker();
        }
    }
}
