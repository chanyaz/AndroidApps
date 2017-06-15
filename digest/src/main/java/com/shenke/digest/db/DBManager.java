package com.shenke.digest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by Cloud on 2017/6/15.
 */

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add digestStatus
     *
     * @param digestStatuses
     */
    public void add(List<DigestStatus> digestStatuses) {
        db.beginTransaction();  //开始事务
        try {
            for (DigestStatus digestStatus : digestStatuses) {
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
        cv.put("isChecked", digestStatus.isChecked);
        db.update("digestStatus", cv, "uuid = ?", new String[]{digestStatus.uuid});
    }

    /**
     * delete old digestStatus
     *
     * @param digestStatus
     */
    public void deleteOldPerson(DigestStatus digestStatus) {
        db.delete("digestStatus", "uuid >= ?", new String[]{String.valueOf(digestStatus.uuid)});
    }

    /**
     * query all persons, return list
     *
     * @return List<Person>
     */
    public DigestStatus query() {

        Cursor c = queryTheCursor();

        DigestStatus digestStatus = new DigestStatus();
        digestStatus._id = c.getInt(c.getColumnIndex("_id"));
        digestStatus.uuid = c.getString(c.getColumnIndex("uuid"));
        // person.isChecked = c.getInt(c.getColumnIndex(isChecked));


        c.close();
        return digestStatus;
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
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
