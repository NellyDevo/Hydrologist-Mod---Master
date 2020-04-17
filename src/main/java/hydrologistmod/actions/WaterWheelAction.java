package hydrologistmod.actions;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class WaterWheelAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:EncapsulatingIceAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private int energyOnUse;
    private boolean freeToPlayOnce;
    private int multiplier;

    public WaterWheelAction(int energyOnUse, boolean freeToPlayOnce, int multiplier) {
        this.duration = DURATION;
        this.energyOnUse = energyOnUse;
        this.freeToPlayOnce = freeToPlayOnce;
        this.multiplier = multiplier;
    }

    @Override
    public void update() {
        int effect = EnergyPanel.totalCount;
        if (energyOnUse != -1) {
            effect = energyOnUse;
        }
        if (AbstractDungeon.player.hasRelic(ChemicalX.ID)) {
            effect += ChemicalX.BOOST;
            AbstractDungeon.player.getRelic(ChemicalX.ID).flash();
        }
        effect *= multiplier;
        final int tmp = effect;
        AbstractDungeon.actionManager.addToTop(new TransmuteCardAction(false, (newCard)-> {
            if (tmp > 0) {
                CardModifierManager.addModifier(newCard, new WaterWheelModifier(tmp));
            }
        }, (newCard) -> (newCard.baseBlock > -1 || newCard.baseDamage > -1)));
        if (!freeToPlayOnce) {
            AbstractDungeon.player.energy.use(EnergyPanel.totalCount);
        }
        isDone = true;
    }

    private static class WaterWheelModifier extends AbstractCardModifier {
        private int amount;

        private WaterWheelModifier(int amount) {
            this.amount = amount;
        }

        public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster monster) {
            return damage + amount;
        }

        public float modifyBlock(float block, AbstractCard card) {
            return block + amount;
        }

        public AbstractCardModifier makeCopy() {
            return new WaterWheelModifier(amount);
        }
    }
}