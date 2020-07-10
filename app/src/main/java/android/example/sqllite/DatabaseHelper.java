package android.example.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "contact.db";
    public static final String TABLE_NAME = "contact_detail";
    public static final String COL_1 = "NAME";
    public static final String COL_2 = "NUMBER";
    public static final String COL_3 = "MESSAGE";
    public static final String COL_0 = "ID";

    public DatabaseHelper(Context context) {

        // CREATE DATABASE WHENEVER CONSTRUCTOR CALLED
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // EXECUTES WHATEVER QUERY AS AN ARGUMENT
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER primary key autoincrement, NAME TEXT,NUMBER TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    //METHOD TO INSERT DATA
    public boolean insertData(String name, String number){

        //CREATE DATABASE AND TABLE
        SQLiteDatabase db = this.getWritableDatabase();

        //INSTANCE OF CLASS CONTENT VALUE
        ContentValues contentValues = new ContentValues();

        // INSERTS DATA IN COLUMNS
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, number);

        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1){
            return false;
        }
        else{
            return  true;
        }
    }

    //GET ALL DATA
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TABLE_NAME,null);
        return result;
    }

    //UPDATE DATA
    public boolean updateData(String id, String name, String number){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_0, id);
        contentValues.put(COL_1, name);
        contentValues.put(COL_2, number);

        db.update(TABLE_NAME,contentValues,"ID = ?",new String[] { id });

        return true;
    }

    //DELETE DATA
    public boolean deleteData(String number){

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,COL_2+"="+number,null) > 0;
    }

    public Cursor viewDataList(){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "select * from "+ TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }
}
