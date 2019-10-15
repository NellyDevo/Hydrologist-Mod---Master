package hydrologistmod.cards;

import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.interfaces.ApplyPowersForHydrologistPower;
import hydrologistmod.patches.HydrologistTags;

import java.util.HashMap;

public abstract class AbstractHydrologistCard extends CustomCard {
    static final String ICE_LARGE_ORB = "hydrologistmod/images/1024/card_hydrologist_ice_orb.png";
    static final String WATER_LARGE_ORB = "hydrologistmod/images/1024/card_hydrologist_water_orb.png";
    static final String STEAM_LARGE_ORB = "hydrologistmod/images/1024/card_hydrologist_steam_orb.png";
    static final String ICE_SMALL_ORB = "hydrologistmod/images/512/card_hydrologist_orb_ice_small.png";
    static final String WATER_SMALL_ORB = "hydrologistmod/images/512/card_hydrologist_orb_water_small.png";
    static final String STEAM_SMALL_ORB = "hydrologistmod/images/512/card_hydrologist_orb_steam_small.png";
    private static HashMap<CardTags, String> smallOrbMap;
    private static HashMap<CardTags, String> largeOrbMap;
    static final TextureAtlas.AtlasRegion ICE_LARGE_ATTACK_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Ice Large Attack Frame.png"), 0, 0, 1024, 1024);
    static final TextureAtlas.AtlasRegion ICE_LARGE_SKILL_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Ice Large Skill Frame.png"), 0, 0, 1024, 1024);
    static final TextureAtlas.AtlasRegion ICE_LARGE_POWER_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Ice Large Power Frame.png"), 0, 0, 1024, 1024);
    static final TextureAtlas.AtlasRegion WATER_LARGE_ATTACK_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Water Large Attack Frame.png"), 0, 0, 1024, 1024);
    static final TextureAtlas.AtlasRegion WATER_LARGE_SKILL_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Water Large Skill Frame.png"), 0, 0, 1024, 1024);
    static final TextureAtlas.AtlasRegion WATER_LARGE_POWER_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Water Large Power Frame.png"), 0, 0, 1024, 1024);
    static final TextureAtlas.AtlasRegion STEAM_LARGE_ATTACK_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Steam Large Attack Frame.png"), 0, 0, 1024, 1024);
    static final TextureAtlas.AtlasRegion STEAM_LARGE_SKILL_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Steam Large Skill Frame.png"), 0, 0, 1024, 1024);
    static final TextureAtlas.AtlasRegion STEAM_LARGE_POWER_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/1024/Steam Large Power Frame.png"), 0, 0, 1024, 1024);
    static final TextureAtlas.AtlasRegion ICE_SMALL_ATTACK_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Ice Small Attack Frame.png"), 0, 0, 512, 512);
    static final TextureAtlas.AtlasRegion ICE_SMALL_SKILL_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Ice Small Skill Frame.png"), 0, 0, 512, 512);
    static final TextureAtlas.AtlasRegion ICE_SMALL_POWER_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Ice Small Power Frame.png"), 0, 0, 512, 512);
    static final TextureAtlas.AtlasRegion WATER_SMALL_ATTACK_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Water Small Attack Frame.png"), 0, 0, 512, 512);
    static final TextureAtlas.AtlasRegion WATER_SMALL_SKILL_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Water Small Skill Frame.png"), 0, 0, 512, 512);
    static final TextureAtlas.AtlasRegion WATER_SMALL_POWER_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Water Small Power Frame.png"), 0, 0, 512, 512);
    static final TextureAtlas.AtlasRegion STEAM_SMALL_ATTACK_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Steam Small Attack Frame.png"), 0, 0, 512, 512);
    static final TextureAtlas.AtlasRegion STEAM_SMALL_SKILL_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Steam Small Skill Frame.png"), 0, 0, 512, 512);
    static final TextureAtlas.AtlasRegion STEAM_SMALL_POWER_FRAME =
            new TextureAtlas.AtlasRegion(new Texture("hydrologistmod/images/512/Steam Small Power Frame.png"), 0, 0, 512, 512);

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
    public void applyPowers() { //TODO do this properly with a full patch, or wait for overload implementation
        int damageHolder = baseDamage;
        int blockHolder = baseBlock;
        int magicHolder = baseMagicNumber;
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof ApplyPowersForHydrologistPower) {
                ((ApplyPowersForHydrologistPower)p).beforeApplyPowers(this);
            }
        }
        super.applyPowers();
        baseDamage = damageHolder;
        baseBlock = blockHolder;
        baseMagicNumber = magicHolder;
        isDamageModified = baseDamage != damage;
        isBlockModified = baseBlock != block;
        isMagicNumberModified = baseMagicNumber != magicNumber;
    }

    @Override
    public void calculateCardDamage(AbstractMonster mo) {
        int damageHolder = baseDamage;
        int blockHolder = baseBlock;
        int magicHolder = baseMagicNumber;
        for (AbstractPower p : AbstractDungeon.player.powers) {
            if (p instanceof ApplyPowersForHydrologistPower) {
                ((ApplyPowersForHydrologistPower)p).beforeApplyPowers(this);
            }
        }
        super.calculateCardDamage(mo);
        baseDamage = damageHolder;
        baseBlock = blockHolder;
        baseMagicNumber = magicHolder;
        isDamageModified = baseDamage != damage;
        isBlockModified = baseBlock != block;
        isMagicNumberModified = baseMagicNumber != magicNumber;
    }

    @SpireOverride
    protected void renderAttackPortrait(SpriteBatch sb, float x, float y) {
        if (hasTag(HydrologistTags.ICE)) {
            renderHelper(sb, Color.WHITE.cpy(), ICE_SMALL_ATTACK_FRAME, x, y);
        } else if (hasTag(HydrologistTags.WATER)) {
            renderHelper(sb, Color.WHITE.cpy(), WATER_SMALL_ATTACK_FRAME, x, y);
        } else if (hasTag(HydrologistTags.STEAM)) {
            renderHelper(sb, Color.WHITE.cpy(), STEAM_SMALL_ATTACK_FRAME, x, y);
        } else {
            SpireSuper.call(sb, x, y);
        }
    }

    @SpireOverride
    protected void renderSkillPortrait(SpriteBatch sb, float x, float y) {
        if (hasTag(HydrologistTags.ICE)) {
            renderHelper(sb, Color.WHITE.cpy(), ICE_SMALL_SKILL_FRAME, x, y);
        } else if (hasTag(HydrologistTags.WATER)) {
            renderHelper(sb, Color.WHITE.cpy(), WATER_SMALL_SKILL_FRAME, x, y);
        } else if (hasTag(HydrologistTags.STEAM)) {
            renderHelper(sb, Color.WHITE.cpy(), STEAM_SMALL_SKILL_FRAME, x, y);
        } else {
            SpireSuper.call(sb, x, y);
        }
    }

    @SpireOverride
    protected void renderPowerPortrait(SpriteBatch sb, float x, float y) {
        if (hasTag(HydrologistTags.ICE)) {
            renderHelper(sb, Color.WHITE.cpy(), ICE_SMALL_POWER_FRAME, x, y);
        } else if (hasTag(HydrologistTags.WATER)) {
            renderHelper(sb, Color.WHITE.cpy(), WATER_SMALL_POWER_FRAME, x, y);
        } else if (hasTag(HydrologistTags.STEAM)) {
            renderHelper(sb, Color.WHITE.cpy(), STEAM_SMALL_POWER_FRAME, x, y);
        } else {
            SpireSuper.call(sb, x, y);
        }
    }

    @SpireOverride
    protected void renderHelper(SpriteBatch sb, Color renderColor, TextureAtlas.AtlasRegion image, float x, float y) {
        SpireSuper.call(sb, renderColor, image, x, y);
    }

}
