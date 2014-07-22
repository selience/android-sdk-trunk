
package com.iresearch.android.local.cache;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A cache that uses {@link WeakHashMap} ensuring to release keys and values
 * when keys are GC approachable
 * 
 * @param <K>
 * @param <V>
 */
public class WeakKeyCache<K, V> extends MapCache<K, V> {

    public WeakKeyCache(Options opts) {
        super(opts);
    }

    @Override
    protected Map<K, V> onCreateMap(MapCache.Options options) {
        final Options opts = (Options)options;
        return new WeakHashMap<K, V>(opts.initialCapacity(), opts.loadFactor());
    }

    public static class Options extends MapCache.CapacityLoadOptions<WeakKeyCache> {

        public Options(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        @Override
        public Class<? extends WeakKeyCache> imp() {
            return WeakKeyCache.class;
        }
    }
}
