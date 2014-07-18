package kr.co.teamcloud.koreatour.util.cache;

import android.app.Activity;
import android.os.Bundle;

public class SomeLoadActivity extends Activity {

	private FileCache fileCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FileCacheFactory.initialize(this);
//		if (! FileCacheFactory.getInstance().has(cacheName)) {
//			FileCacheFactory.getInstance().create(cacheName, cacheSize);
//		}
//		fileCache = FileCacheFactory.getInstance().get(cacheName);
	}

	public void load() {
//		FileEntry fileEntry = fileCache.get(key);
//		if (fileEntry != null) {
			// 캐시에서 읽어온 데이터로 처리
//			String data = loadDataFromFile(fileEntry.getFile());
//			processing(data);
//			return;
//		}

		// 실제 데이터 로딩 (실제로는 웹에서 비동기로 읽어오는 등의 코드)
//		String data = loadingDataRealSource();
		// 캐시에 보관
//		fileCache.put(key, ByteProviderUtil.create(dataFile));
		// 처리
//		processing(data);
	}
}
