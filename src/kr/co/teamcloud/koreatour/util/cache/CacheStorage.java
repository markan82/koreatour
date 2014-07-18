package kr.co.teamcloud.koreatour.util.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.util.Log;

public class CacheStorage {
	private static final String TAG = "CacheStorage";

	private File cacheDir;
	private Map<String, CacheFile> cacheFileMap;

	private long maxBytesSize;
	private AtomicLong currentBytesSize = new AtomicLong();;

	private ReadWriteLock rwl = new ReentrantReadWriteLock();
	private Lock readLock = rwl.readLock();
	private Lock writeLock = rwl.writeLock();

	public CacheStorage(File cacheDir, long maxBytesSize) {
		this.cacheDir = cacheDir;
		this.maxBytesSize = maxBytesSize;
		this.cacheFileMap = Collections.synchronizedMap(new LinkedHashMap<String, CacheFile>(1024));
		createCacheDirIfNotExists();
		initializing();
	}

	private void createCacheDirIfNotExists() {
		if (cacheDir.exists())
			return;
		cacheDir.mkdirs();
	}

	private void initializing() {
		new Thread(new Initializer()).start();
	}

	private class Initializer implements Runnable {

		@Override
		public void run() {
			writeLock.lock();
			try {
				File[] cachedFiles = cacheDir.listFiles();
				for (File file : cachedFiles) {
					putFileToCacheMap(file);
				}
			} catch (Exception ex) {
				Log.e(TAG, "CacheStorage.Initializer: fail to initialize - "
						+ ex.getMessage(), ex);
			} finally {
				writeLock.unlock();
			}
		}
	}

	public File get(String filename) {
		readLock.lock();
		try {
			CacheFile cachdFile = cacheFileMap.get(filename);
			if (cachdFile == null) {
				return null;
			}
			if (cachdFile.file.exists()) {
				moveHitEntryToFirst(filename, cachdFile);
				return cachdFile.file;
			}
			removeCacheFileFromMap(filename, cachdFile);
			return null;
		} finally {
			readLock.unlock();
		}
	}

	private void moveHitEntryToFirst(String filename, CacheFile cachedFile) {
		cacheFileMap.remove(filename);
		cacheFileMap.put(filename, cachedFile);
	}

	private void removeCacheFileFromMap(String filename, CacheFile cachedFile) {
		currentBytesSize.addAndGet(-cachedFile.size);
		cacheFileMap.remove(filename);
	}

	public void write(String filename, ByteProvider provider) throws IOException {
		writeLock.lock();
		try {
			createCacheDirIfNotExists();
			File file = createFile(filename);
			copyProviderToFile(provider, file);
			putToCachMapAndCheckMaxThresold(file);
		} finally {
			writeLock.unlock();
		}
	}

	private File createFile(String filename) {
		return new File(cacheDir, filename);
	}

	private void copyProviderToFile(ByteProvider provider, File file) throws FileNotFoundException, IOException {
		BufferedOutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(file));
			provider.writeTo(os);
		} finally {
			IOUtils.close(os);
		}
	}

	private void putToCachMapAndCheckMaxThresold(File file) {
		putFileToCacheMap(file);
		checkMaxThresoldAndDeleteOldestWhenOverflow();
	}

	private void putFileToCacheMap(File file) {
		cacheFileMap.put(file.getName(), new CacheFile(file));
		currentBytesSize.addAndGet(file.length());
	}

	private void checkMaxThresoldAndDeleteOldestWhenOverflow() {
		if (isOverflow()) {
			List<Entry<String, CacheFile>> deletingCandidates = getDeletingCandidates();
			for (Entry<String, CacheFile> entry : deletingCandidates) {
				delete(entry.getKey());
			}
		}
	}

	private boolean isOverflow() {
		if (maxBytesSize <= 0) {
			return false;
		}
		return currentBytesSize.get() > maxBytesSize;
	}

	private List<Entry<String, CacheFile>> getDeletingCandidates() {
		List<Entry<String, CacheFile>> deletingCandidates = new ArrayList<Entry<String, CacheFile>>();
		long cadidateFileSizes = 0;
		for (Entry<String, CacheFile> entry : cacheFileMap.entrySet()) {
			deletingCandidates.add(entry);
			cadidateFileSizes += entry.getValue().file.length();
			if (currentBytesSize.get() - cadidateFileSizes < maxBytesSize) {
				break;
			}
		}
		return deletingCandidates;
	}

	public void move(String filename, File sourceFile) {
		writeLock.lock();
		try {
			createCacheDirIfNotExists();
			File file = createFile(filename);
			sourceFile.renameTo(file);
			putToCachMapAndCheckMaxThresold(file);
		} finally {
			writeLock.unlock();
		}
	}

	public void delete(String filename) {
		writeLock.lock();
		try {
			CacheFile cacheFile = cacheFileMap.get(filename);
			if (cacheFile == null)
				return;

			removeCacheFileFromMap(filename, cacheFile);
			cacheFile.file.delete();
		} finally {
			writeLock.unlock();
		}
	}

	public void deleteAll() {
		writeLock.lock();
		try {
			List<String> keys = new ArrayList<String>(cacheFileMap.keySet());
			for (String key : keys) {
				delete(key);
			}
		} finally {
			writeLock.unlock();
		}
	}

	private static class CacheFile {
		public File file;
		public long size;

		public CacheFile(File file) {
			super();
			this.file = file;
			this.size = file.length();
		}
	}
}
