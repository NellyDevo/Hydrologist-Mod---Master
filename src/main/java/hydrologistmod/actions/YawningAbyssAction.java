package hydrologistmod.actions;

import com.badlogic.gdx.Gdx;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.powers.ColdPower;
import hydrologistmod.vfx.YawningAbyssEffect;

public class YawningAbyssAction extends AbstractGameAction {
    private int times;
    private DamageInfo info;
    public boolean doingDamage = false;
    private boolean initialized = false;
    private YawningAbyssEffect child;
    private static final float DAMAGE_INTERVAL = 0.1f;
    private static float damageInterval = DAMAGE_INTERVAL;

    public YawningAbyssAction(AbstractMonster target, DamageInfo info, float x, float y, float scale) {
        this.target = target;
        source = AbstractDungeon.player;
        times = 1;
        this.info = info;
        child = new YawningAbyssEffect(x, y, this, scale);
    }

    @Override
    public void update() {
        if (shouldCancelAction()) {
            isDone = true;
            if (child != null) {
                child.finish();
            }
        }
        if (!initialized) {
            if (target.hasPower(ColdPower.POWER_ID)) {
                times += target.getPower(ColdPower.POWER_ID).amount;
            }
            AbstractDungeon.effectList.add(child);
            initialized = true;
        }
        if (doingDamage) {
            damageInterval -= Gdx.graphics.getDeltaTime();
            if (damageInterval <= 0.0f) {
                target.damage(info);
                times -= 1;
                if (times == 0) {
                    isDone = true;
                    child.finish();
                }
                damageInterval = DAMAGE_INTERVAL;
            }
        }
    }

    public void startDamage() {
        doingDamage = true;
    }
}