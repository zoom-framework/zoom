package org.zoomdev.zoom.common.utils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BeanUtils {

    /**
     * 将data合并到dest,并返回dest
     * 需要注意的是，并的依据为从data中获取的字段值不为空，
     * 所以尽量不要有int等字段
     * 本方法只是浅层拷贝
     *
     * @param dest
     * @param data
     * @param <T>
     * @return
     */
    public static  <T> T merge(T dest, T data){
        assert (dest != null && data != null);
        Field[] fields = CachedClasses.getFields(dest.getClass());

        try{
            for (Field field : fields) {
                Object value = field.get(data);
                if (value == null) {
                    continue;
                }
                field.set(dest, value);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }


        return dest;
    }

    /**
     * 合并list,如果data比较多，会增加到末尾
     *
     * @param dest
     * @param data
     * @param keys 主键
     * @param <T>
     * @return
     */
    public static <T> List<T> merge(
            List<T> dest,
            List<T> data,
            String... keys
    )  {

        Map<String, T> destMap = CollectionUtils.toMap(
                dest, keys
        );

        Map<String, T> srcMap = CollectionUtils.toMap(
                data, keys
        );

        //merge src to dest

        Iterator<Map.Entry<String, T>> iterator = srcMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, T> entry = iterator.next();
            T d = destMap.get(entry.getKey());
            if(d!=null){
                //merge
                merge(d,entry.getValue());
                iterator.remove();
            }
        }

        dest.addAll(srcMap.values());

        return dest;
    }
}
