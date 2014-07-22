
package com.iresearch.android.local;

import java.io.File;
import android.content.Context;
import com.iresearch.android.local.cache.Cache;
import com.iresearch.android.local.cache.CacheFactory;
import com.iresearch.android.local.cache.CacheOptions;
import com.iresearch.android.local.cache.CacheType;
import com.iresearch.android.local.cache.MemoryLruCache;
import com.iresearch.android.local.cache.StorageLruCache;
import com.iresearch.android.local.serialization.ObjectSerializer;
import com.iresearch.android.local.serialization.Serializer;

import static com.iresearch.android.local.CacheManager.Defaults.LRU_SIZE;
import static com.iresearch.android.local.CacheManagerApi.CacheBuilder;
import static com.iresearch.android.local.cache.StorageLruCache.MINIMAL_CAPACITY;
import static com.iresearch.android.local.cache.StorageLruCache.Options;
import static com.iresearch.android.local.util.Util.illegalState;
import static com.iresearch.android.local.util.Util.newCacheInstance;
import static com.iresearch.android.local.util.Util.notNull;
import static com.iresearch.android.local.util.Util.notNullArg;
import static com.iresearch.android.local.util.Util.obtainValidKey;

/*package*/class CacheFactoryImp<T> implements CacheFactory<String, T> {
    /* package */static final CacheFactoryImp INSTANCE = new CacheFactoryImp();

    @Override
    public Cache<String, T> build(CacheBuilder cacheBuilder) {
        notNullArg(cacheBuilder, "Invalid cache builder given.");

        Cache<String, T> fromOpts = buildFromOpts(cacheBuilder);
        if (fromOpts != null) {
            return fromOpts;
        }
        // Generate defaults:
        return buildDefault(cacheBuilder);
    }

    @SuppressWarnings("unchecked")
    private Cache<String, T> buildDefault(CacheBuilder options) {
        final CacheType cacheType = options.cacheType();

        switch (cacheType) {
            case MEMORY:
                return new MemoryLruCache<String, T>(new MemoryLruCache.Options(LRU_SIZE));
            case STORAGE:
                final Class type = options.type();
                final File dir = buildCacheDir(options.context(), type);
                final Serializer<T> serializer = new ObjectSerializer<T>();
                final Options storageOpts = new Options(dir, MINIMAL_CAPACITY, type, serializer);
                return new StorageLruCache<T>(storageOpts);
            default:
                illegalState(true, "Not yet implemented cache type " + cacheType);
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Cache<String, T> buildFromOpts(CacheBuilder builder) {
        final CacheOptions opts = builder.opts();

        if (opts == null) {
            return null;
        }

        final Class<? extends Cache> type = opts.imp();

        final Cache<String, T> instance = newCacheInstance(type, opts);

        illegalState(!type.isInstance(instance),
                "Unable to instantiate cache, make sure it has a public constructor with proper options type.");

        return instance;
    }

    private static File buildCacheDir(Context context, Class type) {
        File cacheDir = context.getCacheDir();
        notNull(cacheDir, "context.getCacheDir() returned null.");

        cacheDir = new File(cacheDir, CacheManager.Defaults.STORAGE_DIRECTORY_NAME);
        return new File(cacheDir, obtainValidKey(type));
    }
}
