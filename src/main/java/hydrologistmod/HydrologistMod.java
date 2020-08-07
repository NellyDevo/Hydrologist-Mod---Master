package hydrologistmod;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import hydrologistmod.character.HydrologistCharacter;
import hydrologistmod.patches.HydrologistEnum;
import hydrologistmod.patches.IceBarrierExternalBlock;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;
import hydrologistmod.powers.ThermalShockPower;
import hydrologistmod.relics.*;
import hydrologistmod.vfx.HydrologistParticle;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.classutil.*;

import java.io.File;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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

    private static Logger logger = LogManager.getLogger(HydrologistMod.class.getName());

    private static ArrayList<AbstractCard> nonCorporealCards = new ArrayList<>();

    public HydrologistMod(){
        BaseMod.subscribe(this);

        BaseMod.addColor(HYDROLOGIST_CYAN,
                hydrologistCyan,                                                                                    //Background color, back color, frame color, frame outline color, description box color, glow color
                attackCard, skillCard, powerCard, energyOrb,                                                        //attack background image, skill background image, power background image, energy orb image
                attackCardPortrait, skillCardPortrait, powerCardPortrait, energyOrbPortrait,                        //as above, but for card inspect view
                miniManaSymbol);                                                                                    //appears in Mystic Purple cards where you type [E]

    }

    //Used by @SpireInitializer
    @SuppressWarnings("unused")
    public static void initialize(){
        new HydrologistMod();
    }

    @Override
    public void receiveEditCards() {
        BaseMod.addDynamicVariable(new IceBarrierExternalBlock.IceBarrierBlock());
        try {
            autoAddCards();
        } catch (URISyntaxException | IllegalAccessException | InstantiationException | NotFoundException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void autoAddCards()
            throws URISyntaxException, IllegalAccessException, InstantiationException, NotFoundException, ClassNotFoundException
    {
        logger.info("auto-adding cards to basemod...");
        ClassFinder finder = new ClassFinder();
        URL url = HydrologistMod.class.getProtectionDomain().getCodeSource().getLocation();
        finder.add(new File(url.toURI()));

        ClassFilter filter =
                new AndClassFilter(
                        new NotClassFilter(new InterfaceOnlyClassFilter()),
                        new NotClassFilter(new AbstractClassFilter()),
                        new ClassModifiersClassFilter(Modifier.PUBLIC),
                        new CardFilter()
                );
        Collection<ClassInfo> foundClasses = new ArrayList<>();
        finder.findClasses(foundClasses, filter);

        for (ClassInfo classInfo : foundClasses) {
            CtClass cls = Loader.getClassPool().get(classInfo.getClassName());
            if (cls.hasAnnotation(CardIgnore.class)) {
                continue;
            }
            boolean isCard = false;
            CtClass superCls = cls;
            while (superCls != null) {
                superCls = superCls.getSuperclass();
                if (superCls == null) {
                    break;
                }
                if (superCls.getName().equals(AbstractCard.class.getName())) {
                    isCard = true;
                    break;
                }
            }
            if (!isCard) {
                continue;
            }
            System.out.println(classInfo.getClassName());
            AbstractCard card = (AbstractCard) Loader.getClassPool().getClassLoader().loadClass(cls.getName()).newInstance();
            BaseMod.addCard(card);
            logger.info("Successfully added " + card);
            UnlockTracker.unlockCard(card.cardID);
        }
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(new HydrologistCharacter(CardCrawlGame.playerName), charButton, charPortrait, HydrologistEnum.HYDROLOGIST_CLASS);
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeImg = new Texture("hydrologistmod/images/badge.png");
        ModPanel settingsPanel = new ModPanel();
        BaseMod.registerModBadge(badgeImg, "The Hydrologist Mod", "Johnny Devo", "Adds a new character to the game: The Hydrologist.", settingsPanel);
        HydrologistParticle.initializeRegions();
    }

    @Override
    public void receiveAddAudio() {
        BaseMod.addAudio("hydrologistmod:ICE", "hydrologistmod/sfx/Ice_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:WATER", "hydrologistmod/sfx/Water_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:STEAM", "hydrologistmod/sfx/Steam_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:ICE_CRACK", "hydrologistmod/sfx/Ice_Crack_SFX.ogg");
        BaseMod.addAudio("hydrologistmod:STEAM_LOOP", "hydrologistmod/sfx/Steam_Loop_SFX.ogg");
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
    }

    @Override
    public void receivePostBattle(AbstractRoom room) {
        nonCorporealCards.clear();
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

    public static boolean isHot(AbstractCreature creature) {
        return (creature.hasPower(ThermalShockPower.POWER_ID) || creature.hasPower(HeatPower.POWER_ID));
    }

    public static boolean isCool(AbstractCreature creature) {
        return (creature.hasPower(ThermalShockPower.POWER_ID) || creature.hasPower(ColdPower.POWER_ID));
    }
}
