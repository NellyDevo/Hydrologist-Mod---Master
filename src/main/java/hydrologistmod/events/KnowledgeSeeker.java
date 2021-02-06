package hydrologistmod.events;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class KnowledgeSeeker extends AbstractImageEvent {
    public static final String ID = "hydrologistmod:KnowledgeSeekerLibrary";
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    public static final String NAME = eventStrings.NAME;
    public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    public static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String IMAGE = "hydrologistmod/images/events/knowledgeseeker.png";

    private static final String DIALOG_1 = DESCRIPTIONS[0];
    private static final String SLEEP_RESULT = DESCRIPTIONS[1];

    private int screenNum = 0;
    private boolean pickCard = false;
    private boolean offerCard = false;

    private static final float HP_HEAL_PERCENT = 0.33f;
    private static final float A_2_HP_HEAL_PERCENT = 0.2f;
    private int healAmt;

    public KnowledgeSeeker() {
        super(NAME, DIALOG_1, IMAGE);

        if (AbstractDungeon.ascensionLevel >= 15) {
            healAmt = MathUtils.round(AbstractDungeon.player.maxHealth * A_2_HP_HEAL_PERCENT);
        } else {
            healAmt = MathUtils.round(AbstractDungeon.player.maxHealth * HP_HEAL_PERCENT);
        }

        imageEventText.setDialogOption(OPTIONS[0]); //select from 20
        imageEventText.setDialogOption(OPTIONS[1] + healAmt + OPTIONS[2]); //heal
        imageEventText.setDialogOption(OPTIONS[5]); //transform a card
    }

    @Override
    public void update() {
        super.update();
        if (pickCard) {
            if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0).makeCopy();
                logMetricObtainCard(ID, "Read", c);
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2f, Settings.HEIGHT / 2f));
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
        }
        if (offerCard) {
            if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                AbstractDungeon.player.masterDeck.removeCard(c);
                AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);
                AbstractCard transCard = AbstractDungeon.getTransformedCard();
                AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(transCard, c.current_x, c.current_y));
                AbstractEvent.logMetricTransformCard(ID, "Offer", c, transCard);
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
            }
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (screenNum == 0) {
            switch (buttonPressed) {
                case 0:
                    imageEventText.updateBodyText(getBook());
                    screenNum = 1;
                    imageEventText.updateDialogOption(0, OPTIONS[3]);
                    imageEventText.clearRemainingOptions();
                    pickCard = true;
                    CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);
                    AbstractCard card;

                    for (int i = 0; i < 20; i++) {
                        card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();

                        boolean containsDupe = true;
                        while (containsDupe) {
                            containsDupe = false;

                            for (AbstractCard c : group.group) {
                                if (c.cardID.equals(card.cardID)) {
                                    containsDupe = true;
                                    card = AbstractDungeon.getCard(AbstractDungeon.rollRarity()).makeCopy();
                                    break;
                                }
                            }
                        }

                        if (!group.contains(card)) {
                            for (AbstractRelic r : AbstractDungeon.player.relics) {
                                r.onPreviewObtainCard(card);
                            }
                            group.addToBottom(card);
                        } else {
                            i--;
                        }
                    }

                    for (AbstractCard c : group.group) {
                        UnlockTracker.markCardAsSeen(c.cardID);
                    }
                    AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[4], false);
                    break;
                case 2:
                    imageEventText.updateBodyText(DESCRIPTIONS[5]);
                    screenNum = 1;
                    imageEventText.updateDialogOption(0, OPTIONS[3]);
                    imageEventText.clearRemainingOptions();
                    offerCard = true;
                    if (CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards())
                            .size() > 0) {
                        AbstractDungeon.gridSelectScreen.open(
                                CardGroup.getGroupWithoutBottledCards(
                                        AbstractDungeon.player.masterDeck.getPurgeableCards()),
                                1,
                                OPTIONS[6],
                                false,
                                true,
                                false,
                                false);
                    }
                    break;
                default:
                    imageEventText.updateBodyText(SLEEP_RESULT);
                    AbstractDungeon.player.heal(healAmt, true);
                    logMetricHeal(ID, "Heal", healAmt);
                    screenNum = 1;
                    imageEventText.updateDialogOption(0, OPTIONS[3]);
                    imageEventText.clearRemainingOptions();
                    break;
            }
        } else {
            openMap();
        }
    }

    private String getBook() {
        ArrayList<String> list = new ArrayList<>();
        list.add(DESCRIPTIONS[2]);
        list.add(DESCRIPTIONS[3]);
        list.add(DESCRIPTIONS[4]); //TODO?
        return list.get(MathUtils.random(list.size() - 1));
    }
}
