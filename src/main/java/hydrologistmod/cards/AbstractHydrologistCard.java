package hydrologistmod.cards;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomCard;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.cards.SoulGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import hydrologistmod.patches.HydrologistTags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractHydrologistCard extends CustomCard {
    private static final String ICE_LARGE_ORB = "hydrologistmod/images/1024/card_hydrologist_ice_orb.png";
    private static final String WATER_LARGE_ORB = "hydrologistmod/images/1024/card_hydrologist_water_orb.png";
    private static final String STEAM_LARGE_ORB = "hydrologistmod/images/1024/card_hydrologist_steam_orb.png";
    private static final String ICE_SMALL_ORB = "hydrologistmod/images/512/card_hydrologist_orb_ice_small.png";
    private static final String WATER_SMALL_ORB = "hydrologistmod/images/512/card_hydrologist_orb_water_small.png";
    private static final String STEAM_SMALL_ORB = "hydrologistmod/images/512/card_hydrologist_orb_steam_small.png";
    private static final HashMap<CardTags, String> smallOrbMap = makeMap(ICE_SMALL_ORB, WATER_SMALL_ORB, STEAM_SMALL_ORB);
    private static final HashMap<CardTags, String> largeOrbMap = makeMap(ICE_LARGE_ORB, WATER_LARGE_ORB, STEAM_LARGE_ORB);
    public static final TextureAtlas.AtlasRegion ICE_LARGE_ATTACK_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Ice Large Attack Frame.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion ICE_LARGE_SKILL_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Ice Large Skill Frame.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion ICE_LARGE_POWER_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Ice Large Power Frame.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion WATER_LARGE_ATTACK_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Water Large Attack Frame.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion WATER_LARGE_SKILL_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Water Large Skill Frame.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion WATER_LARGE_POWER_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Water Large Power Frame.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion STEAM_LARGE_ATTACK_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Steam Large Attack Frame.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion STEAM_LARGE_SKILL_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Steam Large Skill Frame.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion STEAM_LARGE_POWER_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Steam Large Power Frame.png"), 0, 0, 1024, 1024);
    private static final TextureAtlas.AtlasRegion ICE_SMALL_ATTACK_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Ice Small Attack Frame.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion ICE_SMALL_SKILL_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Ice Small Skill Frame.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion ICE_SMALL_POWER_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Ice Small Power Frame.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion WATER_SMALL_ATTACK_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Water Small Attack Frame.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion WATER_SMALL_SKILL_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Water Small Skill Frame.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion WATER_SMALL_POWER_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Water Small Power Frame.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion STEAM_SMALL_ATTACK_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Steam Small Attack Frame.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion STEAM_SMALL_SKILL_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Steam Small Skill Frame.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion STEAM_SMALL_POWER_FRAME = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Steam Small Power Frame.png"), 0, 0, 512, 512);
    public static final TextureAtlas.AtlasRegion ICE_LARGE_ATTACK_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/bg_attack_ice.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion ICE_LARGE_SKILL_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/bg_skill_ice.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion ICE_LARGE_POWER_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/bg_power_ice.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion WATER_LARGE_ATTACK_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/bg_attack_water.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion WATER_LARGE_SKILL_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/bg_skill_water.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion WATER_LARGE_POWER_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/bg_power_water.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion STEAM_LARGE_ATTACK_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/bg_attack_steam.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion STEAM_LARGE_SKILL_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/bg_skill_steam.png"), 0, 0, 1024, 1024);
    public static final TextureAtlas.AtlasRegion STEAM_LARGE_POWER_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/bg_power_steam.png"), 0, 0, 1024, 1024);
    private static final TextureAtlas.AtlasRegion ICE_SMALL_ATTACK_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/bg_attack_ice.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion ICE_SMALL_SKILL_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/bg_skill_ice.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion ICE_SMALL_POWER_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/bg_power_ice.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion WATER_SMALL_ATTACK_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/bg_attack_water.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion WATER_SMALL_SKILL_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/bg_skill_water.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion WATER_SMALL_POWER_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/bg_power_water.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion STEAM_SMALL_ATTACK_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/bg_attack_steam.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion STEAM_SMALL_SKILL_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/bg_skill_steam.png"), 0, 0, 512, 512);
    private static final TextureAtlas.AtlasRegion STEAM_SMALL_POWER_BACKGROUND = new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/bg_power_steam.png"), 0, 0, 512, 512);
    public static CardStrings tooltip = CardCrawlGame.languagePack.getCardStrings("hydrologistmod:AbstractHydrologistCard");
    public static String thermalShock = tooltip.DESCRIPTION;
    public static String swappable = tooltip.EXTENDED_DESCRIPTION[0];
    public static String purity = tooltip.EXTENDED_DESCRIPTION[1];
    public static String transmute = tooltip.EXTENDED_DESCRIPTION[2];
    public static String ice = tooltip.EXTENDED_DESCRIPTION[4];
    public static String water = tooltip.EXTENDED_DESCRIPTION[5];
    public static String steam = tooltip.EXTENDED_DESCRIPTION[6];

    private static HashMap<CardTags, String> makeMap(String ice, String water, String steam) {
        HashMap<CardTags, String> retVal = new HashMap<>();
        retVal.put(HydrologistTags.ICE, ice);
        retVal.put(HydrologistTags.WATER, water);
        retVal.put(HydrologistTags.STEAM, steam);
        return retVal;
    }

    public AbstractHydrologistCard(String id, String name, String img, int cost, String rawDescription,
                                   CardType type, CardColor color,
                                   CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
    }

    protected void assignHydrologistSubtype(CardTags tag) {
        setOrbTexture(smallOrbMap.get(tag), largeOrbMap.get(tag));
        tags.add(tag);
    }

    @Override
    public List<TooltipInfo> getCustomTooltips() {
        List<TooltipInfo> retVal = new ArrayList<>();
        if (this.hasTag(HydrologistTags.TEMPERATURE)) {

            retVal.add(new TooltipInfo(BaseMod.getKeywordTitle(thermalShock.toLowerCase()), BaseMod.getKeywordDescription(thermalShock.toLowerCase())));
        }
        return retVal;
    }

    @Override
    public List<String> getCardDescriptors() {
        ArrayList<String> retVal = new ArrayList<>();
        if (hasTag(HydrologistTags.ICE)) {
            retVal.add(ice);
        } else if (hasTag(HydrologistTags.WATER)) {
            retVal.add(water);
        } else if (hasTag(HydrologistTags.STEAM)) {
            retVal.add(steam);
        }
        return retVal;
    }

    @SpireOverride
    protected void renderAttackPortrait(SpriteBatch sb, float x, float y) {
        if (hasTag(HydrologistTags.ICE)) {
            renderHelper(sb, getRenderColor(), ICE_SMALL_ATTACK_BACKGROUND, x, y);
            renderHelper(sb, getRenderColor(), ICE_SMALL_ATTACK_FRAME, x, y);
        } else if (hasTag(HydrologistTags.WATER)) {
            renderHelper(sb, getRenderColor(), WATER_SMALL_ATTACK_BACKGROUND, x, y);
            renderHelper(sb, getRenderColor(), WATER_SMALL_ATTACK_FRAME, x, y);
        } else if (hasTag(HydrologistTags.STEAM)) {
            renderHelper(sb, getRenderColor(), STEAM_SMALL_ATTACK_BACKGROUND, x, y);
            renderHelper(sb, getRenderColor(), STEAM_SMALL_ATTACK_FRAME, x, y);
        } else {
            SpireSuper.call(sb, x, y);
        }
    }

    @SpireOverride
    protected void renderSkillPortrait(SpriteBatch sb, float x, float y) {
        if (hasTag(HydrologistTags.ICE)) {
            renderHelper(sb, getRenderColor(), ICE_SMALL_SKILL_BACKGROUND, x, y);
            renderHelper(sb, getRenderColor(), ICE_SMALL_SKILL_FRAME, x, y);
        } else if (hasTag(HydrologistTags.WATER)) {
            renderHelper(sb, getRenderColor(), WATER_SMALL_SKILL_BACKGROUND, x, y);
            renderHelper(sb, getRenderColor(), WATER_SMALL_SKILL_FRAME, x, y);
        } else if (hasTag(HydrologistTags.STEAM)) {
            renderHelper(sb, getRenderColor(), STEAM_SMALL_SKILL_BACKGROUND, x, y);
            renderHelper(sb, getRenderColor(), STEAM_SMALL_SKILL_FRAME, x, y);
        } else {
            SpireSuper.call(sb, x, y);
        }
    }

    @SpireOverride
    protected void renderPowerPortrait(SpriteBatch sb, float x, float y) {
        if (hasTag(HydrologistTags.ICE)) {
            renderHelper(sb, getRenderColor(), ICE_SMALL_POWER_BACKGROUND, x, y);
            renderHelper(sb, getRenderColor(), ICE_SMALL_POWER_FRAME, x, y);
        } else if (hasTag(HydrologistTags.WATER)) {
            renderHelper(sb, getRenderColor(), WATER_SMALL_POWER_BACKGROUND, x, y);
            renderHelper(sb, getRenderColor(), WATER_SMALL_POWER_FRAME, x, y);
        } else if (hasTag(HydrologistTags.STEAM)) {
            renderHelper(sb, getRenderColor(), STEAM_SMALL_POWER_BACKGROUND, x, y);
            renderHelper(sb, getRenderColor(), STEAM_SMALL_POWER_FRAME, x, y);
        } else {
            SpireSuper.call(sb, x, y);
        }
    }

    @SpireOverride
    protected void renderHelper(SpriteBatch sb, Color renderColor, TextureAtlas.AtlasRegion image, float x, float y) {
        SpireSuper.call(sb, renderColor, image, x, y);
    }

    private Color getRenderColor() {
        Color color = ReflectionHacks.getPrivateInherited(this, AbstractHydrologistCard.class, "renderColor");
        if (color == null) {
            return Color.WHITE.cpy();
        } else {
            return color;
        }
    }

    protected void autoPlayWhenDiscarded() {
        AbstractDungeon.player.discardPile.removeCard(this);
        AbstractDungeon.player.limbo.addToTop(this);
        target_y = Settings.HEIGHT / 2.0f + AbstractDungeon.miscRng.random(-100.0f, 300.0f);
        target_x = Settings.WIDTH / 2.0f + AbstractDungeon.miscRng.random(-Settings.WIDTH / 4.0f, Settings.WIDTH / 4.0f);
        targetAngle = 0;
        targetDrawScale = 0.8f;
        lighten(true);
        ArrayList<Soul> souls = ReflectionHacks.getPrivate(AbstractDungeon.getCurrRoom().souls, SoulGroup.class, "souls");
        for (Soul soul : souls) {
            if (soul.card == this) {
                soul.isDone = true;
                soul.isReadyForReuse = true;
                break;
            }
        }
        AbstractDungeon.actionManager.addCardQueueItem(new CardQueueItem(this, true, energyOnUse, true, true), true);
    }

    public CardTags getHydrologistSubtype() {
        if (this.hasTag(HydrologistTags.WATER)) {
            return HydrologistTags.WATER;
        } else if (this.hasTag(HydrologistTags.ICE)) {
            return HydrologistTags.ICE;
        } else if (this.hasTag(HydrologistTags.STEAM)) {
            return HydrologistTags.STEAM;
        }
        return null;
    }
}
