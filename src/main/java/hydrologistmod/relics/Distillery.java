package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import hydrologistmod.HydrologistMod;
import hydrologistmod.actions.FlowAction;

public class Distillery extends CustomRelic {
    public static final String ID = "hydrologistmod:Distillery";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/Distillery.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/DistilleryOutline.png");
    private static final int TURN_COUNT = 3;

    public Distillery() {
        super(ID, IMG, OUTLINE, RelicTier.STARTER, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + TURN_COUNT + DESCRIPTIONS[1];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (HydrologistMod.isThisCorporeal(card) && !grayscale) {
            flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            addToBot(new FlowAction());
            grayscale = true;
        }
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        grayscale = false;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new Distillery();
    }
}
