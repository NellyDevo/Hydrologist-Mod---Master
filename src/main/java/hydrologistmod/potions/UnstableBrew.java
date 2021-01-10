package hydrologistmod.potions;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import hydrologistmod.cards.AbstractHydrologistCard;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;
import hydrologistmod.powers.ThermalShockPower;

public class UnstableBrew extends AbstractPotion {
    public static final String ID = "hydrologistmod:UnstableBrew";
    public static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString(ID);
    public static final String NAME = potionStrings.NAME;
    public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;
    public static final PotionRarity RARITY = PotionRarity.UNCOMMON;
    public static final PotionSize ICON = PotionSize.H;
    public static final PotionEffect EFFECT = PotionEffect.OSCILLATE;
    public static final Color LIQUID_COLOR = Color.ORANGE.cpy();
    public static final Color HYBRID_COLOR = Color.SKY.cpy();
    public static final Color SPOTS_COLOR = null;

    public UnstableBrew() {
        super(NAME, ID, RARITY, ICON, EFFECT, LIQUID_COLOR, HYBRID_COLOR, SPOTS_COLOR);
        targetRequired = true; //delete for default false
        isThrown = true;
    }

    @Override
    public void use(AbstractCreature m) {
        AbstractPlayer p = AbstractDungeon.player;
        if (m.hasPower(ColdPower.POWER_ID)) {
            addToBot(new ApplyPowerAction(m, p, new HeatPower(m, p, 1), 1));
            addToBot(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, potency - 1), potency - 1));
        } else if (m.hasPower(HeatPower.POWER_ID)) {
            addToBot(new ApplyPowerAction(m, p, new ColdPower(m, p, 1), 1));
            addToBot(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, potency - 1), potency - 1));
        } else {
            addToBot(new ApplyPowerAction(m, p, new ThermalShockPower(m, p, potency), potency));
        }
    }

    @Override
    public void initializeData() {
        potency = getPotency();
        description = potionStrings.DESCRIPTIONS[0] + potency + potionStrings.DESCRIPTIONS[1];
        tips.clear();
        tips.add(new PowerTip(name, description));
        tips.add(new PowerTip(BaseMod.getKeywordTitle(AbstractHydrologistCard.thermalShock.toLowerCase()), BaseMod.getKeywordDescription(AbstractHydrologistCard.thermalShock.toLowerCase())));
    }

    @Override
    public int getPotency(int i) {
        return 4;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new UnstableBrew();
    }
}
