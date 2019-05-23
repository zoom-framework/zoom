package org.zoomdev.zoom.common.utils;

import java.io.InputStream;
import java.net.URL;

public interface StreamClassLoader {

    InputStream getStream(String className);

    URL getUrl(String className);
}
