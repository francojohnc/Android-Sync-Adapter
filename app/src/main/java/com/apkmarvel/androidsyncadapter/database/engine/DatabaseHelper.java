
package com.apkmarvel.androidsyncadapter.database.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private EngineDatabase engineDatabase;
    public static DatabaseHelper databaseHelper;
    public static SQLiteDatabase sqlDb;
    public Context context;

    public DatabaseHelper(Context context, EngineDatabase database) {
        super(context, database.getName(), null, database.getVersion());
        this.context = context;
        engineDatabase = database;
        if (engineDatabase.isCopyFromAsset()) {
            copyFromAssets();
        }
    }

    /**
     * Get Database Path
     */
    public static String databasePath(Context context, String databaseName) {
        String databasePath;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            databasePath = context.getApplicationInfo().dataDir + "/databases/" + databaseName;
        } else {
            databasePath = "/data/data/" + context.getPackageName() + "/databases/" + databaseName;
        }
        return databasePath;
    }

    /**
     * creates the database
     * tip: call this app in the activity that is launch initially
     *
     * @param context  - the application's context
     * @param database - the database information of the app
     */
    public static void createDatabase(Context context, EngineDatabase database) {

        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context, database);
        }
    }


    /**
     * @return the instance of the helper class
     * - returns null when the database is not yet created
     */
    public static DatabaseHelper getInstance() {
        return databaseHelper;
    }

    /**
     * @return the instance of the database information set when creating the database
     * - returns null when the database is not yet created
     */
    public EngineDatabase getDatabaseInfo() {
        return engineDatabase;
    }


    public void close() {
        databaseHelper = null;
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "database create : " + engineDatabase.getName());
        sqlDb = db;
        if (!engineDatabase.isCopyFromAsset()) {
            for (Table table : engineDatabase.getTables()) {
                Log.d(TAG, "adding table : " + table.getName());
                Log.d(TAG, "TableStructure : " + table.getTableStructure());
                db.execSQL(table.getTableStructure());
            }
        }
    }

    private void copyFromAssets() {
        Log.d(TAG, "copy from assets : " + engineDatabase.getName());
        try {
            if (!isExist(context, engineDatabase.getName())) {
                SQLiteDatabase db = this.getReadableDatabase();
                copyDatabase(context.getAssets().open(engineDatabase.getName()), databasePath(context, engineDatabase.getName()));
                for (Table table : engineDatabase.getTables()) {
                    Log.d(TAG, "adding table : " + table.getName());
                    Log.d(TAG, "TableStructure : " + table.getTableStructure());
                    db.execSQL(table.getTableStructure());
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade new version: " + newVersion);
        for (Table table : engineDatabase.getTables()) {
            db.execSQL("DROP TABLE IF EXISTS " + table.getName());
        }
        onCreate(db);
    }
    /**
     * @param sourceDB   - InputStream object of the database to be copied
     * @param outputPath - path to the database of the app
     */
    public boolean copyDatabase(InputStream sourceDB, String outputPath) {
        try {
            Log.d(TAG, "copy database : " + outputPath);
            InputStream myInput = sourceDB;
            String outFileName = outputPath;
            OutputStream myOutput = new FileOutputStream(outFileName);
            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("NewApi")
    public void exportDB(String dbPath, String dir) {
        Log.d(TAG, "exportDB dbPath" + dbPath + " dir : " + dir);
        File sdFile = new File(dir);
        if (!sdFile.exists())
            sdFile.mkdirs();
        FileChannel source = null;
        FileChannel destination = null;
        String backupDBPath = getDatabaseName();
        File currentDB = new File(dbPath); // just added backupDBPath for test // default is dbPath only
        File backupDB = new File(dir, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel(); // default is currentDB
            destination = new FileOutputStream(backupDB).getChannel(); // default is backupDB
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Log.d(TAG, "DB Exported");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public static void exportDB(String dbPath, String dir, String dataseName) {
        File sdFile = new File(dir);
        if (!sdFile.exists())
            sdFile.mkdirs();
        FileChannel source = null;
        FileChannel destination = null;
        File currentDB = new File(dbPath); // just added backupDBPath for test // default is dbPath only
        File backupDB = new File(dir, dataseName);
        try {
            source = new FileInputStream(currentDB).getChannel(); // default is currentDB
            destination = new FileOutputStream(backupDB).getChannel(); // default is backupDB
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            System.out.println("DB Exported");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    public static boolean deleteDatabase(Context context) {
        Log.d(TAG, "database delete : " + databaseHelper.engineDatabase.getName());
        return context.deleteDatabase(databaseHelper.engineDatabase.getName());
    }
}
