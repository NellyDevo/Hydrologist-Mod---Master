package hydrologistmod.credits;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class CreditsInfo {
    private static final String BASE_IMG_PATH = "hydrologistmod/images/cards/";
    public static final String DEFAULT_ID = "DEFAULT";
    private String cardID;
    private String artistName;
    private String artistWebsite;
    private String ID;
    public TextureAtlas.AtlasRegion defaultSmallImage;
    public Texture defaultLargeImage;

    public CreditsInfo(String card, String artist, String url) {
        cardID = card;
        artistName = artist;
        artistWebsite = url;
        ID = card + ":" + artist;
    }

    public CreditsInfo(String card) {
        cardID = card;
        artistName = null;
        artistWebsite = null;
        ID = DEFAULT_ID;
    }

    public String getImgPath() {
        if (ID.equals(DEFAULT_ID)) {
            return null;
        }
        return BASE_IMG_PATH + cardID + "/" + artistName + "/IMG.png";
    }

    public String getLargeImgPath() {
        if (ID.equals(DEFAULT_ID)) {
            return null;
        }
        return BASE_IMG_PATH + cardID + "/" + artistName + "/IMG_LARGE.png";
    }

    public String getArtistWebsite() {
        return artistWebsite;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getCreditsID() {
        return ID;
    }
}
