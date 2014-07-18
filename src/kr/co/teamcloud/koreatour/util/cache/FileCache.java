package kr.co.teamcloud.koreatour.util.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileCache {
	public FileEntry get(String key);

	public void put(String key, ByteProvider provider) throws IOException;

	public void put(String key, InputStream is) throws IOException;

	public void put(String key, File sourceFile, boolean move) throws IOException;

	public void remove(String key);

	public void clear();
}
