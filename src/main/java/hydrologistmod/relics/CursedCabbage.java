package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class CursedCabbage extends CustomRelic {
    public static final String ID = "hydrologistmod:CursedCabbage";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/CursedCabbage.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/CursedCabbageOutline.png");
    private static final int DAMAGE = 10;
    private static final int SIZE_REQUIREMENT = 10;

    public CursedCabbage() {
        super(ID, IMG, OUTLINE, RelicTier.RARE, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + SIZE_REQUIREMENT + DESCRIPTIONS[1] + DAMAGE + DESCRIPTIONS[2];
    }

    @Override
    public void atTurnStartPostDraw() {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                isDone = true;
                addToBot(new AbstractGameAction() {
                    @Override
                    public void update() {
                        isDone = true;
                        if (AbstractDungeon.player.hand.size() >= SIZE_REQUIREMENT) {
                            addToTop(new DamageAllEnemiesAction(AbstractDungeon.player, DamageInfo.createDamageMatrix(DAMAGE, true), DamageInfo.DamageType.THORNS, AttackEffect.NONE));
                        }
                    }
                });
            }
        });
    }

    @Override
    public AbstractRelic makeCopy() {
        return new CursedCabbage();
    }
}