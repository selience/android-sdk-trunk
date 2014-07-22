
package com.iresearch.android.local.cache;

public interface CacheOptions<C extends Cache<?, ?>> {
    CacheType cacheType();

    Class<? extends C> imp();
}
