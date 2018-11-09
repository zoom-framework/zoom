package org.zoomdev.zoom.common.res;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.zoomdev.zoom.common.Destroyable;
import org.zoomdev.zoom.common.filter.Filter;
import org.zoomdev.zoom.common.filter.pattern.PatternFilterFactory;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Classes;
import org.zoomdev.zoom.common.utils.PathUtils;
import org.zoomdev.zoom.common.utils.Visitor;
import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.common.utils.CachedClasses;
import org.zoomdev.zoom.common.utils.Visitor;

/**
 * 资源扫描器 用于在项目启动的时候扫描所有资源 1、扫描所有class并记录 2、扫描jar内部class并记录 3、扫描其他类型文件，并记录
 * 
 * 方便查找
 * 
 * findFile("*.xml"); findFile("*.properties"); findClass("*.controllers.*");
 * 
 * 目前这个版本只支持*,不支持**,也就是*就匹配了模糊路径
 * 
 * 那么 *.controllers.* 会匹配到 如 com.test.controllers.aaa.Test 如
 * com.test.controllers.aaa.bbb.Test
 * 
 * 
 * Q:比如两个jar都有app.properties,怎么区分呢？ 那么这个时候会搜索出来两个结果，根据返回结果的file来判断吧
 * 
 * 
 * 
 * 
 * @author jzoom
 *
 */
public class ResScanner implements Destroyable {

	private static final Log log = LogFactory.getLog(ResScanner.class);
	
	List<ClassRes> classes = new ArrayList<ResScanner.ClassRes>();
	List<Res> jars = new ArrayList<ResScanner.Res>();
	
	public ResScanner() {
	}
 
	/**
	 * 默认不扫描jar文件
	 */
	public static Filter<File> fastFilter = new Filter<File>() {

		@Override
		public boolean accept(File value) {
			return !value.getName().endsWith("jar");
		}

	};
	
	
	
	
	

	public static interface Res {
		/**
		 * 获取实际文件
		 * 
		 * @return
		 */
		File getFile();

		/**
		 * 获取名字,对于class，那么就是类名称
		 * 
		 * @return
		 */
		String getName();

		/**
		 * 获取inputStream,用完要关掉
		 * 
		 * @return
		 */
		InputStream getInputStream() throws IOException;
	}

	static class FileRes implements Res {

		private File file;

		public FileRes(File file) {
			this.file = file;
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public String getName() {
			return file.getName();
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new FileInputStream(file);
		}
		
		
		@Override
		public String toString() {
			return file.getName();
		}

	}
	
	public class JarClassRes extends ClassRes{

		public JarClassRes(String className, File file, ClassLoader classLoader) {
			super(className, file, classLoader);
		}

	
		
		
	}

	public class ClassRes extends FileRes {
		
		private String className;
		private ClassLoader classLoader;
		private Class<?> type;

		public ClassRes(String className, File file ,ClassLoader classLoader) {
			super(file);
			this.classLoader = classLoader;
			this.className = className;
		}
		
		@Override
		public String toString() {
			return className;
		}


		@Override
		public String getName() {
			return className;
		}

		public Class<?> loadClass(){
			try {
				return Class.forName(className, false, classLoader );
			}catch (ClassNotFoundException e) {
				throw new RuntimeException("找不到对应的类"+className,e);
			}
		}

		public Class<?> getType(){
			if(type == null) {
				type = loadClass();
			}
			return type;
		}

		private Field[] fields;

		public Field[] getFields() {
			if(fields==null) {
				fields = CachedClasses.getFields(type);
			}
			return fields;
		}


		private Method[] methods;
		public Method[] getPubMethods() {
			if(methods == null) {
				methods = CachedClasses.getPublicMethods(type);
			}
			return methods;
		}
	}

	private Filter<File> scanFilter;

	private static ResScanner res = new ResScanner();

	public static ResScanner me() {
		return res;
	}

    /**
     *
     * @param classLoader
     * @throws IOException
     */
	public void scan(ClassLoader classLoader) throws IOException {
		scan(classLoader, fastFilter);
	}

