package hydrologistmod;

import basemod.*;
import basemod.helpers.CardBorderGlowManager;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import hydrologistmod.cards.AbstractAdaptiveCard;
import hydrologistmod.cards.AbstractHydrologistCard;
import hydrologistmod.character.HydrologistCharacter;
import hydrologistmod.helpers.DynamicDynamicVariableManager;
import hydrologistmod.interfaces.CorporealRelevantObject;
import hydrologistmod.interfaces.SwappableCard;
import hydrologistmod.patches.HydrologistEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.potions.BottledWater;
import hydrologistmod.potions.FilteredWater;
import hydrologistmod.potions.UnstableBrew;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;
import hydrologistmod.powers.ThermalShockPower;
import hydrologistmod.relics.*;
import hydrologistmod.vfx.HydrologistParticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static hydrologistmod.patches.AbstractCardEnum.HYDROLOGIST_CYAN;

@SpireInitializer
public class HydrologistMod implements AddAudioSubscriber, EditCardsSubscriber, EditCharactersSubscriber, PostInitializeSubscriber, EditKeywordsSubscriber, EditRelicsSubscriber, EditStringsSubscriber, PostBattleSubscriber {

    private static final Color hydrologistCyan = CardHelper.getColor(3, 240, 252); //#03f0fc / 3, 240, 252
    private static final String attackCard = "hydrologistmod/images/512/bg_attack_hydrologist.png";
    private static final String skillCard = "hydrologistmod/images/512/bg_skill_hydrologist.png";
    private static final String powerCard = "hydrologistmod/images/512/bg_power_hydrologist.png";
    private static final String energyOrb = "hydrologistmod/images/512/card_hydrologist_orb_default_small.png";
    private static final String attackCardPortrait = "hydrologistmod/images/1024/bg_attack_hydrologist.png";
    private static final String skillCardPortrait = "hydrologistmod/images/1024/bg_skill_hydrologist.png";
    private static final String powerCardPortrait = "hydrologistmod/images/1024/bg_power_hydrologist.png";
    private static final String energyOrbPortrait = "hydrologistmod/images/1024/card_hydrologist_default_orb.png";
    private static final String charButton = "hydrologistmod/images/charSelect/button.png";
    private static final String charPortrait = "hydrologistmod/images/charSelect/portrait.png";
    private static final String miniManaSymbol = "hydrologistmod/images/manaSymbol.png";
    private static HashMap<AbstractCard.CardTags, ArrayList<AbstractCard>> tagsWithLists = new HashMap<>();

    public static final String ID = "hydrologistmod:hydrologistmod";
    public static UIStrings uiStrings;
    public static String[] TEXT;

    public static SpireConfig hydrologistConfig;

    private static Logger logger = LogManager.getLogger(HydrologistMod.class.getName());

    private static ArrayList<AbstractCard> nonCorporealCards = new ArrayList<>();

    public static final ArrayList<AbstractCard.CardTags> subTypes = new ArrayList<>();

    public HydrologistMod(){
        BaseMod.subscribe(this);

        BaseMod.addColor(HYDROLOGIST_CYAN,
                hydrologistCyan,                                                                                    //Background color, back color, frame color, frame outline color, description box color, glow color
                attackCard, skillCard, powerCard, energyOrb,                                                        //attack background image, skill background image, power background image, energy orb image
                attackCardPortrait, skillCardPortrait, powerCardPortrait, energyOrbPortrait,                        //as above, but for card inspect view
                miniManaSymbol);                                                                                    //appears in Mystic Purple cards where you type [E]

        subTypes.add(HydrologistTags.WATER);
        subTypes.add(HydrologistTags.ICE);
        subTypes.add(HydrologistTags.STEAM);

        Properties hydrologistDefaults = new Properties();
        hydrologistDefaults.setProperty("Subtype Tutorial Seen", "FALSE");
        hydrologistDefaults.setProperty("Swappable Tutorial Seen", "FALSE");
        hydrologistDefaults.setProperty("Temperature Tutorial Seen", "FALSE");
        hydrologistDefaults.setProperty("hydrohomie", "FALSE");
        try {
            hydrologistConfig = new SpireConfig("The Hydrologist", "HydrologistMod", hydrologistDefaults);
        } catch (IOException e) {
            logger.error("HydrologistMod SpireConfig initialization failed:");
            e.printStackTrace();
        }
        logger.info("HYDROLOGIST CONFIG OPTIONS LOADED:");
        logger.info("Subtype tutorial seen: " + hydrologistConfig.getString("Subtype Tutorial Seen") + ".");
        logger.info("Swappable tutorial seen: " + hydrologistConfig.getString("Swappable Tutorial Seen") + ".");
        logger.info("Temperature tutorial seen: " + hydrologistConfig.getString("Temperature Tutorial Seen") + ".");
        logger.info("hydrohomie: " + hydrologistConfig.getString("hydrohomie") + ".");
    }

