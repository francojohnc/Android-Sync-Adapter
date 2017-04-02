/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

/*
 *	Created by Rhys April 13, 2015
 */
package com.apkmarvel.androidsyncadapter.database.engine;

import java.util.ArrayList;

public class EngineDatabase {

	private String DBName;
	private int DBVersion;
	private boolean isCopyFromAsset;
	private ArrayList<Table> tables = new ArrayList<Table>();
	/*
	 * @param dbname - database name
	 * @param dbversion - database version
	 */
	public EngineDatabase(String dbname , int dbversion) {
		DBName = dbname;
		DBVersion = dbversion;
	}
	/**
	 * @param dbname - database name
	 * @param dbversion - database version
	 * @param isCopyFromAsset - copy database from asset to phone
	 */
	public EngineDatabase(String dbname , int dbversion, boolean isCopyFromAsset) {
		DBName = dbname;
		DBVersion = dbversion;
		this.isCopyFromAsset = isCopyFromAsset;
	}
	/**
	 * @return database name
	 */
	public String getName() {
		return DBName;
	}
	
	/**
	 * @return database version
	 */
	public int getVersion() {
		return DBVersion;
	}
	
	/**
	 * @return an arraylist of tables to be created
	 */
	public ArrayList<Table> getTables() {
		return tables;
	}
	/**
	 * @param table - table to be added to the list
	 * @return instance of the EngineDatabase Class
	 */
	public EngineDatabase addTable(Table table) {
		tables.add(table);
		return this;
	}
	public boolean isCopyFromAsset() {
		return isCopyFromAsset;
	}
}
