package cz.alenkacz.bookfan.provider;

import java.util.HashMap;

import cz.alenkacz.bookfan.tools.Constants;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class BooksProvider extends ContentProvider  {

	private static final UriMatcher mUriMatcher;
	private static HashMap<String, String> mProjectionMap;
	
	private static final int BOOKS = 1;
    private static final int BOOKS_ID = 2;
	
	@Override
	public int delete(Uri uri, String where, String[] selectionArgs) {
		SQLiteDatabase db = DatabaseHelperSingleton.getDatabaseHelper(getContext()).getWritableDatabase();
        int count = 0;
        
        if(uri.getPathSegments().size() > 1) {
        	String starredId = uri.getPathSegments().get(1);
        	count = db.delete(DatabaseHelper.BOOKS_TABLE_NAME, Books._ID + "=" + starredId, null);
        	getContext().getContentResolver().notifyChange(uri, null);
        	
        	return count;
        } else if(where != null && where != "") {
        	count = db.delete(DatabaseHelper.BOOKS_TABLE_NAME, where, null);
        	getContext().getContentResolver().notifyChange(uri, null);
        	
        	return count;
        }

        return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
        case BOOKS:
            return Books.CONTENT_TYPE;
        case BOOKS_ID:
            return Books.CONTENT_ITEM_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (mUriMatcher.match(uri) != BOOKS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        SQLiteDatabase db = DatabaseHelperSingleton.getDatabaseHelper(getContext()).getWritableDatabase();
        long rowId = db.insert(DatabaseHelper.BOOKS_TABLE_NAME, null, values);
        if (rowId > 0) {
        	Log.i(Constants.LOGTAG, "Item in the starred database was inserted with id " + rowId);
        	
            Uri itemUri = ContentUris.withAppendedId(Books.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(itemUri, null);
           
            return itemUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        qb.setTables(DatabaseHelper.BOOKS_TABLE_NAME);
        qb.setProjectionMap(mProjectionMap);
        switch (mUriMatcher.match(uri)) {
        	case BOOKS_ID:
	            qb.appendWhere(Books._ID + "=" + uri.getPathSegments().get(1));
	            break;
        	case BOOKS:
	            // intentionally do nothing
        		break;
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy = Books.DEFAULT_SORT_ORDER;

        SQLiteDatabase db = DatabaseHelperSingleton.getDatabaseHelper(getContext()).getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
	
	public static final String AUTHORITY = "cz.alenkacz.bookfan.provider.booksprovider";
	
	public static final class Books {
		public static final String _ID = "_id";
		public static final String SHELF_ID = "shelfId";
		public static final String TITLE = "title";
		public static final String AUTHOR = "author";
		public static final String IMAGE = "image";
		public static final String SERVER_UID = "serverUid";
		public static final String URL = "url";
		
		public static final String DEFAULT_SORT_ORDER = _ID + " DESC";
		
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/books");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/alenkacz.bookfan.books";
        
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/inmite.bookfan.books";
	}
	
	static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "books", BOOKS);
        mUriMatcher.addURI(AUTHORITY, "books/#", BOOKS_ID);

        mProjectionMap = new HashMap<String, String>();
        mProjectionMap.put(Books._ID, Books._ID);
        mProjectionMap.put(Books.TITLE, Books.TITLE);
        mProjectionMap.put(Books.SHELF_ID, Books.SHELF_ID);
        mProjectionMap.put(Books.AUTHOR, Books.AUTHOR);
        mProjectionMap.put(Books.SERVER_UID, Books.SERVER_UID);
        mProjectionMap.put(Books.IMAGE, Books.IMAGE);
        mProjectionMap.put(Books.URL, Books.URL);
    }

}
