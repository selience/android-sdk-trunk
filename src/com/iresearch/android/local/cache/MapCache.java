
package com.iresearch.android.local.cache;

import java.util.Map;
import com.iresearch.android.local.util.L;
import static com.iresearch.android.local.cache.CacheType.MEMORY;
import static com.iresearch.android.local.util.Util.notNullArg;
import static com.iresearch.android.local.util.Util.validateKey;

/**
 * A simple implementation that uses a {@link java.util.Map} to keep strong
 * references to keys and values in Memory
 */
public abstract class MapCache<K, V> implements Cache<K, V> {
    public static final float DEFAULT_LOAD_FACTOR = 0.75F;

    private final Map<K, V> mCache;

    public <O extends MapCache.Options> MapCache(O opts) {
        notNullArg(opts, "Invalid options given");

        mCache = onCreateMap(opts);
    }

    protected abstract Map<K, V> onCreateMap(Options options);

    @Override
    public V get(K key) {
        validateKey(key);
        return mCache.get(key);
    }

    @Override
    public MapCache<K, V> set(K key, V value) {
        if (value == null) {
            return this;
        }

        validateKey(key);
        mCache.put(key, value);
        return this;
    }

    public abstract static class Options<M extends MapCache> implements CacheOptions<M> {

    }

    public abstract static class CapacityLoadOptions<M extends MapCache> extends Options<M> {
        private final int mInitialCapacity;

        private final float mLoadFactor;

        public CapacityLoadOptions(int initialCapacity, float loadFactor) {
            if (initialCapacity < 0) {
                L.i("carbonite:CapacityLoadOptions",
                        "Using initial capacity of 0 as you didn't provide a valid one.");
                initialCapacity = 0;
            }
            mInitialCapacity = initialCapacity;
            if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
                loadFactor = DEFAULT_LOAD_FACTOR;
                L.i("carbonite:CapacityLoadOptions", String.format(
                        "Using %.2f as load factor as you didn't provide a valid one.",
                        DEFAULT_LOAD_FACTOR));
            }
            mLoadFactor = loadFactor;
        }

        public int initialCapacity() {
            return mInitialCapacity;
        }

        public float loadFactor() {
            return mLoadFactor;
        }

        @Override
        public CacheType cacheType() {
            return MEMORY;
        }
    }

}
