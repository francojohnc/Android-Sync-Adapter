package com.apkmarvel.androidsyncadapter;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.apkmarvel.androidsyncadapter.sync.SyncProvider;
import com.apkmarvel.androidsyncadapter.sync.SyncUtils;

/*http://blog.udinic.com/2013/07/24/write-your-own-android-sync-adapter/*/
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public final String TAG = this.getClass().getSimpleName();
    private Object mSyncObserverHandle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*create account*/
        SyncUtils.CreateSyncAccount(this);
        /*register load callback*/
        getSupportLoaderManager().initLoader(SyncProvider.URI_CODE_USER, null, this);
        getSupportLoaderManager().initLoader(SyncProvider.URI_CODE_ORDER, null, this);
    }
    public void sync(View v){
        SyncUtils.TriggerRefresh();
    }
    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(SyncProvider.URI_CODE_USER);
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        @Override
        public void onStatusChanged(int which) {
            Log.e(TAG,"onStatusChanged");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Account account = GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE);
                    if (account == null) {
                        Log.e(TAG,"account null");
                        setRefreshActionButtonState(false);
                        return;
                    }
                    boolean syncActive = ContentResolver.isSyncActive(account, FeedContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(account, FeedContract.CONTENT_AUTHORITY);
                    setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };
    public void setRefreshActionButtonState(boolean refreshing) {
        Log.e(TAG,"refreshing: "+refreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(TAG,"onCreateLoader");
        return new CursorLoader(this,FeedContract.Entry.CONTENT_URI,PROJECTION,null,null, FeedContract.Entry.COLUMN_NAME_PUBLISHED + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(TAG,"onLoadFinished");
        if(data==null)return;
        while (data.moveToNext()) {

            Log.e(TAG,"moveToNext" +   data.toString());
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(TAG,"onLoaderReset");
    }
}
