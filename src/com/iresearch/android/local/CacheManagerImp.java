package com.iresearch.android.local;

import android.content.Context;
import com.iresearch.android.local.cache.Cache;
import com.iresearch.android.local.cache.CacheType;
import com.iresearch.android.local.cache.ReferenceCache;
import com.iresearch.android.local.cache.UnmodifiableCache;
import com.iresearch.android.local.cache.WeakKeyCache;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.setThreadPriority;
import static com.iresearch.android.local.CacheManager.Defaults.LOAD_FACTOR;
import static com.iresearch.android.local.CacheManager.Defaults.THREADS;
import static com.iresearch.android.local.cache.CacheType.MEMORY;
import static com.iresearch.android.local.cache.CacheType.STORAGE;
import static com.iresearch.android.local.util.Util.checkedClass;
import static com.iresearch.android.local.util.Util.empty;
import static com.iresearch.android.local.util.Util.illegalAccess;
import static com.iresearch.android.local.util.Util.illegalArg;
import static com.iresearch.android.local.util.Util.len;
import static com.iresearch.android.local.util.Util.newFixedCachedThread;
import static com.iresearch.android.local.util.Util.nonEmpty;
import static com.iresearch.android.local.util.Util.nonEmptyArg;
import static com.iresearch.android.local.util.Util.notNull;
import static com.iresearch.android.local.util.Util.notNullArg;
import static com.iresearch.android.local.util.Util.obtainValidKey;
import static com.iresearch.android.local.util.Util.present;
import static com.iresearch.android.local.util.Util.validateKey;


