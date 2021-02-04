package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import hydrologistmod.character.HydrologistCharacter;

import java.util.ArrayList;

public class TidalWaveEffect extends AbstractGameEffect {
    public ArrayList<AbstractMonster> passed;
    public boolean goTime = false;
    private Vector2 drawPosition;
    private Vector2 startPosition;
    private Vector2 targetPosition;
    private float effectDuration;
    private float startEffectDuration;
    private HydrologistCharacter player;

    public TidalWaveEffect(Vector2 drawPosition, float travelTime, HydrologistCharacter player) {
        this.drawPosition = drawPosition.cpy();
        effectDuration = startEffectDuration = travelTime;
        startPosition = drawPosition.cpy();
        targetPosition = new Vector2(Settings.WIDTH + 200.0f, drawPosition.y);
        passed = new ArrayList<>();
        this.player = player;
    }

    @Override
    public void update() {
        if (goTime) {
            effectDuration -= Gdx.graphics.getDeltaTime();
            drawPosition.x = Interpolation.linear.apply(targetPosition.x, startPosition.x, effectDuration / startEffectDuration);
            drawPosition.y = Interpolation.linear.apply(targetPosition.y, startPosition.y, effectDuration / startEffectDuration);
            for (AbstractMonster mon : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!passed.contains(mon) && drawPosition.x > mon.hb.cX) {
                    passed.add(mon);
                }
            }
            //todo: spawn particles along the length of the line
            if (effectDuration <= 0.0f && passed.containsAll(AbstractDungeon.getCurrRoom().monsters.monsters)) {
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
