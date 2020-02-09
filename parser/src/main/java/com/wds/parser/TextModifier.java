package com.wds.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.wds.parser.TextParser.*;

public class TextModifier {

    public static String removeNonAlphaNumericChars(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "");
    }
    public static Function<String, String> nonAlphaNumericCharRemover = s -> removeNonAlphaNumericChars(s);


















    public static String removeChars(String s, String regex) {
        String replacement = "";
        return fixMultiSpaces(s.replaceAll(regex, replacement));
    }





    // regex example: "\\b(FT|FEAT|FEATURING|PERFORMED BY)\\b"
    public static String removeWords(String s, String regex) {
        String delimiter = " ";
        return String.join(delimiter, getTokensAsList(s, regex));
    }


    public static String removeAll(String s, Set<String> removables) {
        return joinAsString(getTokensAsList(s, getEqualsAnyRegex(new ArrayList<>(removables))));
    }

    public static String removeLastContentBetweenIfMatchEquals(String s, String from, String to, List<String> matches) {
        if(s.endsWith(to)) {
            int lastIndex = s.lastIndexOf(from);
            if(lastIndex >= 0) {
                String token = s.substring(lastIndex+1, s.indexOf(to, lastIndex)).trim();
                if (equalsAny(token, matches))
                    return removeContentBetweenMarkers(s, from, Optional.of(to));
            }
        }
        return s;
    }

    public static String removeLastContentAfterSeparatorIfMatchEquals(String s, String separator, List<String> matches) {
        int index = s.lastIndexOf(separator);
        if(index < 0) return s;

        String content = s.substring(index + separator.length()).trim();
        if(equalsAny(content, matches)) {
            return s.substring(0, index).trim();
        }
        return s;
    }




//    public static String removeBracketedContentIfRightBracketIsLastChar(String s, boolean round, boolean square, boolean curly) {
//        if(endsWithAny(s, asList(")", "]", "}"))) {
//            if(round && s.endsWith(")"))
//                return removeContentBetweenMarkers(s, "(", Optional.of(")"));
//            else if(square && s.endsWith("]"))
//                return removeContentBetweenMarkers(s, "[", Optional.of(")"));
//            else if(curly && s.endsWith("]}"))
//                return removeContentBetweenMarkers(s, "{", Optional.of(")"));
//
//        }
//        return s;
//    }

    public static String removeContentBetweenChars(String result, char c) {
        long quotes = result.codePoints().filter(s -> s == c).count();
        if(quotes == 2) {
            List<String> l = getTokensAsList(result, String.valueOf(c));
            if(l.size() > 2) {
                return String.join(" ", l.get(0), l.get((2)));
            }
        }
        return result;
    }

    public static String removeSequentialDupes(String s) {
        return s.replaceAll("(?i)\\b([a-z]+)\\b(?:\\s+\\1\\b)+", "$1");
    }

    public static String replaceAllWithSpace(String s, String regex) {
        String replacement = " ";
        return TextParser.fixMultiSpaces(s.replaceAll(regex, replacement));
    }

    public static String replaceParentheses(String s, String replacement) {
        return TextParser.fixMultiSpaces(s.replace("(", replacement).replace(")", replacement));
    }




}
