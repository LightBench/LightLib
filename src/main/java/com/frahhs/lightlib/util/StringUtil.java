package com.frahhs.lightlib.util;

public abstract class StringUtil {
    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
