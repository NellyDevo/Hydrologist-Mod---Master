package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.relics.OnApplyPowerRelic;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.powers.HeatPower;

public class TwinBlades extends CustomRelic implements OnApplyPowerRelic {
    public static final String ID = "hydrologistmod:TwinBlades";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/TwinBlades.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/TwinBladesOutline.png");
    public static final int DAMAGE = 3;
    public static final int BLOCK = 2;

    public TwinBlades() {
        super(ID, IMG, OUTLINE, RelicTier.UNCOMMON, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + BLOCK + DESCRIPTIONS[1] + DAMAGE + DESCRIPTIONS[2];
    }

    @Override
    public boolean onApplyPower(AbstractPower powerToApply, AbstractCreature target, AbstractCreature source) {
        if (powerToApply.ID.equals(HeatPower.POWER_ID)) {
            addToBot(new DamageAction(target, new DamageInfo(AbstractDungeon.player, DAMAGE, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
        }
        if (powerToApply.ID.equals(ColdPower.POWER_ID)) {
            addToBot(new GainBlockAction(AbstractDungeon.player, BLOCK));
        }
        return true;
    }

    @Override
    public AbstractRelic makeCopy() {
        return new TwinBlades();
    }
}