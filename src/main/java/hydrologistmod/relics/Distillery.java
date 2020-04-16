package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
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

    public Distillery() {
        super(ID, IMG, OUTLINE, RelicTier.STARTER, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (HydrologistMod.isThisCorporeal(card) && !grayscale) {
            AbstractRelic relic = this;
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    flash();
                    addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, relic));
                    addToBot(new FlowAction());
                    grayscale = true;
                    isDone = true;
                }
            });
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
