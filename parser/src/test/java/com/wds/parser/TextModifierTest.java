package com.wds.parser;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.wds.parser.TextModifier.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TextModifierTest {

    @Test
    public void nonAlphaNumericCharRemoverTest() {
        assertEquals("FOO", nonAlphaNumericCharRemover.apply("FO-O"));
    }

    @Test
    public void nonAlphaNumericCharRemoverExcludingSpacesTest() {
        assertEquals("FO O", nonAlphaNumericCharRemoverExcludingSpaces.apply("FO -O"));
    }


    @Test
    public void removeCharsTest() {
        String regex = "['()-]";
        assertEquals("REFLEX", removeChars("RE-FLEX", regex));
        assertEquals("GARY US BONDS", removeChars("GARY 'US' BONDS", regex));
        assertEquals("SHAKIN STEVENS", removeChars("SHAKIN' STEVENS", regex));
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
    public void removeSequentialDupesTest() {
        assertEquals("GOOD GOODING", removeSequentialDupes.apply("GOOD GOODING"));
        assertEquals("FOO GOOD", removeSequentialDupes.apply("FOO FOO GOOD"));
        assertEquals("FOO GOOD FOO", removeSequentialDupes.apply("FOO GOOD FOO"));
        assertEquals("GOOD FOO", removeSequentialDupes.apply("GOOD FOO FOO"));
        assertEquals("AND", removeSequentialDupes.apply("AND AND AND"));
        assertEquals("AND OR", removeSequentialDupes.apply("AND AND OR OR"));
        assertEquals("BOO AND GOO AND DOO", removeSequentialDupes.apply("BOO AND AND GOO AND DOO"));
        assertEquals("BOO AND GOO AND DOO", removeSequentialDupes.apply("BOO AND AND GOO AND AND DOO"));
    }

    @Test
    public void replaceParenthesesTest() {
        assertThat(replaceParentheses("some (and) others", " "),
                is("some and others"));
    }

    @Test
    void removeLastCharTest() {
        String regex = "^.*(\\.|;|:)$";
        assertEquals(removeLastChar("DOT.", regex), "DOT");
        assertEquals(removeLastChar("SEMICOLUMN;", regex), "SEMICOLUMN");
        assertEquals(removeLastChar("COLUMN:", regex), "COLUMN");
        assertEquals("PRIMAL SCREAM/IRVINE WELSH", removeLastChar("PRIMAL SCREAM/IRVINE WELSH &", ".*&$"));
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
//        assertEquals(tokenReplacer.apply("SYLVIA (R&B)", TITLE_TOKEN_RENAME_MAP), "SYLVIA"); // this one fails
    }

    @Test
    public void removeSurroundingQuotesTest() {
        assertEquals("IT'S N BLOWING'", removeSurroundingQuotes("IT'S 'N' BLOWING'"));
        assertEquals("IT'S N WORD", removeSurroundingQuotes("IT'S \"N\" WORD"));
        assertEquals("IT'S 'N WORD", removeSurroundingQuotes("IT'S 'N WORD"));
        assertEquals("NO MORE I LOVE YOU'S", removeSurroundingQuotes("NO MORE \"I LOVE YOU'S\""));
        assertEquals("ALIVE 'N KICKIN'", removeSurroundingQuotes("ALIVE 'N KICKIN'"));
    }

    @Test
    public void removeIfFirstCharMatchesRegexTest() {
        assertEquals("FOO", removeIfFirstCharMatchesRegex("?FOO", "^(\\?|!).*$"));
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
        assertEquals("FOO", removeIfLastCharMatchesRegex("FOO?", "^.*(\\?|!)$"));
        assertEquals("FOO", removeIfLastCharMatchesRegex("FOO!", "^.*(\\?|!)$"));
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

        assertEquals("NIGHT BOAT TO CAIRO",
                removeBracketedContent("NIGHT BOAT TO CAIRO (WORK REST AND PLAY) (EP)", true, false, false));

        assertEquals("SOMETHING GOOD",
                removeBracketedContent("SOMETHING [BAD AND] GOOD", false, true, true ));

        assertEquals("SOMETHING GOOD",
                removeBracketedContent("SOMETHING {BAD AND} GOOD", false, true, true));

        assertEquals("SOMETHING VERY GOOD",
                removeBracketedContent("SOMETHING (BAD) [FOO] VERY {BOO} GOOD", true, true, true));
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
    public void removeContentAfterMarkerIfContainsWordTest() {
        assertEquals("WONDERFUL CHRISTMASTIME",
                removeContentAfterMarkerIfContainsWord("WONDERFUL CHRISTMASTIME - 2011 REMASTER", "-", asList("REMASTER", "REMASTERED")));

        assertEquals("BRASS IN POCKET",
                removeContentAfterMarkerIfContainsWord("BRASS IN POCKET - LIVE - THE SANTA MONICA CIVIC, CA, 1981", "-", asList("LIVE")));

        assertEquals("BRASS IN POCKET - LIVELY - FOO",
                removeContentAfterMarkerIfContainsWord("BRASS IN POCKET - LIVELY - FOO", "-", asList("LIVE")));
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
    public void removeContentAfterMatchTest() {
        String result = removeContentAfterMatch("LIKE DREAMERS DO FT COURTNEY PINE", asList("FT", "FEAT", "FEATURING", "GREATEST HITS"));
        assertEquals("LIKE DREAMERS DO", result);
        result = removeContentAfterMatch("GOLD GREATEST HITS", asList("FT", "FEAT", "FEATURING", "GREATEST HITS"));
        assertEquals("GOLD", result);
    }

    @Test
    void removeFromStartOrEndIfMatchTest() {
        assertThat(removeFromStartOrEndIfMatch("JOHN AND YOKO AND THE PLASTIC ONO BAND", List.of("&", "AND")), CoreMatchers.is("JOHN AND YOKO AND THE PLASTIC ONO BAND"));
        assertThat(removeFromStartOrEndIfMatch("& FOO", List.of("&", "AND")), CoreMatchers.is("FOO"));
        assertThat(removeFromStartOrEndIfMatch("FOO &", List.of("&", "AND")), CoreMatchers.is("FOO"));
        assertThat(removeFromStartOrEndIfMatch("& FOO &", List.of("&", "AND")), CoreMatchers.is("FOO"));
    }

    @Test
    public void removeContentBetweenRoundBracketsIfEqualsAnyTest() {

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
    public void removeBracesTest() {
        assertThat(removeBraces("foo (this)"), CoreMatchers.is("foo this"));
        assertThat(removeBraces("foo [this]"), CoreMatchers.is("foo this"));
        assertThat(removeBraces("foo {this}"), CoreMatchers.is("foo this"));
        assertThat(removeBraces("foo {this} and (that)"), CoreMatchers.is("foo this and that"));
    }

    @Test
    void addApostropheIfMissingTest() {
        assertThat(addApostropheIfMissing("B-52S"), CoreMatchers.is("B-52'S"));
        assertThat(addApostropheIfMissing("COOLS"), CoreMatchers.is("COOLS"));
    }

    @Test
    void removeAccentsAndConvertToUpperTest() {
        assertEquals(removeAccentsAndConvertToUpper("tréma"), "TREMA");
        assertThat(StringUtils.stripAccents("MOTÖRHEAD"), CoreMatchers.is("MOTORHEAD"));
        assertThat(StringUtils.stripAccents("SAD CAFÉ"), CoreMatchers.is("SAD CAFE"));
    }

    private String removeContentBetweenRoundBracketsIfEqualsAny(String s) {
        return removeContentBetweenMarkersIfEqualsAny(s, "(", ")", matches);
    }

    private String removeContentBetweenLastRoundBracketsIfContainsWord(String s) {
        return removeContentBetweenLastMarkersIfContainsWord(s, "(", ")", matches);
    }

    private static final Map<String, String> TITLE_TOKEN_RENAME_MAP = Collections.unmodifiableMap(
            Stream.of(
                    new AbstractMap.SimpleEntry<>(" & ", " AND "),
                    new AbstractMap.SimpleEntry<>("(R&B)", ""),
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
}