package com.wds.parser;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public final class TextParser {

    private TextParser() {}

    public static final String nonAlphaNumericAndSpace = "[^a-zA-Z0-9 ]";

    public static Function<String, String> nonAlphaNumericCharRemover = s -> fixMultiSpaces(s.replaceAll(nonAlphaNumericAndSpace, StringUtils.EMPTY));

    /**
     * return true if s contains any value from the list
     * @param s
     * @param values
     * @return
     */
    public static boolean containsAny(String s, List<String> values) {
        return values.stream().parallel().anyMatch(s::contains);
    }

    /**
     * return true if s contains any word match
     * @param s
     * @param values
     * @return
     */
    public static boolean containsAnyWord(String s, List<String> values) {
        String regex = getContainsAnyRegex(values);
        return s.matches(regex);
    }

    /**
     * return true if s equals any value from the list
     * @param s
     * @param values
     * @return
     */
    public static boolean equalsAny(String s, List<String> values) {
        return values.stream().parallel().anyMatch(s::equals);
    }

    /**
     * returns true if s matches exactly all the values built as a regex expression
     * @param s
     * @param values
     * @return
     */
    public static boolean equalsAnyWord(String s, List<String> values) {
        String regex = getEqualsAnyRegex(values);
        return s.matches(regex);
    }

    /**
     * get the regex for matching anywhere within a string any of the list values
     * @param values
     * @return
     */
    public static String getContainsAnyRegex(List<String> values) {
        return "^.*?" + getEqualsAnyRegex(values) + ".*$";
    }

    /**
     * get the regex for matching exactly any of the list values
     * @param values
     * @return
     */
    public static String getEqualsAnyRegex(List<String> values) {
        StringBuffer buff = new StringBuffer("\\b(");
        values.forEach(m -> buff.append(m + "|"));
        buff.setLength(buff.length() - 1);
        buff.append(")\\b");
        return buff.toString();
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
        Set<String> escapedSet = map.keySet().stream().map(TextParser::escapeIfContainsRegex).collect(Collectors.toSet());
        String regex = getEqualsAnyRegex(new ArrayList(escapedSet));
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
     */
    public static String removeSurroundingQuotes(String s) {
        s = fixMultiSpaces(s.replaceAll("'([^\\s]+)'", "$1"));
        return fixMultiSpaces(s.replaceAll("\"(.*)\"", "$1"));

    }

    /**
     * matches tokens in s based on regex and returns a list of fragments broken on tokens
     * it also removes dots from tokens produced when FT. is used as a separator
     * @param s
     * @param regex - examples: "[&,()/]" or "\\b(SEP|SEPS)\\b"
     * @return
     */
    public static List<String> getTokensAsList(String s, String regex) {
        return asList(s.replaceAll(regex, "|")
                .split("\\|")).stream()
                .filter(StringUtils::isNotBlank)
                .map(t -> removeIfStartsWithToken(t, ".")) // FT. BOO or FEAT. BOO will result in . BOO so we don't want the dot
                .map(String::trim)
                .collect(Collectors.toList());
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
     * return true if any string in the list contains s
     * @param list
     * @param s
     * @return
     */
    public static boolean anyContains(List<String> list, String s) {
        return list.stream().parallel().anyMatch(l -> l.contains(s));
    }

    /**
     * break string s by spaces - one or more spaces - into tokens
     * @param s
     * @return
     */
    public static List<String> breakBySpace(String s) {
        return asList(s.split("\\s+"));
    }

    /**
     * join all the strings from list using space into one string
     * @param ls
     * @return
     */
    public static String joinAsString(List<String> ls) {
        return ls.size() == 1 ? ls.get(0).trim() : String.join(StringUtils.SPACE, ls);
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
     * return the first token after breaking s by regex
     * @param s
     * @param regex
     * @return
     */
    public static String getFirstToken(String s, String regex) {
        String[] tokens = s.split(regex);
        return tokens.length > 0 ? tokens[0].trim() : s.trim();
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
     * find the first token from a list of tokens contained by s
     * @param s
     * @param values
     * @return
     */
    public static Optional<String> findFirstTokenContained(String s, List<String> values) {
        return values.stream().filter(v -> s.contains(v)).findFirst();
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
     * remove the first and last chars from s if they match the regular expression
     * @param s
     * @param regex
     * @return
     */
    @Deprecated // replaced by removeFromStartOrEndIfMatch
    public static String removeFirstAndLastCharsIfMatch(String s, String regex) {
        if(s.matches(format("^%s.*$", regex))) {
            s = s.replaceFirst(regex, StringUtils.EMPTY);
        }
        if(s.matches(format("^.*%s$", regex))) {
            s = s.substring(0, s.length()-1);
        }
        return s.trim();
    }

    /**
     * remove the first and last words from s if they match any string from the list
     * @param s
     * @param matches
     * @return
     */
    @Deprecated // replaced by removeFromStartOrEndIfMatch
    public static String removeFirstAndLastWordsIfMatch(String s, List<String> matches) {
        s = s.replaceFirst("^"+getEqualsAnyRegex(matches), StringUtils.EMPTY);
        s = removeIfLastWord(s, matches);
        return s.trim();
    }

    /**
     * remove the first and last match from s
     * @param s
     * @param matches
     * @return
     */
    public static String removeFromStartOrEndIfMatch(String s, List<String> matches) {
        if(startsWithAny(s, matches) || endsWithAny(s, matches)) {
            s = s.replaceFirst("(&\\s|AND\\s)", StringUtils.EMPTY);
            return removeIfLastWord(s, matches);
        }
        return s;
    }

    /**
     * remove chars from s based on the regex
     * @param s
     * @param regex
     * @return
     */
    public static String removeChars(String s, String regex) {
        return fixMultiSpaces(s.replaceAll(regex, StringUtils.EMPTY));
    }

    /**
     * returns true if s starts with any value from the list
     * @param s
     * @param values
     * @return
     */
    public static boolean startsWithAny(String s, List<String> values) {
        return values.stream().parallel().anyMatch(s::startsWith);
    }

    /**
     * returns true if s ends with any value from the list
     * @param s
     * @param values
     * @return
     */
    public static boolean endsWithAny(String s, List<String> values) {
        return values.stream().parallel().anyMatch(s::endsWith);
    }

    /**
     * return the last element of a Set
     * @param l
     * @return
     */
    public static Optional<String> getLastElement(List<String> l) {
        return l.isEmpty() ? Optional.empty() : Optional.of(l.get(l.size()-1));
    }

    /**
     * return the last word from a space separated string
     * @param s
     * @return
     */
    public static Optional<String> getLastWord(String s) {
        if(StringUtils.isBlank(s)) return Optional.empty();
        List<String> words = breakBySpace(s.trim());
        return Optional.of(words.get(words.size()-1));
    }

    /**
     * return the first non blank word from a space separated string
     * @param s
     * @return
     */
    public static Optional<String> getFirstWord(String s) {
        if(StringUtils.isBlank(s)) return Optional.empty();
        return breakBySpace(s.trim()).stream()
                .filter(StringUtils::isNotBlank)
                .findFirst();
    }

    /**
     * count the number of words separated by spaces
     * @param s
     * @return
     */
    public static int countWords(String s) {
        return StringUtils.isBlank(s) ? 0 : s.split("\\s+").length;
    }

    /**
     * count the number of words from the list
     * @param list
     * @return
     */
    public static int countWords(List<String> list) {
        return countWords(joinAsString(list));
    }

    /**
     * return the first words specified by the count arg
     * @param s
     * @param count
     * @return
     */
    public static String firstWordsAsString(String s, Integer count) {
        return count >= countWords(s) ? s : String.join(StringUtils.SPACE, firstWordsAsList(s, count));
    }

    /**
     * return the first words from a string
     * @param s
     * @param count
     * @return
     */
    public static List<String> firstWordsAsList(String s, int count) {
        List<String> list = breakBySpace(s);
        return count >= list.size() ? list : list.subList(0, count);
    }
}
