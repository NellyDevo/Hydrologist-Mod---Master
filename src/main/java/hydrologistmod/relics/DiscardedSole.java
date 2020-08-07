package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.status.Dazed;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class DiscardedSole extends CustomRelic {
    public static final String ID = "hydrologistmod:DiscardedSole";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/DiscardedSole.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/DiscardedSoleOutline.png");
    private static final int DAZED_TO_ADD = 2;

    public DiscardedSole() {
        super(ID, IMG, OUTLINE, RelicTier.SHOP, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + DAZED_TO_ADD + DESCRIPTIONS[1];
    }

    @Override
    public void atTurnStartPostDraw() {
        addToBot(new MakeTempCardInHandAction(new Dazed(), DAZED_TO_ADD));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new DiscardedSole();
    }
}