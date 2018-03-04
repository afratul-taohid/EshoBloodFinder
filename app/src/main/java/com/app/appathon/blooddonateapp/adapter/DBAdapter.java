package com.app.appathon.blooddonateapp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by T_D on 10/15/2016.
 */

public class DBAdapter {

    private static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_PASSWORD = "Password";
    public static final String KEY_PHONE = "PhoneNumber";
    public static final String KEY_BLOOD = "BloodGroup";
    public static final String KEY_AREA = "Area";
    public static final String KEY_DONATEDATE = "LastDonated";


    private static final String TAG = "DBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private static final String DATABASE_NAME = "BloodDonateDB";
    private static final String DONER_INFO = "Doners";
    private static final int DATABASE_VERSION = 1;
    private static final String CHECK_USER = "select * from Doners where Email = ? AND Password = ?";
    private static final String GET_DONER_PROFILE = "select * from Doners where Email = ?";

    private final Context mCtx;

    private static final String CREATE_DONER_INFO =
            "CREATE TABLE if not exists " + DONER_INFO + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_NAME + "," +
                    KEY_EMAIL +"," +
                    KEY_PASSWORD +"," +
                    KEY_BLOOD +"," +
                    KEY_PHONE +"," +
                    KEY_AREA +"," +
                    KEY_DONATEDATE +");";


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, CREATE_DONER_INFO);
            db.execSQL(CREATE_DONER_INFO);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DONER_INFO);
            onCreate(db);
        }
    }

    public DBAdapter(Context ctx) {

        this.mCtx = ctx;
    }

    public DBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public void insertInfo(String name,String email, String pwd, String blood, String phone, String area, String donateDate) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_EMAIL, email);
        initialValues.put(KEY_PASSWORD, pwd);
        initialValues.put(KEY_BLOOD, blood);
        initialValues.put(KEY_PHONE, phone);
        initialValues.put(KEY_AREA, area);
        initialValues.put(KEY_DONATEDATE, donateDate);

        Log.v(TAG, "1 data inserted successfully.\nName: "+name+"\nEmail: "+email+"\nPassword: "+pwd);

        mDb.insert(DONER_INFO, null, initialValues);
    }

    public void updateDonerInfo(String phone, String area, String donateDate, String email) {

        ContentValues values = new ContentValues();
        values.put(KEY_PHONE, phone);
        values.put(KEY_AREA, area);
        values.put(KEY_DONATEDATE, donateDate);

        // updating row
        mDb.update(DONER_INFO, values, "Email=?", new String[]{email});
    }


    public void deleteOneInfo(long rowId) {
        mDb.delete(DONER_INFO, KEY_ROWID + "=" + rowId, null);
    }


    public boolean checkUser(String email, String password) throws SQLException {
        Cursor mCursor = mDb.rawQuery(CHECK_USER, new String[] {email, password});
        if(mCursor.moveToFirst()) {
            return true;
        }
        else
            return false;
    }

    public Cursor fetchDonerInfo(String email) throws SQLException {
        Cursor mCursor = mDb.rawQuery(GET_DONER_PROFILE, new String[] {email});
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

//    public Cursor fetchAreaInfo(String districtName) throws SQLException {
//        Cursor mCursor = mDb.rawQuery(DB_QUERY, new String[] { districtName });
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//    }


    public Cursor fetchAllInfo() {

        mCursor = mDb.query(DONER_INFO, new String[] {KEY_ROWID, KEY_NAME, KEY_EMAIL, KEY_PASSWORD, KEY_BLOOD, KEY_PHONE, KEY_AREA, KEY_DONATEDATE}, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public int getCount(){
        return mCursor.getCount();
    }

}
