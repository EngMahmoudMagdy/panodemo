package com.magdy.panodemo;

import java.util.ArrayList;
    
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DBController  extends SQLiteOpenHelper {

    public DBController(Context applicationcontext) {
        super(applicationcontext, "travelli.db", null, 1);
    }
    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {
        String query;
        query = "CREATE TABLE places ( id INTEGER PRIMARY KEY, name TEXT, longt Float , lat Float , image TEXT , description TEXT, updateStatus TEXT)";
        database.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS places";
        database.execSQL(query);
        onCreate(database);
    }
    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */
    public void insertPlace(Place queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", queryValues.getId());
        values.put("name", queryValues.getName());
        values.put("longt", queryValues.getLongt());
        values.put("lat", queryValues.getLat());
        values.put("image", queryValues.getImageLink());
        values.put("description", queryValues.getDescription());
        values.put("updateStatus", "no");
        database.insert("places", null, values);
        database.close();
    }

    /**
         * Get list of Users from SQLite DB as Array List
     * @return
     */
    public ArrayList<Place> getAllPlaces() {
        ArrayList<Place> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM places";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Place map = new Place();
                map.setId( cursor.getString(0));
                map.setName(cursor.getString(1));
                map.setLongt(cursor.getFloat(2));
                map.setLat(cursor.getFloat(3));
                map.setImageLink(cursor.getString(4));
                map.setDescription(cursor.getString(5));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return wordList;
    }

    /**
     * Compose JSON out of SQLite records
     * @return
     */
    public String composeJSONfromSQLite(){
        ArrayList<Place> wordList;
        wordList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM places where updateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Place map = new Place();
                map.setId( cursor.getString(0));
                map.setName(cursor.getString(1));
                map.setLongt(cursor.getFloat(2));
                map.setLat(cursor.getFloat(3));
                map.setImageLink(cursor.getString(4));
                map.setDescription(cursor.getString(5));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    /**
     * Get Sync status of SQLite
     * @return
     */
    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync needed\n";
        }
        return msg;
    }

    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbSyncCount(){
        int count = 0;
        String selectQuery = "SELECT  * FROM places where updateStatus = '"+"no"+"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        count = cursor.getCount();
        database.close();
        return count;
    }

    /**
     * Update Sync status against each User ID
     * @param id
     * @param status
     */
    public void updateSyncStatus(String id, String status){
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update places set updateStatus = '"+ status +"' where id="+"'"+ id +"'";
        Log.d("query",updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }
}