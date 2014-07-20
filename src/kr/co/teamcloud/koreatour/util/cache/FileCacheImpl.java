package kr.co.teamcloud.koreatour.util.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileCacheImpl implements FileCache {

	private CacheStorage cacheStorage;

	public FileCacheImpl(File cacheDir, int maxKBSizes) {
		long maxBytesSize = maxKBSizes <= 0 ? 0 : maxKBSizes * 1024;
		cacheStorage = new CacheStorage(cacheDir, maxBytesSize);
	}

	@Override
	public FileEntry get(String key) {
		File file = cacheStorage.get(keyToFilename(key));
		if (file == null) {
			return null;
		}
		if (file.exists()) {
			return new FileEntry(key, file);
		}
		return null;
	}

	@Override
	public void put(String key, ByteProvider provider) throws IOException {
		cacheStorage.write(keyToFilename(key), provider);
	}

	@Override
	public void put(String key, InputStream is) throws IOException {
		put(key, ByteProviderUtil.create(is));
	}

	@Override
	public void put(String key, File sourceFile, boolean move) throws IOException {
		if (move) {
			cacheStorage.move(keyToFilename(key), sourceFile);
		} else {
			put(key, ByteProviderUtil.create(sourceFile));
		}
	}

	@Override
	public void remove(String key) {
		cacheStorage.delete(keyToFilename(key));
	}

	private String keyToFilename(String key) {
		String filename = key.replace(":", "_");
		filename = filename.replace("/", "_s_");
		filename = filename.replace("\\", "_bs_");
		filename = filename.replace("&", "_bs_");
		filename = filename.replace("*", "_start_");
		filename = filename.replace("?", "_q_");
		filename = filename.replace("|", "_or_");
		filename = filename.replace(">", "_gt_");
		filename = filename.replace("<", "_lt_");
		return filename;
	}

	@Override
	public void clear() {
		cacheStorage.deleteAll();
	}
}
