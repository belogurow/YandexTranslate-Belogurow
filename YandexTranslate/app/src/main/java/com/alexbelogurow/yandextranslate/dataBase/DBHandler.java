package com.alexbelogurow.yandextranslate.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alexbelogurow.yandextranslate.model.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * В качестве хранения истории и избранного была выбрана SQLite, так как
 * данные должны быть структурированы. Так как данных будет много, то предполагается
 * поиск и добавление данных, лучше всего это реализовать с помощью БД, а не sharedPreferences
 *
 * БД содержит одну таблицу с полями (id, text, translatedText, fromLang, toLang, favourite)
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TranslationHistory";
    private static final String TABLE_TRANSLATION = "Translations";

    private static final String KEY_ID = "id",
            KEY_LANG_FROM = "fromLang",
            KEY_LANG_TO = "toLang",
            KEY_TEXT = "text",
            KEY_TR_TEXT = "translatedText",
            KEY_FAVOURITE = "favorite";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Создание таблицы
     * @param db БД
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TRANSLATION + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_TEXT + " TEXT," +
                KEY_TR_TEXT + " TEXT," +
                KEY_LANG_FROM + " TEXT," +
                KEY_LANG_TO + " TEXT," +
                KEY_FAVOURITE + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    /**
     * Обновление таблицы
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSLATION);

        // Create tables again
        onCreate(db);
    }

    /**
     * Добавление нового перевода в БД
     *
     * При добавлении новой записи используется следующий принцип -
     * если запись с таким (text, langFrom, langTo) уже существует, то
     * она удаляется, и добавляется новая, но уже с обновленными полями
     *                    (например favourite поменял свое значение)
     *
     * @param translation перевод, который надо добавить
     */
    public void addTranslation(Translation translation) {
        String SELECT_TRANSLATE = "SELECT * FROM " + TABLE_TRANSLATION +
                " WHERE " + KEY_TEXT + " ='" + translation.getText() +
                "' AND " + KEY_LANG_FROM + " ='" + translation.getFrom() + "'" +
                " AND " + KEY_LANG_TO + " ='" + translation.getTo() + "'";



        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_TRANSLATE, null);
        int count = cursor.getCount();
        String id = "";
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex(KEY_ID));
        }

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
        values.put(KEY_LANG_FROM, translation.getFrom());
        values.put(KEY_LANG_TO, translation.getTo());
        values.put(KEY_FAVOURITE, translation.getFavourite());

        //Log.d(Log.DEBUG + "", "insert");
        Log.d(Log.DEBUG + "-add", translation.toString());
        db1.insert(TABLE_TRANSLATION, null, values);


        db1.close();
    }


    /**
     * Получение перевода из БД по id
     * @param id id перевода
     * @return
     */
    public Translation getTranslate(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_TRANSLATION, new String[] {
                KEY_ID,
                KEY_TEXT,
                KEY_TR_TEXT,
                        KEY_LANG_FROM,
                        KEY_LANG_TO,
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

    /**
     * Получение последнего перевода из БД
     * @return
     */
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

    /**
     * Получение списка всем переводов из БД
     * @param fav равно true, если нужен список только тех переводов,
     *            которые добавлены в избранное
     * @return
     */
    public List<Translation> getAllTranslations(boolean fav) {

        List<Translation> translationsList = new ArrayList<>();
        String selectQuery;
        if (fav) {
            selectQuery = "SELECT * FROM " + TABLE_TRANSLATION +
                    " WHERE " + KEY_FAVOURITE + " = '1'";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_TRANSLATION;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Translation translation = new Translation(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        Integer.parseInt(cursor.getString(5)));
                translationsList.add(translation);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        Collections.reverse(translationsList);
        return translationsList;
    }


    /**
     * Удаление всех переводов из БД, которые находятся в избранном
     */
    public void deleteFavTranslations() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSLATION, KEY_FAVOURITE + " = ?",
                new String[] { String.valueOf(1) });
        db.close();
    }

    /**
     * Удаление всех переводов из БД
     */
    public void deleteAllTranslations() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_TRANSLATION, null, null);

        db.close();
    }

    /**
     * Получение кол-ва записей в БД
     * @return
     */
    public int getTranslationsCount() {
        String countQuery = "SELECT * FROM " + TABLE_TRANSLATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();

        cursor.close();
        db.close();

        return count;
    }

}
