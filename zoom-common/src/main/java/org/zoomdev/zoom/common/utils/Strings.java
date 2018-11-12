package org.zoomdev.zoom.common.utils;

public class Strings {

    /**
     * 是否整个字符串都是一个字符
     *
     * @param src
     * @param c
     * @return
     */
    public static boolean isAll(String src, char ch) {

        for (int i = 0, c = src.length(); i < c; ++i) {
            if (src.charAt(i) != ch) {
                return false;
            }
        }
        return true;

    }

    /**
     * 字符串连接
     *
     * @param sb
     * @param values
     * @param joint  连接符
     * @return
     */
    public static StringBuilder join(StringBuilder sb, Object[] values, String joint) {
        boolean first = true;

        for (Object object : values) {
            if (first) {
                first = false;
            } else {
                sb.append(joint);
            }
            sb.append(object);
        }

        return sb;
    }

}
