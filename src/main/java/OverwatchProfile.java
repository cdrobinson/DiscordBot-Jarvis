import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

class OverwatchProfile {

    private String userSR;
    private String response;

    OverwatchProfile(String battleTag) {
        String url = "https://playoverwatch.com/en-us/career/pc/" + battleTag.replaceAll("#", "-");
        System.out.println(url);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Elements profileStatus = doc.getElementsByClass("masthead-permission-level-text");
        if (profileStatus.size() != 0) {
            if (profileStatus.first().text().equals("Private Profile")){
                this.response = "This player's profile is private";
            }
        }
        Elements playerName = doc.getElementsByClass("header-masthead");
        if (playerName.size() != 0) {
            Elements content = doc.getElementsByClass("competitive-rank");
            if (content.size() != 0) {
                Elements rank = content.first().getElementsByClass("u-align-center h5");
                this.userSR = rank.first().text();
            } else {
                this.response =  "This player has not placed yet";
            }
        } else {
            this.response =  "This player's profile could not be found (check the case)";
        }
    }

    String getSR() {
        if (userSR != null) {
            return userSR;
        } else {
            return response;
        }
    }
}