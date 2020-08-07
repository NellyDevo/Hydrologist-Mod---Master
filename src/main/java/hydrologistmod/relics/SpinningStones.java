package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class SpinningStones extends CustomRelic {
    public static final String ID = "hydrologistmod:SpinningStones";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/SpinningStones.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/SpinningStonesOutline.png");
    public static final int DRAW_CHANGE = 1;
    public static final int ENERGY = 1;

    public SpinningStones() {
        super(ID, IMG, OUTLINE, RelicTier.BOSS, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onEquip() {
        AbstractDungeon.player.masterHandSize -= DRAW_CHANGE;
        AbstractDungeon.player.energy.energyMaster += ENERGY;
    }

    @Override
    public void onUnequip() {
        AbstractDungeon.player.masterHandSize += DRAW_CHANGE;
        AbstractDungeon.player.energy.energyMaster -= ENERGY;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new SpinningStones();
    }
}