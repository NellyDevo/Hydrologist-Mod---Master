package hydrologistmod.cards;

import basemod.abstracts.AbstractCardModifier;
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
                        incrementDamage(masterDeckCard, amount);
                    }
                    incrementDamage(card, amount);
                }
                isDone = true;
            }
        });
        addToBot(new HydrologistDamageAction(getHydrologistSubtype(), m, new DamageInfo(p, damage, damageTypeForTurn)));
    }

    private void incrementDamage(AbstractCard card, int amt) {
        if (CardModifierManager.hasModifier(card, HeatBlastModifier.ID)) {
            ((HeatBlastModifier) CardModifierManager.getModifiers(card, HeatBlastModifier.ID).get(0)).amount += amt;
            card.initializeDescription();
        } else {
            CardModifierManager.addModifier(card, new HeatBlastModifier(amt));
        }
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

    @Override
    public void update() {
        super.update();
    }

    public static class HeatBlastModifier extends AbstractCardModifier {
        public int amount;
        public static final String ID = "hydrologistmod:HeatBlastModifier";

        public HeatBlastModifier(int amount) {
            this.amount = amount;
        }

        @Override
        public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
            return damage + amount;
        }

        @Override
        public String modifyDescription(String rawDescription, AbstractCard card) {
            return rawDescription + EXTENDED_DESCRIPTION[0] + amount + EXTENDED_DESCRIPTION[1];
        }

        @Override
        public String identifier(AbstractCard card) {
            return ID;
        }

        @Override
        public AbstractCardModifier makeCopy() {
            return new HeatBlastModifier(amount);
        }
    }
}
