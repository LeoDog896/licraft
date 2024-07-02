package com.leodog896.licraft.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class StringUtils {
    public static String titleCaseWord(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String titleCase(String string) {
        return Arrays.stream(string.split(" ")).map(StringUtils::titleCaseWord).collect(Collectors.joining(" "));
    }

    public static String titleCase(String string, String regex) {
        return Arrays.stream(string.split(regex)).map(StringUtils::titleCaseWord).collect(Collectors.joining(" "));
    }
}