/*package*/ class CacheManagerImp extends CacheManager {

  private final Cache<String, Cache> mCaches;
  private final ExecutorService mExecutor;

  /*package*/ CacheManagerImp(Cache<String, Cache> caches, ExecutorService executor) {
    mCaches = new UnmodifiableCache<String, Cache>(caches);
    mExecutor = executor;
  }

  // both
  @Override
  public <T> CacheManager set(String key, T value) {
    if (value == null) {
      return this;
    }

    memory(key, value);

    later(ACTION.SET, key, value, cacheFor(STORAGE, checkedClass(value)));

    return this;
  }

  @Override
  public <T> Future<T> get(String key, Class<T> type) {

    final T memoryResult = memory(key, type);

    if (memoryResult != null) {
      return present(memoryResult);
    }

    return later(ACTION.GET, key, null, cacheFor(STORAGE, type));
  }

  // storage

  @Override
  public <T> CacheManager storage(String key, T value) {
    return doSet(key, value, STORAGE);
  }

  @Override
  public <T> T storage(String key, Class<T> type) {
    return doGet(key, type, STORAGE);
  }

  // memory

  @Override
  public <T> CacheManager memory(String key, T value) {
    return doSet(key, value, MEMORY);
  }

  @Override
  public <T> T memory(String key, Class<T> type) {
    return doGet(key, type, MEMORY);
  }

  private <T> T doGet(String key, Class<T> type, CacheType cacheType) {
    validateKey(key);

    final Cache<String, T> cache = cacheFor(cacheType, type);

    if (cache == null) {
      return null;
    }

    return cache.get(key);
  }

  private <T> CacheManager doSet(String key, T value, CacheType cacheType) {
    if (value == null) {
      return this;
    }

    validateKey(key);

    final Cache<String, T> cache = cacheFor(cacheType, checkedClass(value));

    if (cache != null) {
      cache.set(key, value);
    }

    return this;
  }

  private <T> Cache<String, T> cacheFor(CacheType cacheType, Class<T> type) {
    final Cache<String, T> cache = mCaches.get(buildKey(cacheType, type));
    return cache;
  }

  // Async stuff
  private enum ACTION { SET, GET };

  private <T> Future<T> later(ACTION action, String key, T value, Cache<String, T> cache) {
    notNullArg(action, "No action given.");

    // Optimization: don't validate or send to execution if we are setting null
    if (value == null && action == ACTION.SET) {
      return null; // EEE: We don't use futures for set, we might at some point when listeners are needed
    }

    validateKey(key);

    // TODO allow only unique ACTIONS, if we are setting/getting certain key multiple times submit just once
    switch (action) {
      case SET:
        return (Future<T>) mExecutor.submit(new SetTask(key, value, cache));
      case GET:
        return mExecutor.submit(new GetTask(key, cache));
      default:
        illegalArg(true, "Unknown action " + action);
        return null;
    }

  }

  private static final class SetTask<T> implements Runnable {
    private final String mKey;
    private final T mValue;
    private final Cache<String, T>  mCache;

    private SetTask(String key, T value, Cache<String, T> cache) {
      mKey = key;
      mValue = value;
      mCache = cache;
    }

    @Override
    public void run() {
      mCache.set(mKey, mValue);
    }
  }

  private static final class GetTask<T> implements Callable<T> {
    private final String mKey;
    private final Cache<String, T>  mCache;

    private GetTask(String key, Cache<String, T> cache) {
      mKey = key;
      mCache = cache;
    }

    @Override
    public T call() throws Exception {
      return mCache.get(mKey);
    }
  }

  // Building stuff
  private static volatile KeyCache sKeyCache;
  /*package*/ static final char SEPARATOR = '_';

  private static String buildKey(CacheType cacheType, Class type) {
    if (sKeyCache == null) {
      sKeyCache = new KeyCache();
    }

    return buildKey(cacheType, type, sKeyCache);
  }

  /*package*/ static String buildKey(CacheType cacheType, Class type, KeyCache cacheKeys) {
    notNullArg(cacheType, "Cache type must not be null");
    notNullArg(type, "Class must not be null");

    String key;
    if (cacheKeys != null) {
      final Cache<Class, String> typeCache = cacheKeys.get(cacheType);
      key = typeCache.get(type);
      if (empty(key)) {
        key = buildClassKey(cacheType, type);
        typeCache.set(type, key);
      }
    } else {
      key = buildClassKey(cacheType, type);
    }

    return key;
  }

  private static String buildClassKey(CacheType cacheType, Class type) {
    return buildKey(cacheType, obtainValidKey(type));
  }

  /*package*/ static String buildKey(CacheType cacheType, String givenKey) {
    notNullArg(cacheType, "Cache type must not be null");
    nonEmptyArg(givenKey, "Given key must not be empty.");

    return new StringBuilder(cacheType.getPrefix())
        .append(SEPARATOR)
        .append(givenKey).toString();
  }

  /*package*/ static class Builder extends CacheBuilderBaseImp {
    private Set<CacheBuilder> mOptions;

    public Builder(Context applicationContext) {
      super(applicationContext);
    }

    @Override
    public CacheBuilder retaining(Class type) {
      notNullArg(type, "Class must not be null");

      if (mOptions == null) {
        mOptions = new LinkedHashSet<CacheBuilder>(1, LOAD_FACTOR);
      }

      final CacheBuilder options = new DefaultCacheBuilder(this, type);
      mOptions.add(options);
      return options;
    }

    @Override
    public CacheManager build() {
      nonEmpty(mOptions, "You must specify types you will cache.");

      final int length = len(mOptions);

      // This is where we set all our caches
      final Cache<String, Cache> caches = buildReferenceCache(length);

      // For every retained class
      for (final CacheBuilder options : mOptions) {
        final Class type = options.type();
        final CacheType cacheType = options.cacheType();

        // try to built with given options
        final Cache built = options.factory().build(options);
        notNull(built, "Failure building cache");

        caches.set(buildKey(cacheType, type), built); // alrite let's cache it!
      }

      return new CacheManagerImp(caches, newFixedCachedThread(THREADS, new CarboniteThreadFactory()));
    }
  }

  private static <K, V> ReferenceCache<K, V> buildReferenceCache(int initialCapacity) {
    final ReferenceCache.Options opts = new ReferenceCache.Options(initialCapacity, LOAD_FACTOR);
    return new ReferenceCache<K, V>(opts);
  }

  private static <K, V> WeakKeyCache<K, V> buildWeakKeyCache(int initialCapacity) {
    final WeakKeyCache.Options opts = new WeakKeyCache.Options(initialCapacity, LOAD_FACTOR);
    return new WeakKeyCache<K, V>(opts);
  }

  /*package*/ static class KeyCache implements Cache<CacheType, Cache<Class, String>> {
    private final ReferenceCache<CacheType, Cache<Class, String>> mRealCache;

    KeyCache() {
      mRealCache = buildReferenceCache(CacheType.values().length);
    }

    @Override
    public Cache<Class, String> get(CacheType key) {
      validateKey(key);

      Cache<Class, String> value = mRealCache.get(key);
      if (value == null) {
        value = buildWeakKeyCache(1);
        mRealCache.set(key, value);
      }
      return value;
    }

    @Override
    public Cache<CacheType, Cache<Class, String>> set(CacheType key, Cache<Class, String> value) {
      illegalAccess(true, "Set is not supported as internal values are lazy loaded.");
      return null;
    }
  }

  /*package*/ static class CarboniteThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
      return new CarboniteThread(r);
    }
  }

  /*package*/ static class CarboniteThread extends Thread {
    public CarboniteThread(Runnable r) {
      super(r);
      setName("CarboniteBg");
    }

    @Override
    public void run() {
      setThreadPriority(THREAD_PRIORITY_BACKGROUND);
      super.run();
    }
  }

}
