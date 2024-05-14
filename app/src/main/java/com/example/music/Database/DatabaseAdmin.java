package com.example.music.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.music.Admin.Admin;

public class DatabaseAdmin extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Admin_Database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Admin";
    private static final String COLUMN_ID = "AdminID";
    private static final String COLUMN_NAME = "AdminName";
    private static final String COLUMN_PASSWORD = "AdminPassword";

    public DatabaseAdmin( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public boolean insert(String name, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1; // Trả về true nếu insert thành công, ngược lại trả về false
    }

    public boolean checkName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NAME},
                COLUMN_NAME + "=?",
                new String[]{name}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public Admin checkPass(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_NAME, COLUMN_PASSWORD},
                COLUMN_NAME + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);

        Admin admin = null;
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            String pass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            // Thêm các trường khác nếu cần thiết
            admin = new Admin(name, pass); // Tạo đối tượng Users từ thông tin trong cơ sở dữ liệu
            cursor.close();
        }
        return admin; // Trả về đối tượng Users hoặc null nếu thông tin đăng nhập không hợp lệ
    }
}