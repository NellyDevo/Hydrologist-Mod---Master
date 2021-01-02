package hydrologistmod.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import hydrologistmod.HydrologistMod;
import hydrologistmod.character.HydrologistCharacter;
import hydrologistmod.vfx.FreezeEffect;
import hydrologistmod.vfx.HydrologistWaterbendingManager;

import java.util.ArrayList;

public class FreezeAction extends AbstractGameAction {
    private DamageInfo info;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractCard card;
    private HydrologistCharacter player = null;
    private boolean effectFinished = false;
    private static float TRAVEL_DURATION = 0.1F;
    private float travelDuration = TRAVEL_DURATION;
    private float SHAPE_DURATION = HydrologistWaterbendingManager.SPLINE_LENGTH;
    private float shapeDuration = SHAPE_DURATION;
    private float RETURN_DURATION = HydrologistWaterbendingManager.SPLINE_LENGTH;
    private float returnDuration = RETURN_DURATION;
    private static float ATTACK_DURATION = 0.3F;
    private Vector2 startPosition;
    private Vector2 targetPosition;
    private Vector2 tracePosition;
    private Vector2 attackTargetPosition;
    private float effectWidth = 200.0f * Settings.scale;
    private float effectHeight = 100.0f * Settings.scale;
    private FreezeEffect effect;
    private boolean didDamage = false;

    public FreezeAction(AbstractCreature target, DamageInfo info) {
        this.setValues(target, this.info = info);
        actionType = ActionType.DAMAGE;
        duration = DURATION;
        if (info.owner instanceof HydrologistCharacter) {
            player = (HydrologistCharacter)info.owner;
            startPosition = new Vector2(player.waterCoords);
            targetPosition = new Vector2(player.hb.cX + (200.0f * Settings.scale), player.hb.cY);
            tracePosition = targetPosition.cpy();
            attackTargetPosition = new Vector2(Settings.WIDTH + (200.0f * Settings.scale), target.hb.cY);
        }
    }

    private Vector2 interpolate(float alpha) {
        Vector2 retVal = new Vector2();
        retVal.x = Interpolation.linear.apply(targetPosition.x, startPosition.x, alpha);
        retVal.y = Interpolation.linear.apply(targetPosition.y, startPosition.y, alpha);
        return retVal;
    }
    
    @Override
    public void update() {
        if (player != null && !effectFinished) {
            if (travelDuration > 0.0F) {
                Vector2 interPos = interpolate(travelDuration / TRAVEL_DURATION);
                player.waterbending.override = interPos.cpy();
                travelDuration -= Gdx.graphics.getDeltaTime();
                if (travelDuration <= 0.0F) {
                    startPosition = interPos.cpy();
                    targetPosition.x = tracePosition.x - (effectWidth / 4.0f) + (HydrologistWaterbendingManager.LINE_WIDTH * 2);
                    targetPosition.y = tracePosition.y + (effectHeight / 2.0f) - (HydrologistWaterbendingManager.LINE_WIDTH * 2);
                }
            } else if (shapeDuration > 0.0F) {

                //separate the duration into 4 segments
                if (shapeDuration > (SHAPE_DURATION / 4.0f) * 3.0f) {
                    Vector2 interPos = interpolate((shapeDuration - ((SHAPE_DURATION / 4.0f) * 3.0f)) / (SHAPE_DURATION / 4.0f));
                    player.waterbending.override = interPos.cpy();
                    shapeDuration -= Gdx.graphics.getDeltaTime();
                    if (shapeDuration <= (SHAPE_DURATION / 4.0f) * 3.0f) {
                        startPosition = interPos.cpy();
                        targetPosition.x = tracePosition.x + ((effectWidth / 4.0f) * 3.0f) - (HydrologistWaterbendingManager.LINE_WIDTH * 2);
                        targetPosition.y = tracePosition.y;
                    }

                //second segment
                } else if (shapeDuration > (SHAPE_DURATION / 4.0f) * 2.0f) {
                    Vector2 interPos = interpolate((shapeDuration - (SHAPE_DURATION / 2.0f)) / (SHAPE_DURATION / 4.0f));
                    player.waterbending.override = interPos.cpy();
                    shapeDuration -= Gdx.graphics.getDeltaTime();
                    if (shapeDuration <= (SHAPE_DURATION / 4.0f) * 2.0f) {
                        startPosition = interPos.cpy();
                        targetPosition.x = tracePosition.x - (effectWidth / 4.0f) + (HydrologistWaterbendingManager.LINE_WIDTH * 2);
                        targetPosition.y = tracePosition.y - (effectHeight / 2.0f) + (HydrologistWaterbendingManager.LINE_WIDTH * 2);
                    }

                //third segment
                } else if (shapeDuration > SHAPE_DURATION / 4.0f) {
                    Vector2 interPos = interpolate((shapeDuration - (SHAPE_DURATION / 4.0f)) / (SHAPE_DURATION / 4.0f));
                    player.waterbending.override = interPos.cpy();
                    shapeDuration -= Gdx.graphics.getDeltaTime();
                    if (shapeDuration <= SHAPE_DURATION / 4.0f) {
                        startPosition = interPos.cpy();
                        targetPosition.x = tracePosition.x;
                        targetPosition.y = tracePosition.y;
                    }

                //final segment
                } else {
                    Vector2 interPos = interpolate(shapeDuration / (SHAPE_DURATION / 4.0f));
                    player.waterbending.override = interPos.cpy();
                    shapeDuration -= Gdx.graphics.getDeltaTime();
                    if (shapeDuration <= 0.0f) {
                        startPosition = interPos.cpy();
                        player.waterbending.doCapture = true;
                        effect = new FreezeEffect(tracePosition, attackTargetPosition, target.hb.cX, ATTACK_DURATION, player);
                        AbstractDungeon.effectList.add(effect);
                    }
                }
            } else if (returnDuration > 0.0f) {
                targetPosition = player.waterCoords;
                Vector2 interPos = interpolate(returnDuration / RETURN_DURATION);
                player.waterbending.override = interPos.cpy();
                returnDuration -= Gdx.graphics.getDeltaTime();
                if (returnDuration <= 0.0f) {
                    effect.goTime = true;
                }
            } else if (effect.doDamage) {
                effectFinished = true;
            }
            return;
        }
        if (!didDamage && target != null) {
            didDamage = true;
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(target.hb.cX, target.hb.cY, AttackEffect.NONE));
            target.damage(info);
            if ((((AbstractMonster)target).isDying || target.currentHealth <= 0) && !target.halfDead && HydrologistMod.isCool(target)) {
                    final ArrayList<AbstractCard> possibleCards = new ArrayList<>();
                    for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                        if (c.canUpgrade()) {
                            possibleCards.add(c);
                        }
                    }
                    if (!possibleCards.isEmpty()) {
                        card = possibleCards.get(AbstractDungeon.miscRng.random(0, possibleCards.size() - 1));
                        card.upgrade();
                        AbstractDungeon.player.bottledCardUpgradeCheck(card);
                    }
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
        }
        this.tickDuration();
        if (isDone && card != null) {
            AbstractDungeon.effectsQueue.add(new UpgradeShineEffect(Settings.WIDTH / 2f, Settings.HEIGHT / 2f));
            AbstractDungeon.topLevelEffectsQueue.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy()));
            addToTop(new WaitAction(Settings.ACTION_DUR_MED));
        }
    }
}
