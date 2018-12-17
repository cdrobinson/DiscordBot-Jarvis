/*
 * Copyright (c) 2018 Chris Robinson. All rights reserved.
 */

package srTracking;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class OverwatchProfile {

    private String userSR;
    private String response;
    private String profileURL;
    private String portraitURL;
    private String rankIconURL;

    public OverwatchProfile(String battleTag) {
        this.profileURL = "https://playoverwatch.com/en-us/career/pc/" + battleTag.replaceAll("#", "-");
        System.out.println(profileURL);
        try {
            Document mainDocument = Jsoup.connect(profileURL).get();
            assert mainDocument != null;
            Elements profileStatus = mainDocument.getElementsByClass("masthead-permission-level-text");
            if (profileStatus.size() != 0) {
                Elements playerPortrait = mainDocument.getElementsByClass("player-portrait");
                this.portraitURL = playerPortrait.first().attr("src");
                if (profileStatus.first().text().equals("Private Profile")){
                    this.response = "This player's profile is private";
                    this.rankIconURL = null;
                } else {
                    Elements rankContent = mainDocument.getElementsByClass("competitive-rank");
                    if (rankContent.size() != 0) {
                        this.rankIconURL = rankContent.first().getElementsByTag("img").first().attr("src");
                        Elements rank = rankContent.first().getElementsByClass("u-align-center h5");
                        this.userSR = rank.first().text();
                    } else {
                        this.response = "This player has not placed yet";
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
            if (e.getCause() == new UnknownHostException()) {
                this.userSR = "Error";
            }
        }
    }

    String getPortraitURL() {
        return portraitURL;
    }

    String getRankIconURL() {
        return rankIconURL;
    }

    String getProfileURL() {
        return profileURL;
    }

    String getSR() {
        if (userSR != null) {
            return userSR;
        } else {
            return response;
        }
    }
}