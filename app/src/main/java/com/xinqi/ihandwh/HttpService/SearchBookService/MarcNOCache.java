package com.xinqi.ihandwh.HttpService.SearchBookService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by presisco on 2015/11/29.
 */
public class MarcNOCache {
    protected HashMap<String,String> marcnoMap;
    protected MarcNOCacheHelper cacheHelper;

    public MarcNOCache(Context context)
    {
        marcnoMap=new HashMap<String,String>();
        cacheHelper=new MarcNOCacheHelper(context,null,1);
        cacheHelper.queryMarcNOCache();
    }

    public String getFloorInfo(String marcno)
    {
        if(marcnoMap.containsKey(marcno))
            return marcnoMap.get(marcno);
        else
            return null;
    }

    public void appendData(String[] pair)
    {
        marcnoMap.put(pair[0],pair[1]);
        cacheHelper.addMarcNOCache(pair);
    }

    protected class MarcNOCacheHelper extends SQLiteOpenHelper {
        private static final String DB_TABLE_NAME="MarcNOCacheTable";
        private static final String DB_NAME="marcno_cache.db";
        public static final String KEY_ROW_ID="_id";
        public static final String KEY_MARC_NO="marcno";
        public static final String KEY_FLOOR_INFO="floorname";

        public MarcNOCacheHelper(Context context,SQLiteDatabase.CursorFactory factory,int version)
        {
            super(context,DB_NAME,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //**********************创建搜索历史表**************************
            db.execSQL("CREATE TABLE " + DB_TABLE_NAME + " ( " +
                    KEY_MARC_NO + " TEXT , "+
                    KEY_FLOOR_INFO+" TEXT );");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        }

        public void queryMarcNOCache()
        {
            ArrayList<String> result=new ArrayList<String>();
            SQLiteDatabase db=getReadableDatabase();
            Cursor cursor=db.query(DB_TABLE_NAME, new String[]{KEY_MARC_NO, KEY_FLOOR_INFO}, null, null, null, null, null);
            int marcnoIndex=cursor.getColumnIndex(KEY_MARC_NO);
            int floorinfoIndex=cursor.getColumnIndex(KEY_FLOOR_INFO);
            int i=0;
            for(cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {
                marcnoMap.put(cursor.getString(marcnoIndex), cursor.getString(floorinfoIndex));
            }
            cursor.close();
            db.close();
        }

        public void addMarcNOCache(String[] newData)
        {
            SQLiteDatabase db=getWritableDatabase();
            db.execSQL("insert into "+DB_TABLE_NAME+"("+KEY_MARC_NO+","+KEY_FLOOR_INFO+") values ('"+newData[0]+"','"+newData[1]+"')");
            db.close();
        }
    }
}
