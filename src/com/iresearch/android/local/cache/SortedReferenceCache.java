
package com.iresearch.android.local.cache;

import com.iresearch.android.local.util.L;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * A {@link java.util.TreeMap} implementation of {@link MapCache} using natural
 * order
 */
public class SortedReferenceCache<K, V> extends MapCache<K, V> {

    public SortedReferenceCache(Options options) {
        super(options);
    }

    @Override
    protected Map<K, V> onCreateMap(MapCache.Options options) {
        final Options opts = (Options)options;
        final Comparator<K> comparator = opts.comparator();
        if (comparator == null) {
            L.w("carbonite:SortedReferenceCache", "No comparator given will use natural order.");
        }
        return new TreeMap<K, V>(comparator);
    }

    public static class Options<K> extends MapCache.Options<SortedReferenceCache> {
        private final Comparator<K> mComparator;

        public Options(Comparator<K> comparator) {
            mComparator = comparator;
        }

        public Comparator<K> comparator() {
            return mComparator;
        }

        @Override
        public CacheType cacheType() {
            return CacheType.MEMORY;
        }

        @Override
        public Class<? extends SortedReferenceCache> imp() {
            return SortedReferenceCache.class;
        }
    }

}
