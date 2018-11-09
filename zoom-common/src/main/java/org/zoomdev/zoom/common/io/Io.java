package org.zoomdev.zoom.common.io;

import java.io.*;


/**
 * 提供io支持
 * 
 * @author jzoom
 *
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
			while ((line = reader.readLine()) != null) {
				sb.append(line);
                sb.append("\n");
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

	public static int read(FileInputStream inputStream, byte[] buffer) throws IOException {
		return read(inputStream, buffer,0,buffer.length);
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


	public static void writeAndClose(OutputStream outputStream, byte[] bytes) throws IOException {
		try {
			outputStream.write(bytes);;
			outputStream.flush();
		}finally {
			Io.close(outputStream);
		}
		
	}

	/**
	 * 判断是否是可关闭的
	 * @param any
	 */
	public static void closeAny(Object any) {
		if(any instanceof Closeable) {
			close( (Closeable) any );
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
}
