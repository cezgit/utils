package com.wds.parser;


import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wds.parser.TextModifier.removeContentBetweenLastMarkersIfContainsWord;
import static com.wds.parser.TextModifier.removeContentBetweenMarkersIfEqualsAny;
import static com.wds.parser.TextParser.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.*;

public class TextParserTest {





    @Test
    void containsAnyTest() {
        assertTrue(containsAny("THERE IS A FOO", asList("FOO")));
        assertTrue(containsAny("THERE IS A FOOL", asList("FOO")));
        assertTrue(containsAny("FOOLISH", asList("FOO")));
        assertTrue(containsAny("IT'S BLOWING' OUT THERE", List.of("BLOWING'")));
    }

    @Test
    public void containsAnyWordTest() {
        assertFalse(containsAnyWord("FOOL", asList("FOO", "BOO")));
        assertFalse(containsAnyWord("THERE IS A FOOL SOMEWHERE", asList("FOO", "BOO")));
        assertTrue(containsAnyWord("FOO LOO", asList("FOO", "BOO")));
        assertTrue(containsAnyWord("THERE IS A FOO", asList("FOO", "BOO")));
        assertTrue(containsAnyWord("THERE IS A FOO SOMEWHERE", asList("FOO", "BOO")));
        assertTrue(containsAnyWord("FEAT BLAST", asList("FEAT")));
    }

    @Test
    public void equalsAnyTest() {
        assertTrue(equalsAny("FOO", asList("FOO", "BOO")));
        assertFalse(equalsAny("FOOL", asList("FOO", "BOO")));
        assertFalse(equalsAny("MOO AND FOO", asList("FOO", "BOO")));
    }

    @Test
    void equalsAnyWordTest() {
        assertTrue(equalsAnyWord("VOL 1", asList("FOO", "VOL\\s*\\d")));
        assertTrue(equalsAnyWord("FOO", asList("FOO", "VOL\\s*\\d")));
        assertFalse(equalsAnyWord("FOOL", asList("FOO", "VOL\\s*\\d")));
    }

    @Test
    public void getContainsAnyRegexTest() {
        String regex = getContainsAnyRegex(asList("THE ONE", "ANOTHER"));
        assertEquals("^.*?\\b(THE ONE|ANOTHER)\\b.*$", regex);

        assertTrue("THE ONE FOO".matches(regex));
        assertTrue("FOO ANOTHER".matches(regex));
        assertTrue("FOO THE ONE FOO".matches(regex));
        assertTrue("FOO ANOTHER FOO".matches(regex));
        assertFalse("ANOTHERONE".matches(regex));
    }

    @Test
    public void getEqualsAnyRegexTest() {
        String regex = getEqualsAnyRegex(asList("THE ONE", "ANOTHER"));
        assertEquals("\\b(THE ONE|ANOTHER)\\b", regex);

        assertFalse("THE ONE FOO".matches(regex));
        assertFalse("FOO ANOTHER".matches(regex));
        assertFalse("FOO THE ONE FOO".matches(regex));
        assertFalse("FOO ANOTHER FOO".matches(regex));
        assertFalse("ANOTHERONE".matches(regex));
        assertTrue("ANOTHER".matches(regex));
        assertTrue("THE ONE".matches(regex));
    }



    @Test
    public void getTokensAsListTest() {
        assertThat(TextParser.getTokensAsList("ONE SEP TWO SEPS THREE", "\\b(SEP|SEPS)\\b"),
                contains("ONE", "TWO", "THREE"));

        assertThat(TextParser.getTokensAsList("ONE SEP TWO SEPS SEPS THREE", "\\b(SEP|SEPS)\\b"),
                contains("ONE", "TWO", "THREE"));

        assertThat(TextParser.getTokensAsList("ONE SEP TWO SEP THREE", "\\b(SEP|SEPS)\\b"),
                contains("ONE", "TWO", "THREE"));

        assertThat(TextParser.getTokensAsList("ONE SEPTWO SEP THREE", "\\b(SEP|SEPS)\\b"),
                contains("ONE SEPTWO", "THREE"));

        String regex = "[&,()/]";

        assertThat(TextParser.getTokensAsList("ONE & TWO / THREE (FOUR) , FIVE", regex),
                contains("ONE", "TWO", "THREE", "FOUR", "FIVE"));

        assertThat(TextParser.getTokensAsList("SOMETHING GOOD & BAD", regex),
                contains("SOMETHING GOOD", "BAD"));

        assertThat(TextParser.getTokensAsList("SOMETHING GOOD (BAD)", regex),
                contains("SOMETHING GOOD", "BAD"));

        assertThat(TextParser.getTokensAsList("SOMETHING GOOD/BAD", regex),
                contains("SOMETHING GOOD", "BAD"));

        regex = "\\b(FT|FEAT|FEATURING|PERFORMED BY)\\b";

        assertEquals("SOMETHING GOOD",
                TextParser.getTokensAsList("SOMETHING GOOD FT. FOO", regex).get(0).trim());
        assertEquals("SOMETHING GOOD",
                TextParser.getTokensAsList("SOMETHING GOOD FEAT. FOO", regex).get(0).trim());
        assertEquals("SOMETHING GOOD",
                TextParser.getTokensAsList("SOMETHING GOOD FEATURING FOO", regex).get(0).trim());
        assertEquals("SOMETHING GOOD",
                TextParser.getTokensAsList("SOMETHING GOOD PERFORMED BY FOO", regex).get(0).trim());

        assertThat(Arrays.asList("DEAD&ALIVE".split("&")), hasItems("DEAD", "ALIVE"));

        regex = "\\b(AND|WITH|FT|FEAT|FEATURING|INTRODUCING)\\b";
        assertThat(TextParser.getTokensAsList("DEAD AND ALIVE", regex), hasItems("DEAD", "ALIVE"));
        assertThat(TextParser.getTokensAsList("DEAD & ALIVE", regex), hasItems("DEAD & ALIVE"));
        assertThat(TextParser.getTokensAsList("BAND AID AND BONO", regex), hasItems("BAND AID", "BONO"));
        assertThat(TextParser.getTokensAsList("KANDIDATE", regex), hasItems("KANDIDATE"));
        assertThat(TextParser.getTokensAsList("SOME BAND FT. BOO", regex), hasItems("SOME BAND", "BOO"));
    }



