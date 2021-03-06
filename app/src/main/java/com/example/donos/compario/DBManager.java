package com.example.donos.compario;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

public class DBManager {
    public SQLiteDatabase sqlDB;
    Context context;
    static final String DBName = "ShoppingList";
    static final String TableName = "shopping_list";
    static final String ColTitle = "OfferTitle";
    static final String ColStore = "Store";
    static final String ColPrice = "Price";
    static final String ColCategory = "Category";
    static final int DBVersion = 1;

    static final String CreateTable = "CREATE TABLE IF NOT EXISTS " +TableName+
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+ColTitle+
            " TEXT,"+ ColStore + " TEXT,"+ColPrice+ " TEXT,"+ColCategory+" TEXT);";

    static class DatabaseHelperUser extends SQLiteOpenHelper{
Context context;
        DatabaseHelperUser(Context context){
            super(context,DBName,null,DBVersion);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CreateTable);
            Toast.makeText(context,"Table created",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("Drop table IF EXISTS "+TableName);
            onCreate(db);
        }
    }

    public DBManager(Context context){
        DatabaseHelperUser db = new DatabaseHelperUser(context);
        sqlDB=db.getWritableDatabase();
    }

    public long Insert(ContentValues values){
        long ID = sqlDB.insert(TableName,"",values);
        //could insert id is user id, or fail id is 0
        return ID;
    }

    public Cursor query(String[] projection,String Selection,String[] SelectionArgs,String SortOrder){
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TableName);
        Cursor cursor = qb.query(sqlDB,projection,Selection,SelectionArgs,null,null,SortOrder);
        return cursor;
    }
    public void update(String tableName,ContentValues values,String where,String[] whereArgs){
        sqlDB.update(tableName,values,where,whereArgs);
    }
    public void delete(String whereClause, String[] whereArgs)
    {
        sqlDB.delete(DBManager.TableName,whereClause,whereArgs);
    }


//    public boolean checkEmail(String email){
//        DatabaseHelperUser db = new DatabaseHelperUser(context);
//        sqlDB=db.getReadableDatabase();
//
//    }
}
