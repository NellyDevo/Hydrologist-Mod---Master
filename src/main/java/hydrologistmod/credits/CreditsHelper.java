package hydrologistmod.credits;

import basemod.Pair;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
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

public class CreditsHelper {
    private static HashMap<String, Pair<ArrayList<CreditsInfo>, String>> creditedArts = new HashMap<>();
    public static SpireConfig creditedArtSettings;
    private static final float LEFT_ARROW_X = (Settings.WIDTH / 2.0f) - (500.0f * Settings.scale);
    private static final float LEFT_ARROW_Y = (Settings.HEIGHT / 2.0f) + (300.0f * Settings.scale);
    private static final float RIGHT_ARROW_X = (Settings.WIDTH / 2.0f) + (500.0f * Settings.scale);
    private static final float RIGHT_ARROW_Y = (Settings.HEIGHT / 2.0f) + (300.0f * Settings.scale);
    private static final float ARROW_SIZE = 100.0f * Settings.scale;
    private static final float LINK_X = (Settings.WIDTH / 2.0f);
    private static final float LINK_Y = (Settings.HEIGHT / 2.0f) + (600.0f * Settings.scale);
    private static final float LINK_WIDTH = 800.0f * Settings.scale;
    private static final float LINK_HEIGHT = 100.0f * Settings.scale;
    private static Hitbox leftArrow, rightArrow, link;
    private static String currentArt;
    private static String currentCard;

    public static void update(SingleCardViewPopup screen) {
        if (currentCard != null) {
            if (leftArrow != null) {
                leftArrow.update();
                if (leftArrow.justHovered) {
                    CardCrawlGame.sound.play("UI_HOVER");
                }
                if (leftArrow.clicked) {
                    leftArrow.clicked = false;
                    getPreviousArt(screen);
                }
            }

            if (rightArrow != null) {
                rightArrow.update();
                if (rightArrow.justHovered) {
                    CardCrawlGame.sound.play("UI_HOVER");
                }

                if (rightArrow.clicked) {
                    rightArrow.clicked = false;
                    getNextArt(screen);
                }
            }

            if (link != null) {
                link.update();
                if (link.justHovered) {
                    CardCrawlGame.sound.play("UI_HOVER");
                }

                if (link.clicked) {
                    link.clicked = false;
                    CreditsInfo info = getInfoByID(currentCard, currentArt);
                    if (info != null) {
                        String url = info.getArtistWebsite();
                        openBrowser(url);
                    } else {
                        System.out.println("CreditsHelper: ERROR: no valid info for this card/art. How did this happen?");
                    }
                }
            }
        }
    }

    public static boolean updateInput() {
        if (InputHelper.justClickedLeft) {
            if (leftArrow != null && leftArrow.hovered) {
                leftArrow.clickStarted = true;
                InputHelper.justClickedLeft = false;
                CardCrawlGame.sound.play("UI_CLICK_1");
                return true;
            } else if (rightArrow != null && rightArrow.hovered) {
                rightArrow.clickStarted = true;
                InputHelper.justClickedLeft = false;
                CardCrawlGame.sound.play("UI_CLICK_1");
                return true;
            } else if (link != null && link.hovered) {
                link.clickStarted = true;
                InputHelper.justClickedLeft = false;
                CardCrawlGame.sound.play("UI_CLICK_1");
                return true;
            }
        }
        return false;
    }

