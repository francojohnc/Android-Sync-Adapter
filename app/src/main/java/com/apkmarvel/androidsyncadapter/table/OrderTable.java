package com.apkmarvel.androidsyncadapter.table;


import com.apkmarvel.androidsyncadapter.database.engine.QueryBuilder;
import com.apkmarvel.androidsyncadapter.database.engine.Table;
import com.apkmarvel.androidsyncadapter.model.Order;

/**
 * Created by johncarlofranco on 02/04/2017.
 */

public class OrderTable extends Table {
    public static final String TABLE_NAME = "tbl_order";
    public static final String ID = "id";
    public static final String PRICE = "price";
    public static final String NAME = "name";
    @Override
    public String getTableStructure() {
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.setTableName(TABLE_NAME);
        queryBuilder.setClass(Order.class);
        queryBuilder.setPrimaryKey(ID);
        String query = queryBuilder.build();
        return query;
    }
    @Override
    public String getName() {
        return TABLE_NAME;
    }
}
