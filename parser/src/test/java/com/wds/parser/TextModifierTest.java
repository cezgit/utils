package com.wds.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wds.parser.TextModifier.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TextModifierTest {

    private List<String> matches = asList("12\"", "7\"", "ACOUSTIC", "ANNIVERSARY",
            "BONUS",
            "CONCERT",
            "DEMO", "DEMOS",
            "EDIT", "EP", "EPIC",
            "FAMOUS BY", "FEAT", "FEATURING", "FT",
            "GROOVE",
            "INSTRUMENTAL", "INTERLUDE", "INTRODUCING",
            "KARAOKE",
            "LIVE",
            "MEDLEY", "MELLOSMOOTHE", "MIX", "MIXED", "MONO",
            "ORIGINAL",
            "PERFORMED", "PRESENTS",
            "RECORDED", "RECORDING", "RELEASE", "REMASTER", "REMASTERED", "REMIX", "REMIXED", "REMIXES", "REPRISE",
            "SOUNDTRACK", "STEREO", "STYLE OF",
            "TITLES", "TRIBUTE",
            "VERSION", "VOCAL");

    @Test
    public void removeCharsTest() {
        String regex = "['()-]";
        assertEquals("REFLEX", removeChars("RE-FLEX", regex));
        assertEquals("GARY US BONDS", removeChars("GARY 'US' BONDS", regex));
        assertEquals("SHAKIN STEVENS", removeChars("SHAKIN' STEVENS", regex));
    }



    @Test
    public void removeWordsTest() {

        String regex = "\\b(AND|WITH|FT|FEAT|FEATURING|INTRODUCING)\\b";

        assertEquals("SOMETHING GOOD BAD",
                removeWords("SOMETHING GOOD AND BAD", regex));

        assertEquals("SOMETHING GOOD BAND BAD",
                removeWords("SOMETHING GOOD BAND BAD", regex));

        assertEquals("SOMETHING GOOD BAD",
                removeWords("SOMETHING GOOD WITH BAD", regex));

        assertEquals("SOMETHING GOOD BAD",
                removeWords("SOMETHING GOOD FT. BAD", regex));

        assertEquals("SOMETHING GOOD BAD",
                removeWords("SOMETHING GOOD FEAT BAD", regex));

        assertEquals("SOMETHING GOOD BAD",
                removeWords("SOMETHING GOOD FEATURING BAD", regex));

        assertEquals("SOMETHING GOOD BAD",
                removeWords("SOMETHING GOOD INTRODUCING BAD", regex));
    }








    @Test
    public void removeLastContentBetweenIfMatchEqualsTest() {

        assertEquals("YOUNG BLOOD (GOOD)",
                removeLastContentBetweenIfMatchEquals("YOUNG BLOOD (GOOD) (EP)", "(", ")", asList("LIVE", "EP")));

        assertEquals("YOUNG BLOOD (GOOD) (AND EPIC)",
                removeLastContentBetweenIfMatchEquals("YOUNG BLOOD (GOOD) (AND EPIC)", "(", ")", asList("LIVE", "EP")));
    }









//    @Test
//    public void removeBracketedContentIfRightBracketIsLastCharTest() {
//        String result = removeBracketedContentIfRightBracketIsLastChar("YOU'RE SO SQUARE (BABY, I DON'T CARE)", true, false, false);
//        assertEquals("YOU'RE SO SQUARE", result);
//    }

    @Test
    public void removeSequentialDupesTest() {
        assertEquals("GOOD GOODING", removeSequentialDupes("GOOD GOODING"));
        assertEquals("FOO GOOD", removeSequentialDupes("FOO FOO GOOD"));
        assertEquals("FOO GOOD FOO", removeSequentialDupes("FOO GOOD FOO"));
        assertEquals("GOOD FOO", removeSequentialDupes("GOOD FOO FOO"));
        assertEquals("AND", removeSequentialDupes("AND AND AND"));
        assertEquals("AND OR", removeSequentialDupes("AND AND OR OR"));
        assertEquals("BOO AND GOO AND DOO", removeSequentialDupes("BOO AND AND GOO AND DOO"));
        assertEquals("BOO AND GOO AND DOO", removeSequentialDupes("BOO AND AND GOO AND AND DOO"));
    }

    @Test
    public void replaceAllWithSpaceTest() {

        String regex = "[()-]";

        assertEquals("SOMETHING GOOD AND BAD",
                replaceAllWithSpace("SOMETHING - GOOD - (AND BAD)", regex ));

        regex = "[&,(){}/]";

        assertEquals("SOMETHING GOOD BAD",
                replaceAllWithSpace("SOMETHING GOOD & BAD", regex));

        assertEquals("SOMETHING GOOD BAD",
                replaceAllWithSpace("SOMETHING GOOD (BAD)", regex));

        assertEquals("SOMETHING GOOD BAD",
                replaceAllWithSpace("SOMETHING / (GOOD) & {BAD}", regex));

        assertEquals("SOMETHING GOOD BAD",
                replaceAllWithSpace("SOMETHING GOOD/BAD", regex));
    }

    @Test
    public void replaceParenthesesTest() {
        assertThat(replaceParentheses("some (and) others", " "),
                is("some and others"));
    }






}