package com.example.music.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseUserList extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserList_Database";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "UserList";
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_TRACKID = "TrackID";
    private static final String COLUMN_USERID = "UserID";

    public DatabaseUserList(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRACKID + " TEXT, " +
                COLUMN_USERID + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    // Thêm bài hát vào danh sách yêu thích của người dùng (nếu chưa tồn tại)
    public boolean addTrackToFavorite(Long userID, Long trackID) {
        // Kiểm tra xem bài hát đã tồn tại trong danh sách yêu thích của người dùng chưa
        if (isTrackInFavorites(userID, trackID)) {
            // Nếu đã tồn tại, không thêm vào lại
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRACKID, trackID);
        values.put(COLUMN_USERID, userID);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1; // Trả về true nếu insert thành công, ngược lại trả về false
    }

    // Xóa bài hát khỏi danh sách yêu thích của người dùng
    public boolean removeTrackFromFavorite(Long userID, Long trackID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(
                TABLE_NAME,
                COLUMN_USERID + " = ? AND " + COLUMN_TRACKID + " = ?",
                new String[]{String.valueOf(userID), String.valueOf(trackID)}
        );
        db.close();
        return result > 0; // Trả về true nếu delete thành công, ngược lại trả về false
    }

    // Kiểm tra xem bài hát có trong danh sách yêu thích của người dùng không
    public boolean isTrackInFavorites(Long userID, Long trackID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                null,
                COLUMN_USERID + " = ? AND " + COLUMN_TRACKID + " = ?",
                new String[]{String.valueOf(userID), String.valueOf(trackID)},
                null,
                null,
                null
        );
        boolean isInFavorites = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isInFavorites;
    }

    // Lấy danh sách các bài hát yêu thích của người dùng
    public List<Long> getUserFavoriteTracks(Long userID) {
        List<Long> favoriteTracks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_TRACKID};
        String selection = COLUMN_USERID + " = ?";
        String[] selectionArgs = {String.valueOf(userID)};
        String sortOrder = COLUMN_TRACKID + " ASC"; // Sắp xếp theo thứ tự tăng dần của TrackID

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, sortOrder);

        int trackIdColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_TRACKID);

        while (cursor.moveToNext()) {
            Long trackID = cursor.getLong(trackIdColumnIndex);
            favoriteTracks.add(trackID);
        }

        cursor.close();
        db.close();
        return favoriteTracks;
    }
}