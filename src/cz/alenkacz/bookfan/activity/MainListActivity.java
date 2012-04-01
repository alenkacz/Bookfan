package cz.alenkacz.bookfan.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;

import cz.alenkacz.bookfan.R;
import cz.alenkacz.bookfan.dto.Book;
import cz.alenkacz.bookfan.dto.UserLogin;
import cz.alenkacz.bookfan.rest.pojo.LoggedUserContainer;
import cz.alenkacz.bookfan.tools.Constants;
import cz.alenkacz.bookfan.tools.Utils;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainListActivity extends BaseActivity {
	
	private ListView mBooksList;
	private List<Book> mBooks;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main_list);
		
		initTempList();
		setupViews();
	}
	
	private void initTempList() {
		mBooks = new ArrayList<Book>();
		
		mBooks.add(new Book("Test knihy"));
		mBooks.add(new Book("Kniha 2"));
		mBooks.add(new Book("Kniha 3"));
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.menu_add:
	            	initScan();
	            	return true;
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	    }
	
	private void setupViews() {
		mBooksList = (ListView) findViewById(R.id.books_list_lv);
		mBooksList.setAdapter(new BooksAdapter(this, R.layout.part_book_item, mBooks));
		
		mBooksList.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO put some actual code here
				Intent i = new Intent(getApplicationContext(), BookDetailActivity.class);
				startActivity(i);
			}
			
		});
	}
	
	private void initScan() {
		/*Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "EAN_13_MODE");
        startActivityForResult(intent, 0);*/
        
		// TODO re-enable scanning
		Intent i = new Intent(getApplicationContext(), BookDetailActivity.class);
        i.putExtra(Constants.EXTRA_ISBN, "9788024233109");
        startActivity(i);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            String contents = intent.getStringExtra("SCAN_RESULT");
	            
	            Intent i = new Intent(getApplicationContext(), BookDetailActivity.class);
		        i.putExtra(Constants.EXTRA_ISBN, contents);
		        startActivity(i);
	        } else if (resultCode == RESULT_CANCELED) {
	            // Handle cancel
	        }
	    }
	}
}
