package cz.alenkacz.bookfan.activity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.tools.Constants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_add:
    			initScan();
    			return true;
            case R.id.menu_logout:
    			logout();
    			return true;
            case R.id.menu_shelf_home:
            	item.setChecked(true);
            	return true;
            case R.id.menu_shelf_old:
            	item.setChecked(true);
            	return true;
            case R.id.menu_shelf_read:
            	item.setChecked(true);
            	return true;
            case R.id.menu_shelf_reading:
            	item.setChecked(true);
            	return true;
            case R.id.menu_shelf_toread:
            	item.setChecked(true);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void initScan() {
		/*
		 * Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		 * intent.putExtra("SCAN_MODE", "EAN_13_MODE");
		 * startActivityForResult(intent, 0);
		 */

		// TODO re-enable scanning
		Intent i = new Intent(getApplicationContext(), BookDetailActivity.class);
		i.putExtra(Constants.EXTRA_ISBN, "9788024233109");
		startActivity(i);
	}
    
    public void logout() {
    	Editor e = mPrefs.edit();
    	e.putString(Constants.PREFS_LOGIN_TOKEN, null);
    	e.commit();
    	
    	Intent i = new Intent(getApplicationContext(), LoginActivity.class);
    	i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
    }
    
    protected boolean isLoggedIn() {
    	String token = mPrefs.getString(Constants.PREFS_LOGIN_TOKEN, null);
    	
    	return (token != null);
    }
}
