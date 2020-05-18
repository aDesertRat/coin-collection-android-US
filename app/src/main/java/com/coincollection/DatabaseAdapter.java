/*
 * Copyright (C) 2008 Google Inc.
 * Modified by Andrew Williams
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.coincollection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.spencerpages.MainApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Adapter based on the Simple Notes Database Access Helper Class on the Android site.
 * 
 * This Adapter is used to get information that the user has entered regarding his or her coin
 * collections (from the backing database.)
 */
class DatabaseAdapter {

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mContext;

    /**
     * Record any internal DB names here!
     * Because internal tables and user tables aren't differentiated, we must prohibit
     * users from trying to create database collections that map to internal DB names
     */
    private final List<String> mReservedDbNames = new ArrayList<>(Collections.singletonList(
            "collection_info"
    ));

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    DatabaseAdapter(Context ctx) {
        this.mContext = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    DatabaseAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    // Clean up a bit if we no longer need this DatabaseAdapter
    void close() {
        mDbHelper.close();
    }

    /**
     * Get the total number of coins in the collection
     *
     * @param name String that identifiers which table to query
     * @return int with the total number of coins in the collection
     */
    int fetchTotalCollected(String name) {
        String select_sqlStatement = "SELECT COUNT(_id) FROM [" + name + "] WHERE inCollection=1 LIMIT 1";
        SQLiteStatement compiledStatement;

        compiledStatement = mDb.compileStatement(select_sqlStatement);
        // TODO May generate a SQLITE_SCHEMA error (17) after just deleting a table, doesn't appear to break things though

        int result = (int) compiledStatement.simpleQueryForLong();

        compiledStatement.clearBindings();
        compiledStatement.close();
        return result;
    }

    /**
     * Returns whether a coinIdentifier and coinMint has been marked as collected in a given
     * collection.
     *
     * @param name The collection of interest
     * @param coinIdentifier The coinIdentifier of the coin we are to retrieve data for
     * @param coinMint The coinMint of the coin we are to retrieve data for
     * @return 0 if item is in the collection, 1 otherwise
     * @throws SQLException if coin could not be found (Shouldn't happen???)
     */
    // TODO Rename
    // TODO Retrieving the coin information individually (and onScroll) is inefficient... We should
    // instead have one query that returns all of the info.
    private int fetchInfo(String name, String coinIdentifier, String coinMint) throws SQLException {
        String select_sqlStatement = "SELECT inCollection FROM [" + name + "] WHERE coinIdentifier=? AND coinMint=? LIMIT 1";
        SQLiteStatement compiledStatement = mDb.compileStatement(select_sqlStatement);

        compiledStatement.bindString(1, coinIdentifier);
        compiledStatement.bindString(2, coinMint);
        int result = (int) compiledStatement.simpleQueryForLong();

        compiledStatement.clearBindings();
        compiledStatement.close();
        return result;
    }

    /**
     * Updates a coins presence in the database.
     *
     * @param name The name of the collection of interest
     * @param coinIdentifier The coinIdentifier of the coin we are to retrieve data for
     * @param coinMint The coinMint of the coin we are to retrieve data for
     * @return true if the value was successfully updated, false otherwise
     */

    boolean updateInfo(String name, String coinIdentifier, String coinMint) {
        int result = fetchInfo(name, coinIdentifier, coinMint);
        int newValue = (result + 1) % 2;
        ContentValues args = new ContentValues();
        args.put("inCollection", newValue);

        // TODO Should we do something if update fails?
        return mDb.update("[" + name + "]", args, "coinIdentifier=? AND coinMint =?", new String[] {coinIdentifier, coinMint}) > 0;
    }

    /**
     * Returns the display configured for the table (advanced view, simple view, etc.)
     *
     * @param tableName - Used to know which table to query
     * @return which display we should show.  See MainApplication for the types
     * @throws SQLException if an SQL-related error occurs
     */
    int fetchTableDisplay(String tableName) throws SQLException {

        // The database will only be set up this way in this case
        String select_sqlStatement = "SELECT display FROM collection_info WHERE name=? LIMIT 1";
        SQLiteStatement compiledStatement = mDb.compileStatement(select_sqlStatement);

        compiledStatement.bindString(1, tableName);
        int result = (int) compiledStatement.simpleQueryForLong();

        compiledStatement.clearBindings();
        compiledStatement.close();
        return result;
    }

    /**
     * Updates the display type associated with a given collection
     *
     * @param tableName - Used to know which table to update
     * @param displayType - New displaytype to store for this table
     * @return 0 on update success, 1 otherwise
     * @throws SQLException if an SQL-related error occurs
     */
    boolean updateTableDisplay(String tableName, int displayType) throws SQLException {

        ContentValues args = new ContentValues();
        args.put("display", displayType);

        // TODO Should we do something if update fails?
        return mDb.update("collection_info", args, "name=?", new String[] { tableName }) > 0;
    }

    /**
     * Updates the order in which a collection should appear in the list of collections
     *
     * @param tableName - Used to know which table to update
     * @param displayOrder - New displayOrder to store for this table
     * @return 0 if the update was successful, 1 otherwise
     * @throws SQLException if an SQL-related error occurs
     */
    boolean updateDisplayOrder(String tableName, int displayOrder) throws SQLException {

        ContentValues args = new ContentValues();
        args.put("displayOrder", displayOrder);
        // TODO Should we do something if update fails?
        return mDb.update("collection_info", args, "name=?", new String[] { tableName }) > 0;
    }

    /**
     * Updates the info for the coin in table 'name' where the coin is identified with
     * coinIdentifier and coinMint. This includes the advanced info (coin grade, quantity, and
     * notes) in addition to whether it is inc the collection.
     * @param name The collection name
     * @param coinIdentifier Coin image id
     * @param coinMint Coin mint mark
     * @param grade Coin grade
     * @param quantity Quantity of coins
     * @param notes Notes about the coin
     * @param inCollection whether the coin is in the collection
     * @return 1 on success, 0 otherwise
     */
    int updateAdvInfo(String name, String coinIdentifier, String coinMint, int grade,
                             int quantity, String notes, int inCollection) {

        ContentValues args = new ContentValues();
        args.put("inCollection", inCollection);
        args.put("advGradeIndex", grade);
        args.put("advQuantityIndex", quantity);
        args.put("advNotes", notes);
        return mDb.update("[" + name + "]", args, "coinIdentifier=? AND coinMint =?", new String[] {coinIdentifier, coinMint});
    }

    /**
     * Helper function to issue the SQL needed when creating a new database table for a collection
     * @param name The collection name
     */
    private void createNewTable(String name){
        // v2.2.1 - Until this point all fields had '_id' created with 'autoincrement'
        // which is unnecessary for our purposes.  Removing to improve performance.
        String DATABASE_CREATE = "CREATE TABLE [" + name
        + "] (_id integer primary key,"
        + " coinIdentifier text not null,"
        + " coinMint text,"
        + " inCollection integer,"
        + " advGradeIndex integer default 0,"
        + " advQuantityIndex integer default 0,"
        + " advNotes text default \"\");";

        mDb.execSQL(DATABASE_CREATE);
    }

    /**
     * Helper function to rename a collection
     * @param oldName The original collection name
     * @param newName The new collection name
     * @return true if the value was successfully updated, false otherwise
     */
    boolean updateCollectionName(String oldName, String newName){
        String alterDbSqlStr = "ALTER TABLE [" + oldName + "] RENAME TO [" + newName + "]";
        mDb.execSQL(alterDbSqlStr);

        ContentValues args = new ContentValues();
        args.put("name", newName);

        // TODO Should we do something if update fails?
        return mDb.update("collection_info", args, "name=?", new String[] { oldName }) > 0;
    }

    /**
     * Handles adding everything needed for a collection to store it's data in the database.
     * This is used for creating new collections, and will initialize everything to a blank state.
     *
     * @param name The collection name
     * @param coinType The collection type
     * @param coinIdentifiers A list of the identifiers for the coins in this collection (Ex: 2009)
     * @param coinMints A list of the mints for the coins in this collection
     * @param displayOrder The position in the list of collections in which this should be displayed
     *                     TODO maybe make this not an argument, and determine this internally?
     * @return 1 TODO
     */
    // TODO Rename, since we aren't just creating a new table
    int createNewTable(String name, String coinType, ArrayList<String> coinIdentifiers,
                              ArrayList<String> coinMints, int displayOrder) {

        // Actually make the table
        createNewTable(name);

        // We have the list of identifiers, now set them correctly
        for(int j = 0; j < coinIdentifiers.size(); j++){
            ContentValues initialValues = new ContentValues();
            initialValues.put("coinIdentifier", coinIdentifiers.get(j));
            initialValues.put("coinMint", coinMints.get(j));
            initialValues.put("inCollection", 0);
            // Advanced info gets added automatically, if the columns are there

            long value = mDb.insert("[" + name + "]", null, initialValues);
            // TODO Do something if insert fails?
        }

        // We also need to add the table to the list of tables
        addEntryToCollectionInfoTable(name, coinType, coinIdentifiers.size(), CollectionPage.SIMPLE_DISPLAY, displayOrder);

        return 1;
    }

    /**
     * Helper function to add a collection into the global list of collections
     * @param name Collection name
     * @param coinType Type of collection
     * @param total Number of coins in collection
     * @param display Type of display
     * @param displayOrder Display order of this collection
     */
    private void addEntryToCollectionInfoTable(String name, String coinType, int total, int display, int displayOrder){
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("coinType", coinType);
        values.put("total", total);
        values.put("displayOrder", displayOrder);
        values.put("display", display);

        long value = mDb.insert("collection_info", null, values);
        // TODO Do something if insert fails?
        // TODO It'd be great if there was a way to clear the prepared SQL Statement cache so that
        // we don't get SQL 17 Errors
    }

    /**
     * Handles adding everything needed for a collection to store it's data in the database.
     * This also allows the data to be pre-populated in the database. This is used for importing
     * collections
     * @param name The collection name
     * @param coinType The collection type
     * @param total The total number of coins in the collection (TODO I think)
     * @param display The display type of this collection
     * @param displayOrder The order that this collection should appear in the list of collections
     * @param rawData The data that should be put into the backing database once it is created
     * @return 1 TODO
     */
    // TODO Rename, since we aren't just creating a new table
    int createNewTable(String name, String coinType, int total, int display, int displayOrder, String[][] rawData) {

        // Actually make the table
        createNewTable(name);

        // We have the list of identifiers, now set them correctly
        for (String[] rawRowData : rawData) {

            // coinIdentifier, coinMint, inCollection, advGradeIndex, advQuantityIndex, advNotes
            ContentValues initialValues = new ContentValues();
            initialValues.put("coinIdentifier", rawRowData[0]);
            initialValues.put("coinMint", rawRowData[1]);
            initialValues.put("inCollection", Integer.valueOf(rawRowData[2]));
            initialValues.put("advGradeIndex", Integer.valueOf(rawRowData[3]));
            initialValues.put("advQuantityIndex", Integer.valueOf(rawRowData[4]));
            initialValues.put("advNotes", rawRowData[5]);

            long value = mDb.insert("[" + name + "]", null, initialValues);
            // TODO Do something if insert fails?
        }

        // We also need to add the table to the list of tables
        addEntryToCollectionInfoTable(name, coinType, total, display, displayOrder);
        return 1;
    }

    /**
     * Handles removing a collection from the database
     *
     * @param name The collection name
     */
    // TODO Rename, since it does more than just drop a table
    void dropTable(String name){
        String DATABASE_DROP = "DROP TABLE [" + name + "];";
        mDb.execSQL(DATABASE_DROP);

        //long value = mDb.delete("collection_info", "name=\"" + name + "\"", null);
        int value = mDb.delete("collection_info", "name=?", new String[] { name });
        // TODO Do something if insert fails?
        // TODO It be great if there was a way to clear the prepared SQL Statement cache so that
        // we don't get SQL 17 Errors
        // ^^^ Not sure if this is still an issue
    }

    /**
     * Deletes the table of metadata about all the current collections
     */
    void dropCollectionInfoTable(){

        String DATABASE_DROP = "DROP TABLE [collection_info];";
        mDb.execSQL(DATABASE_DROP);
    }

    /**
     * Creates the table of metadata for all the current collections
     */
    void createCollectionInfoTable(){

        // I would put the functionality here and call it from within the mDbHelper,
        // but I couldn't figure out how to get this working.  :(
        mDbHelper._createCollectionInfoTable(mDb);
    }

    /**
     * Return a Cursor that gives the names of all of the defined collections
     *
     * @return Cursor
     */
    Cursor getAllCollectionNames() {

        return mDb.query("collection_info", new String[] {"name"}, null, null, null, null, "displayOrder");
    }

    /**
     * Return a Cursor that gives information about each of the collections
     *
     * @return Cursor
     */
    Cursor getAllTables() {

        return mDb.query("collection_info", new String[] {"name", "coinType",
        "total"}, null, null, null, null, "displayOrder");
    }

    /**
     * Get the list of identifiers for each collection
     *
     * @param name The name of the collection
     * @return Cursor over all coins in the collection
     */
    Cursor getAllIdentifiers(String name) {

        return mDb.query("[" + name + "]", new String[] {"coinIdentifier", "coinMint"},
                null, null, null, null, "_id");
    }

    /**
     * Get whether each coin is in the collection
     *
     * @return Cursor over all coins in the collection
     */
    Cursor getInCollectionInfo(String tableName) {
        return mDb.query("[" + tableName + "]", new String[] {"inCollection"},
                    null, null, null, null, "_id");
    }


    /**
     * Get the advanced info associated with each coin in the collection
     *
     * @param name The collection name
     * @return Cursor over all coins in the collection
     */
    Cursor getAdvInfo(String name) {

        return mDb.query("[" + name + "]", new String[] {"advGradeIndex", "advQuantityIndex", "advNotes"},
                null, null, null, null, "_id");
    }

    /**
     * Get all collection info (for exporting)
     *
     * @param name The collection name
     * @return Cursor over all coins in the collection
     */
    Cursor getAllCollectionInfo(String name) {

        return mDb.query("[" + name + "]", new String[] {"coinIdentifier", "coinMint", "inCollection", "advGradeIndex", "advQuantityIndex", "advNotes"},
                null, null, null, null, "_id");
    }

    /**
     * Expose the dbHelper's onUpgrade method so we can call it manually when importing collections
     *
     * @param oldVersion the db version to upgrade from
     */
    void upgradeCollections(int oldVersion) {
        mDbHelper.onUpgrade(mDb, oldVersion, MainApplication.DATABASE_VERSION);
    }

    /**
     * Check if a name can be used for a new/renamed collection
     * @param name The collection name
     * @return Empty string if name is valid, otherwise a reason why the name can't be used
     */
    String checkCollectionName(String name) {

        // Make sure the name isn't in the reserved list
        if (mReservedDbNames.contains(name)) {
            return "Collection name is reserved, please choose a different name";
        }

        // By the time the user is able to click this mDbAdapter should not be NULL anymore
        Cursor resultCursor = this.getAllCollectionNames();
        if(resultCursor == null){
            return "Failed to get list of current collections, low on memory perhaps?";
        }
        // THanks! http://stackoverflow.com/questions/2810615/how-to-retrieve-data-from-cursor-class
        if (resultCursor.moveToFirst()){
            do {
                Locale defaultLocale = Locale.getDefault();
                if(resultCursor.getString(resultCursor.getColumnIndex("name")).toLowerCase(defaultLocale).equals(name.toLowerCase(defaultLocale))){
                    resultCursor.close();
                    return "A collection with this name already exists, please choose a different name";
                }

            } while(resultCursor.moveToNext());
        }
        resultCursor.close();
        return "";
    }

    /**
     * Get the next display order for a new collection
     * @return The next display order to use
     */
    int getNextDisplayOrder() {
        String select_sqlStatement = "SELECT MAX(displayOrder) FROM collection_info";
        SQLiteStatement compiledStatement;

        compiledStatement = mDb.compileStatement(select_sqlStatement);
        int result = (int) compiledStatement.simpleQueryForLong();

        compiledStatement.clearBindings();
        compiledStatement.close();

        return result + 1;
    }
}
