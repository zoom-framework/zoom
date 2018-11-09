package org.zoomdev.zoom.common.utils;

import java.io.File;


public class PathUtils {

	private static String webRootPath;
	private static String rootClassPath;

	/**
	 * 获取网站根目录
	 * 
	 * @return
	 */
	public static String getWebRootPath() {
		if (webRootPath == null) {
			try {
				String path = PathUtils.class.getResource("/").toURI().getPath();
				webRootPath = new File(path).getParentFile().getParentFile().getCanonicalPath();
				if (!webRootPath.endsWith(File.separator)) {
					webRootPath = new StringBuilder(webRootPath).append(File.separator).toString();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return webRootPath;

	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static File getWebInfPath(String name) {
		File file =new File( PathUtils.getWebRootPath() + "WEB-INF" + File.separator + name);
		if(!file.exists()){
			file = new File( PathUtils.getWebRootPath() + "src/main/webapp/WEB-INF" + File.separator + name);
		}
		return file;
	}

	/**
	 * 解析路径
	 * * @param key
	 *
	 * @return
	 */
	public static File resolve(String key) {

		return null;
	}
}
