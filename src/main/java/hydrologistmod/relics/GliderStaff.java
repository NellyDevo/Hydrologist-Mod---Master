package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import hydrologistmod.interfaces.FlowAffectingRelic;

public class GliderStaff extends CustomRelic implements FlowAffectingRelic {
    public static final String ID = "hydrologistmod:GliderStaff";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/GliderStaff.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/GliderStaffOutline.png");

    public GliderStaff() {
        super(ID, IMG, OUTLINE, RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public int leftoversToKeep(int amount) {
        return amount;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new GliderStaff();
    }
}