package util;

import org.opencv.core.Core;

public class CVLoader {
	public static void load() {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

	}
}
