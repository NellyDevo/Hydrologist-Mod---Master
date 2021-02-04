package hydrologistmod.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import hydrologistmod.character.HydrologistCharacter;
import hydrologistmod.patches.HydrologistTags;

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
    private static final int PARTICLES_PER_SECOND = 30;
    private static final float PARTICLE_RANGE = 30.0f;
    private float particleTimer = 0.0f;
    private ArrayList<HydrologistWaterbendingManager.Coordinates> splineCopy;
    private boolean sound = false;

    public TidalWaveEffect(Vector2 drawPosition, float travelTime, HydrologistCharacter player, ArrayList<HydrologistWaterbendingManager.Coordinates> splineCopy) {
        this.drawPosition = drawPosition.cpy();
        effectDuration = startEffectDuration = travelTime;
        startPosition = drawPosition.cpy();
        targetPosition = new Vector2(Settings.WIDTH + 200.0f, drawPosition.y);
        passed = new ArrayList<>();
        this.player = player;
        this.splineCopy = splineCopy;
    }

    @Override
    public void update() {
        if (goTime) {
            if (!sound) {
                CardCrawlGame.sound.play("hydrologistmod:TIDAL_WAVE");
                sound = true;
            }
            float time = Gdx.graphics.getDeltaTime();
            effectDuration -= time;
            drawPosition.x = Interpolation.linear.apply(targetPosition.x, startPosition.x, effectDuration / startEffectDuration);
            drawPosition.y = Interpolation.linear.apply(targetPosition.y, startPosition.y, effectDuration / startEffectDuration);
            for (AbstractMonster mon : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!passed.contains(mon) && drawPosition.x > mon.hb.cX) {
                    passed.add(mon);
                }
            }
            particleTimer += time;
            while (particleTimer >= 1.0f / PARTICLES_PER_SECOND) {
                HydrologistWaterbendingManager.Coordinates coords = splineCopy.get(AbstractDungeon.miscRng.random(splineCopy.size() - 1)).cpy();
                coords.x += (drawPosition.x - startPosition.x);
                coords.x += AbstractDungeon.miscRng.random(-PARTICLE_RANGE, PARTICLE_RANGE);
                coords.y += AbstractDungeon.miscRng.random(-PARTICLE_RANGE, PARTICLE_RANGE);
                float rotation = AbstractDungeon.miscRng.random(0.0f, 360.0f);
                float scale = AbstractDungeon.miscRng.random(0.8f, 1.2f);
                AbstractDungeon.effectsQueue.add(new HydrologistParticle(HydrologistTags.WATER, coords.x, coords.y, rotation, scale));
                particleTimer -= 1.0f / PARTICLES_PER_SECOND;
            }
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
