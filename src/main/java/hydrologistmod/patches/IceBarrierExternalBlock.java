package hydrologistmod.patches;

import basemod.abstracts.DynamicVariable;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;

public class IceBarrierExternalBlock {
    @SpirePatch(
            clz = AbstractCard.class,
            method = SpirePatch.CLASS
    )
    public static class DynamicVariableFields {
        public static SpireField<Integer> iceBarrierBlock = new SpireField<>(() -> -1);
        public static SpireField<Integer> iceBarrierBaseBlock = new SpireField<>(() -> -1);
        public static SpireField<Boolean> iceBarrierIsBlockModified = new SpireField<>(() -> false);
        public static SpireField<Boolean> iceBarrierUpgradedBlock = new SpireField<>(() -> false);
    }

    public static class IceBarrierBlock extends DynamicVariable {

        @Override
        public int baseValue(AbstractCard card) {
            return DynamicVariableFields.iceBarrierBaseBlock.get(card);
        }

        @Override
        public boolean isModified(AbstractCard card) {
            return DynamicVariableFields.iceBarrierIsBlockModified.get(card);
        }

        @Override
        public void setIsModified(AbstractCard card, boolean v) {
            DynamicVariableFields.iceBarrierIsBlockModified.set(card, v);
        }

        @Override
        public String key() {
            return "hydrologistmod:ICE";
        }

        @Override
        public boolean upgraded(AbstractCard card) {
            return DynamicVariableFields.iceBarrierUpgradedBlock.get(card);
        }

        @Override
        public int value(AbstractCard card) {
            return DynamicVariableFields.iceBarrierBlock.get(card);
        }
    }
}
