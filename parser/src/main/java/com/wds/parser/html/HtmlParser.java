package com.wds.parser.html;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlParser {


    public static String extractText(Document doc, String selector) {
        Elements els = doc.select(selector);
        if(!els.isEmpty())
            return els.first().text().trim();
        return "";
    }

    public static  String extractLink(Document doc, String selector) {
        Elements els = doc.select(selector);
        if(!els.isEmpty())
            return els.first().attr("abs:href").replaceAll("\"", "");
        return "";
    }

    public static  String extractImageSrc(Document doc, String selector) {
        Elements els = doc.select(selector);
        if(!els.isEmpty())
            return els.first().absUrl("src").replaceAll("\"", "");
        return "";
    }

    public static  String extractImageFromStyle(Document doc, String selector) {
        Elements els = doc.select(selector);
        if(!els.isEmpty()) {
            String urlString = els.first().attr("style");
            if(StringUtils.isBlank(urlString)) {
                urlString = els.first().attr("data-imagesrc");
            }
            return getUrl(urlString).replaceAll("\"", "");
        }
        return "";
    }

    public static String getUrl(String s) {
        if(StringUtils.isBlank(s)) return StringUtils.EMPTY;
        Pattern p = Pattern.compile(".*(https?:\\/\\/[^ )\"]*).*");
        Matcher m = p.matcher(s);
        if(m.matches()) {
            return m.group(1);
        }
        return StringUtils.EMPTY;
    }
}
