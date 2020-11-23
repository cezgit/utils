package com.wds.parser;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wds.parser.TextParser.*;
import static java.lang.String.format;

public class TextModifier {

    public static Function<String, String> nonAlphaNumericCharRemover = s -> s.replaceAll("[^a-zA-Z0-9]", StringUtils.EMPTY);
    public static Function<String, String> nonAlphaNumericCharRemoverExcludingSpaces = s -> fixMultiSpaces(s.replaceAll("[^a-zA-Z0-9 ]", StringUtils.EMPTY));
    public static BiFunction<String, String, String> nonAlphaNumericCharRemoverExceptOtherRegex = (s, exceptionsRegex) -> s.replaceAll("[^a-zA-Z0-9"+exceptionsRegex+"]", StringUtils.EMPTY);
    public static Function<String, String> removeSequentialDupes = (String s) -> s.replaceAll("(?i)\\b([a-z]+)\\b(?:\\s+\\1\\b)+", "$1");

    /**
     * remove chars from s based on the regex
     * @param s
     * @param regex
     * @return
     */
    public static String removeChars(String s, String regex) {
        return fixMultiSpaces(s.replaceAll(regex, StringUtils.EMPTY));
    }

    public static String replaceParentheses(String s, String replacement) {
        return fixMultiSpaces(s.replace("(", replacement).replace(")", replacement));
    }

    /**
     * remove the last char of s if it matches the regex
     * @param s
     * @return
     */
    public static String removeLastChar(String s, String regex) {
        if(s.matches(regex)) //
            return StringUtils.chop(s).trim();
        return s;
    }

    /**
     * matches keys anywhere in s and replaces them with the corresponding values
     * @param s
     * @return
     */
    public static String replaceAll(String s, Map<String, String> replaceMap) {
        for(String key: replaceMap.keySet())
            s = s.replaceAll(key, replaceMap.get(key));
        return s;
    }

    /**
     * replace word tokens from map.keys if they exist in s
     * WARNING: make sure regex chars in map.keys are escaped!
     */
    public static BiFunction<String, Map<String, String>, String> tokenReplacer = (s, map) -> {
        Set<String> escapedSet = map.keySet().stream().map(TextModifier::escapeIfContainsRegex).collect(Collectors.toSet());
        String regex = TextParser.getEqualsAnyRegex(new ArrayList(escapedSet));
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        try {
            while (m.find()) {
                String toReplace = m.group();
                m.appendReplacement(sb, map.get(toReplace));
            }
        } catch(NullPointerException e) {
            throw new RuntimeException("TOKEN MAP keys might contain unescaped regex chars: "+map.keySet());
        }
        m.appendTail(sb);
        return sb.toString();
    };

    /**
     * remove single or double surrounding quotes
     * single quotes are only removed around strings not containing spaces - to avoid clobbering of names like WE'VE GOT A FUZZBOX AND WE'RE GONNA USE IT
     * double quotes are removed around any type of strings
     */
    public static String removeSurroundingQuotes(String s) {
        s = fixMultiSpaces(s.replaceAll("'([^\\s]+)'", "$1"));
        return fixMultiSpaces(s.replaceAll("\"(.*)\"", "$1"));
    }

    /**
     * remove the first char of a string s if char matches the regex
     * @param s
     * @param regex
     * @return
     */
    public static String removeIfFirstCharMatchesRegex(String s, String regex) {
        if(s.length() <= 1)
            return s;
        return s.matches(regex) ?
                s.substring(1).trim() :
                s;
    }

    /**
     * remove token if string starts with it
     * @param s
     * @return
     */
    public static String removeIfStartsWithToken(String s, String token) {
        if(s.length() <= token.length())
            return s;
        if(s.startsWith(token)) {
            return s.replaceFirst(token, StringUtils.EMPTY).trim();
        }
        return s;
    }

    /**
     * remove dots from a string but not instances of dots that are consecutive
     * @param s
     * @return
     */
    public static String removeDotsIfNotConsecutive(String s) {
        if(s.length() <= 1)
            return s;
        return s.replaceAll("(?<!\\.)\\.(?!\\.)", StringUtils.EMPTY).trim();
    }

    /**
     * remove the ending (regex) of a string if matched
     * @param s
     * @param regex
     * @return
     */
    public static String removeIfEndsWith(String s, String regex) {
        return fixMultiSpaces(s.replaceAll("^(.*)"+regex+"$", "$1"));
    }

