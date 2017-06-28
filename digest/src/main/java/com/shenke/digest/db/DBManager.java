package com.shenke.digest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cloud on 2017/6/15.
 */

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * add digestStatus
     *
     * @param digeststatuses
     */
    public void add(List<DigestStatus> digeststatuses) {
        db.beginTransaction();  //开始事务
        try {
            for (DigestStatus digestStatus : digeststatuses) {
                db.execSQL("INSERT INTO digestStatus VALUES(null, ?, ?)", new Object[]{digestStatus.uuid, digestStatus.isChecked});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }

    /**
     * update digestStatus's isChecked status
     *
     * @param digestStatus
     */
    public void updateStatus(DigestStatus digestStatus) {
        ContentValues cv = new ContentValues();
        cv.put("uuid", digestStatus.uuid);
        cv.put("isChecked", digestStatus.isChecked);
        db.update("digestStatus", cv, "uuid = ?", new String[]{digestStatus.uuid});
    }

    /**
     * delete old digestStatus
     *
     * @param digestStatus
     */
    public void deleteOldDigest(DigestStatus digestStatus) {
        db.delete("digestStatus", "uuid >= ?", new String[]{digestStatus.uuid});
    }
    public void deleteOldDigest() {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL( "DELETE FROM digestStatus");
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    /**
     * query all digest, return list
     *
     * @return List<DigestStatus>
     */
    public List<DigestStatus> query() {
        ArrayList<DigestStatus> digests = new ArrayList<DigestStatus>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            DigestStatus digest = new DigestStatus();
            digest._id = c.getInt(c.getColumnIndex("_id"));
            digest.uuid = c.getString(c.getColumnIndex("uuid"));
            digest.isChecked = c.getInt(c.getColumnIndex("isChecked"));
            digests.add(digest);
        }
        c.close();
        return digests;
    }

    /**
     * query all digestStatus, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM digestStatus", null);
        return c;
    }

    /**
     * query one
     */

    public DigestStatus queryItemStatus(String uuid) {
        DigestStatus digestStatus = new DigestStatus();
        Cursor c = queryItemCursor(uuid);
        digestStatus.isChecked = c.getInt(c.getColumnIndex("isChecked"));
        c.close();
        return digestStatus;
    }

    public Cursor queryItemCursor(String uuid) {
        String[] projection = {"_id", "uuid", "isChecked"};
        String selection = "uuid LIKE ?";
        String[] selectionArgs = {uuid};
        Cursor c = db.query("digestStatus", projection, selection, selectionArgs, null, null, null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        this.db.close();
    }
}
