
package com.iresearch.android.database;

import java.util.ArrayList;
import android.provider.BaseColumns;
import android.database.sqlite.SQLiteDatabase;
import com.iresearch.android.database.DataColumn.DataType;
import com.iresearch.android.database.DataColumn.Constraint;

public class DataTable {
    
    private String mTableName;

    private ArrayList<DataColumn> mColumnsDefinitions = new ArrayList<DataColumn>();

    /**
     * 会自动添加主键 BaseColumns._ID
     * 
     * @param tableName
     */
    public DataTable(String tableName) {
        mTableName = tableName;
        mColumnsDefinitions.add(new DataColumn(BaseColumns._ID, Constraint.PRIMARY_KEY, DataType.INTEGER));
    }

    public DataTable addColumn(DataColumn columnsDefinition) {
        mColumnsDefinitions.add(columnsDefinition);
        return this;
    }

    public DataTable addColumn(String columnName, DataType dataType) {
        mColumnsDefinitions.add(new DataColumn(columnName, null, dataType));
        return this;
    }

    public DataTable addColumn(String columnName, Constraint constraint, DataType dataType) {
        mColumnsDefinitions.add(new DataColumn(columnName, constraint, dataType));
        return this;
    }

    public void create(SQLiteDatabase db) {
        String formatter = " %s";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS ");
        stringBuilder.append(mTableName);
        stringBuilder.append("(");
        int columnCount = mColumnsDefinitions.size();
        int index = 0;
        for (DataColumn columnsDefinition : mColumnsDefinitions) {
            stringBuilder.append(columnsDefinition.getColumnName()).append(
                    String.format(formatter, columnsDefinition.getDataType().name()));
            Constraint constraint = columnsDefinition.getConstraint();

            if (constraint != null) {
                stringBuilder.append(String.format(formatter, constraint.toString()));
            }
            if (index < columnCount - 1) {
                stringBuilder.append(",");
            }
            index++;
        }
        stringBuilder.append(");");
        db.execSQL(stringBuilder.toString());
    }

    public void delete(final SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + mTableName);
    }
    
    public String getTableName() {
        return mTableName;
    }
}