    public static void getNextArt(SingleCardViewPopup screen) {
        int index = 0;
        ArrayList<CreditsInfo> list = creditedArts.get(currentCard).getKey();
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).getCreditsID().equals(currentArt)) {
                index = i;
                break;
            }
        }
        if (index + 1 >= list.size()) {
            index = 0;
        } else {
            ++index;
        }
        CreditsInfo info = list.get(index);
        currentArt = info.getCreditsID();
        setSingleViewScreenImage(screen, info.getLargeImage());
    }

    public static void getPreviousArt(SingleCardViewPopup screen) {
        int index = 0;
        ArrayList<CreditsInfo> list = creditedArts.get(currentCard).getKey();
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).getCreditsID().equals(currentArt)) {
                index = i;
                break;
            }
        }
        if (index - 1 < 0) {
            index = list.size() - 1;
        } else {
            --index;
        }
        CreditsInfo info = list.get(index);
        currentArt = info.getCreditsID();
        setSingleViewScreenImage(screen, info.getLargeImage());
    }

    public static void render(SpriteBatch sb) {
        if (currentCard != null) {
            Texture arrow = ImageMaster.POPUP_ARROW;
            int w = arrow.getWidth();
            int h = arrow.getHeight();
            sb.draw(arrow, leftArrow.cX - (w / 2.0f), leftArrow.cY - (h / 2.0f), w / 2.0f, h / 2.0f, w, h, leftArrow.width / w, leftArrow.height / h, 0.0f, 0, 0, w, h, false, false);
            sb.draw(arrow, rightArrow.cX - (w / 2.0f), rightArrow.cY - (h / 2.0f), w / 2.0f, h / 2.0f, w, h, rightArrow.width / w, rightArrow.height / h, 0.0f, 0, 0, w, h, true, false);

            //draw the credits box
            //render name
            //render link
        }
    }

    public static void onScreenOpen(String cardID) {
        if (isArtCredited(cardID)) {
            leftArrow = new Hitbox(LEFT_ARROW_X - (ARROW_SIZE / 2.0f), LEFT_ARROW_Y - (ARROW_SIZE / 2.0f), ARROW_SIZE, ARROW_SIZE);
            rightArrow = new Hitbox(RIGHT_ARROW_X - (ARROW_SIZE / 2.0f), RIGHT_ARROW_Y - (ARROW_SIZE / 2.0f), ARROW_SIZE, ARROW_SIZE);
            link = new Hitbox(LINK_X - (LINK_WIDTH / 2.0f), LINK_Y - (LINK_HEIGHT / 2.0f), LINK_WIDTH, LINK_HEIGHT);
            currentCard = cardID;
            currentArt = getCurrentInfo(currentCard).getCreditsID();
        }
    }

    public static void onScreenClose(SingleCardViewPopup screen) {
        if (currentCard != null && isArtCredited(currentCard)) {
            setCurrentArt(currentCard, currentArt, screen);
            leftArrow = null;
            rightArrow = null;
            link = null;
            currentCard = null;
            setSingleViewScreenImage(screen, null);
        }
    }

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
        CreditsInfo def = getInfoByID(cardID, CreditsInfo.DEFAULT_ID);
        if (def == null) {
            ArrayList<CreditsInfo> infos = creditedArts.get(cardID).getKey();
            CreditsInfo newDefault = new CreditsInfo(cardID);
            infos.add(newDefault);
            return newDefault;
        }
        return def;
    }

    public static CreditsInfo getInfoByID(String cardID, String infoID) {
        ArrayList<CreditsInfo> infos = creditedArts.get(cardID).getKey();
        for (CreditsInfo info : infos) {
            if (info.getCreditsID().equals(infoID)) {
                return info;
            }
        }
        return null;
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
                TextureAtlas.AtlasRegion region = info.getSmallImage();
                Texture texture = info.getLargeImage();
                ArrayList<AbstractCard> allInstances = getAllExistingCards(cardID);
                for (AbstractCard card : allInstances) {
                    setImage(card, region);
                }
                setSingleViewScreenImage(cardViewPopup, texture);
                if (creditedArtSettings != null) {
                    creditedArtSettings.setString(cardID, artID);
                    try {
                        creditedArtSettings.save();
                    } catch (IOException e) {
                        System.out.println("CreditsHelper: ERROR: unable to save art settings.");
                        e.printStackTrace();
                    }
                }
                return;
            }
        }
        System.out.println("CreditsHelper: ALERT: " + artID + " is not a valid art ID");
    }

    public static void setSingleViewScreenImage(SingleCardViewPopup popup, Texture img) {
        ReflectionHacks.setPrivate(popup, SingleCardViewPopup.class, "portraitImg", img);
    }

    public static void setImage(AbstractCard card, TextureAtlas.AtlasRegion img) {
        ReflectionHacks.setPrivate(card, AbstractCard.class, "portrait", img);
    }

    public static TextureAtlas.AtlasRegion getSmallImage(String imgUrl) {
        Texture cardTexture;
        if (CustomCard.imgMap.containsKey(imgUrl)) {
            cardTexture = CustomCard.imgMap.get(imgUrl);
        } else {
            cardTexture = ImageMaster.loadImage(imgUrl);
            cardTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            CustomCard.imgMap.put(imgUrl, cardTexture);
        }
        int tw = cardTexture.getWidth();
        int th = cardTexture.getHeight();
        return new TextureAtlas.AtlasRegion(cardTexture, 0, 0, tw, th);
    }

    public static Texture getLargeImage(String imgUrl) {
        Texture cardTexture;
        if (CustomCard.imgMap.containsKey(imgUrl)) {
            cardTexture = CustomCard.imgMap.get(imgUrl);
        } else {
            cardTexture = ImageMaster.loadImage(imgUrl);
            CustomCard.imgMap.put(imgUrl, cardTexture);
        }
        return cardTexture;
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
        System.out.println("CreditsHelper: INFO: loading user-selected art information");
        try {
            creditedArtSettings = new SpireConfig("hydrologistmod", "CreditedArtSettings");
            System.out.println("CreditsHelper: INFO: SpireConfig successfully loaded");
        } catch (IOException e) {
            System.out.println("CreditsHelper: ERROR: unable to initialize Config for credited art. User art settings will not be loaded or saved this session");
            e.printStackTrace();
        }
        System.out.println("CreditsHelper: INFO: parsing credited art information");
        long time = System.currentTimeMillis();
        Gson gson = new Gson();
        String path = "hydrologistmod/images/CreditedArt.json";
        Type creditType = new TypeToken<HashMap<String, CreditStrings>>() {}.getType();
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
                infos.add(new CreditsInfo(cardID));
                creditedArts.put(cardID, new Pair<>(infos, CreditsInfo.DEFAULT_ID));
            } else {
                infos = creditedArts.get(cardID).getKey();
            }
            for (int i = 0; i < credit.ARTISTS.length; ++i) {
                infos.add(new CreditsInfo(cardID, credit.ARTISTS[i], credit.URLS[i]));
            }
            if (creditedArtSettings != null && creditedArtSettings.has(cardID)) {
                creditedArts.put(cardID, new Pair<>(infos, creditedArtSettings.getString(cardID)));
            }
        }
        System.out.println("CreditsHelper: INFO: credited art info loaded. Time elapsed: " + (System.currentTimeMillis() - time) + "ms");
    }
}
