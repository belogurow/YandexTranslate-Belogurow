package com.alexbelogurow.yandextranslate.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.alexbelogurow.yandextranslate.model.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexbelogurow on 17.04.17.
 */

// TODO добавить метод для удаления только избранного
public class DBHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "TranslationHistory";

    // Contacts table name
    private static final String TABLE_TRANSLATION = "Translations";

    // Contacts Table Columns names
    private static final String KEY_ID = "id",
            KEY_FROM = "fromLang",
            KEY_TO = "toLang",
            KEY_TEXT = "text",
            KEY_TR_TEXT = "translatedText",
            KEY_FAVOURITE = "favorite";

    //private static final String KEY_PH_NO = "phone_number";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Создание таблицы
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TRANSLATION + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_TEXT + " TEXT," +
                KEY_TR_TEXT + " TEXT," +
                KEY_FROM + " TEXT," +
                KEY_TO + " TEXT," +
                KEY_FAVOURITE + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Обновление таблицы
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSLATION);

        // Create tables again
        onCreate(db);
    }

    public void addTranslation(Translation translation) {
        String SELECT_TRANSLATE = "SELECT * FROM " + TABLE_TRANSLATION +
                " WHERE " + KEY_TEXT + " ='" + translation.getText() +
                "' AND " + KEY_FROM + " ='" + translation.getFrom() + "'" +
                " AND " + KEY_TO + " ='" + translation.getTo() + "'";



        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_TRANSLATE, null);
        int count = cursor.getCount();
        String id = "";
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex(KEY_ID));
        }
        //Log.d(Log.DEBUG + "-add", count + "");


        cursor.close();
        db.close();

        SQLiteDatabase db1 = this.getWritableDatabase();

        if (count != 0) {
            db1.delete(TABLE_TRANSLATION, KEY_ID + "=?", new String[] { id });
            //Log.d(Log.DEBUG + "", "delete");
        }


        ContentValues values = new ContentValues();
        values.put(KEY_TEXT, translation.getText());
        values.put(KEY_TR_TEXT, translation.getTranslatedText());
        values.put(KEY_FROM, translation.getFrom());
        values.put(KEY_TO, translation.getTo());
        values.put(KEY_FAVOURITE, translation.getFavourite());

        //Log.d(Log.DEBUG + "", "insert");
        db1.insert(TABLE_TRANSLATION, null, values);


        db1.close();

        /*SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TEXT, translation.getText());
        values.put(KEY_TR_TEXT, translation.getTranslatedText());
        values.put(KEY_FROM, translation.getFrom());
        values.put(KEY_TO, translation.getTo());
        values.put(KEY_FAVOURITE, translation.getFavourite());

        //db.insertWithOnConflict(TABLE_TRANSLATION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.insert(TABLE_TRANSLATION, null, values);

        db.close(); */
    }


    public Translation getTranslate(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_TRANSLATION, new String[] {
                KEY_ID,
                KEY_TEXT,
                KEY_TR_TEXT,
                KEY_FROM,
                KEY_TO,
                KEY_FAVOURITE},
                KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Translation translation = new Translation(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                Integer.parseInt(cursor.getString(5)));

        return translation;
    }

    public Translation getLastTranslation() {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_TRANSLATION;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();

        Translation translation = new Translation(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                Integer.parseInt(cursor.getString(5)));

        cursor.close();
        db.close();

        return translation;
    }

    public List<Translation> getAllTranslations(boolean fav) {

        List<Translation> translationsList = new ArrayList<>();
        // Select All Query
        String selectQuery;
        if (fav) {
            selectQuery = "SELECT * FROM " + TABLE_TRANSLATION +
                    " WHERE " + KEY_FAVOURITE + " = '1'";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_TRANSLATION;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Translation translation = new Translation(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        Integer.parseInt(cursor.getString(5)));
                // Adding contact to list
                translationsList.add(translation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        Collections.reverse(translationsList);

        // return contact list
        return translationsList;
    }

    // Deleting single contact
    public void deleteTranslation(Translation translation) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSLATION, KEY_ID + " = ?",
                new String[] { String.valueOf(translation.getId()) });
        db.close();
    }

    public void deleteAllTranslations() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_TRANSLATION, null, null);

        db.close();
    }



    public int getTranslationsCount() {
        String countQuery = "SELECT * FROM " + TABLE_TRANSLATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();

        cursor.close();
        db.close();

        // return count
        return count;
    }

}
