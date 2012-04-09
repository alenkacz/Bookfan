package cz.alenkacz.bookfan.activity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.tools.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BaseActivity extends SherlockActivity {
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return true;
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
}
