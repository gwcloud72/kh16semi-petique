package com.spring.semi.util;


/**
 * DummyAvatar - 공통 유틸.
 */
public final class DummyAvatar {
    private DummyAvatar() {}

    public static String path(String seed) {
        int idx = 1;
        if (seed != null) {
            idx = Math.floorMod(seed.hashCode(), 12) + 1;
        }
        return "/image/dummy/user" + String.format("%02d", idx) + ".png";
    }
}
