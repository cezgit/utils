package com.wds.parser.html;

import org.hamcrest.MatcherAssert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import static com.wds.parser.html.HtmlParser.getUrl;
import static org.hamcrest.Matchers.is;

class HtmlParserTest {

    @Test
    void extractImageFromStyleTest() {

        MatcherAssert.assertThat(getUrl("background-image: url(https://charts-static.billboard.com/img/1840/12/marty-robbins-8zg.jpg)"),
                is("https://charts-static.billboard.com/img/1840/12/marty-robbins-8zg.jpg"));

        MatcherAssert.assertThat(getUrl("img src=\"https://charts-static.billboard.com/img/1840/12/marty-robbins-8zg.jpg\""),
                is("https://charts-static.billboard.com/img/1840/12/marty-robbins-8zg.jpg"));

        MatcherAssert.assertThat(getUrl("data-imagesrc=\"https://charts-static.billboard.com/img/1965/01/the-beau-brummels-zx5.jpg\""),
                is("https://charts-static.billboard.com/img/1965/01/the-beau-brummels-zx5.jpg"));

        Document doc = Jsoup.parse("<div class=\"chart-row__image\" data-imagesrc=\"https://charts-static.billboard.com/img/1959/01/annette-funicello-ft9.jpg\" style=\"background-image: url(&quot;https://charts-static.billboard.com/img/1959/01/annette-funicello-ft9.jpg&quot;);\">\n" +
                "</div>");
        String image = HtmlParser.extractImageFromStyle(doc, "div.chart-row__image");
        MatcherAssert.assertThat(image, is("https://charts-static.billboard.com/img/1959/01/annette-funicello-ft9.jpg"));

    }
}