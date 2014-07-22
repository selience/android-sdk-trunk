package com.iresearch.android.local;

import android.content.Context;
import com.iresearch.android.local.cache.CacheFactory;
import com.iresearch.android.local.cache.CacheOptions;
import com.iresearch.android.local.cache.CacheType;

import static com.iresearch.android.local.CacheManager.Defaults.FACTORY;
import static com.iresearch.android.local.CacheManager.Defaults.TYPE;
import static com.iresearch.android.local.CacheManagerApi.CacheBuilder;
import static com.iresearch.android.local.CacheManagerApi.CarboniteBuilder;
import static com.iresearch.android.local.util.Util.illegalArg;
import static com.iresearch.android.local.util.Util.notNullArg;

/*package*/ abstract class CacheBuilderBaseImp implements CarboniteBuilder {

  private final Context mContext;

  protected CacheBuilderBaseImp(Context applicationContext) {
    notNullArg(applicationContext, "Context must not be null.");

    mContext = applicationContext;
  }

  @Override
  public final Context context() {
    return mContext;
  }

  @Override
  public final CarboniteBuilder iLoveYou() {
    return this;
  }

  @Override
  public final CacheManager iKnow() {
    return build();
  }

  protected static class DefaultCacheBuilder implements CacheBuilder {
    private final CarboniteBuilder mBuilder;
    private final Class mValueType;

    private CacheType mIn = TYPE;
    private CacheFactory mFactory = FACTORY;

    private CacheOptions mOpts;

    public DefaultCacheBuilder(CarboniteBuilder carboniteBuilder, Class retainingType) {
      notNullArg(carboniteBuilder, "Builder must not be null.");
      notNullArg(retainingType, "Retaining type must not be null.");

      mBuilder = carboniteBuilder;
      mValueType = retainingType;
    }

    @Override
    public CacheBuilder in(CacheType type) {
      notNullArg(type, "CacheType in must not be null.");

      mIn = type;

      return this;
    }

    @Override
    public CacheType cacheType() {
      return mIn;
    }

    @Override
    public CacheBuilder use(CacheFactory factory) {
      notNullArg(factory, "You must provide a valid factory.");

      mFactory = factory;

      return this;
    }

    @Override
    public CacheFactory factory() {
      return mFactory;
    }

    @Override
    public CacheBuilder use(CacheOptions opts) {
      notNullArg(opts, "You must provide valid options.");
      notNullArg(opts.cacheType(), "You must provide valid options with proper cache type (cacheType).");
      illegalArg(opts.cacheType() != mIn, "Cache options are not compatible with target cache type (cacheType != in).");
      notNullArg(opts.imp(), "You must provide valid options with proper cache implementation (imp).");

      mOpts = opts;

      return this;
    }

    @Override
    public CacheOptions opts() {
      return mOpts;
    }

    @Override
    public Class type() {
      return mValueType;
    }

    @Override
    public final CarboniteBuilder builder() {
      return mBuilder;
    }

    // implements CarboniteBuilder
    @Override
    public final CacheBuilder and(CacheType type) {
      return retaining(type()).in(type);
    }

    @Override
    public final Context context() {
      return builder().context();
    }

    @Override
    public final CacheBuilder retaining(Class type) {
      return builder().retaining(type);
    }

    @Override
    public final CarboniteBuilder iLoveYou() {
      return builder().iLoveYou();
    }

    @Override
    public final CacheManager iKnow() {
      return builder().iKnow();
    }

    @Override
    public final CacheManager build() {
      return builder().build();
    }

    //
  }
}
