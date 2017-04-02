package com.apkmarvel.androidsyncadapter.table;


import com.apkmarvel.androidsyncadapter.database.engine.QueryBuilder;
import com.apkmarvel.androidsyncadapter.database.engine.Table;
import com.apkmarvel.androidsyncadapter.model.User;

/**
 * Created by johncarlofranco on 01/04/2017.
 */

public class UserTable extends Table {
    public static final String TABLE_NAME = "tbl_user";
    public static final String ID = "id";
    public static final String FIRSTNAME = "firstname";
    public static final String LASTNAME = "lastname";
    @Override
    public String getTableStructure() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.setTableName(TABLE_NAME);
        queryBuilder.setClass(User.class);
        queryBuilder.setPrimaryKey(ID);
        String query = queryBuilder.build();
        return query;
    }
    @Override
    public String getName() {
        return TABLE_NAME;
    }
}
