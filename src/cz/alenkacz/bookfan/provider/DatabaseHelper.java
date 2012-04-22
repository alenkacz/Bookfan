package cz.alenkacz.bookfan.provider;

import cz.alenkacz.bookfan.provider.BooksProvider.Books;
import cz.alenkacz.bookfan.tools.Constants;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * DB root class
 * 
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper  {
	static final String DATABASE_NAME = "bookfan.db";
    static final int DATABASE_VERSION = 1;
    public static final String BOOKS_TABLE_NAME = "books";
    
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	createDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    private void createDB(SQLiteDatabase db) {
    	Log.i(Constants.LOGTAG, "Creating database version " + DATABASE_VERSION);
    	StringBuffer sql = new StringBuffer();
    	sql.append("CREATE TABLE ");
    	sql.append(BOOKS_TABLE_NAME);
    	sql.append(" (");
			sql.append(Books._ID);
			sql.append(" integer primary key autoincrement,");
			sql.append(Books.SHELF_ID);
			sql.append(" integer,");
			sql.append(Books.TITLE);
			sql.append(" text,");
			sql.append(Books.AUTHOR);
			sql.append(" text,");
			sql.append(Books.SERVER_UID);
			sql.append(" integer,");
			sql.append(Books.IMAGE);
			sql.append(" text,");
			sql.append(Books.URL);
			sql.append(" text");
    	sql.append(");");
    	db.execSQL(sql.toString());
    }
}
