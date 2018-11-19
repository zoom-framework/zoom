package org.zoomdev.zoom.common.config;

import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.json.JSON;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class JsonConfigReader implements ConfigLoader {
    @Override
    public Map<String, Object> load(InputStream is) throws IOException {
        try {
            Map<String, Object> data = JSON.parse(is, Map.class);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                data.put(entry.getKey(), entry.getValue());
            }
            return data;
        }finally {
            Io.close(is);
        }
    }
}
