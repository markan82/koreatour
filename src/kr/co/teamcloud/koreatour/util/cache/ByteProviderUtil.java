package kr.co.teamcloud.koreatour.util.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ByteProviderUtil {

	public static ByteProvider create(final InputStream is) {
		return new ByteProvider() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				IOUtils.copy(is, os);
			}
		};
	}

	public static ByteProvider create(final File file) {
		return new ByteProvider() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				IOUtils.copy(file, os);
			}
		};
	}

	public static ByteProvider create(final String str) {
		return new ByteProvider() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				IOUtils.copy(str, os);
			}
		};
	}
}