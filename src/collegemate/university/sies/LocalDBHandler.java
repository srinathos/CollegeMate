package collegemate.university.sies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Srinath
 * Date: 28/12/13
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class LocalDBHandler extends SQLiteOpenHelper {
    final String P_KEY="_id" ;
    final String KEY_TITLE="title";
    final String KEY_DATE="date";
    final String KEY_CONTENT="content";
    final String KEY_FROM="sender";
    final String KEY_MESSAGE_STATUS="message_status";
    final String TABLE_NAME="notices";
    final static String CREATE_TABLE="CREATE TABLE IF NOT EXISTS notices(_id INTEGER PRIMARY KEY AUTOINCREMENT,title VARCHAR,date VARCHAR,content LONGTEXT,sender VARCHAR,message_status INTEGER);";

    //Local DataBase Handler, All SQLiteDB interactions through THIS portal ONLY.

    public LocalDBHandler(Context context)
    {
        super(context,"contents",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void insert(String[] data) //INSERTING DB DATA..ROW..By..row...
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_TITLE,data[1]);
        values.put(KEY_DATE,data[2]);
        values.put(KEY_CONTENT,data[3]);
        values.put(KEY_FROM,data[4]);
        values.put(KEY_MESSAGE_STATUS,0);
        db.insert(TABLE_NAME,null,values);
        //db.close();
    }

    public boolean doesExists() //TO CHECK IF DB EXISTS
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{KEY_TITLE},null,null,null,null,null);
        if(cursor!=null && cursor.getCount()>0)
        {
            Log.d("NmE","DATABASE ENTRIES: "+cursor.getCount());
            //db.close();
            return true;
        }
        Log.d("NmE","NO DATABASE;WILL CREATE ACCORDINGLY");
        //db.close();
        return false;
    }

    public Cursor getNotice() //NOTICE HEADERS FOR THE LIST
    {
        SQLiteDatabase db=getReadableDatabase();
        Cursor temp=db.query(TABLE_NAME, new String[]{P_KEY,KEY_TITLE , KEY_FROM, KEY_MESSAGE_STATUS},null,null,null,null,"_id DESC");
        //db.close();
        return temp;
        //db.query(TABLE_NAME, new String[]{P_KEY,KEY_TITLE , KEY_FROM},null,null,null,null,"DESC");
        //return db.rawQuery("SELECT title,sender FROM notices DESC",new String[]{});
    }
    public Cursor getNoticeData(int position) //NOTICE DATA AFTER EXPANSION
    {
        //Position Correction

        Log.d("NMe","ExpandPosition: "+position);
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{KEY_TITLE,KEY_DATE,KEY_CONTENT,KEY_FROM},"_id=\""+(position)+"\"",null,null,null,null);
        //db.close();
        return cursor;
    }

    public int cursorCount()//Notices available
    {
        SQLiteDatabase db=getReadableDatabase();
        Cursor cursor=db.query(TABLE_NAME,new String[]{KEY_MESSAGE_STATUS},null,null,null,null,null);
        return cursor.getCount();
    }

    public void markRead(int position)
    {
        Log.d("NMe","Marking read");
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_MESSAGE_STATUS,1);
        db.update(TABLE_NAME,values,P_KEY+"="+position,null);
    }
}
