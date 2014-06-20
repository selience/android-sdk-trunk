
package com.iresearch.android.database.bean;

import android.content.Context;
import com.iresearch.android.database.BaseDBTable;
import com.iresearch.android.database.DataTable;
import com.iresearch.android.database.SQLiteHelper;
import com.iresearch.android.database.DataColumn.DataType;

public class RssTable extends BaseDBTable {

    // 定义Rss表名
    public static final String TABLE_NAME = "t_websites";
    
    // 定义Rss表字段
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";

    public static DataTable TABLE=new DataTable(TABLE_NAME)
                  .addColumn(KEY_ID, DataType.INTEGER)
                  .addColumn(KEY_TITLE, DataType.TEXT)
                  .addColumn(KEY_DESCRIPTION, DataType.TEXT);
    
    
    public RssTable(Context context) {
        super(SQLiteHelper.getInstance(context));
    }
}
