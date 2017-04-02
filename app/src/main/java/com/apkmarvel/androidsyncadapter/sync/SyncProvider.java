package com.apkmarvel.androidsyncadapter.sync;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.apkmarvel.androidsyncadapter.database.AppDb;
import com.apkmarvel.androidsyncadapter.database.engine.DatabaseHelper;
import com.apkmarvel.androidsyncadapter.table.OrderTable;
import com.apkmarvel.androidsyncadapter.table.UserTable;

/**
 * Created by johncarlofranco on 02/04/2017.
 */

public class SyncProvider extends ContentProvider {
    public final String TAG = getClass().getSimpleName();
    /*Content Provider Name Same on  Manifest authorities*/
    public static final String PROVIDER_NAME = "com.apkmarvel.androidsyncadapter";
    /*uri*/
   public static final String URL_USER = "content://" + PROVIDER_NAME + "/cpuser";
   public static final String URL_ORDER = "content://" + PROVIDER_NAME + "/cporder";
    /*content uri*/
    public static final Uri CONTENT_URL_USER = Uri.parse(URL_USER);
    public static final Uri CONTENT_URL_ORDER = Uri.parse(URL_ORDER);
    /*uri code*/
    public static final int URI_CODE_USER = 1;
    public static final int URI_CODE_ORDER = 2;
    // Used to match uris with Content Providers
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "cpuser", URI_CODE_USER);
        uriMatcher.addURI(PROVIDER_NAME, "cporder", URI_CODE_ORDER);
    }


    /*content provider on create*/
    @Override
    public boolean onCreate() {
        Log.e(TAG,"onCreate");
        DatabaseHelper.createDatabase(getContext(), new AppDb());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e(TAG, "query uri: " + uri.toString());
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
            case URI_CODE_USER:
                UserTable userTable = new UserTable();
                cursor = userTable.select();
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case URI_CODE_ORDER:
                OrderTable orderTable = new OrderTable();
                cursor = orderTable.select();
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
        }
        return cursor;
    }

    // Handles requests for the MIME type (Type of Data) of the data at the URI
    @Override
    public String getType(Uri uri) {
        // Used to match uris with Content Providers
        switch (uriMatcher.match(uri)) {
            // vnd.android.cursor.dir/cpcontacts states that we expect multiple pieces of data
            case URI_CODE_USER:
                return "vnd.android.cursor.dir/cpuser";
            case URI_CODE_ORDER:
                return "vnd.android.cursor.dir/cporder";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.e(TAG, "insert uri: " + uri.toString());
        long rowID;
        Uri _uri = null;
        switch (uriMatcher.match(uri)) {
            case URI_CODE_USER:
                UserTable userTable = new UserTable();
                rowID = userTable.insert(values);
                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URL_USER, rowID);
                    /*notifyChange to all observers*/
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case URI_CODE_ORDER:
                OrderTable orderTable = new OrderTable();
                rowID = orderTable.insert(values);
                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(CONTENT_URL_ORDER, rowID);
                    /*notifyChange to all observers*/
                    getContext().getContentResolver().notifyChange(_uri, null);

                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        switch (uriMatcher.match(uri)) {
            case URI_CODE_USER:
                UserTable userTable = new UserTable();
                rowsDeleted = userTable.delete(selection, selectionArgs);
                break;
            case URI_CODE_ORDER:
                OrderTable orderTable = new OrderTable();
                rowsDeleted = orderTable.delete(selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // notifyChange notifies all observers that a row was updated
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    // Used to update a row or a selection of rows
    // Returns to number of rows updated
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
//
//        // Used to match uris with Content Providers
//        switch (uriMatcher.match(uri)) {
//            case uriCode:
//                // Update the row or rows of data
//                rowsUpdated = sqlDB.update(TABLE_NAME, values, selection, selectionArgs);
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown URI " + uri);
//        }
//
//        // getContentResolver provides access to the content model
//        // notifyChange notifies all observers that a row was updated
//        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

}