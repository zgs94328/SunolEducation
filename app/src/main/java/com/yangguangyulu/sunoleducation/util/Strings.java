package com.yangguangyulu.sunoleducation.util;

import android.text.TextUtils;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

public final class Strings {
    /* package */ static final char[] ELLIPSIS_NORMAL = {'\u2026'}; // this is "..."
    public static final String ELLIPSIS_STRING = new String(ELLIPSIS_NORMAL);
    public static final String EMPTY = "";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String SECOND = "\"";


    /***
     * 判断字符串是否null，或""，或"null"
     *
     * @param source
     * @return
     */
    public static boolean isEmptyOrNull(String source) {
        return source == null || "".equals(source) || "null".equals(source);
    }


    public static boolean isBlank(CharSequence text) {
        if (text == null || text.length() == 0) return true;
        for (int i = 0, L = text.length(); i < L; i++) {
            if (!Character.isWhitespace(text.charAt(i))) return false;
        }
        return true;
    }

    public static String trimAll(CharSequence s) {
        if (s == null || s.length() == 0) return Strings.EMPTY;
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0, L = s.length(); i < L; i++) {
            char c = s.charAt(i);
            if (c != ' ') sb.append(c);
        }
        return sb.toString();
    }

    public static boolean isPhoneNum(CharSequence phoneNum) {
        if (phoneNum == null || phoneNum.length() != 11) return false;
        if (!TextUtils.isDigitsOnly(phoneNum)) return false;
//        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
//        return p.matcher(phoneNum).find();
        return true;
    }

    /**
     * 隐私显示手机号
     * 如：130****0000
     */
    public static String proguardPhone(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            return "";
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 隐私显示卡号
     */
    public static String proguardCard(String card) {
        if (card == null || card.length() <= 7) {
            return "";
        }
        StringBuilder star = new StringBuilder();
        for (int i = 0; i < card.length() - 3 - 4; i++) {
            star.append("*");
        }
        return card.substring(0, 3) + star + card.substring(card.length() - 4, card.length());
    }

    /**
     * 隐私显示名字
     */
    public static String getProguardName(String name) {
        if (name == null || name.length() == 0) {
            return "";
        }
        switch (name.length()) {
            case 1:
                return "*";
            case 2:
                return name.substring(0, 1) + "*";
            default:
                String star = "";
                for (int i = 0; i < name.length() - 2; i++) {
                    star += "*";
                }
                return name.substring(0, 1) + star + name.substring(name.length() - 1, name.length());
        }
    }

    public static boolean isTelNum(CharSequence num) {
        if (TextUtils.isEmpty(num)) return false;
        Pattern p = Pattern.compile("(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{8}");
//        Pattern p = Pattern.compile("^(0[0-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$");
        return p.matcher(num).find();
    }

    public static boolean isPhoneStart(CharSequence text) {
        if (!TextUtils.isDigitsOnly(text)) return false;
        Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[^4,\\D])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{0,8}$");
        return p.matcher(text).find();
    }

    public static boolean isChinese(String string) {
        for (int i = 0, L = string.length(); i < L; i++) {
            char c = string.charAt(i);
            if ((c < 0x4e00) || (c > 0x9FA5)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEnglish(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    /**
     * 密码是否过于简单
     */
    public static boolean isSimplePassword(@NonNull String password) {
//        if (TextUtils.isDigitsOnly(password)) return true;
        if (password.length() < 6 || password.length() > 18) return true;
        return false;
    }

    /**
     * 密码是否过于简单,8-20位，有数字和密码
     */
    public static boolean isSimplePassword2(@NonNull String password) {
        if (TextUtils.isEmpty(password)) {
            return true;
        }

        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$";
        return !password.matches(regex);
    }

    /**
     * 判断字符长度是否在范围里面。 当start end 为 -1 时，表示字符长度不考虑上线或下线
     *
     * @param str
     * @param start
     * @param end
     * @return
     */
    public static boolean isLengthInRange(String str, int start, int end) {
        boolean isInRange = true;
        int length = str.length();
        if (start != -1 && length < start) {
            isInRange = false;
        }
        if (end != -1 && end < length) {
            isInRange = false;
        }
        return isInRange;
    }

    public static long toLong(String text, long defaultVal) {
        if (TextUtils.isDigitsOnly(text)) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
            }
        }
        return defaultVal;
    }

    public static int toInt(String text, int defaultVal) {
        if (TextUtils.isDigitsOnly(text)) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
            }
        }
        return defaultVal;
    }

    public static double toFloat(String text, float defaultVal) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException ignored) {
        }
        return defaultVal;
    }

    public static double toDouble(String text, double defaultVal) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ignored) {
        }
        return defaultVal;
    }

    public static String join(Iterable<?> iterable, String separator) {

        // handle null, zero and one elements before building a buffer
        if (iterable == null) {
            return null;
        }
        Iterator<?> iterator = iterable.iterator();
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return toString(first);
        }

        // two or more elements
        StringBuffer buf = new StringBuffer(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String toString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static boolean toBoolean(String property, boolean defaultVal) {
        return property == null ? defaultVal : Boolean.valueOf(property);
    }

    //截取数字
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

}
