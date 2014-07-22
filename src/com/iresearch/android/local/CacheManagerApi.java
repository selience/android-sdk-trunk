
package com.iresearch.android.local;

import android.content.Context;
import com.iresearch.android.local.cache.CacheFactory;
import com.iresearch.android.local.cache.CacheOptions;
import com.iresearch.android.local.cache.CacheType;

import java.util.concurrent.Future;

public interface CacheManagerApi {
    <T> CacheManager set(String key, T value);

    <T> Future<T> get(String key, Class<T> type);

    <T> CacheManager memory(String key, T value);

    <T> CacheManager storage(String key, T value);

    <T> T memory(String key, Class<T> type);

    <T> T storage(String key, Class<T> type);

    public interface CarboniteBuilder {
        Context context();

        CacheBuilder retaining(Class type);

        // not funny
        CarboniteBuilder iLoveYou();

        // build methods
        CacheManager iKnow();

        CacheManager build();
    }

    public interface CacheBuilder extends CarboniteBuilder {

        CacheBuilder in(CacheType type);

        CacheType cacheType();

        CacheBuilder use(CacheFactory factory);

        CacheFactory factory();

        CacheBuilder use(CacheOptions opts);

        CacheOptions opts();

        Class type();

        CarboniteBuilder builder();

        // implements CarboniteBuilder
        CacheBuilder and(CacheType type);
    }

}
