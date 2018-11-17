package org.zoomdev.zoom.dao.alias.impl;

import org.apache.commons.lang3.mutable.MutableInt;
import org.zoomdev.zoom.dao.alias.AliasPolicy;
import org.zoomdev.zoom.dao.alias.AliasPolicyFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 自动检测字段的前缀，并以驼峰式来重命名 {@link AliasPolicyFactory}
 * 策略是：
 * 所谓前缀：是第一个下划线前面的部分加上下划线，比如TP_ID,前缀为TP_
 * 以第一个字段出现的前缀作为全部字段的前缀，还有个前提是这个前缀出现的次数最多
 *
 * 当第一个前缀
 *
 * @author jzoom
 */
public class DetectPrefixAliasPolicyFactory implements AliasPolicyFactory {

    public static final AliasPolicyFactory DEFAULT = new DetectPrefixAliasPolicyFactory();


    private DetectPrefixAliasPolicyFactory() {

    }

    // 这个逻辑如果数据库字段比较规范是够用的，不够再说
    @Override
    public AliasPolicy getAliasPolicy(String[] names) {
        Map<String, MutableInt> countMap = new LinkedHashMap<String, MutableInt>();

        for (String name : names) {
            String[] arr = name.split("_");
            String prefixThisColumn = arr[0];
            MutableInt value = countMap.get(prefixThisColumn);
            if (value == null) {
                value = new MutableInt(1);
                countMap.put(prefixThisColumn, value);
            } else {
                value.add(1);
            }
        }
        AliasPolicy aliasPolicy = null;
        // 只有最大的为第一个的才行
        MutableInt first = null;
        String key = null;
        if (countMap.size() == 1) {
            key = countMap.keySet().iterator().next();
            aliasPolicy = new PrefixAliasPolicy(new StringBuilder(key).append("_").toString());
        } else {

            for (Entry<String, MutableInt> entry : countMap.entrySet()) {
                if (first == null) {
                    first = entry.getValue();
                    key = entry.getKey();
                } else {
                    if (first.intValue() > entry.getValue().intValue()) {
                        aliasPolicy = new PrefixAliasPolicy(new StringBuilder(key).append("_").toString());
                    }
                    break;
                }
            }
        }

        if(aliasPolicy!=null){

            return aliasPolicy;
        }


        return CamelAliasPolicy.DEFAULT;
    }

}
