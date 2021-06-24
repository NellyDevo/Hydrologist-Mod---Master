package hydrologistmod.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import hydrologistmod.cards.AbstractAdaptiveCard;
import hydrologistmod.cards.AbstractHydrologistCard;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.patches.AbstractCardEnum;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CardExporter {
    public static HashMap<String, CardInfo> cards;
    private static StringBuilder stringBuilder;

    public static void initialize() {
        cards = new HashMap<>();
        stringBuilder = new StringBuilder();
    }

    public static void getCardData() {
        System.out.println("Retrieving card data --");
        for (AbstractCard card : CardLibrary.getAllCards()) {
            if (card.color == AbstractCardEnum.HYDROLOGIST_CYAN) {
                AbstractHydrologistCard copy = (AbstractHydrologistCard) card.makeCopy();
                CardInfo info = new CardInfo();
                info.NAME = copy.name;
                info.TYPE = copy.type.toString();
                AbstractCard.CardTags subtype = copy.getHydrologistSubtype();
                if (subtype != null) {
                    info.SUBTYPE = subtype.toString();
                } else {
                    info.SUBTYPE = null;
                }

                info.COST = copy.cost;
                for (DescriptionLine line : copy.description) {
                    info.DESCRIPTION += line.text + " <br> ";
                }
                ArrayList<String> tmp = new ArrayList<>(copy.keywords);
                String tmpDesc = copy.rawDescription;
                String beforeParse = info.DESCRIPTION;
                parseDescription(info, copy);

                copy.upgrade();

                info.UPGRADED_COST = copy.cost;
                if (!copy.rawDescription.equals(tmpDesc)) {
                    for (DescriptionLine line : copy.description) {
                        info.UPGRADED_DESCRIPTION += line.text + " <br> ";
                    }
                } else {
                    info.UPGRADED_DESCRIPTION = beforeParse;
                }
                for (String keyword : copy.keywords) {
                    boolean add = true;
                    for (String key : tmp) {
                        if (key.equals(keyword)) {
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        tmp.add(keyword);
                    }
                }
                info.KEYWORDS = new String[tmp.size()];
                for (int i = 0; i < tmp.size(); ++i) {
                    info.KEYWORDS[i] = tmp.get(i);
                }

                parseDescription(info, copy);

                if (SwapperHelper.isCardSwappable(card)) {
                    info.SWAPS_TO = SwapperHelper.getNextCard(card).cardID;
                }

                cards.put(copy.cardID, info);
                System.out.println("PARSED: " + info.NAME);
            }
        }
    }

    private static void parseDescription(CardInfo info, AbstractHydrologistCard card) {
        AbstractCard cpy = card.makeCopy();
        stringBuilder.setLength(0);
        String desc;
        if (card.upgraded) {
            desc = info.UPGRADED_DESCRIPTION;
        } else {
            desc = info.DESCRIPTION;
        }
        for (String word : desc.split(" ")) {
            word = word.trim();
            if (word.equals("!D!")) {
                word = String.valueOf(card.baseDamage);
                if (card.baseDamage != cpy.baseDamage) {
                    word = "<span class=\"upgrade\">" + word + "</span>";
                }
            }
            if (word.equals("!B!")) {
                word = String.valueOf(card.baseBlock);
                if (card.baseBlock != cpy.baseBlock) {
                    word = "<span class=\"upgrade\">" + word + "</span>";
                }
            }
            if (word.equals("!M!")) {
                word = String.valueOf(card.baseMagicNumber);
                if (card.baseMagicNumber != cpy.baseMagicNumber) {
                    word = "<span class=\"upgrade\">" + word + "</span>";
                }
            }
            if (word.equals("!hydrologistmod:A!")) {
                word = String.valueOf(((AbstractAdaptiveCard)card).baseAdaptiveNumber);
                if (((AbstractAdaptiveCard)card).baseAdaptiveNumber != ((AbstractAdaptiveCard)cpy).baseAdaptiveNumber) {
                    word = "<span class=\"upgrade\">" + word + "</span>";
                }
            }
            if (word.equals("[E]")) {
                word = "<img src=\"/mana-symbol.png\" class=\"mana-symbol\">";
            }
            if (word.startsWith("*")) {
                word = word.replace("*", "");
                word = "<span class=\"keyword\">" + word + "</span>";
            }
            stringBuilder.append(word);
            stringBuilder.append(" ");
        }
        String result = stringBuilder.toString();
        result = result.substring(0, result.length() - 6); //cut off the extra " <br> "
        if (card.upgraded) {
            info.UPGRADED_DESCRIPTION = result;
        } else {
            info.DESCRIPTION = result;
        }
    }

    public static void exportToJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try {
            FileWriter writer = new FileWriter("hydro-cards.json");
            gson.toJson(cards, writer);
            writer.close();
            System.out.println("Successfully exported to hydro-cards.json");
        } catch (IOException e) {
            System.out.println("Exception occured when creating output file:");
            e.printStackTrace();
        }
        cards = null;
        stringBuilder = null;
    }
}
