package com.wds.parser;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.math.BigDecimal;

public class CompareUtils {

    // how different is the original from the
    public static int levenshteinDistance(String comparable, String s) {
//        String comparableClean = RegexUtils.removeNonAlphaNumericChars(comparable, " ").replaceAll(" ", "");
//        String sClean = RegexUtils.removeNonAlphaNumericChars(s, " ").replaceAll(" ", "");
        Integer d = new LevenshteinDistance().apply(comparable, s);
        Integer biggestStringLength = Math.max(s.length(), comparable.length());
        return new BigDecimal(((biggestStringLength - d) / (double)biggestStringLength) * 100).intValue();
    }

    public static String longestSubstring(String str1, String str2) {

        StringBuilder sb = new StringBuilder();
        if (str1 == null || str1.isEmpty() || str2 == null || str2.isEmpty())
            return "";

        // java initializes them already with 0
        int[][] num = new int[str1.length()][str2.length()];
        int maxlen = 0;
        int lastSubsBegin = 0;

        for (int i = 0; i < str1.length(); i++) {
            for (int j = 0; j < str2.length(); j++) {
                if (str1.charAt(i) == str2.charAt(j)) {
                    if ((i == 0) || (j == 0))
                        num[i][j] = 1;
                    else
                        num[i][j] = 1 + num[i - 1][j - 1];

                    if (num[i][j] > maxlen) {
                        maxlen = num[i][j];
                        // generate substring from str1 => i
                        int thisSubsBegin = i - num[i][j] + 1;
                        if (lastSubsBegin == thisSubsBegin) {
                            //if the current LCS is the same as the last time this block ran
                            sb.append(str1.charAt(i));
                        } else {
                            //this block resets the string builder if a different LCS is found
                            lastSubsBegin = thisSubsBegin;
                            sb = new StringBuilder();
                            sb.append(str1.substring(lastSubsBegin, i + 1));
                        }
                    }
                }
            }}

        return sb.toString();
    }
}
