package hydrologistmod.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.character.HydrologistCharacter;

public class IceSpikeAction extends AbstractGameAction {
    private HydrologistCharacter player;
    private DamageInfo info;
    private static final float TRAVEL_DURATION = 0.3f;
    private static final float HANG_DURATION = 0.3f;
    private static final float SLASH_DURATION = 0.1f;
    private static final float RETURN_DURATION = 0.3f;
    private float travelDuration = TRAVEL_DURATION;
    private float hangDuration = HANG_DURATION;
    private float slashDuration = SLASH_DURATION;
    private float returnDuration = RETURN_DURATION;
    private Vector2 startPosition;
    private Vector2 currentPosition;
    private Vector2 targetPosition;
    private boolean didDamage = false;

    public IceSpikeAction(AbstractCreature source, AbstractMonster target, DamageInfo info) {
        if (source instanceof HydrologistCharacter) {
            player = (HydrologistCharacter)source;
        }
        this.source = source;
        this.target = target;
        this.info = info;
    }

    @Override
    public void update() {
        if (player == null) {
            addToTop(new DamageAction(target, info));
            isDone = true;
            return;
        }
        if (shouldCancelAction()) {
            isDone = true;
            return;
        }
        if (startPosition == null) {
            startPosition = player.waterCoords.cpy();
            currentPosition = startPosition.cpy();
            targetPosition = new Vector2(target.hb.cX, target.hb.cY + target.hb.height);
        }
        float time = Gdx.graphics.getDeltaTime();
        if (travelDuration > 0.0f) {
            //go to above monster
            float alpha = 1.0f - (travelDuration / TRAVEL_DURATION);
            currentPosition.x = Interpolation.exp5In.apply(startPosition.x, targetPosition.x, alpha);
            currentPosition.y = Interpolation.exp5Out.apply(startPosition.y, targetPosition.y, alpha);
            player.waterbending.override(currentPosition);
            travelDuration -= time;
            if (travelDuration <= 0.0f) {
                startPosition = currentPosition.cpy();
                targetPosition = new Vector2(target.hb.cX, target.hb.cY - target.hb.height);
            }
        } else if (hangDuration > 0.0f) {
            //override the same spot every frame
            player.waterbending.override(currentPosition);
            hangDuration -= time;
            if (hangDuration <= 0.0f) {
                CardCrawlGame.sound.play("hydrologistmod:ICE_SPIKE");
            }
        } else if (slashDuration > 0.0f) {
            //go to below monster
            float alpha = 1.0f - (slashDuration / SLASH_DURATION);
            currentPosition.x = Interpolation.linear.apply(startPosition.x, targetPosition.x, alpha);
            currentPosition.y = Interpolation.linear.apply(startPosition.y, targetPosition.y, alpha);
            if (!didDamage && currentPosition.x < target.hb.cX) {
                target.damage(info);
                didDamage = true;
            }
            player.waterbending.override(currentPosition);
            slashDuration -= time;
            if (slashDuration <= 0.0f) {
                startPosition = currentPosition.cpy();
                if (!didDamage) {
                    target.damage(info);
                    didDamage = true;
                }
            }
        } else if (returnDuration > 0.0f) {
            //return to default position
            float alpha = 1.0f - (returnDuration / RETURN_DURATION);
            currentPosition.x = Interpolation.linear.apply(startPosition.x, targetPosition.x, alpha);
            currentPosition.y = Interpolation.linear.apply(startPosition.y, targetPosition.y, alpha);
            player.waterbending.override(currentPosition);
            returnDuration -= time;
            if (returnDuration <= 0.0f) {
                isDone = true;
            }
        }
    }
}