    /**
     * 扫描一个jar文件
     * @param is
     * @param classLoader
     * @throws IOException
     */
	public void scan(InputStream is,ClassLoader classLoader) throws IOException {
		ZipInputStream inputStream = null;
		try {
			inputStream = new ZipInputStream(is);
			ZipEntry entry;
			while((entry = inputStream.getNextEntry() ) != null) {
				String name = entry.getName();
				if(name.endsWith("class")) {
					addClass(name.replace(".class", "").replace("/", "."),classLoader, null);
				}
			}
			
		}finally {
			Io.close(inputStream);
		}
		
	}
	/**
	 * 扫描项目所有的资源文件
     * @param classLoader
     * @param filter            如果需要限制文件，自定义filter
	 *
	 * @throws IOException
	 */
	public void scan(ClassLoader classLoader, Filter<File> filter) throws IOException {
		assert (filter != null);

		this.scanFilter = filter;
		/// 如果有缓存，那么清理一下
		clear();
		
		Set<String> pathes = new HashSet<String>();

		Enumeration<URL> enumeration = classLoader.getResources("");
		while (enumeration.hasMoreElements()) {
			URL url = enumeration.nextElement();
			String folder = URLDecoder.decode(url.getFile(), "UTF-8");
			File file = new File(folder);
			if (!file.isDirectory()) {
				continue;
			}
			pathes.add(file.getAbsolutePath());
		}
		
		File lib = PathUtils.getWebInfPath("lib");
		if(!pathes.contains(lib.getAbsolutePath())) {
			pathes.add(lib.getAbsolutePath());
		}
		
		for (String path : pathes) {
			if(log.isInfoEnabled())
				log.info("正在扫描目录"+path);
			scanFoler(path, classLoader,new File(path));
		}



		//检查classpath是否有没有被扫描过得
        scanClassPath(classLoader);



        scanned.clear();
	}
    /**
     * 获取所有的含有 .class文件的路径
     * @return
     */
    private void scanClassPath(ClassLoader classLoader) throws IOException {
        String str =  System.getProperty("java.class.path");
        String[] parts = str.split( ":" );
        for(String part : parts){
            File file = new File(part);
            if(!scanned.contains(file)){
                if(file.isDirectory()){
                    scanFoler(part,classLoader,file);
                }else{
                    parseFile(part,classLoader,file);
                }
            }
        }
    }

    public void clear() {
		classes.clear();
		
	}
	
	@Override
	public void destroy() {
		clear();
		
	}

	

	/**
	 * 根据模式查找class,也会搜索到jar中的class ,注意不支持class中定义的内部class *com.a.b* 匹配含有com.a.b的类
	 * com.a.b/* 匹配com.a.b这个包下的所有类,但是不含子包 com.a.b/** 匹配com.a.b这个包下的所有类,含有子包
	 * 
	 * 
	 * @param pattern
	 * @return
	 */
	public List<ClassRes> findClass(String pattern) {
		Filter<String> filter = PatternFilterFactory.createFilter(pattern);
		return findClass(filter);
	}
	
	public List<ClassRes> findClass(Filter<String> pattern) {
		List<ClassRes> result = new ArrayList<ResScanner.ClassRes>();
		for (ClassRes res : classes) {
			if(pattern.accept(res.getName())) {
				result.add(res);
			}
		}
		return result;
	}
	
	

	/**
	 * 如果你能确定只有一个，那么用className精确匹配 搜索顺序为 class 文件 -> jar文件
	 * 
	 * @param className
	 * @return
	 */
	public Res getClass(String className) {
		assert (!StringUtils.isEmpty(className));
		

		// 资源文件应该没有几个
		for (Res res : classes) {
			if (className.equals(res.getName())) {
				return res;
			}
		}

		return null;
	}

	/**
	 * 如果你能确定只有这么一个文件叫这个名字，那么精确匹配
	 * 
	 * @param name
	 * @return
	 */
	public Res getFile(String name) {
		assert (!StringUtils.isEmpty(name));
		

		// 资源文件应该没有几个
		for (Res res : files) {
			if (name.equals(res.getName())) {
				return res;
			}
		}

		return null;
	}



	/**
	 * 如果上述的搜索条件满足不了，那么就可以自定义搜索
	 * 
	 * @param filter
	 * @return
	 */
	public List<Res> findFile(Filter<Res> filter) {
		List<Res> res = new ArrayList<ResScanner.Res>();
		for (Res file : files) {
			if(filter.accept(file)) {
				res.add(file);
			}
		}
		return res;
	}
	
