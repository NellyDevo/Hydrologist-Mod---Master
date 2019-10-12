package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

public class YawningAbyssAction extends AbstractGameAction {
//    private static final String ID = "hydrologistmod:YawningAbyssAction";
//    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
//    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractMonster m;
    private int times;
    private DamageInfo info;

    public YawningAbyssAction(AbstractMonster target, DamageInfo info) {
        this.duration = DURATION;
        m = target;
        times = 1;
        this.info = info;
    }

    @Override
    public void update() {
        if (m.hasPower(WeakPower.POWER_ID)) {
            times += m.getPower(WeakPower.POWER_ID).amount;
        }
        for (int i = 0; i < times; ++i) {
            AbstractDungeon.actionManager.addToTop(new DamageAction(m, info));
        }
        isDone = true;
    }
}