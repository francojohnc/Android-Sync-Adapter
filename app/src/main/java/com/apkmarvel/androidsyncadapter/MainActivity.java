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
import com.apkmarvel.androidsyncadapter.table.OrderTable;
import com.apkmarvel.androidsyncadapter.table.UserTable;

import static com.apkmarvel.androidsyncadapter.sync.SyncProvider.CONTENT_URL_ORDER;
import static com.apkmarvel.androidsyncadapter.sync.SyncProvider.CONTENT_URL_USER;

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
                    boolean syncActive = ContentResolver.isSyncActive(account,  SyncProvider.PROVIDER_NAME);
                    boolean syncPending = ContentResolver.isSyncPending(account,  SyncProvider.PROVIDER_NAME);
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
        switch (id) {
            case SyncProvider.URI_CODE_USER:
                UserTable userTable = new UserTable();
                return new CursorLoader(this, CONTENT_URL_USER, userTable.getColums(), null, null, null);
            case SyncProvider.URI_CODE_ORDER:
                OrderTable orderTable = new OrderTable();
                return new CursorLoader(this, CONTENT_URL_ORDER, orderTable.getColums(), null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(TAG, "onLoadFinished " + loader.getId());
        String listData = "";
        switch (loader.getId()) {
            case SyncProvider.URI_CODE_USER:

                while (data.moveToNext()) {
                    String id = data.getString(data.getColumnIndex("id"));
                    String name = data.getString(data.getColumnIndex("firstname"));
                    String lastname = data.getString(data.getColumnIndex("lastname"));
                    listData = listData + id + " : " + name + " "+lastname+"\n";
                }
                break;
            case SyncProvider.URI_CODE_ORDER:
                while (data.moveToNext()) {
                    String id = data.getString(data.getColumnIndex("id"));
                    String name = data.getString(data.getColumnIndex("name"));
                    listData = listData + id + " : " + name + "\n";
                }
                break;
        }
        Log.e(TAG, "listData " + listData);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(TAG,"onLoaderReset");
    }
}
