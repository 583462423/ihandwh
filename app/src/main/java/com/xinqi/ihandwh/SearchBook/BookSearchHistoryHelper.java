package com.xinqi.ihandwh.SearchBook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by presisco on 2015/11/19.
 */
public class BookSearchHistoryHelper extends SQLiteOpenHelper {
    private static final String DB_TABLE_NAME="BookSearchHistory";
    private static final String DB_NAME="book_search_history.db";
    public static final String KEY_ROW_ID="_id";
    public static final String KEY_BOOK_NAME="bookname";

    public BookSearchHistoryHelper(Context context,SQLiteDatabase.CursorFactory factory,int version)
    {
        super(context,DB_NAME,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //**********************创建搜索历史表**************************
        db.execSQL("CREATE TABLE " + DB_TABLE_NAME + " ( " +
                KEY_BOOK_NAME + " TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
    }

    public ArrayList<String> querySearchHistory(int count)
    {
        ArrayList<String> result=new ArrayList<String>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.query(DB_TABLE_NAME,new String[]{KEY_BOOK_NAME},null,null,null,null,null);
        int nameIndex=cursor.getColumnIndex(KEY_BOOK_NAME);
        int i=0;
        for(cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {
            result.add(cursor.getString(nameIndex));
            if(i++>=count)
                break;
        }cursor.close();
        db.close();
        return result;
    }

    public void addSearchHistory(String newHistory)
    {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("insert into "+DB_TABLE_NAME+"("+KEY_BOOK_NAME+") values ('"+newHistory+"')");
        db.close();
    }
}