	/**
	 * 根据模式查找文件 *keyword* 模糊匹配含有keyword的资源 keyword* 模糊匹配以keyword开头的资源 *keyword
	 * 模糊匹配以keyword结尾的资源
	 * 
	 * 
	 * @param pattern
	 * @return
	 */
	public List<Res> findFile(String pattern) {
		
		final Filter<String> patternFilter = PatternFilterFactory.createFilter(pattern);
			
		return findFile(new Filter<ResScanner.Res>() {

			@Override
			public boolean accept(Res value) {
				return patternFilter.accept(value.getName());
			}
		});
	}

	private Set<File> scanned = new HashSet<File>();

	/**
	 * 扫描目录
	 * 
	 * @param root
	 * @param folder
	 * @throws IOException
	 */
	public void scanFoler(String root, ClassLoader classLoader, File folder) throws IOException {
		File[] files = folder.listFiles();
		scanned.add(folder);
		if (files == null) {
			return;
		}

		for (File file : files) {
			if (file.isDirectory()) {
				scanFoler(root,classLoader, file);
			} else {
                scanned.add(file);
                parseFile(root,classLoader,file);
			}
		}
	}
	private void parseFile(String root,ClassLoader classLoader,File file) throws IOException {
        // 文件
        String name = file.getName();
        if(log.isTraceEnabled()) {
            log.trace("扫描到了文件"+file.getAbsolutePath());
        }
        if (name.endsWith("class")) {
            if (scanFilter.accept(file))
                parseClass(root, classLoader, file);
        } else if (name.endsWith("jar")) {
            if (scanFilter.accept(file))
                parseJar(file,classLoader);

        } else {
            if (scanFilter.accept(file))
                parseOther(name, file);
        }
    }

	/**
	 * 解析jar文件
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void parseJar(File file,ClassLoader classLoader) throws IOException {
		if(log.isInfoEnabled()) {
			log.info("正在解析jar文件"+file.getAbsolutePath());
		}
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				// 如果是一个.class文件 而且不是目录
				if (name.endsWith(".class") && !entry.isDirectory()) {
					// 去掉后面的".class" 获取真正的类名
					String className = name.substring(0, name.length() - 6).replace("/", ".");
					// 假设同一个className，则应该以文件中的为准
					addJarClass(className,classLoader, file);
				} else {
					addJarFile(name,classLoader, file);
				}
			}
		} finally {
			if (jarFile != null)
				try {
					jarFile.close();
				} catch (Exception e) {
				}
		}

	}

	/**
	 * 增加一个jar中的文件
	 * 
	 * @param name
	 * @param file
	 */
	private void addJarFile(String name, ClassLoader classLoader, File file) {

	}

	/**
	 * 添加一个class文件
	 * 
	 * @param className
	 * @param file
	 * 
	 */
	private void addClass(String className, ClassLoader classLoader,File file) {

		classes.add(new ClassRes(className, file,classLoader));

	}

	/**
	 * 添加一个jar中的class
	 * 
	 * @param className
     * @param classLoader
     * @param jar
	 * 
	 */
	private void addJarClass(String className, ClassLoader classLoader, File jar) {
		if(className.contains("package-info")) {
			return;
		}
		classes.add( new JarClassRes(className,jar,classLoader) );
	}

	/**
	 * 解析其他的资源
	 * 
	 * @param name
	 * @param file
	 */
	private void parseOther(String name, File file) {
		files.add(new FileRes(file));
	}

	private List<Res> files = new ArrayList<ResScanner.Res>();

	/**
	 * 
	 * @param root
	 * @param file
	 */
	private void parseClass(String root, ClassLoader classLoader, File file) {
		String className = getClassName(root, file.getAbsolutePath());
		if(className.contains("package-info")) {
			return;
		}
		addClass(className, classLoader,file);
	}

	/**
	 * 解析一个class文件对应的class名称
	 * 
	 * @param root
	 *            扫描根目录
	 * @param path
	 *            class文件实际路径 如 root: /path/WEB-INF/classes path:
	 *            /path/WEB-INF/classes/com/jzoom/Test.class 对应的结果为:com.jzoom.Test
	 * @return
	 */
	private static String getClassName(String root, String path) {
		String result = path.substring(root.length(), path.length() - 6).replace(File.separatorChar, '.');
		if (result.startsWith(".")) {
			return result.substring(1);
		}
		return result;
	}

	public Filter<File> getScanFilter() {
		return scanFilter;
	}

	public void setScanFilter(Filter<File> scanFilter) {
		this.scanFilter = scanFilter;
	}

	public void visitClass(Visitor<ClassRes> visitor) {
		for (ClassRes res : classes) {
			visitor.visit(res);
		}
	}

	

}
