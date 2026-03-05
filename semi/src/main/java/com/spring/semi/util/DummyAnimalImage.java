package com.spring.semi.util;


/**
 * DummyAnimalImage - 공통 유틸.
 */
public final class DummyAnimalImage {
    private DummyAnimalImage() {}

    public static String path(int seed) {
        int idx = Math.floorMod(seed - 1, 12) + 1;
        return "/image/dummy/animal" + String.format("%02d", idx) + ".jpg";
    }
}
