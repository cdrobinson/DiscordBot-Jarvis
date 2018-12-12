import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;

class SR_OverwatchProfile {

    private String userSR;
    private String response;

    SR_OverwatchProfile(String battleTag) {
        String url = "https://playoverwatch.com/en-us/career/pc/" + battleTag.replaceAll("#", "-");
        System.out.println(url);
        try {
            Document doc = Jsoup.connect(url).get();
            assert doc != null;
            Elements profileStatus = doc.getElementsByClass("masthead-permission-level-text");
            if (profileStatus.size() != 0) {
                if (profileStatus.first().text().equals("Private Profile")){
                    this.response = "This player's profile is private";
                } else {
                    Elements playerName = doc.getElementsByClass("header-masthead");
                    if (playerName.size() != 0) {
                        Elements content = doc.getElementsByClass("competitive-rank");
                        if (content.size() != 0) {
                            Elements rank = content.first().getElementsByClass("u-align-center h5");
                            this.userSR = rank.first().text();
                        } else {
                            this.response = "This player has not placed yet";
                        }
                    }
                }
            } else {
                this.response =  "This player's profile could not be found (check the case)";
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getCause() == new SocketTimeoutException()) {
                this.userSR = "Error";
            }
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