
package com.iresearch.android.local.cache;

import com.iresearch.android.local.util.Util;

public enum CacheType {
    MEMORY("m"), STORAGE("s");

    private final String mPrefix;

    CacheType(String prefix) {
        Util.nonEmptyArg(prefix, "Prefix must not be empty.");

        mPrefix = prefix;
    }

    public String getPrefix() {
        return mPrefix;
    }

}
