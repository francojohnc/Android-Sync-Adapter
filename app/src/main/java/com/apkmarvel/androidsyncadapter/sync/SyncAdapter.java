package com.apkmarvel.androidsyncadapter.sync;


import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.apkmarvel.androidsyncadapter.table.UserTable;

import java.util.ArrayList;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public final String TAG = this.getClass().getSimpleName();
    private final ContentResolver contentResolver;

    /**
     * Project used when querying content provider. Returns all known fields.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        Log.e(TAG, "SyncAdapter");
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.e(TAG, "Beginning network synchronization");
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        /*get data*/
        String PROJECTION[] = new String[]{"id", "firstname", "lastname"};
        Cursor c = contentResolver.query(SyncProvider.CONTENT_URL_USER, PROJECTION, null, null, null);
        while (c.moveToNext()) {
            String id = c.getString(c.getColumnIndex("id"));
            String name = c.getString(c.getColumnIndex("firstname"));
            Log.e(TAG, "list " + id + " " + name);
        }
        /*insert*/
        /*content values*/
        ContentValues values = new ContentValues();
        values.put(UserTable.FIRSTNAME, "John");
        values.put(UserTable.LASTNAME, "franco");
        /*add to batch operation*/
        batch.add(ContentProviderOperation.newInsert(SyncProvider.CONTENT_URL_USER).withValues(values).build());
        /*apply batch operation*/
        try {
            contentResolver.applyBatch(SyncProvider.PROVIDER_NAME, batch);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

        // Provides access to other applications Content Providers
        Uri uri = contentResolver.insert(SyncProvider.CONTENT_URL_USER, values);

//        /*data 1*/
//        batch.add(ContentProviderOperation.newInsert(FeedContract.Entry.CONTENT_URI)
//                .withValue(FeedContract.Entry.COLUMN_NAME_ENTRY_ID,"2")
//                .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, "2")
//                .withValue(FeedContract.Entry.COLUMN_NAME_LINK, "2")
//                .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED, 2)
//                .build());
//        syncResult.stats.numInserts++;
//        /*data 2*/
//        batch.add(ContentProviderOperation.newInsert(FeedContract.Entry.CONTENT_URI)
//                .withValue(FeedContract.Entry.COLUMN_NAME_ENTRY_ID,"3")
//                .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, "3")
//                .withValue(FeedContract.Entry.COLUMN_NAME_LINK, "3")
//                .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED, 3)
//                .build());
//        syncResult.stats.numInserts++;
//        /*delete*/
//        Uri deleteUri = FeedContract.Entry.CONTENT_URI.buildUpon().appendPath("2").build();
//        batch.add(ContentProviderOperation.newDelete(deleteUri).build());
//        syncResult.stats.numDeletes++;
//        /*update*/
//        Uri existingUri = FeedContract.Entry.CONTENT_URI.buildUpon().appendPath("2").build();
//        batch.add(ContentProviderOperation.newUpdate(existingUri).withValue(FeedContract.Entry.COLUMN_NAME_TITLE,"2 new title")
//                .withValue(FeedContract.Entry.COLUMN_NAME_LINK,"2 new link")
//                .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED,2)
//                .build());
//        try {
//            /*apply*/
//            contentResolver.applyBatch(FeedContract.CONTENT_AUTHORITY, batch);
//            contentResolver.notifyChange(FeedContract.Entry.CONTENT_URI,null,false);
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (OperationApplicationException e) {
//            e.printStackTrace();
//        }
    }
}
