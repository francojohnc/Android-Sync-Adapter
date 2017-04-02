

package com.apkmarvel.androidsyncadapter.database;


import com.apkmarvel.androidsyncadapter.database.engine.EngineDatabase;
import com.apkmarvel.androidsyncadapter.table.OrderTable;
import com.apkmarvel.androidsyncadapter.table.UserTable;

public class AppDb extends EngineDatabase {

    public final static String DB_NAME = "app.db";
    public final static int DB_VERSION = 1;
    public AppDb() {
        super(DB_NAME, DB_VERSION);
        addTable(new UserTable());
        addTable(new OrderTable());
    }
}