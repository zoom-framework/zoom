package org.zoomdev.zoom.common.utils;

import java.io.InputStream;

public interface StreamClassLoader {

    InputStream getStream(String className);

}
