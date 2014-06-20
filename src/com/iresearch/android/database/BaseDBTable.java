package com.iresearch.android.database;

import android.provider.BaseColumns;

public abstract class BaseDBTable implements BaseColumns {
    
    public abstract DataTable getTable();
    
}
