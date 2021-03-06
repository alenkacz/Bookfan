package cz.alenkacz.bookfan.activity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.provider.BooksProvider.Books;
import cz.alenkacz.bookfan.tools.Constants;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Root of all activities with common functionality
 * 
 * @author Alena Varkockova (varkockova.a@gmail.com)
 *
 */
public class BaseActivity extends SherlockActivity {
	
	private SharedPreferences mPrefs;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mPrefs = getSharedPreferences(Constants.PREFS, MODE_PRIVATE);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                return true;
            case R.id.menu_add:
    			initScan();
    			return true;
            case R.id.menu_logout:
    			logout();
    			return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void initScan() {
		 Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		 intent.putExtra("SCAN_MODE", "EAN_13_MODE");
		 startActivityForResult(intent, 0);
	}
    
    public void logout() {
    	Editor e = mPrefs.edit();
    	e.putString(Constants.PREFS_LOGIN_TOKEN, null);
    	e.putInt(Constants.PREFS_SHELF_ID, 1);
    	e.commit();
    	
    	deleteAllInDb();
    	
    	Intent i = new Intent(getApplicationContext(), LoginActivity.class);
    	i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
    }
    
    public void deleteAllInDb() {
		ContentResolver cr = getContentResolver();
		final String[] projection = { Books._ID };
		Cursor result = cr.query(Books.CONTENT_URI, projection, null, null, null);
		if(result.moveToFirst()) {
			while(!result.isAfterLast()) {
				String id = result.getString(result.getColumnIndex(Books._ID));
				cr.delete(Books.CONTENT_URI, Books._ID + "=" + id, null);
				result.moveToNext();
			}
		}
	}
    
    protected boolean isLoggedIn() {
    	String token = mPrefs.getString(Constants.PREFS_LOGIN_TOKEN, null);
    	
    	return (token != null);
    }
}
