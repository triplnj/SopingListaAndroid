package com.kreativadezign.grocerylist.mygrocerylist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;

import com.kreativadezign.grocerylist.mygrocerylist.Model.Grocery;
import com.kreativadezign.grocerylist.mygrocerylist.Util.Constants;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cyberdog on 4/15/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private Context ctx;

    public DatabaseHandler(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        this.ctx = context;
    }





    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_GROCERY_TABLE = "CREATE TABLE " + Constants.TABLE_NAME + "(" + Constants.KEY_ID + " INTEGER PRIMARY KEY,"
                + Constants.KEY_GROCERY_ITEM + " TEXT,"
                + Constants.KEY_QTY_NUMBER + " TEXT,"
                + Constants.KEY_DATE_NAME + " LONG,"
                + Constants.KEY_CENA_ITEM + " REAL);";

        db.execSQL(CREATE_GROCERY_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        onCreate(db);

    }

    ///CRUD OPERATIONS: Create, Read, Update, Delete Methods
    //Add Grocery
    public void addGrocery(Grocery grocery) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, grocery.getName());
        values.put(Constants.KEY_QTY_NUMBER, grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME, java.lang.System.currentTimeMillis());
        int nv;
        nv = grocery.getCena();


        values.put(Constants.KEY_CENA_ITEM, nv);


        //Insert the row
        db.insert(Constants.TABLE_NAME, null, values);
        Log.d("Saved!", "Saved to DB");

    }

    //Get a Grocery Item
    public Grocery getGrocery(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]
                        {Constants.KEY_ID, Constants.KEY_GROCERY_ITEM,
                                Constants.KEY_QTY_NUMBER, Constants.KEY_DATE_NAME, Constants.KEY_CENA_ITEM},
                Constants.KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();


        Grocery grocery = new Grocery();
        grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
        grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
        grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QTY_NUMBER)));


        //konverzija timestampa u nesto citljivo
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formatedDate = dateFormat.format
                (new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_NAME))).getTime());
        grocery.setCena(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_CENA_ITEM))));


        grocery.setDateItemAdded(formatedDate);


        return grocery;

    }

    //Get all groceries
    public List<Grocery> getAllGroceries() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Grocery> groceryList = new ArrayList<>();
        Cursor cursor = db.query(Constants.TABLE_NAME, new String[]{
                Constants.KEY_ID, Constants.KEY_GROCERY_ITEM, Constants.KEY_QTY_NUMBER,
                Constants.KEY_DATE_NAME, Constants.KEY_CENA_ITEM
        }, null, null, null, null, Constants.KEY_DATE_NAME + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Grocery grocery = new Grocery();
                grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
                grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QTY_NUMBER)));
                grocery.setCena(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_CENA_ITEM))));

                //konverzija timestampa u nesto citljivo
                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                String formatedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_NAME))));


                grocery.setDateItemAdded(formatedDate);

                groceryList.add(grocery);


            } while (cursor.moveToNext());
        }
        return groceryList;
    }

    //Update grocery
    public int updateGrocery(Grocery grocery) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, grocery.getName());
        values.put(Constants.KEY_QTY_NUMBER, grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME, java.lang.System.currentTimeMillis());//sistemsko vreme
        values.put(Constants.KEY_CENA_ITEM, grocery.getCena());
        //update row
        return db.update(Constants.TABLE_NAME, values, Constants.KEY_ID + "=?", new String[]{
                String.valueOf(grocery.getId())
        });
    }

    //Delete grocery
    public void deleteGrocery(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.KEY_ID + "=?", new String[]{
                String.valueOf(id)
        });
        db.close();

    }

    //count groceries
    public int getGroceriesCount() {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();


    }

    public int getSum() {

        int sum = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + Constants.KEY_CENA_ITEM + ") " +
                "as Total FROM " + Constants.TABLE_NAME, null);
        if (cursor.moveToFirst())
            sum = cursor.getInt(cursor.getColumnIndex("Total"));


            return sum;



    }
}
