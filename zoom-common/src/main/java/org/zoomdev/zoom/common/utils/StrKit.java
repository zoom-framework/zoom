package org.zoomdev.zoom.common.utils;

public class StrKit {

    /**
     * 首字母大写
     * @param string
     * @return
     */
    public static String upperCaseFirst(String string) {
        if (string == null) return null;
        char[] arr = string.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }

    /**
     * 下划线变成驼峰
     *
     * @return
     */
    public static String toCamel(String str) {
        assert (str != null);
        str = str.toLowerCase();
        String[] names = str.split("_");
        StringBuilder result = new StringBuilder();
        int index = 0;
        for (String string : names) {
            if (string.isEmpty()) {
                continue;
            }
            if (index > 0) {
                char[] arr = string.toCharArray();
                arr[0] = Character.toUpperCase(arr[0]);
                result.append(arr);
            } else {
                result.append(string);
            }
            ++index;
        }
        return result.toString();
    }

    /**
     * 驼峰式变成下划线
     * @param str
     * @return
     */
    public static String toUnderLine(String str) {
        assert (str != null);
        //反向命名
        char[] arr = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0, c = arr.length; i < c; ++i) {
            char ch = arr[i];
            if (Character.isUpperCase(ch)) {
                sb.append("_");
                sb.append(ch);
            } else {
                sb.append(Character.toUpperCase(ch));
            }
        }

        return sb.toString();
    }
}
