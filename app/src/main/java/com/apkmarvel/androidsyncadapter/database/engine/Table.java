
/*
 *	Created by Rhys April 13, 2015
 */
package com.apkmarvel.androidsyncadapter.database.engine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public abstract class Table {
    public final String TAG = getClass().getSimpleName();
    private SQLiteDatabase db;

    //table structure
    public abstract String getTableStructure();

    //table name
    public abstract String getName();

    public int update(ContentValues values, String whereClause, String[] whereArgs) throws SQLiteException, SQLException {
        Log.e(TAG, getName() + " update:" + whereClause + " =" + whereArgs);
        db = DatabaseHelper.getInstance().getWritableDatabase();
        if (db != null) return db.update(getName(), values, whereClause, whereArgs);
        return -1;
    }

    public int delete(String whereClause, String[] whereArgs) throws SQLiteException, SQLException {
        Log.e(TAG, getName() + " delete:" + whereClause + " =" + whereArgs);
        db = DatabaseHelper.getInstance().getWritableDatabase();
        if (db != null) return db.delete(getName(), whereClause, whereArgs);
        return -1;
    }

    public int delete() throws SQLiteException, SQLException {
        Log.e(TAG, getName() + " delete all data");
        db = DatabaseHelper.getInstance().getWritableDatabase();
        if (db != null) return db.delete(getName(), null, null);
        return -1;
    }

    public long insert(ContentValues values) {
        Log.e(TAG, getName() + " insert:" + values.toString());
        db = DatabaseHelper.getInstance().getWritableDatabase();
        if (db != null) return db.insertOrThrow(getName(), null, values);
        return -1;
    }
    /*insert list of object*/
    public <T>void insert(ArrayList<T> list) {
        for (Object x : list) {
            insert(x);
        }
    }
    /*insert object*/
    public long insert(Object object) {
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            ContentValues values = queryBuilder.objectToContentValues(object);
            db = DatabaseHelper.getInstance().getWritableDatabase();
            if (db != null) return db.insertOrThrow(getName(), null, values);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public Cursor select() throws SQLiteException, SQLException {
        Log.e(TAG, getName() + " select");
        db = DatabaseHelper.getInstance().getReadableDatabase();
        if (db != null) return db.rawQuery("SELECT * FROM " + getName(), null);
        return null;
    }
    public Cursor select(String whereClause, String[] whereArgs) throws SQLiteException, SQLException {
        Log.e(TAG, getName() + " select:" + whereClause + " =" + whereArgs);
        db = DatabaseHelper.getInstance().getReadableDatabase();
        if (db != null)
            return db.query(getName(), null, whereClause, whereArgs, null, null, null);
        return null;
    }

    public Cursor rawQuery(String query, String[] filterValues) throws SQLiteException, SQLException {
        db = DatabaseHelper.getInstance().getReadableDatabase();
        if (db != null) return db.rawQuery(query, filterValues);
        return null;
    }

    public SQLiteDatabase db() {
        return DatabaseHelper.getInstance().getReadableDatabase();
    }

    public ArrayList<HashMap<String, String>> cursorToListMap(Cursor cursor) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<String, String>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                map.put(cursor.getColumnName(i), cursor.getString(i));
            }
            list.add(map);
        }
        return list;
    }

    public ContentValues mapToCV(HashMap<String, String> map) {
        ContentValues cv = new ContentValues();
        for (Entry<String, String> x : map.entrySet()) {
            cv.put(x.getKey(), x.getValue());
        }
        return cv;
    }

    public boolean isExist() throws SQLException {
        try {
            if (select().getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
