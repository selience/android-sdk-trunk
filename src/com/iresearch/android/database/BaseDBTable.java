
package com.iresearch.android.database;

import java.util.List;
import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class BaseDBTable implements BaseColumns {
    /**
     * Default Primary key
     */
    private String mPrimaryKey = "_id";

    /**
     * SQLiteDatabase Open Helper
     */
    private SQLiteOpenHelper mDatabaseOpenHelper;

    /**
     * Construct
     * 
     * @param databaseOpenHelper
     */
    public BaseDBTable(SQLiteOpenHelper databaseOpenHelper) {
        mDatabaseOpenHelper = databaseOpenHelper;
    }

    /**
     * Construct
     * 
     * @param databaseOpenHelper
     * @param primaryKey
     */
    public BaseDBTable(SQLiteOpenHelper databaseOpenHelper, String primaryKey) {
        this(databaseOpenHelper);
        setPrimaryKey(primaryKey);
    }

    /**
     * 添加一条数据
     * 
     * @param table
     * @param id
     * @param values
     * @return
     */
    public long insert(String table, ContentValues values) {
        long rowId = 0;
        SQLiteDatabase db = getDb(true);
        db.beginTransaction();
        try {
            rowId = db.insert(table, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return rowId;
    }

    /**
     * 添加多条数据
     * 
     * @param table
     * @param id
     * @param values
     * @return
     */
    public int bulkInsert(String table, ContentValues[] values) {
        SQLiteDatabase db = getDb(true);
        db.beginTransaction();
        try {
            for (ContentValues contentValues : values) {
                db.insertWithOnConflict(table, BaseColumns._ID, contentValues,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.setTransactionSuccessful();
            return values.length;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 根据主键id删除条数据
     * 
     * @param table
     * @param id
     * @return
     */
    public int deleteById(String table, String... ids) {
        if (ids.length == 0)
            return 0;

        StringBuffer sb = new StringBuffer();
        String[] idstr = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            sb.append("?").append(",");
            idstr[i] = String.valueOf(ids[i]);

        }
        sb.deleteCharAt(ids.length);
        return delete(table, mPrimaryKey + " in (" + sb + ")", idstr);
    }

    /**
     * 根据条件删除数据
     * 
     * @param table
     * @param selection
     * @param selectionArgs
     * @return
     */
    public int delete(String table, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = getDb(true);
        db.beginTransaction();
        try {
            count = db.delete(table, selection, selectionArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return count;
    }

    /**
     * 根据主键更新一行数据
     * 
     * @param table
     * @param id
     * @param values
     * @return
     */
    public int updateById(String table, String id, ContentValues values) {
        return update(table, values, mPrimaryKey + "=?", new String[] { id });
    }

    /**
     * 根据条件更新数据
     * 
     * @param table
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    public int update(String table, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = getDb(true);
        db.beginTransaction();
        try {
            count = db.update(table, values, selection, selectionArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return count;
    }

    /**
     * 根据主键查看某条数据是否存在
     * 
     * @param table
     * @param id
     * @return
     */
    public boolean isExistsById(String table, String id) {
        return isExistsByField(table, mPrimaryKey, id);
    }

    /**
     * 根据某字段/值查看某条数据是否存在
     * 
     * @param status
     * @return
     */
    public boolean isExistsByField(String table, String field, String value) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ")
            .append(table).append(" WHERE ")
            .append(field).append(" =?");

        return isExistsBySQL(sql.toString(), new String[] { value });
    }

    /**
     * 使用SQL语句查看某条数据是否存在
     * 
     * @param sql
     * @param selectionArgs
     * @return
     */
    public boolean isExistsBySQL(String sql, String[] selectionArgs) {
        boolean result = false;
        SQLiteDatabase db = getDb(false);
        final Cursor c = db.rawQuery(sql, selectionArgs);
        try {
            if (c.moveToFirst()) {
                result = (c.getInt(0) > 0);
            }
        } finally {
            c.close();
            db.close();
        }
        return result;
    }

    /**
     * 返回单个<T>对象
     * 
     * @param <T>
     * @param rowMapper
     * @return a cursor
     */
    public <T> T queryForObject(RowMapper<T> rowMapper, String table, String[] columns,
            String selection, String[] selectionArgs, String groupBy, String having,
            String orderBy, String limit) {
        T object = null;
        SQLiteDatabase db = getDb(false);
        final Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having,
                orderBy, limit);
        try {
            if (c.moveToFirst()) {
                object = rowMapper.mapRow(c, c.getCount());
            }
        } finally {
            c.close();
            db.close();
        }
        return object;
    }

    /**
     * 返回查询的<T>列表
     * 
     * @param <T>
     * @param rowMapper
     * @return list of object
     */
    public <T> List<T> queryForList(RowMapper<T> rowMapper, String table, String[] columns,
            String selection, String[] selectionArgs, String groupBy, String having,
            String orderBy, String limit) {
        List<T> list = new ArrayList<T>();
        SQLiteDatabase db = getDb(false);
        final Cursor c = db.query(table, columns, selection, selectionArgs, groupBy, having,
                orderBy, limit);
        try {
            while (c.moveToNext()) {
                list.add(rowMapper.mapRow(c, 1));
            }
        } finally {
            c.close();
            db.close();
        }
        return list;
    }

    /**
     * Get Database Connection
     * 
     * @param writeable
     * @return
     */
    protected SQLiteDatabase getDb(boolean writeable) {
        if (writeable) {
            return mDatabaseOpenHelper.getWritableDatabase();
        } else {
            return mDatabaseOpenHelper.getReadableDatabase();
        }
    }

    /**
     * Get Primary Key
     * 
     * @return
     */
    public String getPrimaryKey() {
        return mPrimaryKey;
    }

    /**
     * Set Primary Key
     * 
     * @param primaryKey
     */
    public void setPrimaryKey(String primaryKey) {
        this.mPrimaryKey = primaryKey;
    }

    /**
     * Query for object RowMapper
     * 
     * @param <T>
     */
    public interface RowMapper<T> {

        public T mapRow(Cursor cursor, int rowNum);
    }
}
