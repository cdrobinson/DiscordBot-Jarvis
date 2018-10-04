import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

class ProfileReader {

    static String getSR(String battleTag) {

        StringBuilder url = new StringBuilder();
        url.append("https://playoverwatch.com/en-us/career/pc/");
        url.append(battleTag.replaceAll("#", "-"));
        Document doc = null;
        try {
            doc = Jsoup.connect(url.toString()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Elements profileStatus = doc.getElementsByClass("masthead-permission-level-text");
        if (profileStatus.size() != 0) {
            if (profileStatus.first().text().equals("Private Profile")){
                return "This player's profile is private";
            }
        }
        Elements playerName = doc.getElementsByClass("header-masthead");
        if (playerName.size() != 0) {
            Elements content = doc.getElementsByClass("competitive-rank");
            if (content.size() != 0) {
                Elements rank = content.first().getElementsByClass("u-align-center h5");
                return "This player's SR is: " + rank.first().text();
            } else {
                return "This player has not placed yet";
            }
        } else {
            return "This player's profile could not be found (check the case)";
        }
    }
}