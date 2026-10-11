package com.nnm.bot.core.jmcomic.service.impl;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 按目录中的章节序号、页码自然排序，不使用网络完成顺序或 ZIP 写入顺序。 */
final class JmPageOrder {
    private static final Pattern PART = Pattern.compile("\\d+|\\D+");

    private JmPageOrder() {
    }

    static int compare(String left, String right) {
        String[] a = left.replace('\\', '/').split("/");
        String[] b = right.replace('\\', '/').split("/");
        for (int index = 0; index < Math.min(a.length, b.length); index++) {
            int order = comparePart(a[index], b[index]);
            if (order != 0) return order;
        }
        return Integer.compare(a.length, b.length);
    }

    private static int comparePart(String left, String right) {
        Matcher a = PART.matcher(left);
        Matcher b = PART.matcher(right);
        while (a.find() && b.find()) {
            String x = a.group();
            String y = b.group();
            int order = Character.isDigit(x.charAt(0)) && Character.isDigit(y.charAt(0))
                    ? new BigInteger(x).compareTo(new BigInteger(y))
                    : x.compareToIgnoreCase(y);
            if (order != 0) return order;
        }
        int length = Integer.compare(left.length(), right.length());
        return length != 0 ? length : left.compareTo(right);
    }
}