    /**
     * remove last char from s if it ends with charToMatch
     * @param s
     * @return
     */
    public static String removeIfLastCharMatches(String s, String charToMatch) {
        if(s.length() <= 1)
            return s;
        return s.endsWith(charToMatch) ? StringUtils.chop(s) : s;
    }

    /**
     * remove the last char of a string s if char matches the regex
     * @param s
     * @param regex
     * @return
     */
    public static String removeIfLastCharMatchesRegex(String s, String regex) {
        if(s.length() <= 1)
            return s;
        return s.matches(regex) ?
                StringUtils.chop(s).trim() :
                s;
    }

    /**
     * remove last word from s if it ends with any value
     * @param s
     * @return
     */
    public static String removeIfLastWord(String s, List<String> values) {
        Optional<String> lastWord = getLastWord(s);
        if(!lastWord.isPresent())
            return s;
        return values.contains(lastWord.get()) ?
                s.substring(0, s.lastIndexOf(lastWord.get())).trim() : s;
    }

    /**
     * remove char attached to a word
     * @param s
     * @param ch
     * @return
     */
    public static String removeCharAttachedToWord(String s, String ch) {
        return s.replaceAll(format("\\B%s\\b|\\b%s\\B", ch, ch), StringUtils.EMPTY);
    }

    /**
     * replace all multi spaces with one space
     * @param s
     * @return
     */
    public static String fixMultiSpaces(String s) {
        return s.replaceAll(" +", StringUtils.SPACE).trim();
    }

    /**
     * replace repeating chars with one char
     * @param s
     * @return
     */
    public static String fixMultiChars(String s, String charToFixAsRegex) {
        if(s.length() <= 1 || s.replaceAll(charToFixAsRegex, StringUtils.EMPTY).trim().length() <= 1)
            return s;
        return s.replaceAll("(?s)("+charToFixAsRegex+")\\1+", "$1");
    }

    /**
     * remove bracketed content from string s, for the brackets marked as true
     * @param s
     * @param round
     * @param square
     * @param curly
     * @return
     */
    public static String removeBracketedContent(
            String s, boolean round, boolean square, boolean curly) {

        String result = s.trim();

        if(round) {
            result = result.replaceAll("[\\(].*[\\)]", StringUtils.SPACE);
        }
        if(square) {
            result = result.replaceAll("[\\[].*[\\]]", StringUtils.SPACE);
        }
        if(curly) {
            result = result.replaceAll("[\\{].*[\\}]", StringUtils.SPACE);
        }
        return fixMultiSpaces(result);
    }

    /**
     * remove content between 2 chars if they are identical to any string in the list
     * @param s
     * @param from
     * @param to
     * @param matches
     * @return
     */
    public static String removeContentBetweenMarkersIfEqualsAny(String s, String from, String to, List<String> matches) {

        String regex = getEqualsAnyRegex(matches);
        return fixMultiSpaces(s.replaceAll("(?s)"+escapeIfRegex(from)+regex+"?"+escapeIfRegex(to), StringUtils.EMPTY));
    }

    /**
     * remove content between 2 chars if they start with any string in the list
     * @param s
     * @param from
     * @param to
     * @param matches
     * @return
     */
    public static String removeContentBetweenMarkersIfContentStartWithAny(String s, String from, String to, List<String> matches) {
        String regex = "("+matches.stream().collect(Collectors.joining("|"))+")"+".*";
        return fixMultiSpaces(s.replaceAll(escapeIfRegex(from)+regex+escapeIfRegex(to)+".*?", StringUtils.EMPTY));
    }

    /**
     * remove content after a marker if it equals any of the strings in the list
     * @param s
     * @param separator
     * @param matches
     * @return
     */
    public static String removeContentAfterMarkerIfEqualsAny(String s, String separator, List<String> matches) {
        int index = s.indexOf(separator);
        if(index < 0) return s;

        String content = s.substring(index + separator.length()).trim();
        if(equalsAny(content, matches)) {
            return s.substring(0, index).trim();
        }
        return s;
    }

    /**
     * remove content between last instance of 2 markers if it contains any of the words in the list
     * @param s
     * @param from
     * @param to
     * @param words
     * @return
     */
    public static String removeContentBetweenLastMarkersIfContainsWord(String s, String from, String to, List<String> words) {
        if(s.endsWith(to)) {
            int lastIndex = s.lastIndexOf(from);
            if(lastIndex >= 0) {
                String token = s.substring(lastIndex+1, s.indexOf(to, lastIndex)).trim();
                if (containsAnyWord(token, words))
                    return removeContentBetweenMarkers(s, from, Optional.of(to));
            }
        }
        else {
            int lastIndex = s.lastIndexOf(from);
            if(lastIndex >= 0 && s.indexOf(to, lastIndex) < 0) {
                String token = s.substring(lastIndex + 1).trim();
                if (containsAnyWord(token, words))
                    return removeContentBetweenMarkers(s, from, Optional.empty());
            }
        }

        return s;
    }

