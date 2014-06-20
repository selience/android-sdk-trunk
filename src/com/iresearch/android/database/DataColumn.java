
package com.iresearch.android.database;

public class DataColumn {

    private String mColumnName;

    private Constraint mConstraint;

    private DataType mDataType;

    public DataColumn(String columnName, Constraint constraint, DataType dataType) {
        mColumnName = columnName;
        mConstraint = constraint;
        mDataType = dataType;
    }

    public String getColumnName() {
        return mColumnName;
    }

    public Constraint getConstraint() {
        return mConstraint;
    }

    public DataType getDataType() {
        return mDataType;
    }
    
    /**
     * SQLite字段的约束 
     */
    public static enum Constraint {
        UNIQUE("UNIQUE"), NOT_NULL("NOT NULL"), CHECK("CHECK"), 
        FOREIGN_KEY("FOREIGN KEY"), PRIMARY_KEY("PRIMARY KEY");

        private String value;

        private Constraint(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * SQLite的基本5种类型
     */
    public static enum DataType {
        NULL, INTEGER, REAL, TEXT, BLOB
    }
}
