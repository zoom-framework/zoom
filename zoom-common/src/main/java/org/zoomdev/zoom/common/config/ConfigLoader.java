package org.zoomdev.zoom.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface ConfigLoader {
	Map<String, Object> load(InputStream is,ConfigValueParser parser) throws IOException;
}
