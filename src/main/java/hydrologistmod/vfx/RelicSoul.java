package hydrologistmod.vfx;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.combat.EmpowerEffect;

public class RelicSoul extends Soul {
    public static final CardGroup dummyGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
    public AbstractRelic targetRelic;

    public RelicSoul() {
        super();
    }

    @Override
    public void update() {
        super.update();
        if (isDone && targetRelic != null && group == dummyGroup) {
            AbstractDungeon.effectList.add(new EmpowerEffect(targetRelic.hb.cX, targetRelic.hb.cY));
            targetRelic.flash();
            targetRelic = null;
        }
    }
}
