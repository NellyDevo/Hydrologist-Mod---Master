package hydrologistmod.credits;

import basemod.Pair;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.ShopRoom;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.shop.Merchant;
import hydrologistmod.helpers.SwapperHelper;
import hydrologistmod.patches.SwapperCardPatch;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreditsHelper {
    private static HashMap<String, Pair<ArrayList<CreditsInfo>, String>> creditedArts = new HashMap<>();

    public static void openBrowser(String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    System.out.println("Error occurred when trying to open a webpage");
                    e.printStackTrace();
                }
            } else {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url);
                } catch (IOException e) {
                    System.out.println("Error occurred when trying to open a webpage");
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isArtCredited(String cardID) {
        return creditedArts.containsKey(cardID);
    }

    public static CreditsInfo getCurrentInfo(String cardID) {
        String currentID = creditedArts.get(cardID).getValue();
        ArrayList<CreditsInfo> infos = creditedArts.get(cardID).getKey();
        for (CreditsInfo info : infos) {
            if (info.getCreditsID().equals(currentID)) {
                return info;
            }
        }
        System.out.println("CreditsHelper: ALERT: " + cardID + " has no valid current art set. Setting to index 0.");
        CreditsInfo info = creditedArts.get(cardID).getKey().get(0);
        creditedArts.put(cardID, new Pair<>(infos, info.getCreditsID()));
        return creditedArts.get(cardID).getKey().get(0);
    }

    public static CreditsInfo getDefaultInfo(String cardID) {
        ArrayList<CreditsInfo> infos = creditedArts.get(cardID).getKey();
        for (CreditsInfo info : infos) {
            if (info.getCreditsID().equals(CreditsInfo.DEFAULT_ID)) {
                return info;
            }
        }
        CreditsInfo newDefault = new CreditsInfo(cardID);
        infos.add(newDefault);
        return newDefault;
    }

    public static void setCurrentArt(String cardID, String artID, SingleCardViewPopup cardViewPopup) {
        String currentID = creditedArts.get(cardID).getValue();
        if (artID.equals(currentID)) {
            return;
        }
        ArrayList<CreditsInfo> infos = creditedArts.get(cardID).getKey();
        for (CreditsInfo info : infos) {
            if (info.getCreditsID().equals(artID)) {
                creditedArts.put(cardID, new Pair<>(infos, artID));
                String imgUrl = null;
                TextureAtlas.AtlasRegion region = null;
                if (artID.equals(CreditsInfo.DEFAULT_ID)) {
                     region = getDefaultInfo(cardID).defaultSmallImage;
                } else {
                     imgUrl = info.getImgPath();
                }
                ArrayList<AbstractCard> allInstances = getAllExistingCards(cardID);
                if (imgUrl != null) {
                    for (AbstractCard card : allInstances) {
                        loadImage(card, imgUrl);
                    }
                } else if (region != null) {
                    for (AbstractCard card : allInstances) {
                        ReflectionHacks.setPrivate(card, AbstractCard.class, "portrait", region);
                    }
                } else {
                    System.out.println("CreditsHelper: ERROR: how did you get here?");
                }
                setSingleViewScreenPortrait(cardViewPopup);
                return;
            }
        }
        System.out.println("CreditsHelper: ALERT: " + artID + " is not a valid art ID");
    }

    public static void setSingleViewScreenPortrait(SingleCardViewPopup popup) {
        String imgPath = null;
        AbstractCard card = ReflectionHacks.getPrivate(popup, SingleCardViewPopup.class, "card");
        if (CreditsHelper.isArtCredited(card.cardID)) {
            imgPath = CreditsHelper.getCurrentInfo(card.cardID).getLargeImgPath();
        }
        if (imgPath != null) {
            Texture cardTexture;
            if (CustomCard.imgMap.containsKey(imgPath)) {
                cardTexture = CustomCard.imgMap.get(imgPath);
            } else {
                cardTexture = ImageMaster.loadImage(imgPath);
                CustomCard.imgMap.put(imgPath, cardTexture);
            }
            ReflectionHacks.setPrivate(popup, SingleCardViewPopup.class, "portraitImg", cardTexture);
        } else {
            ReflectionHacks.setPrivate(popup, SingleCardViewPopup.class, "portraitImg", CreditsHelper.getDefaultInfo(card.cardID).defaultLargeImage);
        }
    }

    public static void loadImage(AbstractCard card, String imgUrl) {
        Texture cardTexture;
        if (imgUrl == null) {
            ReflectionHacks.setPrivate(card, AbstractCard.class, "portrait", getDefaultInfo(card.cardID).defaultSmallImage);
            return;
        }
        if (CustomCard.imgMap.containsKey(imgUrl)) {
            cardTexture = CustomCard.imgMap.get(imgUrl);
        } else {
            cardTexture = ImageMaster.loadImage(imgUrl);
            cardTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            CustomCard.imgMap.put(imgUrl, cardTexture);
        }
        int tw = cardTexture.getWidth();
        int th = cardTexture.getHeight();
        TextureAtlas.AtlasRegion cardImg = new TextureAtlas.AtlasRegion(cardTexture, 0, 0, tw, th);
        ReflectionHacks.setPrivate(card, AbstractCard.class, "portrait", cardImg);
    }

    public static ArrayList<AbstractCard> getAllExistingCards(String cardID) {
        ArrayList<AbstractCard> list = new ArrayList<>();
        for (AbstractCard card : CardLibrary.getAllCards()) {
            if (card.cardID.equals(cardID)) {
                list.add(card);
            }
            if (SwapperHelper.isCardSwappable(card)) {
                for (AbstractCard swapCard : SwapperCardPatch.SwappableChainField.swappableCards.get(card)) {
                    if (swapCard.cardID.equals(cardID)) {
                        list.add(swapCard);
                    }
                }
            }
        }
        if (AbstractDungeon.player != null) {
            CardGroup group = AbstractDungeon.player.drawPile;
            if (group != null) {
                for (AbstractCard card : group.group) {
                    if (SwapperHelper.isCardSwappable(card)) {
                        for (AbstractCard swapCard : SwapperCardPatch.SwappableChainField.swappableCards.get(card)) {
                            if (swapCard.cardID.equals(cardID)) {
                                list.add(swapCard);
                            }
                        }
                    } else if (card.cardID.equals(cardID)) {
                        list.add(card);
                    }
                }
            }
            group = AbstractDungeon.player.discardPile;
            if (group != null) {
                for (AbstractCard card : group.group) {
                    if (SwapperHelper.isCardSwappable(card)) {
                        for (AbstractCard swapCard : SwapperCardPatch.SwappableChainField.swappableCards.get(card)) {
                            if (swapCard.cardID.equals(cardID)) {
                                list.add(swapCard);
                            }
                        }
                    } else if (card.cardID.equals(cardID)) {
                        list.add(card);
                    }
                }
            }
            group = AbstractDungeon.player.exhaustPile;
            if (group != null) {
                for (AbstractCard card : group.group) {
                    if (SwapperHelper.isCardSwappable(card)) {
                        for (AbstractCard swapCard : SwapperCardPatch.SwappableChainField.swappableCards.get(card)) {
                            if (swapCard.cardID.equals(cardID)) {
                                list.add(swapCard);
                            }
                        }
                    } else if (card.cardID.equals(cardID)) {
                        list.add(card);
                    }
                }
            }
            group = AbstractDungeon.player.hand;
            if (group != null) {
                for (AbstractCard card : group.group) {
                    if (SwapperHelper.isCardSwappable(card)) {
                        for (AbstractCard swapCard : SwapperCardPatch.SwappableChainField.swappableCards.get(card)) {
                            if (swapCard.cardID.equals(cardID)) {
                                list.add(swapCard);
                            }
                        }
                    } else if (card.cardID.equals(cardID)) {
                        list.add(card);
                    }
                }
            }
            group = AbstractDungeon.player.masterDeck;
            if (group != null) {
                for (AbstractCard card : group.group) {
                    if (SwapperHelper.isCardSwappable(card)) {
                        for (AbstractCard swapCard : SwapperCardPatch.SwappableChainField.swappableCards.get(card)) {
                            if (swapCard.cardID.equals(cardID)) {
                                list.add(swapCard);
                            }
                        }
                    } else if (card.cardID.equals(cardID)) {
                        list.add(card);
                    }
                }
            }
            AbstractRoom room = AbstractDungeon.getCurrRoom();
            if (room != null && room.rewards != null) {
                for (RewardItem reward : room.rewards) {
                    if (reward.cards != null) {
                        for (AbstractCard card : reward.cards) {
                            if (SwapperHelper.isCardSwappable(card)) {
                                for (AbstractCard swapCard : SwapperCardPatch.SwappableChainField.swappableCards.get(card)) {
                                    if (swapCard.cardID.equals(cardID)) {
                                        list.add(swapCard);
                                    }
                                }
                            } else if (card.cardID.equals(cardID)) {
                                list.add(card);
                            }
                        }
                    }
                }
            }
            if (room instanceof ShopRoom) {
                ShopRoom shop = (ShopRoom)room;
                ArrayList<AbstractCard> cards1 = ReflectionHacks.getPrivate(shop.merchant, Merchant.class, "cards1");
                ArrayList<AbstractCard> cards2 = ReflectionHacks.getPrivate(shop.merchant, Merchant.class, "cards2");
                if (cards1 != null) {
                    for (AbstractCard card : cards1) {
                        if (SwapperHelper.isCardSwappable(card)) {
                            for (AbstractCard swapCard : SwapperCardPatch.SwappableChainField.swappableCards.get(card)) {
                                if (swapCard.cardID.equals(cardID)) {
                                    list.add(swapCard);
                                }
                            }
                        } else if (card.cardID.equals(cardID)) {
                            list.add(card);
                        }
                    }
                }
                if (cards2 != null) {
                    for (AbstractCard card : cards2) {
                        if (SwapperHelper.isCardSwappable(card)) {
                            for (AbstractCard swapCard : SwapperCardPatch.SwappableChainField.swappableCards.get(card)) {
                                if (swapCard.cardID.equals(cardID)) {
                                    list.add(swapCard);
                                }
                            }
                        } else if (card.cardID.equals(cardID)) {
                            list.add(card);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static void initialize() {
        System.out.println("CreditsHelper: INFO: parsing credited art information");
        long time = System.currentTimeMillis();
        Gson gson = new Gson();
        String path = "hydrologistmod/images/CreditedArt.json";
        Type creditType = new TypeToken<Map<String, CreditStrings>>() {}.getType();
        String jsonString = Gdx.files.internal(path).readString(String.valueOf(StandardCharsets.UTF_8));
        HashMap<String, CreditStrings> credits = gson.fromJson(jsonString, creditType);

        for (String cardID : credits.keySet()) {
            CreditStrings credit = credits.get(cardID);
            if (credit.ARTISTS.length != credit.URLS.length) {
                System.out.println("CreditsHelper: ERROR: credits for " + cardID + " are mismatched. No arts will be loaded for this card during this session");
                break;
            }
            ArrayList<CreditsInfo> infos;
            if (!isArtCredited(cardID)) {
                infos = new ArrayList<>();
                creditedArts.put(cardID, new Pair<>(infos, CreditsInfo.DEFAULT_ID));
            } else {
                infos = creditedArts.get(cardID).getKey();
            }
            for (int i = 0; i < credit.ARTISTS.length; ++i) {
                infos.add(new CreditsInfo(cardID, credit.ARTISTS[i], credit.URLS[i]));
            }
        }
        System.out.println("CreditsHelper: INFO: credited art info loaded. Time elapsed: " + (System.currentTimeMillis() - time) + "ms");
    }
}
