package com.spring.semi.util;


/**
 * DummyImage - 공통 유틸.
 */
public final class DummyImage {
	private DummyImage() {
	}

	public static String path(int seed) {
		int idx = Math.floorMod(seed - 1, 30) + 1;
		return "/image/dummy/pet" + String.format("%02d", idx) + ".jpg";
	}
}