    /**
     * remove content between 2 markers or after the first marker if second marker is not specified
     * @param s
     * @param from
     * @param to
     * @return
     */
    public static String removeContentBetweenMarkers(String s, String from, Optional<String> to) {
        int lastIndex = s.lastIndexOf(from);
        if(lastIndex >= 0)
            if(to.isPresent())
                return s.endsWith(to.get()) ? s.substring(0, lastIndex).trim() : s;
            else
                return s.substring(0, lastIndex).trim();
        return s;
    }

    /**
     * remove content after a marker if it contains any of the words in the list
     * @param s
     * @param marker
     * @param words
     * @return
     */
    public static String removeContentAfterMarkerIfContainsWord(String s, String marker, List<String> words) {
        int index = s.indexOf(marker);
        if(index < 0) return s;

        String content = s.substring(index + marker.length()).trim();
        if(containsAnyWord(content, words)) {
            return s.substring(0, index).trim();
        }
        return s;
    }

    /**
     * return escaped strings if they are regex strings
     * @param s
     * @return
     */
    public static String escapeIfRegex(String s) {
        List<String> specialChars = List.of("(",")","[","]","{","}","+","?");
        return specialChars.contains(s) ? format("\\%s",s) : s;
    }

    public static String escapeIfContainsRegex(String s) {
        List<String> specialChars = List.of("(",")","[","]","{","}","+","?");
        return s.codePoints()
                .mapToObj(c -> specialChars.contains(Character.toString(c)) ? "\\"+(char)c : StringUtils.EMPTY+(char)c)
                .collect(Collectors.joining());
    }

    /**
     * remove the content after any match form the list
     * @param s
     * @param matches
     * @return
     */
    public static String removeContentAfterMatch(String s, List<String> matches) {
        String splitter = "|";
        String result = fixMultiSpaces(s.replaceAll(getEqualsAnyRegex(matches), splitter));
        return getFirstToken(result, "\\"+splitter).trim();
    }

    /**
     * remove the first and last match from s
     * @param s
     * @param matches
     * @return
     */
    public static String removeFromStartOrEndIfMatch(String s, List<String> matches) {
        if(startsWithAny(s, matches) || endsWithAny(s, matches)) {
            String regex = format("(%s)", matches.stream().map(m -> m+"\\s").collect(Collectors.joining("|")));
            s = s.replaceFirst(regex, StringUtils.EMPTY);
            return TextModifier.removeIfLastWord(s, matches);
        }
        return s;
    }

    /**
     * if s ends in a digit followed by an "S" - 80S - put a quote between the last digit and the S - 80'S
     * @param s
     * @return
     */
    public static String addApostropheIfMissing(String s) {
        if(s.matches("^.*\\dS$"))
            return StringUtils.chop(s)+ "'" + s.charAt(s.length()-1);
        return s;
    }

    /**
     * strip all accents and convert to upper case
     * @param s
     * @return
     */
    public static String removeAccentsAndConvertToUpper(String s) {
        if (StringUtils.isEmpty(s))
            return s;

        String result = StringUtils.stripAccents(s);
        return fixMultiSpaces(result.toUpperCase());
    }

    public static String removeBraces(String s) {
        return fixMultiSpaces(s.replaceAll("(\\[|\\{|\\(|\\)|\\}|\\])", StringUtils.SPACE));
    }

    public static Function<Parenthesis, Tuple2<Character, Character>> parens = (pType) -> {
        char openParen = '(';
        char closeParen = ')';
        if(pType == Parenthesis.SQUARE) {
            openParen = '[';
            closeParen = ']';
        }
        else if(pType == Parenthesis.CURLY) {
            openParen = '{';
            closeParen = '}';
        }
        return Tuple.of(openParen, closeParen);
    };

    public static BiFunction<String, Parenthesis, String> removeAllParensIfUnmatched = (a, pType) -> {
        if (a.isEmpty())
            return a;

        Tuple2<Character, Character> parenthesis = parens.apply(pType);
        long openedCount = a.chars().filter(ch -> ch == parenthesis._1).count();
        long closedCount = a.chars().filter(ch -> ch == parenthesis._2).count();

        if(openedCount != closedCount) {
            return removeChars(a, "[\\"+parenthesis._1+"\\"+parenthesis._2+"]");
        }
        return a;
    };
}
