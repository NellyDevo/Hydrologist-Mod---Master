package hydrologistmod.powers;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.NonStackablePower;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.powers.AbstractPower;
import hydrologistmod.helpers.CardBorderGlowManager;

public abstract class AbstractStateChangePower extends AbstractPower implements NonStackablePower {
    private String uniqueID;
    protected AbstractCard.CardTags tag;

    @Override
    public void onInitialApplication() {
        uniqueID = String.valueOf(System.currentTimeMillis());
        CardBorderGlowManager.addGlowInfo(new CardBorderGlowManager.GlowInformation() {
            @Override
            public boolean test(AbstractCard card) {
                return card.hasTag(tag);
            }

            @Override
            public Color getColor(AbstractCard card) {
                return Color.RED.cpy();
            }

            @Override
            public String glowID() {
                return uniqueID;
            }
        });
    }

    @Override
    public void onVictory() {
        CardBorderGlowManager.removeGlowInfo(uniqueID);
    }

    @Override
    public void onRemove() {
        CardBorderGlowManager.removeGlowInfo(uniqueID);
    }

}
