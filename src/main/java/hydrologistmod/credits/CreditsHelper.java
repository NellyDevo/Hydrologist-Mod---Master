package hydrologistmod.credits;

import basemod.Pair;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

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

    public static boolean artIsDefault(String cardID) {
        return creditedArts.get(cardID).getValue().equals("DEFAULT");
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

    public static void setCurrentArt(String cardID, String artID) {
        String currentID = creditedArts.get(cardID).getValue();
        if (artID.equals(currentID)) {
            return;
        }
        ArrayList<CreditsInfo> infos = creditedArts.get(cardID).getKey();
        for (CreditsInfo info : infos) {
            if (info.getCreditsID().equals(artID)) {
                creditedArts.put(cardID, new Pair<>(infos, artID));
            }
        }
        System.out.println("CreditsHelper: ALERT: " + artID + " is not a valid art ID");
    }
}
