package kr.co.teamcloud.koreatour.util.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public abstract class IOUtils {

	public static String read(InputStream is) throws IOException {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(is);
			StringBuilder builder = new StringBuilder();
			char[] readDate = new char[1024];
			int len = -1;
			while ((len = reader.read(readDate)) != -1) {
				builder.append(readDate, 0, len);
			}
			return builder.toString();
		} finally {
			close(reader);
		}
	}

	public static void copy(InputStream is, OutputStream out)
	throws IOException {
		byte[] buff = new byte[4096];
		int len = -1;
		while ((len = is.read(buff)) != -1) {
			out.write(buff, 0, len);
		}
	}
	public static void copy(File source, OutputStream os) throws IOException {
		BufferedInputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(source));
			IOUtils.copy(is, os);
		} finally {
			IOUtils.close(is);
		}
	}

	public static void copy(InputStream is, File target) throws IOException {
		OutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(target));
			IOUtils.copy(is, os);
		} finally {
			IOUtils.close(os);
		}
	}

	public static void copy(String str, OutputStream os) throws IOException {
		os.write(str.getBytes());
	}

	public static void close(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}
}
