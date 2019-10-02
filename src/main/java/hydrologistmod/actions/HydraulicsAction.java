package hydrologistmod.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HydraulicsAction extends AbstractGameAction {
    private static final String ID = "hydrologistmod:HydraulicsAction";
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    private static final float DURATION = Settings.ACTION_DUR_FAST;
    private AbstractMonster monster;

    public HydraulicsAction(AbstractMonster monster) {
        duration = DURATION;
        this.monster = monster;
    }

    @Override
    public void update() {
        if (monster != null && monster.getIntentBaseDmg() >= 0) {
            AbstractDungeon.actionManager.addToTop(new FlowAction());
        }
    }
}