    //Used by @SpireInitializer
    @SuppressWarnings("unused")
    public static void initialize(){
        new HydrologistMod();
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addDynamicVariable(new DynamicDynamicVariableManager());
        BaseMod.addDynamicVariable(new AbstractAdaptiveCard.AdaptiveVariable());
        new AutoAdd("HydrologistMod")
                .packageFilter(AbstractHydrologistCard.class)
                .setDefaultSeen(true)
                .cards();
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(new HydrologistCharacter(CardCrawlGame.playerName), charButton, charPortrait, HydrologistEnum.HYDROLOGIST_CLASS);
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addPotion(BottledWater.class, BottledWater.LIQUID_COLOR, BottledWater.HYBRID_COLOR, BottledWater.SPOTS_COLOR, BottledWater.ID, HydrologistEnum.HYDROLOGIST_CLASS);
        BaseMod.addPotion(UnstableBrew.class, UnstableBrew.LIQUID_COLOR, UnstableBrew.HYBRID_COLOR, UnstableBrew.SPOTS_COLOR, UnstableBrew.ID, HydrologistEnum.HYDROLOGIST_CLASS);
        BaseMod.addPotion(FilteredWater.class, FilteredWater.LIQUID_COLOR, FilteredWater.HYBRID_COLOR, FilteredWater.SPOTS_COLOR, FilteredWater.ID, HydrologistEnum.HYDROLOGIST_CLASS);

        if (Loader.isModLoaded("widepotions")) {
            try {
                Class<?> widePotionsMod = Class.forName("com.evacipated.cardcrawl.mod.widepotions.WidePotionsMod");
                Method whitelistMethod = widePotionsMod.getDeclaredMethod("whitelistSimplePotion", String.class);
                whitelistMethod.invoke(null, BottledWater.ID);
                whitelistMethod.invoke(null, UnstableBrew.ID);
                whitelistMethod.invoke(null, FilteredWater.ID);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        Texture badgeImg = new Texture("hydrologistmod/images/badge.png");
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(badgeImg, "The Hydrologist Mod", "Johnny Devo", "Adds a new character to the game: The Hydrologist.", settingsPanel);
        HydrologistParticle.initializeRegions();

        CardBorderGlowManager.addGlowInfo(new CardBorderGlowManager.GlowInfo() {
            @Override
            public boolean test(AbstractCard card) {
                if (isThisCorporeal(card)) {
                    return hasCorporealRelevantObject(card) || hasCorporealRelevantCard();
                }
                return false;
            }

            @Override
            public Color getColor(AbstractCard card) {
                return Color.PURPLE.cpy();
            }

            @Override
            public String glowID() {
                return "HydrologistMod:CorporealGlow";
            }
        });

        uiStrings = CardCrawlGame.languagePack.getUIString(ID);
        TEXT = uiStrings.TEXT;
        settingsPanel.addUIElement(new ModLabel(TEXT[0], 450.0f, 725.0f, settingsPanel, me -> {}));
        ModButton showSubtypeTutorial = new ModButton(325.0f, 675.0f, settingsPanel, button -> {
            hydrologistConfig.setString("Subtype Tutorial Seen", "FALSE");
            CardCrawlGame.sound.play("UI_CLICK_1");
            try {hydrologistConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        settingsPanel.addUIElement(showSubtypeTutorial);

        settingsPanel.addUIElement(new ModLabel(TEXT[1], 450.0f, 625.0f, settingsPanel, me -> {}));
        ModButton showSwappableTutorial = new ModButton(325.0f, 575.0f, settingsPanel, button -> {
            hydrologistConfig.setString("Swappable Tutorial Seen", "FALSE");
            CardCrawlGame.sound.play("UI_CLICK_1");
            try {hydrologistConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        settingsPanel.addUIElement(showSwappableTutorial);

        settingsPanel.addUIElement(new ModLabel(TEXT[2], 450.0f, 525.0f, settingsPanel, me -> {}));
        ModButton showTemperatureTutorial = new ModButton(325.0f, 475.0f, settingsPanel, button -> {
            hydrologistConfig.setString("Temperature Tutorial Seen", "FALSE");
            CardCrawlGame.sound.play("UI_CLICK_1");
            try {hydrologistConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        settingsPanel.addUIElement(showTemperatureTutorial);

        ModLabeledToggleButton hydroHomie = new ModLabeledToggleButton(TEXT[3], 350.0f, 450.0f, Settings.CREAM_COLOR, FontHelper.charDescFont, hydrologistConfig.getBool("hydrohomie"), settingsPanel, label -> {}, button -> {
            hydrologistConfig.setBool("hydrohomie", button.enabled);
            try {hydrologistConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        settingsPanel.addUIElement(hydroHomie);
    }

    @Override
    public void receiveAddAudio() {
        BaseMod.addAudio("hydrologistmod:ICE", "hydrologistmod/sfx/Ice_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:WATER", "hydrologistmod/sfx/Water_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:STEAM", "hydrologistmod/sfx/Steam_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:ICE_CRACK", "hydrologistmod/sfx/Ice_Crack_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:STEAM_LOOP", "hydrologistmod/sfx/Steam_Loop_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:WATER_IMPACT_1", "hydrologistmod/sfx/Water_Impact_SFX_1.ogg");
        BaseMod.addAudio("hydrologistmod:WATER_IMPACT_2", "hydrologistmod/sfx/Water_Impact_SFX_2.ogg");
        BaseMod.addAudio("hydrologistmod:WATER_IMPACT_3", "hydrologistmod/sfx/Water_Impact_SFX_3.ogg");
        BaseMod.addAudio("hydrologistmod:ICE_IMPACT_1", "hydrologistmod/sfx/Ice_Impact_SFX_1.ogg");
        BaseMod.addAudio("hydrologistmod:ICE_IMPACT_2", "hydrologistmod/sfx/Ice_Impact_SFX_2.ogg");
        BaseMod.addAudio("hydrologistmod:ICE_IMPACT_3", "hydrologistmod/sfx/Ice_Impact_SFX_3.ogg");
        BaseMod.addAudio("hydrologistmod:STEAM_IMPACT_1", "hydrologistmod/sfx/Steam_Impact_SFX_1.ogg");
        BaseMod.addAudio("hydrologistmod:STEAM_IMPACT_2", "hydrologistmod/sfx/Steam_Impact_SFX_2.ogg");
        BaseMod.addAudio("hydrologistmod:STEAM_IMPACT_3", "hydrologistmod/sfx/Steam_Impact_SFX_3.ogg");
        BaseMod.addAudio("hydrologistmod:CRYSTAL_SPEAR", "hydrologistmod/sfx/Crystal_Spear_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:TIDAL_WAVE", "hydrologistmod/sfx/Tidal_Wave_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:ENCAPSULATING_ICE", "hydrologistmod/sfx/Ice_Shell_Formation_SFX.ogg");
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();

        String keywordStrings = Gdx.files.internal("hydrologistmod/strings/keywords.json").readString(String.valueOf(StandardCharsets.UTF_8));
        Type typeToken = new TypeToken<Map<String, com.evacipated.cardcrawl.mod.stslib.Keyword>>() {}.getType();

        Map<String, Keyword> keywords = gson.fromJson(keywordStrings, typeToken);

        keywords.forEach((k,v)->{
            // Keyword word = (Keyword)v;
            logger.info("Adding Keyword - " + v.NAMES[0]);
            BaseMod.addKeyword("hydrologistmod:", v.PROPER_NAME, v.NAMES, v.DESCRIPTION);
        });
    }

    @Override
    public void receiveEditRelics() {
        //starter
        BaseMod.addRelicToCustomPool(new WaterPouch(), HYDROLOGIST_CYAN);

        //common
        BaseMod.addRelicToCustomPool(new PreciousNecklace(), HYDROLOGIST_CYAN);

        //uncommon
        BaseMod.addRelicToCustomPool(new LoyalBoomerang(), HYDROLOGIST_CYAN);
        BaseMod.addRelicToCustomPool(new TwinBlades(), HYDROLOGIST_CYAN);

        //rare
        BaseMod.addRelicToCustomPool(new GliderStaff(), HYDROLOGIST_CYAN);
        BaseMod.addRelicToCustomPool(new CursedCabbage(), HYDROLOGIST_CYAN);
        BaseMod.addRelicToCustomPool(new SpiritMask(), HYDROLOGIST_CYAN);

        //boss
        BaseMod.addRelicToCustomPool(new MysticalPouch(), HYDROLOGIST_CYAN); //starter upgrade
        BaseMod.addRelicToCustomPool(new SpinningStones(), HYDROLOGIST_CYAN); //energy
        BaseMod.addRelicToCustomPool(new TranquilTeacup(), HYDROLOGIST_CYAN); //special utility

        //shop
        BaseMod.addRelicToCustomPool(new DiscardedSole(), HYDROLOGIST_CYAN);
    }

    @Override
    public void receiveEditStrings() {
        String cardStrings = Gdx.files.internal("hydrologistmod/strings/cards.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(CardStrings.class, cardStrings);
        String characterStrings = Gdx.files.internal("hydrologistmod/strings/character.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(CharacterStrings.class, characterStrings);
        String eventStrings = Gdx.files.internal("hydrologistmod/strings/events.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(EventStrings.class, eventStrings);
        String potionStrings = Gdx.files.internal("hydrologistmod/strings/potions.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(PotionStrings.class, potionStrings);
        String powerStrings = Gdx.files.internal("hydrologistmod/strings/powers.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(PowerStrings.class, powerStrings);
        String relicStrings = Gdx.files.internal("hydrologistmod/strings/relics.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(RelicStrings.class, relicStrings);
        String uiStrings = Gdx.files.internal("hydrologistmod/strings/ui.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(UIStrings.class, uiStrings);
        String tutorialStrings = Gdx.files.internal("hydrologistmod/strings/tutorials.json").readString(String.valueOf(StandardCharsets.UTF_8));
        BaseMod.loadCustomStrings(TutorialStrings.class, tutorialStrings);
    }

    @Override
    public void receivePostBattle(AbstractRoom room) {
        nonCorporealCards.clear();
        DynamicDynamicVariableManager.clearVariables();
    }

    public static void determineNonCorporealCards() {
        nonCorporealCards.clear();
        nonCorporealCards.addAll(AbstractDungeon.player.hand.group);
        nonCorporealCards.addAll(AbstractDungeon.player.drawPile.group);
        nonCorporealCards.addAll(AbstractDungeon.player.discardPile.group);
    }

    public static boolean isThisCorporeal(AbstractCard card) {
        return (!nonCorporealCards.contains(card));
    }

    public static boolean hasCorporealRelevantObject(AbstractCard card) {
        AbstractPlayer p = AbstractDungeon.player;
        if (isInCombat()) {
            for (AbstractPower power : p.powers) {
                if (power instanceof CorporealRelevantObject) {
                    if (((CorporealRelevantObject)power).activateGlow(card)) {
                        return true;
                    }
                }
            }
            for (AbstractRelic relic : p.relics) {
                if (relic instanceof CorporealRelevantObject) {
                    if (((CorporealRelevantObject)relic).activateGlow(card)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasCorporealRelevantCard() {
        AbstractPlayer p = AbstractDungeon.player;
        if (isInCombat()) {
            for (AbstractCard card : p.hand.group) {
                if (card.hasTag(HydrologistTags.CORPOREAL_EFFECT)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isHot(AbstractCreature creature) {
        return (creature.hasPower(ThermalShockPower.POWER_ID) || creature.hasPower(HeatPower.POWER_ID));
    }

    public static boolean isCool(AbstractCreature creature) {
        return (creature.hasPower(ThermalShockPower.POWER_ID) || creature.hasPower(ColdPower.POWER_ID));
    }

    public static AbstractCard returnTrulyRandomCardWithTagInCombat(AbstractCard.CardTags tag) {
        if (tagsWithLists.get(tag) == null) {
            ArrayList<AbstractCard> list = new ArrayList<>();
            for (Map.Entry<String, AbstractCard> potentialCard : CardLibrary.cards.entrySet()) {
                AbstractCard card = potentialCard.getValue();
                if (card.rarity != AbstractCard.CardRarity.BASIC && card.rarity != AbstractCard.CardRarity.SPECIAL
                        && card.hasTag(tag) && !card.hasTag(AbstractCard.CardTags.HEALING)) {
                    if (card instanceof SwappableCard && ((SwappableCard)card).isPairCard()) {
                        continue;
                    }
                    list.add(card.makeCopy());
                }
            }
            tagsWithLists.put(tag, list);
        }
        ArrayList<AbstractCard> list = tagsWithLists.get(tag);
        return list.get(AbstractDungeon.cardRandomRng.random(list.size() - 1));
    }

    public static boolean isInCombat() {
        return CardCrawlGame.isInARun() && AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    public static FrameBuffer createBuffer() {
        return new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, false);
    }

    public static void beginBuffer(FrameBuffer fbo) {
        fbo.begin();
        Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glColorMask(true,true,true,true);
    }

    public static TextureRegion getBufferTexture(FrameBuffer fbo) {
        TextureRegion texture = new TextureRegion(fbo.getColorBufferTexture());
        texture.flip(false, true);
        return texture;
    }

}
