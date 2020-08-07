package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import hydrologistmod.actions.TransmuteCardAction;
import hydrologistmod.interfaces.TransmutableAffectingRelic;

public class TranquilTeacup extends CustomRelic implements TransmutableAffectingRelic {
    public static final String ID = "hydrologistmod:TranquilTeacup";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/TranquilTeacup.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/TranquilTeacupOutline.png");
    private static final int EXTRA_CHOICES = 2;

    public TranquilTeacup() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + (EXTRA_CHOICES + 1) + DESCRIPTIONS[1];
    }

    @Override
    public void onTransmute(TransmuteCardAction action) {
        action.choices += EXTRA_CHOICES;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new TranquilTeacup();
    }
}