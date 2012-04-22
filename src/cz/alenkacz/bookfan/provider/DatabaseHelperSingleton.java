package cz.alenkacz.bookfan.provider;

import android.content.Context;

/**
 * DB helper singleton provider
 * 
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class DatabaseHelperSingleton {
	private static DatabaseHelper databaseHelper;
	
	public static synchronized DatabaseHelper getDatabaseHelper(Context context) {
		if (databaseHelper == null) {
			databaseHelper = new DatabaseHelper(context);
		}
		
		return databaseHelper;
	}
}
