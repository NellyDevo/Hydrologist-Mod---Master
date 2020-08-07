package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import hydrologistmod.interfaces.HeatAndColdRelic;

public class SpiritMask extends CustomRelic implements HeatAndColdRelic {
    public static final String ID = "hydrologistmod:SpiritMask";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/SpiritMask.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/SpiritMaskOutline.png");
    public static final int HEAT_PERCENT_INCREASE = 20;
    public static final int COLD_PERCENT_INCREASE = 10;

    public SpiritMask() {
        super(ID, IMG, OUTLINE, RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + COLD_PERCENT_INCREASE + DESCRIPTIONS[1] + HEAT_PERCENT_INCREASE + DESCRIPTIONS[2];
    }

    @Override
    public int increaseHeatPercent() {
        return HEAT_PERCENT_INCREASE;
    }

    public int increaseColdPercent() {
        return COLD_PERCENT_INCREASE;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SpiritMask();
    }
}