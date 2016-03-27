package com.xinqi.ihandwh.OrderSeats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.xinqi.ihandwh.Model.FloorInfo;
import com.xinqi.ihandwh.Model.OrderInfo;

import java.util.ArrayList;

/**
 * Created by syd on 2015/11/19.
 */
public class FloorInfoHelper extends SQLiteOpenHelper {
    //数据库名
    private static final String DB_NAME="Floorinfo.db";
    //表名
    private static final String TABLE_NAME="Floorinfo";
    //行id，安卓一般以_id开始
    private static final String KEY_ROW_ID="_id";
    //列名，本数据库包括两列
    private static final String KEY_TOTAL ="total";
    private static final String CURRENT ="current";
    private static final String LAYER ="layer";
   /* private static final String KEY_CONFIRMTIME="confirmtime";//最晚刷卡时间
    private static final String KEY_SEAT_NUM="seatnum";
    private static final String KEY_HAS_CANCELED="hascanceled";*/
    //String firstid;
    final Context context;
    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    //调用父类构造方法，参数name
    public FloorInfoHelper(Context context) {
        super(context, DB_NAME, null, 2);
        this.context = context;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建预约历史表
      /*  db.execSQL("create table" + TABLE_NAME + " ( " + KEY_ROW_ID + " integer primary key autoincrement," +
                KEY_TOTAL + " text not null," + CURRENT + " text not null," + KEY_SEAT_NUM + " text not null);");*/
//        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " +
//                KEY_ROW_ID + " INTEGER PRIMARY KEY , " +
//                KEY_TOTAL + " TEXT ,"+KEY_SEAT_NUM+" TEXT ,"+CURRENT+"TEXT);");
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " +
                KEY_ROW_ID + " INTEGER PRIMARY KEY , " +
                KEY_TOTAL + " TEXT ," +
                LAYER + " TEXT ," + CURRENT + " TEXT );");
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }
    public ArrayList<FloorInfo> quryFloorinfo(){
            ArrayList<FloorInfo> floorInfos=new ArrayList<>();
            SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        //按时间先后排序获取
        Cursor cursor=sqLiteDatabase.query(TABLE_NAME, new String[]{KEY_TOTAL, LAYER, CURRENT}, null, null, null, null,null);
        for (cursor.moveToLast();!(cursor.isBeforeFirst());cursor.moveToPrevious()) {
            FloorInfo floorInfo=new FloorInfo();
            floorInfo.setLayer(cursor.getString(cursor.getColumnIndex(LAYER)));
            floorInfo.setRest(Integer.parseInt(cursor.getString(cursor.getColumnIndex(CURRENT))));
            floorInfo.setTotal(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_TOTAL))));
////          /*  floorInfo.(cursor.getString(cursor.getColumnIndex(KEY_TOTAL)));
//            orderInfo.setSeatNum(cursor.getString(cursor.getColumnIndex(KEY_SEAT_NUM)));
//            orderInfo.setDate(cursor.getString(cursor.getColumnIndex(CURRENT)));
//            orderInfo.setConfirmTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_CONFIRMTIME))));
//            orderInfo.setHascancele*/d(cursor.getString(cursor.getColumnIndex(KEY_HAS_CANCELED)));
            floorInfos.add(floorInfo);
        }
        cursor.close();
        sqLiteDatabase.close();
        return floorInfos;
    }

    //delete a orderhistory
   /* public boolean deleteFirstOrderinfo()
    {   SQLiteDatabase db=getWritableDatabase();
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        Cursor cursor=sqLiteDatabase.query(TABLE_NAME, new String[]{KEY_ROW_ID}, null, null, null, null, CURRENT);
        cursor.moveToFirst();
        firstid=cursor.getString(cursor.getColumnIndex(KEY_ROW_ID));
        Log.i("bac","firstid:"+firstid);
        cursor.close();
        boolean b=db.delete(TABLE_NAME, KEY_ROW_ID + "=" + firstid, null)>0 ;
        db.close();
        Log.i("bac", b + "");
        return b;
    }*/
    public void deleteAllFloorInfo()
    {   SQLiteDatabase db=getWritableDatabase();
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        Cursor cursor=sqLiteDatabase.query(TABLE_NAME, new String[]{KEY_ROW_ID}, null, null, null, null, CURRENT);
        cursor.moveToFirst();

        for (cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            String id=cursor.getString(cursor.getColumnIndex(KEY_ROW_ID));
             boolean b=db.delete(TABLE_NAME, KEY_ROW_ID + "=" +id, null)>0 ;
            //Log.i("sxj","删除"+b);
        }
//        Log.i("bac","firstid:"+lasttid);
        cursor.close();
        db.close();
       /* Log.i("bac",b+"");
        return b;*/
    }
    //updata one item
    //get the size of OrderInfo
    public long getOrderInfoSize() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }
    //add an orderinfo
    public boolean insertFloorInfo(String layer ,int total,int rest){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(KEY_TOTAL,total);
        contentValues.put(LAYER,layer);
        contentValues.put(CURRENT,rest);
     /*  *//* contentValues.put(KEY_CONFIRMTIME,confirmTime);
        contentV*//*alues.put(KEY_HAS_CANCELED,hascanceled);*/
        //保证最多只保留50条记录
       /* long count=getOrderInfoSize();
        Log.i("bac",count+"====================");
        if (count>=50){
            //delete first
            deleteFirstOrderinfo();
        }*/
        sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        sqLiteDatabase.close();
        return true;
    }

}
