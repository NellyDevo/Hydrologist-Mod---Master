package hydrologistmod.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import hydrologistmod.vfx.PressureBlastEffect;

public class PressureBlastAction extends AbstractGameAction {
    public boolean beginBlasting = false;
    private PressureBlastEffect effect;
    private boolean initialized = false;
    private boolean freeToPlayOnce;
    private AbstractPlayer p;
    private AbstractMonster m;
    private DamageInfo info;
    private int energyOnUse;
    private int amount;
    private int damagesDealt = 0;
    private int damagesPerSecond = 12;
    private float durationPerDamage = damagesPerSecond / 60.0f;
    private float damageDuration = durationPerDamage;

    public PressureBlastAction(AbstractPlayer p, AbstractMonster m, int damage, DamageInfo.DamageType damageTypeForTurn, boolean freeToPlayOnce, int energyOnUse) {
        this.freeToPlayOnce = freeToPlayOnce;
        this.p = p;
        this.m = m;
        this.energyOnUse = energyOnUse;
        info = new DamageInfo(p, damage, damageTypeForTurn);
    }

    @Override
    public void update() {
        if (!initialized) {
            initialized = true;
            amount = EnergyPanel.totalCount;
            if (energyOnUse != -1) {
                amount = energyOnUse;
            }
            if (p.hasRelic(ChemicalX.ID)) {
                amount += 2;
                p.getRelic(ChemicalX.ID).flash();
            }
            if (amount > 0) {
                effect = new PressureBlastEffect(p.hb.cX, p.hb.cY, 0.5f, 1.0f, 2.0f, this);
                AbstractDungeon.effectList.add(effect);
                if (!freeToPlayOnce) {
                    p.energy.use(EnergyPanel.totalCount);
                }
            } else {
                isDone = true;
            }
        } else if (beginBlasting) {
            if (damagesDealt < amount) {
                if (damageDuration <= 0.0f) {
                    ++damagesDealt;
                    if (!m.isDeadOrEscaped()) {
                        m.tint.color.set(Color.GRAY.cpy());
                        m.tint.changeColor(Color.WHITE.cpy());
                        m.damage(info);
                    } else {
                        effect.doneBlasting = true;
                        isDone = true;
                        return;
                    }
                    damageDuration = durationPerDamage;
                } else {
                    damageDuration -= Gdx.graphics.getDeltaTime();
                }
            } else {
                effect.doneBlasting = true;
                isDone = true;
            }
        }
    }
}