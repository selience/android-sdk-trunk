
package com.iresearch.android.local;

import android.content.Context;
import com.iresearch.android.local.cache.CacheFactory;
import com.iresearch.android.local.cache.CacheType;
import com.iresearch.android.local.util.L;
import static com.iresearch.android.local.cache.CacheType.MEMORY;

public abstract class CacheManager implements CacheManagerApi {

    /* package */static final class Defaults {
        public static final CacheType TYPE = MEMORY;

        public static final CacheFactory FACTORY = CacheFactoryImp.INSTANCE;

        public static final String STORAGE_DIRECTORY_NAME = CacheManager.class.getSimpleName();

        public static final float LOAD_FACTOR = 0.75f;

        public static final int LRU_SIZE = 256;

        public static final int THREADS = 4;
    }

    public static CarboniteBuilder using(Context context) {
        return new CacheManagerImp.Builder(context.getApplicationContext());
    }

    public static void setLogEnabled(boolean enabled) {
        L.setLogEnabled(enabled);
    }

}