    @Test
    public void anyContainsTest() {
        assertTrue(anyContains(asList("SOME FOOLISH", "BOO"), "FOO"));
        assertTrue(anyContains(asList("SOME FOO", "BOO"), "FOO"));
        assertFalse(anyContains(asList("FOO", "BOO"), "ROO"));
        assertTrue(anyContains(asList("FOO/HOO", "BOO"), "/"));
        assertFalse(anyContains(asList("FOO", "BOO"), "/"));
    }

    @Test
    public void breakBySpaceTest() {
        assertEquals(asList("A", "AND", "B"), breakBySpace("A AND B"));
    }



    @Test
    public void getFirstTokenTest() {
        assertEquals("HEAD", getFirstToken("HEAD/TAIL", "/"));
        assertEquals("HEAD", getFirstToken("HEAD/CENTER/TAIL", "/"));
        assertEquals("SOMETHING", getFirstToken("SOMETHING - ELSE", "[\\-\\[\\{]"));
        assertEquals("SOMETHING", getFirstToken("SOMETHING (ELSE)", "[\\-\\[\\(]"));
        assertEquals("SOMETHING", getFirstToken("SOMETHING [ELSE]", "[\\-\\[\\{]"));
    }





    @Test
    public void findFirstTokenContainedTest() {
        assertThat(findFirstTokenContained("THERE IS A FOO", asList("FOO", "BOO")).get(), is("FOO"));
        assertThat(findFirstTokenContained("THERE IS A LOO", asList("FOO", "BOO")), is(Optional.empty()));
    }



    @Test
    public void startsWithAnyTest() {
        assertTrue(startsWithAny("THERE IS A FOO", asList("FOO", "THERE")));
        assertFalse(startsWithAny("THERE IS A FOOL", asList("FOO")));
        assertFalse(startsWithAny("FOOL AND BOO", asList("FOO")));
    }

    @Test
    public void endsWithAnyTest() {
        assertTrue(endsWithAny("THERE IS A FOO", asList("FOO")));
        assertFalse(endsWithAny("THERE IS A FOOL", asList("FOO")));
        assertFalse(endsWithAny("FOO AND BOO", asList("FOO")));
        assertFalse(endsWithAny("FOO AND ABOO", asList("BOO")));
    }

    @Test
    void getLastElementTest() {
        assertThat(getLastElement(List.of("BOO")).get(), is("BOO"));
        assertThat(getLastElement(List.of("FOO", "BOO")).get(), is("BOO"));
        assertFalse(getLastElement(Collections.EMPTY_LIST).isPresent());
    }

    @Test
    void getLastWordTest() {
        assertThat(getLastWord(""), is(Optional.empty()));
        assertThat(getLastWord("word").get(), is("word"));
        assertThat(getLastWord(" word").get(), is("word"));
        assertThat(getLastWord("word ").get(), is("word"));
        assertThat(getLastWord(" word ").get(), is("word"));
        assertThat(getLastWord("get me last word").get(), is("word"));
        assertThat(getLastWord("give me $33").get(), Matchers.is("$33"));

        assertThat(getLastWord("   "), is(Optional.empty()));
    }

    @Test
    void getFirstWordTest() {
        assertThat(getFirstWord(""), is(Optional.empty()));
        assertThat(getFirstWord("word").get(), is("word"));
        assertThat(getFirstWord(" word").get(), is("word"));
        assertThat(getFirstWord(" word ").get(), is("word"));
        assertThat(getFirstWord("get me last word").get(), is("get"));
    }

    @Test
    void countTokensTest() {
        assertEquals(0, countWords((String)null));
        assertEquals(0, countWords("   "));
        assertEquals(3, countWords("ONE TWO THREE"));

        assertEquals(3, countWords(List.of("ONE TWO", "THREE")));
    }

    @Test
    public void firstWordsAsStringTest() {
        assertThat(firstWordsAsString("MC SAR THE REAL MCCOY", 4), is("MC SAR THE REAL"));
        assertThat(firstWordsAsString("MC SAR", 3), is("MC SAR"));
    }

    @Test
    void joinAsStringTest() {
        assertEquals("AB CD", joinAsString(List.of("AB", "CD")));
        assertEquals("AB CD", joinAsString(asList("AB", "CD")));
        assertEquals(joinAsString(getTokensAsList("ABC DE RRR", " ")), "ABC DE RRR");
    }

    @Test
    void wordCounterTest() {
        assertEquals(wordCounter.apply("THIS COUNT SHOULD BE FIVE"), 5);
    }

}