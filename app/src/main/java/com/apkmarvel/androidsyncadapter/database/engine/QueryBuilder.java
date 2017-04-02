/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package com.apkmarvel.androidsyncadapter.database.engine;


import android.content.ContentValues;
import android.database.Cursor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
    public static final String TAG = QueryBuilder.class.getSimpleName();
    private String tbl_name;
    private String primaryKey;
    private Class<?> clz;
    public QueryBuilder(){

    }
    public QueryBuilder setTableName(String tbl_name){
        this.tbl_name=tbl_name;
        return this;
    }
    public QueryBuilder setPrimaryKey(String primaryKey){
        this.primaryKey=primaryKey;
        return this;
    }
    public QueryBuilder setClass(Class<?> clz){
        this.clz=clz;
        return this;
    }
    public String build(){
        getTableName();
        return "CREATE TABLE `"+tbl_name+"` ("+getFields()+");";
    }
    private String getFields() {
        String fields = "";
        for (Field field : clz.getDeclaredFields()) {
            if(field.isSynthetic())break;
            if(field.getName().equals(primaryKey)){
                fields+="`"+field.getName()+"`"+"INTEGER PRIMARY KEY AUTOINCREMENT,";
                continue;
            }
            fields+="`"+field.getName()+"`"+getType(field);
        }
        fields = trimField(fields);
        return fields;
    }
    public static String[] getColums(Class<?> clzz){
        List<String> columns = new ArrayList<>();
        for (Field field : clzz.getDeclaredFields()) {
            if(field.isSynthetic())break;
            columns.add(field.getName());
        }
        return columns.toArray(new String[columns.size()]);
    }
    private String trimField(String fields) {
        return fields.replaceFirst(".$","");
    }
    private String getTableName() {
        if(tbl_name==null){
            tbl_name = clz.getSimpleName();
        }
        return tbl_name;
    }


    public Field getField(Object object, String fieldName) throws NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
    public ContentValues objectToContentValues(Object object) throws NoSuchFieldException, IllegalAccessException {
        ContentValues cv = new ContentValues();
        for (Field field : object.getClass().getDeclaredFields()) {
            if(field.isSynthetic())break;
            Field fd = getField(object,field.getName());
            Object value = fd.get(object);
            cv.put(field.getName(),value.toString());
        }
        return cv;
    }
    public void cursorToObject(Cursor cursor,Object object) throws NoSuchFieldException, IllegalAccessException {
        for (Field field : object.getClass().getDeclaredFields()) {
            if(field.isSynthetic())break;
            Field fd = getField(object,field.getName());
            /*set value*/
            setValue(object,field,fd,cursor);
        }
    }
    private void setValue(Object object,Field field,Field fd,Cursor cursor) throws IllegalAccessException {
        String type = field.getType().getSimpleName();
        if(type.equals("int")){
            fd.set(object,cursor.getInt(cursor.getColumnIndex(field.getName())));
        }else if(type.equals("Boolean")){
            boolean isTrue = cursor.getInt(cursor.getColumnIndex(field.getName()))==1;
            fd.set(object,isTrue);
        } else {
            fd.set(object,cursor.getString(cursor.getColumnIndex(field.getName())));
        }
    }
    private String getType(Field field){
        String type = field.getType().getSimpleName();
        if(type.equals("int") || type.equals("Boolean")){
            return "INTEGER,";
        }else {
            return "TEXT,";
        }
    }

}
