package com.wds.parser;


import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wds.parser.TextModifier.removeContentBetweenChars;
import static com.wds.parser.TextModifier.removeLastContentAfterSeparatorIfMatchEquals;
import static com.wds.parser.TextParser.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.*;

public class TextParserTest {

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

    public static final Map<String, String> TITLE_TOKEN_RENAME_MAP = Collections.unmodifiableMap(
            Stream.of(
                    new AbstractMap.SimpleEntry<>(" & ", " AND "),
                    new AbstractMap.SimpleEntry<>("A M", "A.M."),
                    new AbstractMap.SimpleEntry<>("B THERE", "BE THERE"),
                    new AbstractMap.SimpleEntry<>("BLOWIN'", "BLOWING"),
                    new AbstractMap.SimpleEntry<>("BLOWIN", "BLOWING"),
                    new AbstractMap.SimpleEntry<>("BREAK-A-WAY", "BREAKAWAY"),
                    new AbstractMap.SimpleEntry<>("CMON", "C'MON"),
                    new AbstractMap.SimpleEntry<>("COREVETTE", "CORVETTE"),
                    new AbstractMap.SimpleEntry<>("DO YA", "DO YOU"),
                    new AbstractMap.SimpleEntry<>("DREAMIN'", "DREAMING"),
                    new AbstractMap.SimpleEntry<>("DREAMIN", "DREAMING"),
                    new AbstractMap.SimpleEntry<>("FEELIN'", "FEELING"),
                    new AbstractMap.SimpleEntry<>("FEELIN", "FEELING"),
                    new AbstractMap.SimpleEntry<>("LOVER GIRL", "LOVERGIRL"),
                    new AbstractMap.SimpleEntry<>("LOVIN'", "LOVING"),
                    new AbstractMap.SimpleEntry<>("LOVIN", "LOVING"),
                    new AbstractMap.SimpleEntry<>("LOVING'", "LOVING"),
                    new AbstractMap.SimpleEntry<>("PYT", "PRETTY YOUNG THING"),
                    new AbstractMap.SimpleEntry<>("ROCK 'N' ROLL", "ROCK AND ROLL"),
                    new AbstractMap.SimpleEntry<>("ROCK 'N ROLL", "ROCK AND ROLL"),
                    new AbstractMap.SimpleEntry<>("ROCK & ROLL", "ROCK AND ROLL"),
                    new AbstractMap.SimpleEntry<>("SOMETHIN'", "SOMETHING"),
                    new AbstractMap.SimpleEntry<>("SOMETHIN", "SOMETHING"),
                    new AbstractMap.SimpleEntry<>("WANNA", "WANT TO"),
                    new AbstractMap.SimpleEntry<>("WENDAL", "WENDEL"),
                    new AbstractMap.SimpleEntry<>("WHATS", "WHAT'S")
            ).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()))
    );

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
    void removeLastCharTest() {
        String regex = "^.*(\\.|;|:)$";
        assertEquals(removeLastChar("DOT.", regex), "DOT");
        assertEquals(removeLastChar("SEMICOLUMN;", regex), "SEMICOLUMN");
        assertEquals(removeLastChar("COLUMN:", regex), "COLUMN");
    }

    @Test
    void replaceAllTest() {
        Map<String, String> map = Map.of("FT.", "FT",
                "FEAT.", "FEAT",
                "MIX", "REMIX",
                "SOMETHIN", "SOMETHING",
                "\"", "'");
        assertEquals(replaceAll("FOO FT. \"ONE\" FEAT. ANOTHER", map), "FOO FT 'ONE' FEAT ANOTHER");
        assertEquals(replaceAll("AFT.ER FEAT.URE MIXED FOOMIXED", map), "AFTER FEATURE REMIXED FOOREMIXED");
        assertEquals(replaceAll("SOMETHING ABOUT YOU", map), "SOMETHINGG ABOUT YOU");
    }

    @Test
    public void tokenReplacerTest() {
        Map<String, String> tokenMap = Map.of("BOYS TOWN", "BOYSTOWN",
                "ABC", "ACC",
                "MIX", "REMIX",
                "C+C MUSIC FACTORY", "C&C MUSIC FACTORY");
        assertEquals(tokenReplacer.apply("C+C MUSIC FACTORY", tokenMap), "C&C MUSIC FACTORY");
        assertEquals(tokenReplacer.apply("FOO BOYS TOWN ABC FOO", tokenMap), "FOO BOYSTOWN ACC FOO");
        assertEquals(tokenReplacer.apply("FOOMIXED MIX", tokenMap), "FOOMIXED REMIX");
        assertEquals(tokenReplacer.apply("AFTER FT ONE", Map.of("FT", "AND", "ONE", "1")), "AFTER AND 1");
        assertEquals(tokenReplacer.apply("AFTER FT ONE", Map.of("FT", "AND", "ONE", "1")), "AFTER AND 1");
        assertEquals(tokenReplacer.apply("LOOK BEFORE YOU LEAP", TITLE_TOKEN_RENAME_MAP), "LOOK BEFORE YOU LEAP");
        assertEquals(tokenReplacer.apply("BREAK A WAY", TITLE_TOKEN_RENAME_MAP), "BREAK A WAY");
        assertEquals(tokenReplacer.apply("BREAK-A-WAY", TITLE_TOKEN_RENAME_MAP), "BREAKAWAY");
        assertEquals(tokenReplacer.apply("BREAK & WAY", TITLE_TOKEN_RENAME_MAP), "BREAK AND WAY");
        assertEquals(tokenReplacer.apply("G.L.A.D.", TITLE_TOKEN_RENAME_MAP), "G.L.A.D.");
    }

    @Test
    public void removeSurroundingQuotesTest() {
        assertEquals("IT'S N BLOWING'", removeSurroundingQuotes("IT'S 'N' BLOWING'"));
        assertEquals("IT'S N WORD", removeSurroundingQuotes("IT'S \"N\" WORD"));
        assertEquals("IT'S 'N WORD", removeSurroundingQuotes("IT'S 'N WORD"));
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
    public void removeIfFirstCharMatchesRegexTest() {
        assertEquals("FOO", TextParser.removeIfFirstCharMatchesRegex("?FOO", "^(\\?|!).*$"));
    }

    @Test
    void removeDotsIfNotConsecutiveTest() {
        assertEquals("LA", removeDotsIfNotConsecutive("L.A."));
        assertEquals("FT", removeDotsIfNotConsecutive("FT."));
        assertEquals("SOME...", removeDotsIfNotConsecutive("SOME..."));
        assertEquals("SOME", removeDotsIfNotConsecutive(".SOME"));
    }

    @Test
    void removeIfStartsWithTokenTest() {
        assertEquals("FOO", removeIfStartsWithToken("#FOO", "#"));
        assertEquals("FOO", removeIfStartsWithToken("...FOO", "..."));
        assertEquals("FOO", removeIfStartsWithToken(".FOO", "."));
        assertEquals("FOO FOO", removeIfStartsWithToken(".FOO FOO", "."));
        assertEquals("FOO.FOO", removeIfStartsWithToken("FOO.FOO", "."));
        assertEquals("FOO FOO", removeIfStartsWithToken("FOO FOO", "."));
        assertEquals("THE FOO", removeIfStartsWithToken("THE THE FOO", "THE"));
    }

    @Test
    void removeIfEndsWithTest() {
        assertEquals("FOO", removeIfEndsWith("FOO '98", "\\s'\\d\\d"));
        assertEquals("FOO", removeIfEndsWith("FOO --", "\\s--"));
        assertEquals("FOO", removeIfEndsWith("FOO -", "\\s-"));
        assertEquals("SOME EP FOO", removeIfEndsWith("SOME EP FOO EP", "\\sEP"));
    }

    @Test
    void removeIfLastCharMatchesTest() {
        assertEquals("FOO", removeIfLastCharMatches("FOO/", "/"));
    }

    @Test
    public void removeIfLastCharMatchesRegexTest() {
        assertEquals("FOO", TextParser.removeIfLastCharMatchesRegex("FOO?", "^.*(\\?|!)$"));
        assertEquals("FOO", TextParser.removeIfLastCharMatchesRegex("FOO!", "^.*(\\?|!)$"));
    }

    @Test
    void removeIfLastWordTest() {
        assertEquals("", removeIfLastWord("", List.of("VS")));
        assertEquals("SOME", removeIfLastWord("SOME VS", List.of("VS")));
        assertEquals("SOME OVS", removeIfLastWord("SOME OVS", List.of("VS")));
    }

    @Test
    void removeCharAttachedToWordTest() {
        assertEquals("FOO", removeCharAttachedToWord("'FOO", "'"));
        assertEquals("FOO", removeCharAttachedToWord("FOO'", "'"));
        assertEquals("FOO BOO HER'S", removeCharAttachedToWord("FOO' 'BOO HER'S", "'"));
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
    void joinAsStringTest() {
        assertEquals("AB CD", joinAsString(List.of("AB", "CD")));
        assertEquals("AB CD", joinAsString(asList("AB", "CD")));
        assertEquals(joinAsString(getTokensAsList("ABC DE RRR", " ")), "ABC DE RRR");
    }

    @Test
    public void fixMultiSpacesTest() {
        assertEquals("abc ddd", fixMultiSpaces("abc ddd"));
        assertEquals("abc ddd", fixMultiSpaces(" abc  ddd  "));
    }

    @Test
    public void fixMultiCharsTest() {
        assertEquals("?", fixMultiChars("?", "\\?"));
        assertEquals("???", fixMultiChars("???", "\\?"));
        assertEquals("FOO?", fixMultiChars("FOO???", "\\?"));
        assertEquals("FOO.", fixMultiChars("FOO....", "\\."));
        assertEquals("FOO!", fixMultiChars("FOO!!", "!"));
        assertEquals("FOO! BOO?", fixMultiChars("FOO!! BOO??", "(!|\\?)"));
    }

    @Test
    public void removeBracketedContentTest() {

        assertEquals("SOMETHING GOOD",
                removeBracketedContent("SOMETHING [BAD AND] GOOD", false, true, true ));

        assertEquals("SOMETHING GOOD",
                removeBracketedContent("SOMETHING {BAD AND} GOOD", false, true, true));

        assertEquals("SOMETHING VERY GOOD",
                removeBracketedContent("SOMETHING (BAD) [FOO] VERY {BOO} GOOD", true, true, true));
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
    public void removeContentBetweenRoundBracketsIfEqualsAny() {

        assertEquals("FOO", removeContentBetweenRoundBracketsIfEqualsAny("FOO"));
        assertEquals("FOO", removeContentBetweenRoundBracketsIfEqualsAny("FOO (LIVE)"));
        assertEquals("FOO", removeContentBetweenRoundBracketsIfEqualsAny("FOO (LIVE) (MIX)"));
        assertEquals("FOO", removeContentBetweenRoundBracketsIfEqualsAny("(MIX) FOO"));
        assertEquals("FOO (LIVELY)", removeContentBetweenRoundBracketsIfEqualsAny("FOO (LIVELY)"));
        assertEquals("FOO", removeContentBetweenRoundBracketsIfEqualsAny("(MIX) FOO (MIX)"));

        assertEquals("INTO THE GROOVE FT TRACY ACKERMAN", removeContentBetweenRoundBracketsIfEqualsAny("INTO THE GROOVE (MEDLEY) FT TRACY ACKERMAN"));
        assertEquals("INTO THE GROOVE TRACY ACKERMAN", removeContentBetweenRoundBracketsIfEqualsAny("INTO THE GROOVE (MEDLEY) (FT) TRACY ACKERMAN"));
        assertEquals("YOU WEAR IT WELL (LIVE", removeContentBetweenRoundBracketsIfEqualsAny("YOU WEAR IT WELL (LIVE"));
    }

    @Test
    public void removeContentBetweenMarkersIfStartWithAnyTest() {
        List<String> matches = List.of(".*THEME.*", ".*MEDLEY.*", ".*MIX.*", "EP");
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("(THEME FROM FOO) BOO", "(", ")", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO (THEME FROM FOO)", "(", ")", matches));
        assertEquals("BOO BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO (THEME FROM FOO) BOO", "(", ")", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("(LOVE THEME) BOO", "(", ")", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO (LOVE THEME)", "(", ")", matches));
        assertEquals("BOO BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO (LOVE THEME) BOO", "(", ")", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("[THEME FROM FOO] BOO", "[", "]", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO 'REMIX'", "'", "'", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO (MEDLEY)", "(", ")", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO 'THEME'", "'", "'", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO 'SOME MIX'", "'", "'", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO 'MIX'", "'", "'", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO 'REMIX'", "'", "'", matches));
        assertEquals("BOO", removeContentBetweenMarkersIfContentStartWithAny("BOO 'EP'", "'", "'", matches));
    }

    @Test
    public void removeContentAfterMarkerIfEqualsAnyTest() {

        assertEquals("EXODUS",
                removeContentAfterMarkerIfEqualsAny("EXODUS - LIVE", " - ", asList("LIVE", "EP")));

        assertEquals("EXODUS - LIVE CONCERT",
                removeContentAfterMarkerIfEqualsAny("EXODUS - LIVE CONCERT", " - ", asList("LIVE", "EP")));

    }

    @Test
    public void removeContentBetweenLastRoundBracketsIfContainsWordTest() {
        assertEquals("YOUNG BLOOD",
                removeContentBetweenLastRoundBracketsIfContainsWord("YOUNG BLOOD (LIVE CHICAGO 1980)"));

        assertEquals("YOUNG BLOOD (GOOD)",
                removeContentBetweenLastRoundBracketsIfContainsWord("YOUNG BLOOD (GOOD) (LIVE EP)"));

        assertEquals("YOUNG BLOOD (GOOD)",
                removeContentBetweenLastRoundBracketsIfContainsWord("YOUNG BLOOD (GOOD) (AND EPIC)"));

        assertEquals("(I HAVE) YOUNG BLOOD (FOR YOU)",
                removeContentBetweenLastRoundBracketsIfContainsWord("(I HAVE) YOUNG BLOOD (FOR YOU)"));

        assertEquals("YOU WEAR IT WELL",
                removeContentBetweenLastRoundBracketsIfContainsWord("YOU WEAR IT WELL (LIVE - LEICESTER, ENGLAND, 12/4/76"));
    }

    @Test
    public void removeContentBetweenMarkersTest() {
        assertEquals("SOMETHING GOOD", removeContentBetweenMarkers("SOMETHING GOOD (AND BAD)", "(", Optional.of(")") ));
        assertEquals("SOMETHING (VERY) GOOD", removeContentBetweenMarkers("SOMETHING (VERY) GOOD", "(", Optional.of(")")));
        assertEquals("(SOMETHING) GOOD", removeContentBetweenMarkers("(SOMETHING) GOOD", "(", Optional.of(")")));
    }


    @Test
    public void removeLastContentAfterSeparatorIfMatchEqualsTest() {

        assertEquals("EXODUS",
                removeLastContentAfterSeparatorIfMatchEquals("EXODUS / LIVE", " / ", asList("LIVE", "EP")));
    }

    @Test
    public void removeContentAfterMarkerIfContainsWordTest() {
        assertEquals("WONDERFUL CHRISTMASTIME",
                removeContentAfterMarkerIfContainsWord("WONDERFUL CHRISTMASTIME - 2011 REMASTER", "-", asList("REMASTER", "REMASTERED")));

        assertEquals("BRASS IN POCKET",
                removeContentAfterMarkerIfContainsWord("BRASS IN POCKET - LIVE - THE SANTA MONICA CIVIC, CA, 1981", "-", asList("LIVE")));

        assertEquals("BRASS IN POCKET - LIVELY - FOO",
                removeContentAfterMarkerIfContainsWord("BRASS IN POCKET - LIVELY - FOO", "-", asList("LIVE")));
    }

    @Test
    public void removeContentBetweenCharsTest() {
        String result = removeContentBetweenChars("STEPHEN 'TIN TIN' DUFFY", '\'');
        assertEquals("STEPHEN DUFFY", result);

        result = removeContentBetweenChars("STEPHEN 'TIN DUFFY", '\'');
        assertEquals("STEPHEN 'TIN DUFFY", result);

        result = removeContentBetweenChars("STEPHEN 'TIN TIN' DUFFY'S", '\'');
        assertEquals("STEPHEN 'TIN TIN' DUFFY'S", result);

        result = removeContentBetweenChars("STEPHEN \"TIN TIN\" DUFFY", '"');
        assertEquals("STEPHEN DUFFY", result);
    }

    @Test
    public void escapeIfRegexTest() {
        assertEquals(escapeIfRegex("("), "\\(");
        assertEquals(escapeIfRegex(")"), "\\)");
    }

    @Test
    public void escapeIfContainsRegexTest() {
        assertEquals("c\\+c", escapeIfContainsRegex("c+c") );
        assertEquals("c,c", escapeIfContainsRegex("c,c") );
    }

    @Test
    public void findFirstTokenContainedTest() {
        assertThat(findFirstTokenContained("THERE IS A FOO", asList("FOO", "BOO")).get(), is("FOO"));
        assertThat(findFirstTokenContained("THERE IS A LOO", asList("FOO", "BOO")), is(Optional.empty()));
    }

    @Test
    public void removeContentAfterMatchTest() {
        String result = removeContentAfterMatch("LIKE DREAMERS DO FT COURTNEY PINE", asList("FT", "FEAT", "FEATURING", "GREATEST HITS"));
        assertEquals("LIKE DREAMERS DO", result);
        result = removeContentAfterMatch("GOLD GREATEST HITS", asList("FT", "FEAT", "FEATURING", "GREATEST HITS"));
        assertEquals("GOLD", result);
    }

    @Test
    void removeFirstAndLastCharsIfMatchTest() {
        String regex = "[/\\-:]";
        assertThat(removeFirstAndLastCharsIfMatch(": asdf", regex), Matchers.is("asdf"));
        assertThat(removeFirstAndLastCharsIfMatch(": asdf :", regex), Matchers.is("asdf"));
        assertThat(removeFirstAndLastCharsIfMatch("asdf-", regex), Matchers.is("asdf"));
        assertThat(removeFirstAndLastCharsIfMatch("/ asd/f/", regex), Matchers.is("asd/f"));
    }

    @Test
    void removeFirstAndLastWordsIfMatchTest() {
        assertThat(removeFirstAndLastWordsIfMatch("AND FOO", List.of("AND", "OR", "NOT")), Matchers.is("FOO"));
        assertThat(removeFirstAndLastWordsIfMatch("FOO AND", List.of("AND", "OR", "NOT")), Matchers.is("FOO"));
        assertThat(removeFirstAndLastWordsIfMatch("OR FOO AND", List.of("AND", "OR", "NOT")), Matchers.is("FOO"));
        assertThat(removeFirstAndLastWordsIfMatch("OR FOO AND BOO", List.of("AND", "OR", "NOT")), Matchers.is("FOO AND BOO"));
        assertThat(removeFirstAndLastWordsIfMatch("NOT BOO OR FOO OR", List.of("AND", "OR", "NOT")), Matchers.is("BOO OR FOO"));
        assertThat(removeFirstAndLastWordsIfMatch("FOO BOO AND LOO", List.of("AND", "OR", "NOT")), Matchers.is("FOO BOO AND LOO"));
    }

    @Test
    public void removeCharsTest() {
        assertEquals("FOO", removeChars("FOO....", "\\.{2,}"));
        assertEquals("FOO BOO MOO", removeChars("FOO!!, BOO,:- !MOO?", "(!|\\?|,|:|-)"));
        assertEquals("abc ddd", removeChars("ab'c - ddd", "[^a-zA-Z0-9 ]"));
        assertEquals("ONLY 3 INCHES", removeChars("ONLY! 3\" IN-CHES", "[^a-zA-Z0-9 ]"));
        assertEquals("abc ddd", removeChars("ab'c - ddd", "[^a-zA-Z0-9 ]"));
        assertEquals("ONLY 3:33 INCHES", removeChars("ONLY! 3:33 IN-CHES", "[^a-zA-Z0-9: ]"));
        assertEquals("abc ddd (keep)", removeChars("ab'c - ddd (keep)", "[^a-zA-Z0-9() ]"));
        assertEquals("abcddd", removeChars("ab'c - ddd", "[^a-zA-Z0-9]"));
        assertEquals("ONLY3INCHES", removeChars("ONLY! 3\" IN-CHES", "[^a-zA-Z0-9]"));
    }

    @Test
    public void startsWithAnyTest() {
        assertTrue(startsWithAny("THERE IS A FOO", asList("FOO", "THERE")));
        assertFalse(startsWithAny("THERE IS A FOOL", asList("FOO")));
        assertTrue(startsWithAny("FOOL AND BOO", asList("FOO")));
    }

    @Test
    public void endsWithAnyTest() {
        assertTrue(endsWithAny("THERE IS A FOO", asList("FOO")));
        assertFalse(endsWithAny("THERE IS A FOOL", asList("FOO")));
        assertFalse(endsWithAny("FOO AND BOO", asList("FOO")));
        assertTrue(endsWithAny("FOO AND ABOO", asList("BOO")));
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
    public void firstValidWordsAsListTest() {
        assertThat(
                TextParser.firstValidWordsAsList("TO BE OR NOT TO BE", asList("NOT", "AND", "OR"), 3),
            is(asList("TO", "BE")));

        assertThat(
                TextParser.firstValidWordsAsList("THIS IS VALID", asList("NOT", "AND", "OR"), 3),
                is(asList("THIS", "IS", "VALID")));

        assertThat(
                TextParser.firstValidWordsAsList("THIS NOT", asList("NOT", "AND", "OR"), 3),
                is(asList("THIS")));

        assertThat(
                TextParser.firstValidWordsAsList("WONDERLAND", asList("AND", "OR", "NOT"), 3),
                is(asList("WONDERLAND")));

    }

    private String removeContentBetweenRoundBracketsIfEqualsAny(String s) {
        return removeContentBetweenMarkersIfEqualsAny(s, "(", ")", matches);
    }

    private String removeContentBetweenLastRoundBracketsIfContainsWord(String s) {
        return removeContentBetweenLastMarkersIfContainsWord(s, "(", ")", matches);
    }









































//    @Test
//    public void anyContainsWordTest() {
//        assertFalse(anyContainsWord(asList("SOME FOOLISH", "BOO"), "FOO"));
//        assertTrue(anyContainsWord(asList("SOME FOO", "BOO"), "FOO"));
//        assertFalse(anyContainsWord(asList("FOO", "BOO"), "ROO"));
//    }
//
//    @Test
//    public void endsWithAnyTest() {
//        assertTrue(endsWithAny("THERE IS A FOO", asList("FOO")));
//        assertFalse(endsWithAny("THERE IS A FOOL", asList("FOO")));
//        assertFalse(endsWithAny("FOO AND BOO", asList("FOO")));
//    }
//

//
//    @Test
//    public void startsWithAnyWordTest() {
//        assertFalse(startsWithAnyWord("THERE IS A FOO", matches));
//        assertTrue(startsWithAnyWord("ORIGINAL IS A FOOL", matches));
//        assertTrue(startsWithAnyWord("STYLE OF IS A FOOL", matches));
//        assertFalse(startsWithAnyWord("STYLE IS A FOOL", matches));
//        assertFalse(startsWithAnyWord("NOT ORIGINAL IS A FOOL", matches));
//        assertFalse(startsWithAnyWord("ORIGINALLY FOOL AND BOO", matches));
//    }
//
//    /** =============== splitters =============**/
//
//
//    @Test
//    public void getFirstTokensAsListTest() {
//        assertThat(
//                firstWordsAsList("FIRST SECOND THIRD", 2),
//                containsInAnyOrder("FIRST", "SECOND")
//        );
//
//        assertThat(
//                firstWordsAsList("FIRST SECOND THIRD", 4),
//                containsInAnyOrder("FIRST", "SECOND", "THIRD")
//        );
//
//        assertThat(
//                firstWordsAsList("FIRST SECOND THIRD", 3),
//                containsInAnyOrder("FIRST", "SECOND", "THIRD")
//        );
//    }
//
//    @Test
//    public void getUniqueTokensFromListOfStringsTest() {
//        assertThat(getUniqueTokensFromListOfStrings(asList("A AND B", "B", "A OR B")),
//                containsInAnyOrder("A", "AND", "B", "OR"));
//    }
//
//    /** =============== other =============**/
//

//
//
//    @Test
//    void findAllSequentialStringCombinationsTest() {
//        List<String> result = findAllSequentialStringCombinations(asList("ONE", "TWO", "THREE"));
//        assertThat(result, containsInAnyOrder("ONE", "ONE TWO", "ONE TWO THREE", "TWO", "TWO THREE", "THREE"));
//    }
//
//    @Test
//    public void getContigousMatchesTest() {
//        String s1 = "WHEN IT RAINS IN CALIFORNIA",
//                s2 = "WHEN IT RAINS";
//
//        List<String> expected = asList("WHEN IT RAINS");
//        assertEquals(expected, getContiguousMatches(s1, s2));
//
//        s1 = "WHEN IT RAINS IN SOUTHERN CALIFORNIA";
//        s2 = "WHEN IT RAINS SOUTHERN CALIFORNIA";
//
//        expected = asList("SOUTHERN CALIFORNIA","WHEN IT RAINS");
//        assertEquals(expected, getContiguousMatches(s1, s2));
//
//        s1 = "WHEN IT RAINS AND IT RAINS";
//        s2 = "WHEN IT RAINS IT RAINS";
//
//        expected = asList("WHEN IT RAINS");
//        assertEquals(expected, getContiguousMatches(s1, s2));
//
//        s1 = "SHAKIE BONNIE";
//        s2 = "SHAKIN' STEVENS BONNIE TYLER";
//
//        expected = asList("BONNIE");
//        assertEquals(expected, getContiguousMatches(s1, s2));
//    }
//
//    @Test
//    public void getCommonTokensTest() {
//        assertEquals(2, getCommonTokens("ONE TWO THREE",
//                "ONE THREE").size());
//
//        assertEquals(1, getCommonTokens("ONE TWO THREE",
//                "ONE FOUR").size());
//
//        assertEquals(1, getCommonTokens("NEVER NEVER",
//                "NEVER EVEN TRUE").size());
//    }
//
//    @Test
//    public void getLastContentBetweenTest() {
//        Optional<String> result = getLastContentBetween(" INCLUDES (BRACKET)", "(", ")");
//        assertEquals("BRACKET", result.get());
//
//        result = getLastContentBetween(" INCLUDES BRACKET", "(", ")");
//        assertTrue(!result.isPresent());
//    }
//
//    @Test
//    public void getBracketedContentIfRightBracketIsLastCharTest() {
//        Optional<String> result = getBracketedContentIfRightBracketIsLastChar(" INCLUDES (BRACKET)", true, false, false);
//        assertEquals("BRACKET", result.get());
//
//        result = getBracketedContentIfRightBracketIsLastChar(" INCLUDES BRACKET", true, false, false);
//        assertTrue(!result.isPresent());
//    }
//

//
//
//
//
//
//    @Test
//    void getLastWordTest() {
//        assertThat(getLastWord(""), is(Optional.empty()));
//        assertThat(getLastWord("word").get(), is("word"));
//        assertThat(getLastWord(" word").get(), is("word"));
//        assertThat(getLastWord("word ").get(), is("word"));
//        assertThat(getLastWord(" word ").get(), is("word"));
//        assertThat(getLastWord("get me last word").get(), is("word"));
//        assertThat(getLastWord("give me $33").get(), Matchers.is("$33"));
//
//        assertThat(getLastWord("   "), is(Optional.empty()));
//    }
//
//    @Test
//    void getFirstWordTest() {
//        assertThat(getFirstWord(""), is(Optional.empty()));
//        assertThat(getFirstWord("word").get(), is("word"));
//        assertThat(getFirstWord(" word").get(), is("word"));
//        assertThat(getFirstWord(" word ").get(), is("word"));
//        assertThat(getFirstWord("get me last word").get(), is("get"));
//    }
//
//    @Test
//    void getLastTokenTest() {
//        assertThat(getLastElement(asLinkedHashSet("BOO")).get(), is("BOO"));
//        assertThat(getLastElement(asLinkedHashSet("FOO", "BOO")).get(), is("BOO"));
//        assertFalse(getLastElement(Collections.EMPTY_SET).isPresent());
//    }
}