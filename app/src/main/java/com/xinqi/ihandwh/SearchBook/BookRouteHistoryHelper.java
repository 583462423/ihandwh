package com.xinqi.ihandwh.SearchBook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xinqi.ihandwh.Model.BookRouteInfo;

import java.util.ArrayList;

/**
 * Created by syd on 2015/11/19.
 */
public class BookRouteHistoryHelper extends SQLiteOpenHelper {
    //数据库名
    private static final String DB_NAME="BookRoute.db";
    //表名
    private static final String TABLE_NAME="BookRoute";
    //行id，安卓一般以_id开始
    private static final String KEY_ROW_ID="_id";
    //列名，本数据库包括四列
    private static final String KEY_BOOK_NAME ="bookname";
    private static final String KEY_BOOK_CODE ="bookcode";
    private static final String KEY_BOOK_DETAILS ="bookdetails";
    private static final String KEY_BOOK_POS ="pos";
//    String firstid;
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
    public BookRouteHistoryHelper(Context context) {
        super(context, DB_NAME, null, 1);
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
                KEY_BOOK_NAME + " text not null," + KEY_BOOK_CODE + " text not null," + KEY_BOOK_DETAILS + " text not null);");*/
//        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " +
//                KEY_ROW_ID + " INTEGER PRIMARY KEY , " +
//                KEY_BOOK_NAME + " TEXT ,"+KEY_BOOK_DETAILS+" TEXT ,"+KEY_BOOK_CODE+"TEXT);");
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " +
                KEY_ROW_ID + " INTEGER PRIMARY KEY , " +
                KEY_BOOK_NAME + " TEXT ," +
                KEY_BOOK_POS + " TEXT ," + KEY_BOOK_DETAILS + " TEXT ," + KEY_BOOK_CODE + " TEXT );");
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
    public ArrayList<BookRouteInfo> quryBookRoute(){
            ArrayList<BookRouteInfo> bookRouteInfos=new ArrayList<>();
            SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        //按时间先后排序获取
        Cursor cursor=sqLiteDatabase.query(TABLE_NAME, new String[]{KEY_BOOK_NAME, KEY_BOOK_DETAILS, KEY_BOOK_CODE,KEY_BOOK_POS}, null, null, null, null,null);
        for (cursor.moveToLast();!(cursor.isBeforeFirst());cursor.moveToPrevious()) {
          /*  OrderInfo orderInfo=new OrderInfo();
            orderInfo.setRoom(cursor.getString(cursor.getColumnIndex(KEY_BOOK_NAME)));
            orderInfo.setSeatNum(cursor.getString(cursor.getColumnIndex(KEY_BOOK_DETAILS)));
            orderInfo.setDate(cursor.getString(cursor.getColumnIndex(KEY_BOOK_CODE)));
            orderInfos.add(orderInfo);*/
            BookRouteInfo bookRouteInfo=new BookRouteInfo();
            bookRouteInfo.setBookname(cursor.getString(cursor.getColumnIndex(KEY_BOOK_NAME)));
            bookRouteInfo.setBookcode(cursor.getString(cursor.getColumnIndex(KEY_BOOK_CODE)));
            bookRouteInfo.setBookpos(cursor.getString(cursor.getColumnIndex(KEY_BOOK_POS)));
            bookRouteInfo.setBookdetails(cursor.getString(cursor.getColumnIndex(KEY_BOOK_DETAILS)));
            bookRouteInfos.add(bookRouteInfo);

        }
        cursor.close();
        sqLiteDatabase.close();
        return bookRouteInfos;
    }

    //delete a orderhistory
    // TODO: 2015/11/22 ！！！！！！！！！！！！！！！
    public boolean deleteBookRoute(String bookcode)
    {   SQLiteDatabase db=getWritableDatabase();
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        Cursor cursor=sqLiteDatabase.query(TABLE_NAME, new String[]{KEY_ROW_ID,KEY_BOOK_CODE}, null, null, null, null, KEY_BOOK_CODE);
//        cursor.moveToFirst();
        //从后往前找
        for (cursor.moveToLast();!(cursor.isBeforeFirst());cursor.moveToPrevious()) {
            if (cursor.getString(cursor.getColumnIndex(KEY_BOOK_CODE)).equals(bookcode)){
                String deleteid=cursor.getString(cursor.getColumnIndex(KEY_ROW_ID));
                boolean b=db.delete(TABLE_NAME, KEY_ROW_ID + "=" +deleteid, null)>0 ;
                cursor.close();
                db.close();
                 Log.i("bac","删除搜藏路线："+b);
                return b;
            }
        }
//        String =cursor.getString(cursor.getColumnIndex(KEY_ROW_ID));
//        Log.i("bac","firstid:"+firstid);
        cursor.close();
        db.close();
        return false;
    }
    //get the size of OrderInfo
   /* public long getOrderInfoSize() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME;
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }*/
    //add an orderinfo
    public boolean insertBookRoute(String bookname, String bookcode, String bookdetails,String bookpos){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(KEY_BOOK_NAME,bookname);
        contentValues.put(KEY_BOOK_DETAILS,bookdetails);
        contentValues.put(KEY_BOOK_CODE,bookcode);
        contentValues.put(KEY_BOOK_POS,bookpos);
        //保证最多只保留50条记录
       /* long count=getOrderInfoSize();
        Log.i("bac",count+"====================");*/
       /* if (count>=50){
            //delete first
            deleteBookRoute();
        }*/
        long b=sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        sqLiteDatabase.close();
        return b>0;
    }/**/

}
