
package com.iresearch.android.local.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link HashMap} implementation of {@link MapCache}
 */
public class ReferenceCache<K, V> extends MapCache<K, V> {

    public ReferenceCache(Options opts) {
        super(opts);
    }

    @Override
    protected Map<K, V> onCreateMap(MapCache.Options options) {
        final Options opts = (Options)options;
        return new HashMap<K, V>(opts.initialCapacity(), opts.loadFactor());
    }

    public static class Options extends MapCache.CapacityLoadOptions<ReferenceCache> {

        public Options(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        @Override
        public Class<? extends ReferenceCache> imp() {
            return ReferenceCache.class;
        }
    }

}
