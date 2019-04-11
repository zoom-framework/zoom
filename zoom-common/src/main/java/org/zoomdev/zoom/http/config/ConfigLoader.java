package org.zoomdev.zoom.http.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface ConfigLoader {
    Map<String, Object> load(InputStream is) throws IOException;
}
