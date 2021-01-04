package hydrologistmod.cards;

import basemod.BaseMod;
import basemod.abstracts.CustomCard;
import basemod.helpers.TooltipInfo;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import hydrologistmod.patches.HydrologistTags;

import java.lang.reflect.Field;
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
    private static HashMap<CardTags, String> smallOrbMap;
    private static HashMap<CardTags, String> largeOrbMap;
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

    private static void makeOrbMap() {
        smallOrbMap = new HashMap<>();
        largeOrbMap = new HashMap<>();
        smallOrbMap.put(HydrologistTags.ICE, ICE_SMALL_ORB);
        largeOrbMap.put(HydrologistTags.ICE, ICE_LARGE_ORB);
        smallOrbMap.put(HydrologistTags.WATER, WATER_SMALL_ORB);
        largeOrbMap.put(HydrologistTags.WATER, WATER_LARGE_ORB);
        smallOrbMap.put(HydrologistTags.STEAM, STEAM_SMALL_ORB);
        largeOrbMap.put(HydrologistTags.STEAM, STEAM_LARGE_ORB);
    }

    public AbstractHydrologistCard(String id, String name, String img, int cost, String rawDescription,
                                   CardType type, CardColor color,
                                   CardRarity rarity, CardTarget target) {
        super(id, name, img, cost, rawDescription, type, color, rarity, target);
        if (smallOrbMap == null) {
            makeOrbMap();
        }
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

    private Field renderColorField = null;

    private Color getRenderColor() {
        Color reflectedColor = null;
        if (renderColorField == null) {
            try {
                renderColorField = AbstractCard.class.getDeclaredField("renderColor");
                renderColorField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        try {
            reflectedColor = (Color)renderColorField.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (reflectedColor == null) {
            return Color.WHITE.cpy();
        } else {
            return reflectedColor;
        }
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
