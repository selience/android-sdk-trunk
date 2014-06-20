
package com.iresearch.android.database;

import java.util.List;

public interface DBTable<T> {

    public long insert(T _t);

    public long bulkInsert();

    public int delete(int... ids);

    public int update(T _t);

    public List<T> findAll();

    public boolean exists();
}
