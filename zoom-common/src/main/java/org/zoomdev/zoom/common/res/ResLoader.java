package org.zoomdev.zoom.common.res;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class ResLoader {

	public static File getResourceAsFile(String name) {
		URL url = ResLoader.class.getClassLoader().getResource(name);
		if(url==null)return null;
		return new File(url.getFile());
	}
	
	public static InputStream getResourceAsStream(String name) {
		return ResLoader.class.getClassLoader().getResourceAsStream(name);
	}
}
