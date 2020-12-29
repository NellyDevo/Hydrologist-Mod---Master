package hydrologistmod.helpers;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.cardManip.CardGlowBorder;
import hydrologistmod.HydrologistMod;
import javassist.CtBehavior;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CardBorderGlowManager {
    private static final Color defaultGlowColor = ReflectionHacks.getPrivateStatic(AbstractCard.class, "BLUE_BORDER_GLOW_COLOR");
    private static ArrayList<GlowInformation> glowInfo = new ArrayList<>();

    public static void addGlowInfo(GlowInformation newInfo) {
        boolean hasGlow = false;
        for (GlowInformation info : glowInfo) {
            if (info.glowID().equals(newInfo.glowID())) {
                hasGlow = true;
            }
        }
        if (!hasGlow) {
            glowInfo.add(newInfo);
            Collections.sort(glowInfo);
        }
    }

    public static void removeGlowInfo(GlowInformation info) {
        glowInfo.remove(info);
        Collections.sort(glowInfo);
    }

    public static void removeGlowInfo(String glowID) {
        GlowInformation info = null;
        for (GlowInformation i : glowInfo) {
            if (i.glowID().equals(glowID)) {
                info = i;
                break;
            }
        }
        if (info != null) {
            removeGlowInfo(info);
        }
    }

    public static GlowInformation getGlowInfo(String glowID) {
        GlowInformation retVal = null;
        for (GlowInformation i : glowInfo) {
            if (i.glowID().equals(glowID)) {
                retVal = i;
                break;
            }
        }
        return retVal;
    }

    public static boolean hasCustomGlows(AbstractCard card) {
        for(GlowInformation glow : glowInfo) {
            if (glow.test(card)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<GlowInformation> getCustomGlowColors(AbstractCard card) {
        ArrayList<GlowInformation> retVal = new ArrayList<>();
        for (GlowInformation info : glowInfo) {
            if (info.test(card)) {
                retVal.add(info);
            }
        }
        if (!card.glowColor.equals(defaultGlowColor)) {
            Color color = card.glowColor.cpy();
            retVal.add(new GlowInformation() {
                @Override
                public boolean test(AbstractCard card) {
                    return false;
                }

                @Override
                public Color getColor(AbstractCard card) {
                    return color.cpy();
                }

                @Override
                public String glowID() {
                    return null;
                }
            });
        }
        return retVal;
    }

    public static abstract class GlowInformation implements Comparable<GlowInformation>{
        int priority = 0;
        public abstract boolean test(AbstractCard card);
        public abstract Color getColor(AbstractCard card);
        public abstract String glowID();

        @Override
        public int compareTo(GlowInformation other) {
            return priority - other.priority;
        }
    }

    @SpirePatch(
            clz = AbstractCard.class,
            method = "renderGlow"
    )
    public static class RenderGlowPatch {
        private static FrameBuffer fbo = HydrologistMod.createBuffer();
        private static FrameBuffer maskfbo = HydrologistMod.createBuffer();
        private static ShapeRenderer shape = new ShapeRenderer();

        private static TextureRegion currentMask = null;
        private static ArrayList<GlowInformation> colorTracker = null;
        private static HashMap<GlowInformation, MaskInfo> masks = null;
        private static GlowInformation currentRender = null;
        private static Color defaultColor = null;

        public static void Prefix(AbstractCard __instance, SpriteBatch sb) {
            //run if system is active OR if system should be active
            if (!Settings.hideCards && (colorTracker != null || hasCustomGlows(__instance))) {

                //if not already initialized, initialize colors, masks, and variables
                if (colorTracker == null) {
                    defaultColor = __instance.glowColor;
                    colorTracker = getCustomGlowColors(__instance);

                    //if more than one border glow, create and store mask rendering info
                    if (colorTracker.size() > 1) {
                        masks = new HashMap<>();
                        float segmentSize = 180f / (float)colorTracker.size();
                        float arcStart = -90f;
                        for (int i = 0; i < colorTracker.size(); ++i) {
                            MaskInfo mask = new MaskInfo(__instance.current_x, __instance.current_y, AbstractCard.IMG_HEIGHT, arcStart + __instance.angle, segmentSize + ((i == 0 || i == colorTracker.size() - 1) ? 90f : 0f));
                            masks.put(colorTracker.get(i), mask);
                            if (i == 0) {
                                arcStart += 90f;
                            }
                            arcStart += segmentSize;
                        }
                    }
                }

                //determine next color for actual method to render
                currentRender = colorTracker.get(0);
                colorTracker.remove(currentRender);

                //if more than one border glow, create a mask, then begin a framebuffer
                if (masks != null) {
                    sb.end();
                    HydrologistMod.beginBuffer(maskfbo);
                    shape.begin(ShapeRenderer.ShapeType.Filled);
                    shape.setColor(Color.BLACK);
                    MaskInfo mask = masks.get(currentRender);
                    shape.arc(mask.x, mask.y, mask.radius, mask.start, mask.degrees);
                    shape.end();
                    maskfbo.end();
                    currentMask = HydrologistMod.getBufferTexture(maskfbo);

                    HydrologistMod.beginBuffer(fbo);
                    sb.begin();
                }

                //send the color to the border glow method
                __instance.glowColor = currentRender.getColor(__instance);
            }
        }

        public static void Postfix(AbstractCard __instance, SpriteBatch sb) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            //only run if system is active
            if (!Settings.hideCards && colorTracker != null) {

                //if more than one border glow, apply the mask, end previously activated frame buffer and render the texture
                if (masks != null) {
                    sb.setColor(Color.WHITE.cpy());
                    sb.setBlendFunction(0, GL20.GL_SRC_ALPHA);
                    sb.draw(currentMask, 0, 0);
                    sb.end();
                    fbo.end();
                    TextureRegion texture = HydrologistMod.getBufferTexture(fbo);
                    sb.begin();
                    sb.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
                    sb.draw(texture, 0, 0);
                    sb.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                }

                //if all colors have been rendered, reset the variables before exiting the loop
                if (colorTracker.isEmpty()) {
                    colorTracker = null;
                    masks = null;
                    currentMask = null;
                    currentRender = null;
                    __instance.glowColor = defaultColor;
                    defaultColor = null;

                //if not all colors have been rendered, call the method again.
                } else {
                    ReflectionHacks.privateMethod(AbstractCard.class, "renderGlow", SpriteBatch.class).invoke(__instance, sb);
                }
            }
        }

        public static class MaskInfo {
            public float x, y, radius, start, degrees;

            public MaskInfo(float x, float y, float radius, float start, float degrees) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.start = start;
                this.degrees = degrees;
            }
        }

        @SpirePatch(
                clz = CardGlowBorder.class,
                method = "render"
        )
        public static class CardGlowBorderEffectPatch {

            @SpireInsertPatch(
                    locator = Locator.class
            )
            public static void Insert(CardGlowBorder __instance, SpriteBatch sb) {
                if (currentRender != null) {
                    Color color = currentRender.getColor(ReflectionHacks.getPrivate(__instance, CardGlowBorder.class, "card"));
                    color.a = sb.getColor().a;
                    sb.setColor(color);
                }
            }

            private static class Locator extends SpireInsertLocator {
                @Override
                public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                    Matcher finalMatcher = new Matcher.MethodCallMatcher(SpriteBatch.class, "draw");
                    return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
                }
            }
        }
    }
}
