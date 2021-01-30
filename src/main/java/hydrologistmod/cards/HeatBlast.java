package hydrologistmod.cards;

import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import hydrologistmod.actions.HydrologistDamageAction;
import hydrologistmod.cardmods.HeatBlastModifier;
import hydrologistmod.patches.AbstractCardEnum;
import hydrologistmod.patches.HydrologistTags;
import hydrologistmod.powers.HeatPower;

public class HeatBlast extends AbstractHydrologistCard {
    public static final String ID = "hydrologistmod:HeatBlast";
    public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    public static final String IMG_PATH = "hydrologistmod/images/cards/HeatBlast.png";
    private static final int COST = 3;
    private static final int DAMAGE_AMT = 5;
    private static final int UPGRADED_COST = 2;

    public HeatBlast() {
        super(ID, NAME, IMG_PATH, COST, DESCRIPTION,
                CardType.ATTACK, AbstractCardEnum.HYDROLOGIST_CYAN,
                CardRarity.UNCOMMON, CardTarget.ENEMY);
        assignHydrologistSubtype(HydrologistTags.STEAM);
        damage = baseDamage = DAMAGE_AMT;
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        AbstractCard card = this;
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (m.hasPower(HeatPower.POWER_ID)) {
                    AbstractCard masterDeckCard = StSLib.getMasterDeckEquivalent(card);
                    int amount = m.getPower(HeatPower.POWER_ID).amount;
                    if (masterDeckCard != null) {
                        CardModifierManager.addModifier(card, new HeatBlastModifier(amount));
                    }
                    CardModifierManager.addModifier(card, new HeatBlastModifier(amount));
                }
                isDone = true;
            }
        });
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
    }

    @Override
    public AbstractCard makeCopy() {
        return new HeatBlast();
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADED_COST);
        }
    }
}
