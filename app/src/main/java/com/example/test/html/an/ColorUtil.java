package com.example.test.html.an;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.ColorInt;

class ColorUtil {

    @ColorInt public static final int BLACK       = 0xFF000000;
    @ColorInt
    public static final int DKGRAY      = 0xFF444444;
    @ColorInt public static final int GRAY        = 0xFF888888;
    @ColorInt public static final int LTGRAY      = 0xFFCCCCCC;
    @ColorInt public static final int WHITE       = 0xFFFFFFFF;
    @ColorInt public static final int RED         = 0xFFFF0000;
    @ColorInt public static final int GREEN       = 0xFF00FF00;
    @ColorInt public static final int BLUE        = 0xFF0000FF;
    @ColorInt public static final int YELLOW      = 0xFFFFFF00;
    @ColorInt public static final int CYAN        = 0xFF00FFFF;
    @ColorInt public static final int MAGENTA     = 0xFFFF00FF;
    @ColorInt public static final int TRANSPARENT = 0;

    private static final HashMap<String, Integer> sColorNameMap;
    static {
        sColorNameMap = new HashMap<>();
        sColorNameMap.put("black", BLACK);
        sColorNameMap.put("darkgray", DKGRAY);
        sColorNameMap.put("gray", GRAY);
        sColorNameMap.put("lightgray", LTGRAY);
        sColorNameMap.put("white", WHITE);
        sColorNameMap.put("red", RED);
        sColorNameMap.put("green", GREEN);
        sColorNameMap.put("blue", BLUE);
        sColorNameMap.put("yellow", YELLOW);
        sColorNameMap.put("cyan", CYAN);
        sColorNameMap.put("magenta", MAGENTA);
        sColorNameMap.put("aqua", 0xFF00FFFF);
        sColorNameMap.put("fuchsia", 0xFFFF00FF);
        sColorNameMap.put("darkgrey", DKGRAY);
        sColorNameMap.put("grey", GRAY);
        sColorNameMap.put("lightgrey", LTGRAY);
        sColorNameMap.put("lime", 0xFF00FF00);
        sColorNameMap.put("maroon", 0xFF800000);
        sColorNameMap.put("navy", 0xFF000080);
        sColorNameMap.put("olive", 0xFF808000);
        sColorNameMap.put("purple", 0xFF800080);
        sColorNameMap.put("silver", 0xFFC0C0C0);
        sColorNameMap.put("teal", 0xFF008080);

    }

    public static int getHtmlColor(String color) {
        Integer i = sColorNameMap.get(color.toLowerCase(Locale.ROOT));
        if (i != null) {
            return i;
        } else {
            try {
                return convertValueToInt(color, -1);
            } catch (NumberFormatException nfe) {
                return -1;
            }
        }
    }

    public static final int
    convertValueToInt(CharSequence charSeq, int defaultValue)
    {
        if (TextUtils.isEmpty(charSeq)) {
            return defaultValue;
        }

        String nm = charSeq.toString();

        // XXX This code is copied from Integer.decode() so we don't
        // have to instantiate an Integer!

        int value;
        int sign = 1;
        int index = 0;
        int len = nm.length();
        int base = 10;

        if ('-' == nm.charAt(0)) {
            sign = -1;
            index++;
        }

        if ('0' == nm.charAt(index)) {
            //  Quick check for LINK_PATTERN zero by itself
            if (index == (len - 1))
                return 0;

            char    c = nm.charAt(index + 1);

            if ('x' == c || 'X' == c) {
                index += 2;
                base = 16;
            } else {
                index++;
                base = 8;
            }
        }
        else if ('#' == nm.charAt(index))
        {
            index++;
            base = 16;
        }

        return Integer.parseInt(nm.substring(index), base) * sign;
    }
}
