package com.todolist.android.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Алексей on 03.07.2015.
 */
public class ToDoDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "applicationdata";

    private static final int DB_VERSION = 1;

    // запрос на создание базы данных
    private static final String DB_CREATE = "create table todo (_id integer primary key autoincrement, "
            + "category text not null, summary text not null, description text not null);";

    public ToDoDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.w(ToDoDBHelper.class.getName(),
                "Обновление базы данных с версии " + oldVersion + " до версии "
                        + newVersion + ", с удаление старых данных");
        database.execSQL("DROP TABLE IF EXISTS todo");
        onCreate(database);
    }
}
