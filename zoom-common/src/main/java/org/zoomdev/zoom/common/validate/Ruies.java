package org.zoomdev.zoom.common.validate;

import org.zoomdev.zoom.common.json.JSON;
import org.zoomdev.zoom.common.utils.CollectionUtils;
import org.zoomdev.zoom.common.utils.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Ruies {


    private static List<Validator.Biilder> builders = new ArrayList<Validator.Biilder>();


    public static void register(Validator.Biilder builder){
        builders.add(builder);
    }

    public static Validator fromJson(Map<String,Object> data){
        for(Validator.Biilder builder : builders){
            Validator validator = builder.fromJson(data);
            if(validator!=null){
                return validator;
            }
        }
        return null;
    }

    public static List<Validator> createFromJson(
            String content
    ){
        List<Map<String,Object>> data = JSON.parse(content,List.class);

        return CollectionUtils.map(
                data,
                new Converter<Map<String, Object>, Validator>() {
                    @Override
                    public Validator convert(Map<String, Object> data) {
                        return fromJson(data);
                    }
                }
        );


    }

}
