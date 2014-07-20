package kr.co.teamcloud.koreatour.util.cache;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class FileCacheFactory {

	private final String TAG = "FileCacheFactory";
	private static boolean initialized = false;

	private static FileCacheFactory instance = new FileCacheFactory();

	public static void initialize(Context context) {
		if (!initialized) {
			synchronized (instance) {
				if (!initialized) {
					instance.init(context);
					initialized = true;
				}
			}
		}
	}

	public static FileCacheFactory getInstance() {
		if (!initialized) {
			throw new IllegalStateException(
					"Not initialized. You must call FileCacheFactory.initialize() before getInstance()");
		}
		return instance;
	}

	private HashMap<String, FileCache> cacheMap = new HashMap<String, FileCache>();
	private File cacheBaseDir;
	private FileCacheFactory() {

	}

	private void init(Context context) {
		cacheBaseDir = context.getCacheDir();
	}

	public FileCache create(String cacheName, int maxKbSizes) {
		synchronized (cacheMap) {
			FileCache cache = cacheMap.get(cacheName);
			if (cache != null) {
				//throw new FileCacheAleadyExistException(String.format("FileCache[%s] Aleady exists", cacheName));
				Log.w(TAG, String.format("FileCache[%s] Aleady exists", cacheName));
			}

			File cacheDir = new File(cacheBaseDir, cacheName);
			cache = new FileCacheImpl(cacheDir, maxKbSizes);
			cacheMap.put(cacheName, cache);
			return cache;
		}
	}

	public FileCache get(String cacheName) {
		synchronized (cacheMap) {
			FileCache cache = cacheMap.get(cacheName);
			if (cache == null) {
				//throw new FileCacheNotFoundException(String.format("FileCache[%s] not founds.", cacheName));
				Log.w(TAG, String.format("FileCache[%s] Aleady exists", cacheName));
			}
			return cache;
		}
	}

	public boolean has(String cacheName) {
		return cacheMap.containsKey(cacheName);
	}

}
