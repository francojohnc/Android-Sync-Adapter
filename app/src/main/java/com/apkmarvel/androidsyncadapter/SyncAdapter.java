package com.apkmarvel.androidsyncadapter;


import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public final String TAG = this.getClass().getSimpleName();

    private static final String FEED_URL = "http://android-developers.blogspot.com/atom.xml";
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds
    private final ContentResolver contentResolver;

    /**
     * Project used when querying content provider. Returns all known fields.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        Log.e(TAG,"SyncAdapter");
    }
    private static final String[] PROJECTION = new String[] {
            FeedContract.Entry._ID,
            FeedContract.Entry.COLUMN_NAME_ENTRY_ID,
            FeedContract.Entry.COLUMN_NAME_TITLE,
            FeedContract.Entry.COLUMN_NAME_LINK,
            FeedContract.Entry.COLUMN_NAME_PUBLISHED};
    public static final int COLUMN_TITLE = 2;
    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.e(TAG, "Beginning network synchronization");
        ArrayList<ContentProviderOperation> batch = new ArrayList<>();
        /*get data*/
        Uri uri = FeedContract.Entry.CONTENT_URI;
        Cursor c = contentResolver.query(uri, PROJECTION, null, null, null);
        assert c != null;
        Log.e(TAG,"cursor count: "+c.getCount());
        while (c.moveToNext()) {
            String title = c.getString(COLUMN_TITLE);
            Log.e(TAG,"title "+title);
        }
        /*insert*/
        /*data 1*/
        batch.add(ContentProviderOperation.newInsert(FeedContract.Entry.CONTENT_URI)
                .withValue(FeedContract.Entry.COLUMN_NAME_ENTRY_ID,"2")
                .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, "2")
                .withValue(FeedContract.Entry.COLUMN_NAME_LINK, "2")
                .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED, 2)
                .build());
        syncResult.stats.numInserts++;
        /*data 2*/
        batch.add(ContentProviderOperation.newInsert(FeedContract.Entry.CONTENT_URI)
                .withValue(FeedContract.Entry.COLUMN_NAME_ENTRY_ID,"3")
                .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, "3")
                .withValue(FeedContract.Entry.COLUMN_NAME_LINK, "3")
                .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED, 3)
                .build());
        syncResult.stats.numInserts++;
        /*delete*/
        Uri deleteUri = FeedContract.Entry.CONTENT_URI.buildUpon().appendPath("2").build();
        batch.add(ContentProviderOperation.newDelete(deleteUri).build());
        syncResult.stats.numDeletes++;
        /*update*/
        Uri existingUri = FeedContract.Entry.CONTENT_URI.buildUpon().appendPath("2").build();
        batch.add(ContentProviderOperation.newUpdate(existingUri).withValue(FeedContract.Entry.COLUMN_NAME_TITLE,"2 new title")
                .withValue(FeedContract.Entry.COLUMN_NAME_LINK,"2 new link")
                .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED,2)
                .build());
        try {
            /*apply*/
            contentResolver.applyBatch(FeedContract.CONTENT_AUTHORITY, batch);
            contentResolver.notifyChange(FeedContract.Entry.CONTENT_URI,null,false);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
