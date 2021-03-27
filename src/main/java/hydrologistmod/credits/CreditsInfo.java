package hydrologistmod.credits;

public class CreditsInfo {
    private static final String BASE_IMG_PATH = "hydrologistmod/images/cards/";
    private String cardName;
    private String artistName;
    private String artistWebsite;
    private String ID;

    public CreditsInfo(String card, String artist, String url) {
        cardName = card;
        artistName = artist;
        artistWebsite = url;
        ID = card + ":" + artist;
    }

    public CreditsInfo(String card) {
        cardName = card;
        artistName = null;
        artistWebsite = null;
        ID = "DEFAULT";
    }

    public String getImgPath() {
        if (!ID.equals("DEFAULT")) {
            return BASE_IMG_PATH + cardName + "/" + artistName + "/IMG.png";
        } else return null;
    }

    public String getLargeImgPath() {
        if (!ID.equals("DEFAULT")) {
            return BASE_IMG_PATH + cardName + "/" + artistName + "/IMG_LARGE.png";
        } else return null;
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
