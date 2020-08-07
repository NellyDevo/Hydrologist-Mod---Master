package hydrologistmod.relics;

import basemod.abstracts.CustomRelic;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class LoyalBoomerang extends CustomRelic {
    public static final String ID = "hydrologistmod:LoyalBoomerang";
    public static final Texture IMG = new Texture("hydrologistmod/images/relics/LoyalBoomerang.png");
    public static final Texture OUTLINE = new Texture("hydrologistmod/images/relics/LoyalBoomerangOutline.png");

    public LoyalBoomerang() {
        super(ID, IMG, OUTLINE, RelicTier.UNCOMMON, LandingSound.MAGICAL);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }

    @Override
    public void onPlayerEndTurn() {
        if (!AbstractDungeon.player.discardPile.isEmpty()) {
            flash();
            AbstractCard topCard = AbstractDungeon.player.discardPile.getTopCard();
            AbstractDungeon.player.discardPile.removeCard(topCard);
            AbstractDungeon.player.drawPile.moveToDeck(topCard, false);
        }
    }

    @Override
    public AbstractRelic makeCopy() {
        return new LoyalBoomerang();
    }
}