package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import hydrologistmod.actions.TransmuteCardAction;

public class Distillery extends CustomRelic {
    public static final String ID = "hydrologistmod:Distillery";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/Distillery.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/DistilleryOutline.png");

    public Distillery() {
        super(ID, IMG, OUTLINE, RelicTier.STARTER, LandingSound.MAGICAL);
        counter = 0;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void atTurnStartPostDraw() {
        ++counter;
        if (counter == 3) {
            AbstractDungeon.actionManager.addToBottom(new TransmuteCardAction());
            counter = 0;
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Distillery();
    }
}
