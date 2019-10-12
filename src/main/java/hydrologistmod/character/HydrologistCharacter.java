package hydrologistmod.character;

import basemod.abstracts.CustomPlayer;
import basemod.animations.SpriterAnimation;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import hydrologistmod.cards.*;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistEnum;
import hydrologistmod.relics.Distillery;

import java.util.ArrayList;

public class HydrologistCharacter extends CustomPlayer {
    public static final int ENERGY_PER_TURN = 3;
    public static final String MY_CHARACTER_SHOULDER_2 = "hydrologistmod/images/char/shoulder2.png";
    public static final String MY_CHARACTER_SHOULDER_1 = "hydrologistmod/images/char/shoulder.png";
    public static final String MY_CHARACTER_CORPSE = "hydrologistmod/images/char/corpse.png";
    public static final String MY_CHARACTER_ANIMATION = "hydrologistmod/images/char/idle/Animation.scml";
    private static final String ID = "hydrologistmod:HydrologistCharacter";
    private static final CharacterStrings characterStrings = CardCrawlGame.languagePack.getCharacterString(ID);
    private static final String[] NAMES = characterStrings.NAMES;
    private static final String[] TEXT = characterStrings.TEXT;
    private static final Color hydrologistCyan = CardHelper.getColor(3, 240, 252); //#03f0fc / 3, 240, 252
    private static final float DIALOG_X_ADJUSTMENT = 0.0F;
    private static final float DIALOG_Y_ADJUSTMENT = 220.0F;
    public static final String[] orbTextures = {
            "hydrologistmod/images/char/orb/layer1.png",
            "hydrologistmod/images/char/orb/layer2.png",
            "hydrologistmod/images/char/orb/layer3.png",
            "hydrologistmod/images/char/orb/layer4.png",
            "hydrologistmod/images/char/orb/layer5.png",
            "hydrologistmod/images/char/orb/layer6.png",
            "hydrologistmod/images/char/orb/layer1d.png",
            "hydrologistmod/images/char/orb/layer2d.png",
            "hydrologistmod/images/char/orb/layer3d.png",
            "hydrologistmod/images/char/orb/layer4d.png",
            "hydrologistmod/images/char/orb/layer5d.png"
    };

    public HydrologistCharacter(String name) {
        super(name, HydrologistEnum.HYDROLOGIST_CLASS, orbTextures, "hydrologistmod/images/char/orb/vfx.png", null, new SpriterAnimation(MY_CHARACTER_ANIMATION));

        this.dialogX = this.drawX + DIALOG_X_ADJUSTMENT * Settings.scale;
        this.dialogY = this.drawY + DIALOG_Y_ADJUSTMENT * Settings.scale;

        initializeClass(null,
                MY_CHARACTER_SHOULDER_2,
                MY_CHARACTER_SHOULDER_1,
                MY_CHARACTER_CORPSE,
                getLoadout(), 20.0F, -10.0F, 220.0F, 290.0F, new EnergyManager(ENERGY_PER_TURN));
    }

    @Override
    public String getSpireHeartText() {
        return TEXT[1]; //UPDATE BODY TEXT :(
    }

    @Override
    public Color getSlashAttackColor() {
        return hydrologistCyan;
    }

    @Override
    public String getVampireText() {
        return TEXT[2];
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return NAMES[0];
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return AbstractCardEnum.HYDROLOGIST_CYAN;
    }

    @Override
    public Color getCardRenderColor() {
        return hydrologistCyan;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
                AbstractGameAction.AttackEffect.FIRE,
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL,
                AbstractGameAction.AttackEffect.POISON,
                AbstractGameAction.AttackEffect.BLUNT_LIGHT,
                AbstractGameAction.AttackEffect.BLUNT_LIGHT,
                AbstractGameAction.AttackEffect.BLUNT_LIGHT,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
                };
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new WaterWhip();
    }

    @Override
    public Color getCardTrailColor() {
        return hydrologistCyan;
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return 5;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontRed;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.sound.playA("ATTACK_FIRE", MathUtils.random(-0.2f, 0.2f));
        CardCrawlGame.sound.playA("ATTACK_FAST", MathUtils.random(-0.2f, 0.2f));
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_FIRE";
    }

    @Override
    public String getLocalizedCharacterName() {
        return NAMES[0];
    }

    @Override
    public AbstractPlayer newInstance() {
        return new HydrologistCharacter(this.name);
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(StrikeHydrologist.ID);
        retVal.add(StrikeHydrologist.ID);
        retVal.add(StrikeHydrologist.ID);
        retVal.add(UnstableStrike.ID);
        retVal.add(DefendHydrologist.ID);
        retVal.add(DefendHydrologist.ID);
        retVal.add(DefendHydrologist.ID);
        retVal.add(UnstableStrike.ID);
        retVal.add(WaterWhip.ID);
        retVal.add(IceBarrier.ID);
        return retVal;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> retVal = new ArrayList<>();
        retVal.add(Distillery.ID);
        UnlockTracker.markRelicAsSeen(Distillery.ID);
        return retVal;
    }

    @Override
    public CharSelectInfo getLoadout() {
        return new CharSelectInfo(NAMES[0], TEXT[0],
                77, 77, 0, 99, 5, //starting hp, max hp, max orbs, starting gold, starting hand size
                this, getStartingRelics(), getStartingDeck(), false);
    }
}
