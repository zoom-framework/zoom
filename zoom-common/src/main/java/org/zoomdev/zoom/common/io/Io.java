package org.zoomdev.zoom.common.io;

import java.io.*;


/**
 * 提供io支持
 *
 * @author jzoom
 */
public class Io {

    public static String readString(File file, String charset) throws IOException {
        return readString(new FileInputStream(file), charset);
    }

    public static String readString(InputStream is, String charset) throws IOException {
        return readString(new InputStreamReader(is, charset));
    }

    public static String readString(Reader reader) throws IOException {
        return readString(new BufferedReader(reader));
    }

    public static String readString(BufferedReader reader) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if(first){
                    first = false;
                }else{
                    sb.append("\n");
                }
                sb.append(line);

            }
            return sb.toString();
        } finally {
            close(reader);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }

    public static int read(InputStream inputStream, byte[] buffer) throws IOException {
        return read(inputStream, buffer, 0, buffer.length);
    }

    public static int read(final InputStream input, final byte[] buffer, final int offset, final int length)
            throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        int remaining = length;
        while (remaining > 0) {
            final int location = length - remaining;
            final int count = input.read(buffer, offset + location, remaining);
            if (EOF == count) { // EOF
                break;
            }
            remaining -= count;
        }
        return length - remaining;
    }


    public static final int EOF = -1;




    /**
     * 判断是否是可关闭的
     *
     * @param any
     */
    public static void closeAny(Object any) {
        if (any instanceof Closeable) {
            close((Closeable) any);
        }
    }

    public static void writeString(File file, String content) throws IOException {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(file)
                    ));
            writer.write(content);
        } finally {
            Io.close(writer);
        }
    }

    /**
     * 适合小文件
     * @param src
     * @return
     */
    public static byte[] readBytes(File src) throws IOException {
        assert(src!=null);
        InputStream is = null;
        try{
            is = new FileInputStream(src);
            byte[] bytes = new byte[(int) src.length()];
            read(is,bytes);
            return bytes;
        }finally {
            Io.close(is);
        }

    }

    /**
     * copy streams
     * @param inputStream
     * @param outputStream
     * @throws IOException
     */
    public static void copy(
            InputStream inputStream,
            OutputStream outputStream)  throws IOException{
        byte[] bytes = new byte[4096];
        int len = 0;
        while((len=inputStream.read(bytes,0,4096)) != EOF){
            outputStream.write(bytes,0,len);
        }
    }


    public static void copyAndClose(
            InputStream inputStream,
            OutputStream outputStream) throws IOException{
        try{
            copy(inputStream,outputStream);
        }finally {
            close(inputStream);
            close(outputStream);
        }
    }

    public static void writeBytes(File file,byte[] bytes) throws IOException {
        OutputStream outputStream =null;
        try{
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.flush();
        }finally {
            close(outputStream);
        }

    }
}
