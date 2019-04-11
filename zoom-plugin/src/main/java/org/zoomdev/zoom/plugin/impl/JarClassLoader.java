package org.zoomdev.zoom.plugin.impl;

import org.zoomdev.zoom.common.io.Io;
import org.zoomdev.zoom.http.HttpUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarClassLoader extends ClassLoader {
    private Map<String,byte[]> bytesMap;

    public JarClassLoader() {
        super(JarClassLoader.class.getClassLoader());
    }


    public Set<String> getAllClassNames(){
        return bytesMap.keySet();
    }

    public byte[] getBytes(String key){
        return bytesMap.get(key);
    }



    public static JarClassLoader fromUrls(String[] urls) throws IOException {
        Map<String,byte[]> bytesMap = new HashMap<String, byte[]>();
        for(String url : urls){
            InputStream is = null;
            URL uri = new URL(url);
            try{
                is = uri.openStream();
                fromInputStream(bytesMap,is);
            }finally {
                Io.close(is);
            }
        }
        return new JarClassLoader(bytesMap);

    }


    public static void fromInputStream(Map<String, byte[]> map,InputStream is) throws IOException {
        ZipInputStream inputStream =null;
        try{
            inputStream = new ZipInputStream(is);
            ZipEntry entry = null;
            while ( (entry = inputStream.getNextEntry()) != null ) {
                if (entry.isDirectory()) {
                    continue;
                }
                String name = entry.getName();
                if (name.endsWith(".class") && !name.startsWith("java") && !name.contains("package-info")) {
                    String className = name.substring(0, name.length() - 6)
                            .replace("/", ".").replace("\\", ".");
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    Io.copy(inputStream, byteArrayOutputStream);
                    map.put(className, byteArrayOutputStream.toByteArray());
                }
            }
        }finally {
            Io.close(inputStream);
        }
    }





    public JarClassLoader(Map<String,byte[]> map) {
        this();
        bytesMap = map;
    }
    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        //加载完当期类还会去加载该类的父类
        if(className.startsWith("java")||className.startsWith("javax")){
            return super.loadClass(className);
        }
        byte[] classData = bytesMap.get(className);
        if(classData==null){
            return super.loadClass(className);
        }
        //重新加载class

        return defineClass(className, classData, 0, classData.length);
    }
}
