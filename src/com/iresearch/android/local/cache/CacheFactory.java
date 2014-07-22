
package com.iresearch.android.local.cache;

import com.iresearch.android.local.CacheManagerApi;

public interface CacheFactory<K, T> {
    Cache<K, T> build(CacheManagerApi.CacheBuilder options);
}
