package com.wds.parser;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wds.parser.TextModifier.removeIfStartsWithToken;
import static java.util.Arrays.asList;

public final class TextParser {

    private TextParser() {}

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
     * find the first token from a list of tokens contained by s
     * @param s
     * @param values
     * @return
     */
    public static Optional<String> findFirstTokenContained(String s, List<String> values) {
        return values.stream().filter(v -> s.contains(v)).findFirst();
    }

    /**
     * returns true if s starts with any value from the list
     * @param s
     * @param values
     * @return
     */
    public static boolean startsWithAny(String s, List<String> values) {
        return values.stream().parallel().anyMatch(m -> s.equals(m) || s.startsWith(m+StringUtils.SPACE));
    }

    /**
     * returns true if s ends with any value from the list
     * @param s
     * @param values
     * @return
     */
    public static boolean endsWithAny(String s, List<String> values) {
        return values.stream().parallel().anyMatch(m -> s.equals(m) || s.endsWith(StringUtils.SPACE+m));
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
        return StringUtils.isBlank(s) ? 0 : wordCounter.apply(s);
    }

    /**
     * count the words separated by spaces in a string s
     */
    public static Function<String, Integer> wordCounter = (String s) -> s.split("\\s+").length;

    /**
     * join all the strings from list using space into one string
     * @param ls
     * @return
     */
    public static String joinAsString(List<String> ls) {
        return ls.size() == 1 ? ls.get(0).trim() : String.join(StringUtils.SPACE, ls);
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
