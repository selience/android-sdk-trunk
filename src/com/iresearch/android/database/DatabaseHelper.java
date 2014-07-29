package com.iresearch.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @file SQLiteHelper.java
 * @create 2012-9-6 下午3:35:18
 * @author lilong
 * @description SQLite使用辅助类 1. 减少String "+"操作，使用StringBuilder代替 2.
 *              循环插入多条语句时采用compileStatement进行复用，循环体外仅编译一次
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	// 类没有实例化,是不能用作父类构造器的参数,必须声明为静态
	private final static String DATABASE_NAME = "northwind"; // 数据库名称

	private final static int DATABASE_VERSION = 1; // 数据库版本

	public DatabaseHelper(Context context) {
		// 第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
