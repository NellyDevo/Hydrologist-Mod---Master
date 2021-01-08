package hydrologistmod.tutorials;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.ui.FtueTip;
import com.megacrit.cardcrawl.ui.buttons.GotItButton;
import hydrologistmod.HydrologistMod;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.patches.RenderSwappablePreviewPatch;

import java.io.IOException;

public class SwappableTutorial extends FtueTip {
    private AbstractCard card;
    private float oldX;
    private float oldY;
    private float oldAngle;
    private float oldScale;

    public SwappableTutorial(String label, String text, float x, float y, AbstractCard c, float cardX, float cardY) {
        super(label, text, x, y, c);
        card = c;
        oldX = c.target_x;
        oldY = c.target_y;
        oldAngle = c.targetAngle;
        oldScale = c.targetDrawScale;
        c.target_x = cardX;
        c.target_y = cardY;
        c.targetAngle = 0;
        c.targetDrawScale = 1.0f;
    }

    @Override
    public void update() {
        super.update();
        if (((GotItButton)ReflectionHacks.getPrivateInherited(this, SwappableTutorial.class, "button")).hb.clicked) {
            card.target_x = oldX;
            card.target_y = oldY;
            card.targetAngle = oldAngle;
            card.targetDrawScale = oldScale;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        RenderSwappablePreviewPatch.renderSwappablesPreviewPatch.Postfix(card, sb);
    }

    @SpirePatch(
            clz = DrawCardAction.class,
            method = "update"
    )
    public static class ShowSwappablesTutorialPatch {
        public static final String ID = "hydrologistmod:SwappablesTutorial";
        public static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(ID);
        public static final String[] LABEL = tutorialStrings.LABEL;
        public static final String[] TEXT = tutorialStrings.TEXT;

        public static void Postfix(DrawCardAction __instance) {
            if (__instance.isDone && !HydrologistMod.hydrologistConfig.getBool("Swappable Tutorial Seen")) {
                for (AbstractCard c : DrawCardAction.drawnCards) {
                    if (SwapperHelper.isCardSwappable(c)) {
                        AbstractDungeon.ftue = new SwappableTutorial(LABEL[0], TEXT[0], Settings.WIDTH / 2.0f - (500.0f * Settings.scale), Settings.HEIGHT / 2.0f, c, (Settings.WIDTH / 2.0f) + (200.0f * Settings.scale), Settings.HEIGHT / 2.0f);
                        HydrologistMod.hydrologistConfig.setBool("Swappable Tutorial Seen", true);
                        try { HydrologistMod.hydrologistConfig.save(); } catch (IOException e) { e.printStackTrace(); }
                        break;
                    }
                }
            }
        }
    }
}
