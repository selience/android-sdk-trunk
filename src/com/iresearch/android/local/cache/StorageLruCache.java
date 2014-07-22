
package com.iresearch.android.local.cache;

import com.iresearch.android.local.util.DiskLruCache;
import com.iresearch.android.local.util.L;
import com.iresearch.android.local.util.DiskLruCache.Editor;
import com.iresearch.android.local.util.DiskLruCache.Snapshot;
import com.iresearch.android.local.serialization.Serializer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static com.iresearch.android.local.cache.CacheType.STORAGE;
import static com.iresearch.android.local.util.Util.notNull;
import static com.iresearch.android.local.util.Util.notNullArg;
import static com.iresearch.android.local.util.Util.validateKey;

/**
 * Best effort storage cache to use a LRU cache in a directory and a
 * {@link Serializer} to put values on it.
 * 
 * @param <T>
 */
public class StorageLruCache<T> implements Cache<String, T> {
    public static final long MINIMAL_CAPACITY = 1048576;

    public static final String TAG = "carbonite:StorageLruCache";

    private static final int VALUE_INDEX = 0;

    private final File mDir;

    private final long mMaxSize;

    private final Class<T> mType;

    private final Serializer<T> mSerializer;

    private DiskLruCache mCache;

    public StorageLruCache(Options options) {
        notNullArg(options, "You must provide options.");

        mDir = options.directory();
        notNull(mDir, "Directory must not be null.");

        mType = options.type();
        notNull(mType, "Type must not be null.");

        mSerializer = options.serializer();
        notNull(mSerializer, "Serializer must not be null.");

        mMaxSize = Math.max(options.maxSize(), MINIMAL_CAPACITY);
    }

    @Override
    public T get(String key) {
        validateKey(key);
        openCache();
        if (mCache == null) {
            return null;
        }

        Snapshot snapshot = null;
        try {
            snapshot = mCache.get(key);
        } catch (IOException e) {
            L.e(TAG, "Unable to get " + key + " for " + mType, e);
        }

        if (snapshot == null) {
            return null;
        }

        final InputStream in = snapshot.getInputStream(VALUE_INDEX);
        return mSerializer.read(in);
    }

    @Override
    public StorageLruCache<T> set(String key, T value) {
        if (value == null) {
            return this;
        }

        validateKey(key);
        openCache();
        if (mCache == null) {
            return this;
        }

        Editor editor = null;
        try {
            editor = mCache.edit(key);
        } catch (IOException e) {
            L.e(TAG, String.format("Unable to edit %s for %s of type %s ", key, value, mType), e);
        }

        if (editor == null) {
            return this;
        }

        try {
            final OutputStream out = editor.newOutputStream(VALUE_INDEX);
            final boolean success = mSerializer.write(out, value);
            if (!success) {
                throw new IOException("Serializer failed to write.");
            }
            editor.commit();
        } catch (IOException e) {
            e(e, "Unable to set key %s to %s of type %s ", key, value, mType);
            try {
                editor.abort();
            } catch (IOException eAbort) {
                e(eAbort, "Unable to abort editor on key %s for %s of type %s ", key, value, mType);
            }
        }

        return this;
    }

    private void openCache() {
        if (mCache != null) {
            return;
        }

        try {
            mCache = DiskLruCache.open(mDir, 1, 1, mMaxSize);
        } catch (IOException e) {
            final String msg = "Failure to open DiskLruCache for type " + mType;
            L.e(TAG, msg, e);
        }
    }

    private static void e(Throwable th, String tmpl, Object... args) {
        L.e(TAG, String.format(tmpl, args), th);
    }

    public static class Options implements CacheOptions<StorageLruCache> {
        private final File mDirectory;

        private final long mMaxSize;

        private final Serializer mSerializer;

        private final Class mType;

        public <T> Options(File directory, long maxSize, Class<T> type, Serializer<T> serializer) {
            notNullArg(directory, "You must provide a directory.");
            notNullArg(serializer, "You must provide a serializer.");
            notNullArg(type, "You must provide a class of type T.");

            mDirectory = directory;
            mMaxSize = Math.max(maxSize, MINIMAL_CAPACITY);
            if (maxSize != mMaxSize) {
                L.i("carbonite:StorageLruCache",
                        String.format("Using at least %d bytes", MINIMAL_CAPACITY));
            }
            mSerializer = serializer;
            mType = type;
        }

        public File directory() {
            return mDirectory;
        }

        public long maxSize() {
            return mMaxSize;
        }

        public <T> Serializer<T> serializer() {
            return mSerializer;
        }

        public <T> Class<T> type() {
            return mType;
        }

        @Override
        public CacheType cacheType() {
            return STORAGE;
        }

        @Override
        public Class<? extends StorageLruCache> imp() {
            return StorageLruCache.class;
        }
    }

}
