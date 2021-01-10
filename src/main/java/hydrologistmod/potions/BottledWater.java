package hydrologistmod.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import hydrologistmod.actions.BottledWaterAction;

public class BottledWater extends AbstractPotion {
    public static final String ID = "hydrologistmod:BottledWater";
    public static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(ID);
    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;
    public static final PotionRarity RARITY = PotionRarity.COMMON;
    public static final PotionSize ICON = PotionSize.BOTTLE;
    public static final PotionEffect EFFECT = PotionEffect.NONE;
    public static final Color LIQUID_COLOR = Color.SKY.cpy();
    public static final Color HYBRID_COLOR = null;
    public static final Color SPOTS_COLOR = null;

    public BottledWater() {
        super(NAME, ID, RARITY, ICON, EFFECT, LIQUID_COLOR, HYBRID_COLOR, SPOTS_COLOR);
        isThrown = false;
    }

    @Override
    public void use(AbstractCreature m) {
        addToBot(new BottledWaterAction(potency));
    }

    @Override
    public void initializeData() {
        potency = getPotency();
        if (potency == 1) {
            description = DESCRIPTIONS[0];
        } else {
            description = DESCRIPTIONS[1] + potency + DESCRIPTIONS[2];
        }
        tips.clear();
        tips.add(new PowerTip(name, description));
    }

    @Override
    public int getPotency(int i) {
        return 1;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new BottledWater();
    }
}
