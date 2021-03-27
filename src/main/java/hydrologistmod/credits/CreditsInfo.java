package hydrologistmod.credits;

public class CreditsInfo {
    private static final String BASE_IMG_PATH = "hydrologistmod/images/cards/";
    private String cardName;
    private String artistName;
    private String artistWebsite;

    public CreditsInfo(String card, String artist, String url) {
        cardName = card;
        artistName = artist;
        artistWebsite = url;
    }

    public String getImgPath() {
        return BASE_IMG_PATH + cardName + "/" + artistName + "/IMG.png";
    }

    public String getArtistWebsite() {
        return artistWebsite;
    }

    public String getArtistName() {
        return artistName;
    }
}
