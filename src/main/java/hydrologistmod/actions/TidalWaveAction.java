package hydrologistmod.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.character.HydrologistCharacter;
import hydrologistmod.vfx.HydrologistWaterbendingManager;
import hydrologistmod.vfx.TidalWaveEffect;

import java.util.ArrayList;
import java.util.HashMap;

public class TidalWaveAction extends AbstractGameAction {
    private HydrologistCharacter player = null;
    private int[] amount;
    private DamageInfo.DamageType type;
    private TidalWaveEffect child;
    public HashMap<AbstractMonster, DamageInfo> damageMap = null;
    private static final int SPEED_MULTIPLIER = 4;
    private static float TRAVEL_DURATION = 0.1F;
    private float travelDuration = TRAVEL_DURATION;
    private float SHAPE_DURATION = HydrologistWaterbendingManager.SPLINE_LENGTH;
    private float shapeDuration = SHAPE_DURATION;
    private float RETURN_DURATION = HydrologistWaterbendingManager.SPLINE_LENGTH;
    private float returnDuration = RETURN_DURATION;
    private static final float ATTACK_DURATION = 0.3F;
    private Vector2 startPosition;
    private Vector2 targetPosition;
    private Vector2 currentPosition;
    private Vector2 drawPosition;
    private float effectWidth = 300.0f * Settings.scale;
    private float effectHeight = 600.0f * Settings.scale;

    public TidalWaveAction(AbstractCreature source, int[] amount, DamageInfo.DamageType type) {
        if (source instanceof HydrologistCharacter) {
            player = (HydrologistCharacter)source;
        } else {
            this.source = source;
        }
        this.amount = amount;
        this.type = type;
        drawPosition = new Vector2(source.hb.cX + effectWidth, source.hb.cY - (effectHeight / 2.0f));
    }

    @Override
    public void update() {
        if (player == null) {
            isDone = true;
            addToTop(new DamageAllEnemiesAction(source, amount, type, AttackEffect.NONE));
        } else {
            //initialize damage matrix
            if (damageMap == null) {
                damageMap = new HashMap<>();
                ArrayList<AbstractMonster> mon = AbstractDungeon.getCurrRoom().monsters.monsters;
                for (int i = 0; i < mon.size(); ++i) {
                    if (!mon.get(i).isDeadOrEscaped()) {
                        damageMap.put(mon.get(i), new DamageInfo(player, amount[i], type));
                    }
                }
                startPosition = player.waterCoords.cpy();
                targetPosition = new Vector2(
                        drawPosition.x + (HydrologistWaterbendingManager.LINE_WIDTH * 2),
                        drawPosition.y + (HydrologistWaterbendingManager.LINE_WIDTH * 2));
                currentPosition = new Vector2();
            }

            //if drawing
            float time = Gdx.graphics.getDeltaTime();
            for (int i = 0; i < SPEED_MULTIPLIER; ++i) {
                if (travelDuration > 0.0f) {
                    //override spline position to travel towards draw start
                    float alpha = travelDuration / TRAVEL_DURATION;
                    currentPosition.x = Interpolation.linear.apply(targetPosition.x, startPosition.x, alpha);
                    currentPosition.y = Interpolation.linear.apply(targetPosition.y, startPosition.y, alpha);
                    player.waterbending.override(currentPosition);
                    travelDuration -= time;
                    if (travelDuration <= 0.0f) {
                        startPosition = currentPosition.cpy();
                        targetPosition = new Vector2(
                                drawPosition.x + effectWidth - (HydrologistWaterbendingManager.LINE_WIDTH * 2),
                                drawPosition.y + effectHeight - (HydrologistWaterbendingManager.LINE_WIDTH * 2));
                    }
                } else if (shapeDuration > 0.0f) {
                    //override spline position to travel along tidal wave shape
                    float alpha = 1.0f - (shapeDuration / SHAPE_DURATION);
                    currentPosition.x = Interpolation.circleIn.apply(startPosition.x, targetPosition.x, alpha);
                    currentPosition.y = Interpolation.circleOut.apply(startPosition.y, targetPosition.y, alpha);
                    player.waterbending.override(currentPosition);
                    shapeDuration -= time;
                    //when shape is drawn, snapshot the bending and create the child effect
                    if (shapeDuration <= 0.0f) {
                        player.waterbending.doCapture(drawPosition.x, drawPosition.y, effectWidth, effectHeight);
                        child = new TidalWaveEffect(drawPosition, ATTACK_DURATION, player, player.waterbending.captureSpline());
                        AbstractDungeon.effectList.add(child);
                        startPosition = currentPosition.cpy();
                    }
                } else if (returnDuration > 0.0f) {
                    //override spline position to travel back towards player waterbending position
                    float alpha = returnDuration / RETURN_DURATION;
                    currentPosition.x = Interpolation.linear.apply(player.waterCoords.x, startPosition.x, alpha);
                    currentPosition.y = Interpolation.linear.apply(player.waterCoords.y, startPosition.y, alpha);
                    player.waterbending.override(currentPosition);
                    returnDuration -= time;
                    if (returnDuration <= 0.0f) {
                        child.goTime = true;
                    }
                }
            }

            //else if waiting for damage:
            if (child != null) {
                if (damageMap.isEmpty()) {
                    isDone = true;
                } else {
                    for (AbstractMonster mon : child.passed) {
                        if (damageMap.containsKey(mon)) {
                            mon.damage(damageMap.get(mon));
                            damageMap.remove(mon);
                        }
                    }
                }
            }
        }
    }
}
