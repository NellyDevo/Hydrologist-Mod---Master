package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import hydrologistmod.character.HydrologistCharacter;

public class FreezeEffect extends AbstractGameEffect {
    private Vector2 startPosition;
    private Vector2 targetPosition;
    private Vector2 drawPosition;
    private float monsterX;
    private float EFFECT_DURATION;
    private float effectDuration;
    private HydrologistCharacter player;
    public boolean doDamage = false;
    public boolean goTime = false;
    private boolean soundPlayed = false;

    public FreezeEffect(Vector2 startPosition, Vector2 targetPosition, float monsterX, float duration, HydrologistCharacter player) {
        this.startPosition = startPosition.cpy();
        this.targetPosition = targetPosition.cpy();
        this.monsterX = monsterX;
        this.player = player;
        drawPosition = startPosition.cpy();
        effectDuration = EFFECT_DURATION = duration;
    }

    @Override
    public void update() {
        if (goTime) {
            if (!soundPlayed) {
                CardCrawlGame.sound.play("hydrologistmod:CRYSTAL_SPEAR");
                soundPlayed = true;
            }
            effectDuration -= Gdx.graphics.getDeltaTime();
            drawPosition.x = Interpolation.linear.apply(targetPosition.x, startPosition.x, effectDuration / EFFECT_DURATION);
            drawPosition.y = Interpolation.linear.apply(targetPosition.y, startPosition.y, effectDuration / EFFECT_DURATION);
            if (drawPosition.x >= monsterX) {
                doDamage = true;
            }
            if (effectDuration <= 0.0f) {
                isDone = true;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        player.waterbending.renderCaptured(sb, drawPosition);
    }

    @Override
    public void dispose() {

    }
}
